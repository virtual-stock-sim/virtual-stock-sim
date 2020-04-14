class Datastream
{
    constructor(streamURI = "/dataStream")
    {
        this.uri = streamURI;
        this.source = new EventSource(this.uri);
        this.source.onopen = function (e)
        {
            console.log("Connection to " + streamURI + " established");
        }

        this.source.onerror = function(e)
        {
            console.log("Error in connection with " + streamURI);
        }

        this.sender = new XMLHttpRequest();

    }

    setOnMessage(func)
    {
        this.source.onmessage = func;
    }

    sendMessage(msg)
    {

    }
}