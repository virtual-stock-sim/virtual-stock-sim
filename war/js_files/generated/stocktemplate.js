import { getStockData } from "./stockstorage.js";
import { drawPriceHistoryGraph } from "./graphs.js";
import { DataStream } from "./datastream.js";
import * as json from "./jsonformats.js";
import { HttpRequest } from "./httprequest.js";
import { loadDependencies } from "./dependencyloader.js";
if (!document.getElementById("stockInit")) {
    let dependencies = [
        { uri: "https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js", type: "script", async: false },
        { uri: "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js", type: "script", async: false },
        { uri: "https://www.gstatic.com/charts/loader.js", type: "script" },
        { uri: "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css", type: "stylesheet" },
        { uri: "https://fonts.googleapis.com/icon?family=Material+Icons", type: "stylesheet" },
        { uri: "../../cssfiles/stockTemplateStyle.css", type: "stylesheet" }
    ];
    loadDependencies(dependencies);
    // loadDependencies();
    // Setup the server stream for updating stocks
    let stream = new DataStream("stockStream");
    stream.onMessageReceived = (event) => {
        // Attempt to deserialize the incoming message
        let msg = json.deserialize(event.data);
        // Make sure that the message is valid and contains something we want
        if (msg && msg.update === 'stock') {
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
    window.addEventListener("load", () => {
        let script = document.createElement("script");
        script.type = "text/javascript";
        script.text = "let shownBsCollapseEvent = new Event('shown.bs.collapse'); $('.collapse').on('shown.bs.collapse', (event) => { event.target.dispatchEvent(shownBsCollapseEvent); });";
        document.head.appendChild(script);
        let configs = [];
        let stockSymbols = findStocksInPage();
        for (let symbol of stockSymbols) {
            let config = {
                element: document.getElementById(symbol + "-depth-graph"),
                stockSymbol: symbol
            };
            configs.push(config);
            //TODO: Range slider has weird width until collapse then expand again after first collapse if graph is not drawn beforehand
            document.getElementById(symbol + "-dropdown").addEventListener("shown.bs.collapse", () => drawPriceHistoryGraph([config]));
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
