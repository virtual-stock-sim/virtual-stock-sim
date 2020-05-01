/**
 * XMLHttpRequest wrapper
 */
export class HttpRequest
{
    private _params: MessageParams;
    constructor(params: MessageParams)
    {
        this._params = params;
        HttpRequest.processParams(params);
    }

    get params() { return this._params; }
    set params(params: MessageParams) { this._params = params; }

    private static processParams(params: MessageParams)
    {
        if(!params.onReceived)
        {
            params.onReceived = () => {};
        }
        if(!params.headers)
        {
            params.headers = [];
        }
        if(params.headers.filter((h) => h.name === "Content-type").length === 0)
        {
            params.headers.push({name: "Content-type", value: "application/x-www-form-urlencoded"});
        }
        if(!params.useAsync)
        {
            params.useAsync = true;
        }
    }

    send()
    {
        let req: XMLHttpRequest = new XMLHttpRequest();
        req.open(this._params.protocol, this._params.uri, this._params.useAsync);
        this._params.headers.forEach((header) => req.setRequestHeader(header.name, header.value));
        req.onload = () => { this._params.onReceived(req.response); };
        req.send(encodeURI(this._params.message));
    }
}

export interface MessageParams
{
    message: string;
    protocol: string;       //GET,POST
    uri: string;    //where to send
    onReceived?: (response: string) => void; //when don't care about response dont set
    headers?: {name: string, value: string}[];  //Array of headers that you want to send
    useAsync?: boolean;                         //Request should be sent to async?
}