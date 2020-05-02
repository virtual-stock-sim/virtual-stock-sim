import {getStockData} from "./stockstorage.js";
import {drawPriceHistoryGraph, GraphConfig} from "./graphs.js";
import {DataStream} from "./datastream.js";
import * as json from "./jsonformats.js";
import {Dependency, DependencyType, loadDependencies} from "./dependencyloader.js";
import {StockRequest} from "./stockrequest.js";

if(!document.getElementById("stockInit"))
{
    let dependencies: Dependency[] =
            [
                {uri: "https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js", type: DependencyType.SCRIPT, async: false},
                {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js", type: DependencyType.SCRIPT, async: false},
                {uri: "https://www.gstatic.com/charts/loader.js", type: DependencyType.SCRIPT},
                {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css", type: DependencyType.STYLESHEET},
                {uri: "https://fonts.googleapis.com/icon?family=Material+Icons", type: DependencyType.STYLESHEET},
                {uri: "../../cssfiles/stockTemplateStyle.css", type: DependencyType.STYLESHEET}
            ];

    loadDependencies(dependencies);

    // loadDependencies();

    // Setup the server stream for updating stocks
    let stream = new DataStream("stockStream");
    stream.onMessageReceived = (event: MessageEvent) =>
    {
        // Attempt to deserialize the incoming message
        let msg: json.UpdateMessage = json.Jsonable.deserialize(json.UpdateMessage, event.data);
        // Make sure that the message is valid and contains something we want
        if(msg && msg.type === json.StockType.STOCK)
        {
            // Form and send the request for updated stocks
            let requestItems = findStocksInPage().map(stock => new json.StockRequestItem(json.StockType.STOCK, stock));
            let request = new StockRequest(requestItems, updateStocks);
            request.send();
        }
    }

    // Create a hidden div to indicate that this script has already run
    let tag = document.createElement("div");
    tag.hidden = true;
    tag.id = "stockInit"
    document.body.append(tag);

    // Find the symbols of all the stock templates in the page
    let stockSymbols = findStocksInPage();

    // Add the stock data to each of the templates
    getStockData(stockSymbols, stockDatas =>
    {
        for(let stockData of stockDatas)
        {
            let desc = document.getElementById(stockData.symbol + "-desc");
            desc.innerText = stockData.description;
        }
    });

    window.addEventListener("load", () =>
    {
        let script: HTMLScriptElement = document.createElement("script");
        script.type = "text/javascript";
        script.text = "let shownBsCollapseEvent = new Event('shown.bs.collapse'); $('.collapse').on('shown.bs.collapse', (event) => { event.target.dispatchEvent(shownBsCollapseEvent); });";
        document.head.appendChild(script);

        let configs: GraphConfig[] = []
        let stockSymbols = findStocksInPage();
        for(let symbol of stockSymbols)
        {
            let config =
                    {
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

function findStocksInPage(): string[]
{
    let stockSymbols = [];
    for(let templateTag of document.getElementsByClassName("stock-template"))
    {
        let stockSymbolTag = templateTag.getElementsByClassName("stockSymbol")[0];
        stockSymbols.push(stockSymbolTag.innerHTML);
    }

    return stockSymbols;
}

function updateStocks(responseItems: json.StockResponseItem[])
{
    for(let item of responseItems)
    {
        if(item.code === json.StockResponseCode.PROCESSING)
        {
            // TODO: Notify user
        }
        else if(item.code === json.StockResponseCode.OK && item.stock)
        {
            let stock = item.stock;
            let tags: NodeListOf<HTMLElement>;
            if(stock.currPrice)
            {
                let tags = document.getElementsByName(stock.symbol + "-curr_price");
                for(let tag of tags) tag.innerHTML = "$" + stock.currPrice;
            }

            if(stock.prevClose)
            {
                tags = document.getElementsByName(stock.symbol + "-prev_close");
                for(let tag of tags) tag.innerHTML = "$" + stock.prevClose;
            }

            if(stock.currVolume)
            {
                tags = document.getElementsByName(stock.symbol + "-curr_volume");
                for(let tag of tags) tag.innerHTML = stock.currVolume.toString();
            }

            if(stock.prevVolume)
            {
                tags = document.getElementsByName(stock.symbol + "-curr_volume");
                for(let tag of tags) tag.innerHTML = stock.currVolume.toString();
            }

            if(stock.percentChange)
            {
                tags = document.getElementsByName(stock.symbol + "-pchange");
                let pChangeColor = stock.percentChange >= 0.0 ? "green!important" : "red!important";
                let percentChange = stock.percentChange + "%";
                for(let tag of tags)
                {
                    tag.style.color = pChangeColor;
                    tag.innerHTML = percentChange;
                }
            }
        }
        else
        {

        }
    }
}