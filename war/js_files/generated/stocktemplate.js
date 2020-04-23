import { getStockData } from "./stockstorage.js";
import { drawPriceHistoryGraph } from "./graphs.js";
import { DataStream } from "./datastream.js";
import * as json from "./jsonformats.js";
import { HttpRequest } from "./httprequest.js";
if (!document.getElementById("stockInit")) {
    // Setup the server stream for updating stocks
    let stream = new DataStream("stockStream");
    stream.onMessageReceived = (event) => {
        // Attempt to deserialize the incoming message
        let msg = json.deserialize(event.data);
        // Make sure that the message is valid and contains something we want
        if (msg && msg.update == 'stock') {
            // Form and send the request for updated stocks
            let dataRequest = { type: "stock", symbols: findStocksInPage() };
            let params = {
                message: "dataRequest=" + json.serialize(dataRequest),
                protocol: "POST",
                uri: "/dataStream",
                headers: [{ name: "Listener-name", value: "stockRequest" }],
                onReceived: response => updateStocks(response)
            };
            let request = new HttpRequest(params);
            request.send();
        }
    };
    // Create a hidden div to indicate that this script has already run
    let tag = document.createElement("div");
    tag.hidden = true;
    tag.id = "stockInit";
    document.body.append(tag);
    // Find the symbols of all the stock templates in the page
    let stockSymbols = findStocksInPage();
    // Add the stock data to each of the templates
    getStockData(stockSymbols, stockDatas => {
        for (let stockData of stockDatas) {
            let desc = document.getElementById(stockData.symbol + "-desc");
            desc.innerText = stockData.description;
        }
    });
    // After window load (to make sure that the google graph script has loaded) draw the graphs
    window.addEventListener("load", () => {
        let configs = [];
        for (let symbol of stockSymbols) {
            configs.push({
                element: document.getElementById(symbol + "-depth-graph"),
                stockSymbol: symbol
            });
        }
        drawPriceHistoryGraph(configs);
    });
}
function findStocksInPage() {
    let stockSymbols = [];
    for (let templateTag of document.getElementsByClassName("stock-template")) {
        let stockSymbolTag = templateTag.getElementsByClassName("stockSymbol")[0];
        stockSymbols.push(stockSymbolTag.innerHTML);
    }
    return stockSymbols;
}
function updateStocks(response) {
    let result = json.deserialize(response);
    if (response && result.type == 'stock') {
        for (let stock of result.data) {
            let tags;
            if (stock.currPrice) {
                let tags = document.getElementsByName(stock.symbol + "-curr_price");
                for (let tag of tags)
                    tag.innerHTML = "$" + stock.currPrice;
            }
            if (stock.prevClose) {
                tags = document.getElementsByName(stock.symbol + "-prev_close");
                for (let tag of tags)
                    tag.innerHTML = "$" + stock.prevClose;
            }
            if (stock.currVolume) {
                tags = document.getElementsByName(stock.symbol + "-curr_volume");
                for (let tag of tags)
                    tag.innerHTML = stock.currVolume;
            }
            if (stock.prevVolume) {
                tags = document.getElementsByName(stock.symbol + "-curr_volume");
                for (let tag of tags)
                    tag.innerHTML = stock.currVolume;
            }
            if (stock.percentChange) {
                tags = document.getElementsByName(stock.symbol + "-pchange");
                let pChangeColor = parseFloat(stock.percentChange) >= 0.0 ? "green!important" : "red!important";
                let percentChange = stock.percentChange + "%";
                for (let tag of tags) {
                    tag.style.color = pChangeColor;
                    tag.innerHTML = percentChange;
                }
            }
        }
    }
}
