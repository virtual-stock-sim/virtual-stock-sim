import {HttpRequest, HttpRequestType, MessageParams} from "./httprequest.js";
import * as json from "./jsonformats.js";

/**
 * HttpRequest with pre-defined headers for a stock request.
 */
export class StockRequest extends HttpRequest
{
    private _stockRequest: json.StockRequest;
    constructor(requestItems: json.StockRequestItem[], onReceived: (responseItems: json.StockResponseItem[]) => void = () => {})
    {
        let stockRequest = new json.StockRequest(requestItems);
        let params: MessageParams =
                {
                    message: "stockRequest=" + json.Jsonable.serialize(stockRequest),
                    protocol: HttpRequestType.POST,
                    uri: "/dataStream",
                    headers: [{name: "Listener-name", value: "stockRequest"}],
                    onReceived: response =>
                    {
                        let resp: json.StockResponse = json.Jsonable.deserialize(json.StockResponse, response);
                        // resp.code is the code about the message integrity, so there's no feedback to give to the client
                        // other than an error occurred that they can't fix. That's why resp.code is checked here
                        let result = resp === undefined || resp.code !== json.StockResponseCode.OK ? undefined : resp.items;
                        onReceived(result);
                    }
                };

        super(params);
        this._stockRequest = stockRequest;
    }
}