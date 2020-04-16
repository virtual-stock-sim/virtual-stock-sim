/**
 * @class Message
 * @classdesc Outgoing HTTP message for DataStream
 */
class Message
{
    /**
     * @param msg {string} Message to be sent
     * @param protocol {string} HTTP Protocol to send message over
     * @param onReceived {function(XMLHttpRequest)} Message response handler
     * @param async {boolean} Should this be sent asynchronously
     */
    constructor(msg, protocol, onReceived = () => {}, async = true)
    {
        /** @type {string} Message to be sent */
        this.message = msg;
        /** @type {string} HTTP Protocol to send message over */
        this.protocol = protocol;
        /** @type {function(XMLHttpRequest)} Message response handler */
        this.onReceived = onReceived;
        /** @type {boolean} Should this be sent asynchronously */
        this.useAsync = async;
    }

    /** @returns {string} */
    getMessage() { return this.message; }
    /** @returns {string} */
    getProtocol() { return this.protocol; }
    /** @returns {function(XMLHttpRequest)} */
    getOnReceived() { return this.onReceived; }
    /** @returns {boolean} */
    getUseAsync() { return this.useAsync; }

}

/**
 * @class DataStream
 * @classdesc Handles incoming and outgoing data from/to a event stream.
 */
class DataStream
{
    /**
     *
     * @param streamName Name of stream that coincides with a server-side stream handler
     * @param streamURI Where to connect to
     */
    constructor(streamName, streamURI = "/dataStream")
    {
        /** @type {string} Connection ID from server */
        this.id = "";
        /** @type {string} */
        this.streamName = streamName;
        /** @type {string} */
        this.streamURI = streamURI;
        /** @type {EventSource} Stream connection */
        this.source = null;
        /** @type {int} */
        this.reconnectAttempts = 0;
        /** @type {int} */
        this.maxReconnectAttempts = 5;
        /** @type {function} Handler for when a message is received from the stream */
        this.onMessage = (e) => {};
        /** @type {boolean} Has a message from the server containing the stream id been received */
        this.idMessageReceived = false;
        /** @type {boolean} Is connection closed */
        this.closed = false;
        /** @type {Message[]} Queue of messages to be sent to stream source through an HTTP protocol */
        this.outgoingMsgQueue = []

        this._newConnection();
        window.addEventListener("beforeunload", (e) => {
            this.close();
        });

        this._sendNextMsgInQueue();
    }

    // Message queue to make sure that id is available for header
    _sendNextMsgInQueue()
    {
        if(this.outgoingMsgQueue.length > 0 && this.idMessageReceived)
        {
            let msg = this.outgoingMsgQueue.shift();
            this._sendMessage(msg);
            setTimeout(() => this._sendNextMsgInQueue(), 1);
        }
        else
        {
            setTimeout(() => this._sendNextMsgInQueue(), 10);
        }
    }

    /**
     * Set callback function for message received event
     * @param func {function(Event)} Callback function
     */
    setOnMessageReceived(func)
    {
        this.onMessage = func;
        // Only set the function if the message containing the stream ID
        // has been sent, otherwise let the default message handler
        // set and execute the message function
        if(this.idMessageReceived)
        {
            this.source.onmessage = this.onMessage;
        }
    }

    /**
     * Adds message to outgoing message queue
     * Sends message and receives response through HTTP requests
     * IMPORTANT: Server responses that contain large amounts of data
     * or take a longer amount of time to process should not be sent
     * back through the protocol to prevent request blocks.
     * Instead set up a parameter system to indicate that the server
     * should send the data through the stream and handle the response
     * in onMessage.
     *
     * @param message {Message} Message to send
     * @public
     */
    sendMessage(message)
    {
        this.outgoingMsgQueue.push(message);
    }

    /**
     *
     * @param message {Message}
     * @private
     */
    _sendMessage(message)
    {
        let req = new XMLHttpRequest();
        req.open(message.getProtocol(), this.streamURI, message.getUseAsync());
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.setRequestHeader("Stream-name", this.streamName);
        req.setRequestHeader("id", this.id);
        req.onreadystatechange = () => { message.getOnReceived()(req) };
        req.send(message.getMessage());
    }

    /**
     * Opens a new connection to the stream source
     * @private
     */
    _newConnection()
    {

        // Close the stream if its already open
        if(this.source !== null && !this.closed)
        {
            this.close();
        }

        this.source = new EventSource(this.streamURI);
        // First message should be a message from the stream servlet containing
        // the async context's id
        this.source.onmessage = (e) =>
        {
            let data = e.data;
            if(data.substr(0, 3) === "id=" && !this.idMessageReceived)
            {
                this.id = data.substr(3);
                this.idMessageReceived = true;
                this.source.onmessage = this.onMessage;
            }
        }

        this.source.onopen = (e) =>
        {
            this.reconnectAttempts = 1;
            console.log("Connection to " + this.streamURI + " established");
            this.closed = false;
        }

        this.source.onerror = (e) =>
        {
            if(this.reconnectAttempts <= this.maxReconnectAttempts)
            {
                console.log("Error in connection with " + this.streamURI + ". Reconnect attempt #" + this.reconnectAttempts);
                this.idMessageReceived = false;
                this._newConnection();
                this.reconnectAttempts += 1;
            }
            else
            {
                console.log("Error in connection with " + this.streamURI + ". Max reconnect attempts reached");
            }
        }
    }

    /**
     * Close the stream connection
     * @public
     */
    close()
    {
        this.source.close();
        // Disable async to make sure the message is sent before page unload
        // Also bypass message queue to ensure that the id of the stream being closed is sent with the message,
        // not the id of a new stream that may be in the process of opening during a reconnection attempt
        this._sendMessage(new Message("op=close", "POST", () => {}, false));
        this.closed = true;
    }
}