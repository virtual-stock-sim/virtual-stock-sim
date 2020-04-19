/**
 * XMLHttpRequest wrapper
 */
export class HttpRequest {
    constructor(params) {
        this._params = params;
        if (!this._params.onReceived) {
            this._params.onReceived = () => { };
        }
        if (!this._params.headers) {
            this._params.headers = [];
        }
        if (this._params.headers.filter((h) => h.name === "Content-type").length === 0) {
            this._params.headers.push({ name: "Content-type", value: "application/x-www-form-urlencoded" });
        }
        if (!this._params.useAsync) {
            this._params.useAsync = true;
        }
    }
    get params() { return this._params; }
    set params(params) { this._params = params; }
    send() {
        let req = new XMLHttpRequest();
        req.open(this._params.protocol, this._params.uri, this._params.useAsync);
        this._params.headers.forEach((header) => req.setRequestHeader(header.name, header.value));
        req.onload = () => { this._params.onReceived(req.response); };
        req.send(this._params.message);
    }
}
