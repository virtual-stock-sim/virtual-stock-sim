class DataStream
{
    id = "";
    streamName = "";
    connectAttempts = 1;
    maxConnectAttempts = 5;
    source = null;
    onMessage = (e) => {};
    idMessageReceived = false;
    closed = false;

    /**
     *
     * @param streamName Name of data stream for message queue if applicable
     * @param streamURI Where to connect to
     */
    constructor(streamName, streamURI = "/dataStream")
    {
        this.streamName = streamName;
        this.streamURI = streamURI;
        this._newConnection();
        window.addEventListener("beforeunload", (e) => {
            this.close();
        });
    }

    /**
     * Set callback function for message received event
     * @param func Callback function. Needs single parameter for event
     */
    setOnMessage(func)
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
     * Sends a message to the stream source and returns response if applicable
     * @param msg Message to send
     * @param protocol Protocol to use. i.e. GET or POST
     * @param onReceived Callback function for when response is received
     * @param async Should asynchronous requests be used
     */
    sendMessage({msg, protocol, onReceived = () => {}, async = true})
    {
        console.log("Sent Message:\n\t" + msg);
        let req = new XMLHttpRequest();
        req.open(protocol, this.streamURI, async);
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.setRequestHeader("Stream-name", this.streamName);
        req.setRequestHeader("id", this.id);
        req.onreadystatechange = () => { onReceived(req) };
        req.send(msg);
    }

    _newConnection()
    {

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
            this.connectAttempts = 1;
            console.log("Connection to " + this.streamURI + " established");
            this.closed = false;
        }

        this.source.onerror = (e) =>
        {
            if(this.connectAttempts <= this.maxConnectAttempts)
            {
                console.log("Error in connection with " + this.streamURI + ". Reconnect attempt #" + this.connectAttempts);
                this.idMessageReceived = false;
                this._newConnection();
                this.connectAttempts += 1;
            }
            else
            {
                console.log("Error in connection with " + this.streamURI + ". Max reconnect attempts reached");
            }
        }
    }

    close()
    {
        this.source.close();
        // Disable async to make sure the message is sent before page unload
        this.sendMessage({msg: "op=close", protocol: "POST", async: false});
        this.closed = true;
    }
}