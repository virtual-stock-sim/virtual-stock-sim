import {HttpRequest} from "./httprequest.js";

export interface MessageReceived
{
    (event: MessageEvent): void;
}


export class DataStream
{
    // Connection ID from server
    private _id: string;
    // Name of the stream that coincides with a server-side stream handler
    private readonly _name: string;
    // Stream source URI (Where to connect to)
    private readonly _uri: string;
    // Stream connection source
    private _connection: EventSource;
    // How many times a reconnect has been attempted
    private _reconnectAttempts: number = 0;
    // Max amount of reconnection attempts
    private _maxReconnectionAttempts: number = 5;
    // Message received callback function
    private _onMessageReceived: MessageReceived = () => {};
    // Has the id message from the server been received
    private _idMessageReceived: boolean = false;
    // Is the stream connection closed
    private _closed: boolean = false;
    // Queue of requests to be sent to stream source through Http protocol
    private _outgoingRequestQueue: HttpRequest[] = [];

    /**
     *
     * @param streamName Name of the stream that coincides with a server-side stream handler
     * @param streamURI Stream source URI (Where to connect to)
     */
    constructor(streamName: string, streamURI: string = "/dataStream")
    {
        this._name = streamName;
        this._uri = streamURI;

        this.newConnection();
        window.addEventListener("beforeunload", () =>
        {
            this.close();
        });

        this.sendNextReqInQueue();
    }

    // Request queue to make sure that id is available to be set as a header
    private sendNextReqInQueue()
    {
        if(this._outgoingRequestQueue.length > 0 && this._idMessageReceived)
        {
            let req: HttpRequest = this._outgoingRequestQueue.shift();
            req.send();
            setTimeout(() => this.sendNextReqInQueue(), 0);
        }
        else
        {
            setTimeout(() => this.sendNextReqInQueue(), 10);
        }
    }

    /**
     * Set callback function for message received event
     * @param callback Callback function
     */
    set onMessageReceived(callback: MessageReceived)
    {
        this._onMessageReceived = callback;

        // Only set the callback for the EventSource if stream ID
        // is available. Otherwise let the default message callback
        // set the callback when the ID is ready
        if(this._idMessageReceived)
        {
            this._connection.onmessage = this._onMessageReceived;
        }
    }

    /**
     * Add required headers to a HttpRequest
     * @param request
     */
    private prepareRequest(request: HttpRequest)
    {
        request.params.headers.push({name: "Listener-name", value: this._name});
        request.params.headers.push({name: "id", value: this._id});
    }

    /**
     * Adds HttpRequest to outgoing HttpRequest queue
     * Sends request and receives response through HTTP requests
     * with the stream ID
     *
     * @param request HttpRequest to send
     */
    sendRequest(request: HttpRequest)
    {
        this.prepareRequest(request);
        this._outgoingRequestQueue.push(request);
    }

    /**
     * Close the stream connection
     */
    close()
    {
        this._connection.close();
        // Disable async to make sure the message is sent before page unload
        // Also bypass message queue to ensure that the id of the stream being closed is sent with the message,
        // not the id of a new stream that may be in the process of opening during a reconnection attempt
        let request = new HttpRequest({message: "op=close", protocol: "POST", uri: this._uri, useAsync: false});
        this.prepareRequest(request);
        request.send();
        this._closed = true;
    }

    /**
     * Open a new connection to the stream source
     */
    private newConnection()
    {
        // Close the connection if its already open
        if(this._connection && !this._closed)
        {
            this.close();
        }

        this._connection = new EventSource(this._uri);

        // First message should be a message from the stream servlet containing
        // the async context's id
        this._connection.onmessage = (e: MessageEvent) =>
        {
            let data: string = e.data;
            if(data.substr(0, 3) === "id=" && !this._idMessageReceived)
            {
                this._id = data.substr(3);
                this._idMessageReceived = true;
                this._connection.onmessage = this._onMessageReceived;
            }
        }

        this._connection.onopen = (e: Event) =>
        {
            this._reconnectAttempts = 1;
            console.log("Connection to " + this._uri + " established");
            this._closed = false;
        }

        this._connection.onerror = (e: Event) =>
        {
            if(this._reconnectAttempts <= this._maxReconnectionAttempts)
            {
                console.log("Error in connection with " + this._uri + ". Reconnect attempt #" + this._reconnectAttempts);
                this._idMessageReceived = false;
                this.newConnection();
                this._reconnectAttempts += 1;
            }
            else
            {
                console.log("Error in connection with " + this._uri + ". Max reconnection attempts reached");
            }
        }
    }
}