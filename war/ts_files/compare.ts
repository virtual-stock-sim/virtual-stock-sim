import {DataStream} from "./datastream.js";
import {getStockData} from "./stockstorage.js";
import {drawPriceHistoryGraph} from "./graphs.js";
import {HttpRequest, MessageParams} from "./httprequest.js";
import * as json from "./jsonformats.js";

let stream = new DataStream("stockStream", "/dataStream");
stream.onMessageReceived = (event) =>
{
    let msg: {update: string} = json.deserialize(event.data);
    if(msg && msg.update === 'stock')
    {
        let params: MessageParams =
                {
                    message: "dataRequest=" + json.serialize({type: "stock", symbols: ["AMZN", "F"]}),
                    protocol: "POST",
                    uri: "/dataStream",
                    headers: [{name: "Listener-name", value: "stockRequest"}],
                    onReceived: response => console.log(response)
                }

        let request = new HttpRequest(params);
        request.send();
    }
}

getStockData(["AMZN"], (d) => console.log(d[0]));

window.addEventListener("load", () =>
{
        drawPriceHistoryGraph(
                [
                        {element: document.getElementById("amzn-graph"), stockSymbol: "AMZN", minDate: null, maxDate: null},
                        {element: document.getElementById("tsla-graph"), stockSymbol: "TSLA", minDate: Date.parse("2016-01-05"), maxDate: Date.now()}
                ]
        );
    drawPriceHistoryGraph(
            [
                {element: document.getElementById("f-graph"), stockSymbol: "F", minDate: null, maxDate: null},
            ]
    );
});