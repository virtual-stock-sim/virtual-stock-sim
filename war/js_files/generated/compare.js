import { DataStream } from "./datastream.js";
import { drawPriceHistoryGraph } from "./graphs.js";
import { StockRequest } from "./stockrequest.js";
import * as json from "./jsonformats.js";
import { ezStockSearch } from "./stocksearch.js";
import { storeStockData } from "./stockstorage.js";
// Stocks present in page to keep track of
let stocks = [];
// Set up data stream to handle stock updates
let stream = new DataStream("stockStream", "/dataStream");
stream.onMessageReceived = (event) => {
    let msg = json.Jsonable.deserialize(json.UpdateMessage, event.data);
    if (stocks.length > 0 && msg && msg.type === json.StockType.STOCK_DATA) {
        let requestItems = stocks.map(stock => new json.StockRequestItem(json.StockType.STOCK_DATA, stock));
        let request = new StockRequest(requestItems, onStockUpdate);
        request.send();
    }
};
// Set up the stock search bar
let inputField = document.getElementById("search-input");
let graphsInPage = new Map();
ezStockSearch(inputField, result => {
    // Reset error text
    document.getElementById("error-text").innerText = "";
    if (result.data && (result.code === json.StockResponseCode.OK || result.code === json.StockResponseCode.PROCESSING)) {
        storeStockData([result.data]);
        stocks.push(result.symbol);
        let element = document.getElementById(result.symbol + "-graph");
        if (!element) {
            element = document.createElement("div");
            element.id = result.symbol + "-graph";
            document.body.appendChild(element);
        }
        let config = { element: element, stockSymbol: result.symbol, minDate: null, maxDate: null };
        drawPriceHistoryGraph([config]);
        graphsInPage.set(element.id, config);
    }
    else {
        document.getElementById("error-text").innerText = "Error: " + result.code;
    }
}, errorCode => {
    document.getElementById("error-text").innerText = inputField.value + " not found. Error Code: " + errorCode;
});
function onStockUpdate(response) {
    let updatedStocks = [];
    for (let item of response) {
        if (item.data && (item.code == json.StockResponseCode.OK || item.code == json.StockResponseCode.PROCESSING)) {
            updatedStocks.push(item.data);
        }
        else {
            // TODO: Handle error
            console.log(item);
        }
    }
    console.log(response);
    storeStockData(updatedStocks);
    drawPriceHistoryGraph(Array.from(graphsInPage.values()));
}
