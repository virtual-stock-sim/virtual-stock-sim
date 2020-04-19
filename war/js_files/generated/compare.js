import { DataStream } from "./datastream.js";
import { drawPriceHistoryGraph } from "./graphs.js";
import { HttpRequest } from "./httprequest.js";
import * as json from "./jsonformats.js";
import { ezStockSearch } from "./stocksearch.js";
let stream = new DataStream("stockStream", "/dataStream");
stream.onMessageReceived = (event) => {
    let msg = json.deserialize(event.data);
    if (msg && msg.update === 'stock') {
        let params = {
            message: "dataRequest=" + json.serialize({ type: "stock", symbols: ["AMZN", "F"] }),
            protocol: "POST",
            uri: "/dataStream",
            headers: [{ name: "Listener-name", value: "stockRequest" }],
            onReceived: response => console.log(response)
        };
        let request = new HttpRequest(params);
        request.send();
    }
};
/*window.addEventListener("load", () =>
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
});*/
let inputField = document.getElementById("search-input");
ezStockSearch(inputField, stock => {
    let element = document.getElementById(stock.symbol + "-graph");
    if (!element) {
        element = document.createElement("div");
        element.id = stock.symbol + "-graph";
    }
    document.body.appendChild(element);
    drawPriceHistoryGraph([{ element: element, stockSymbol: stock.symbol, minDate: null, maxDate: null }]);
}, () => {
    document.getElementById("error-text").innerText = inputField.value + " not found";
});
