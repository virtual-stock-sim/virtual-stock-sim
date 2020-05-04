/**
 * XMLHttpRequest wrapper
 */
export class HttpRequest {
    constructor(params) {
        this._params = params;
        HttpRequest.processParams(params);
    }
    get params() { return this._params; }
    set params(params) { this._params = params; }
    static processParams(params) {
        if (!params.onReceived) {
            params.onReceived = () => { };
        }
        if (!params.headers) {
            params.headers = [];
        }
        if (params.headers.filter((h) => h.name === "Content-type").length === 0) {
            params.headers.push({ name: "Content-type", value: "application/x-www-form-urlencoded" });
        }
        if (!params.useAsync) {
            params.useAsync = true;
        }
    }
    send() {
        let req = new XMLHttpRequest();
        req.open(this._params.protocol, this._params.uri, this._params.useAsync);
        this._params.headers.forEach((header) => req.setRequestHeader(header.name, header.value));
        req.onload = () => { this._params.onReceived(req.response); };
        req.send(encodeURI(this._params.message));
    }
}
export var HttpRequestType;
(function (HttpRequestType) {
    HttpRequestType["GET"] = "GET";
    HttpRequestType["POST"] = "POST";
})(HttpRequestType || (HttpRequestType = {}));
