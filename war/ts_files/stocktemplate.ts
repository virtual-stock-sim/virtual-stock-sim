import {getStockData} from "./stockstorage.js";
import {drawPriceHistoryGraph, GraphConfig} from "./graphs.js";

if(!document.getElementById("stockInit"))
{
    let tag = document.createElement("div");
    tag.hidden = true;
    tag.id = "stockInit"
    document.body.append(tag);

    let stockSymbols = [];

    for(let templateTag of document.getElementsByClassName("stock-template"))
    {
        let stockSymbolTag = templateTag.getElementsByClassName("stockSymbol")[0];
        stockSymbols.push(stockSymbolTag.innerHTML);
    }

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
        let configs: GraphConfig[] = [];
        for(let symbol of stockSymbols)
        {
            configs.push
                   ({
                        element: document.getElementById(symbol + "-depth-graph"),
                        stockSymbol: symbol
                    });
        }
        drawPriceHistoryGraph(configs);
    });

}
