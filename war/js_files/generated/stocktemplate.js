import { getStockData } from "./stockstorage.js";
if (!document.getElementById("stockInit")) {
    let tag = document.createElement("div");
    tag.hidden = true;
    tag.id = "stockInit";
    document.body.append(tag);
    let stockSymbols = [];
    for (let templateTag of document.getElementsByClassName("stock-template")) {
        let stockSymbolTag = templateTag.getElementsByClassName("stockSymbol")[0];
        stockSymbols.push(stockSymbolTag.innerHTML);
    }
    getStockData(stockSymbols, stockDatas => {
        for (let stockData of stockDatas) {
            let desc = document.getElementById(stockData.symbol + "-desc");
            desc.innerText = stockData.description;
        }
    });
}
