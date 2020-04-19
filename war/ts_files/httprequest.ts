/**
 * XMLHttpRequest wrapper
 */
export class HttpRequest
{
    private _params: MessageParams;
    constructor(params: MessageParams)
    {
        this._params = params;
        if(!this._params.onReceived)
        {
            this._params.onReceived = () => {};
        }
        if(!this._params.headers)
        {
            this._params.headers = [];
        }
        if(this._params.headers.filter((h) => h.name === "Content-type").length === 0)
        {
            this._params.headers.push({name: "Content-type", value: "application/x-www-form-urlencoded"});
        }
        if(!this._params.useAsync)
        {
            this._params.useAsync = true;
        }
    }

    get params() { return this._params; }
    set params(params: MessageParams) { this._params = params; }

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
    protocol: string;
    uri: string;
    onReceived?: (response: string) => void;
    headers?: {name: string, value: string}[];
    useAsync?: boolean;
}