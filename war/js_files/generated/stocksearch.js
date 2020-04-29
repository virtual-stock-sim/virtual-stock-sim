import { HttpRequest } from "./httprequest.js";
import * as json from "./jsonformats.js";
import { storeStockData } from "./stockstorage.js";
export function stockSearch(stockSymbol, onStockFound, onStockNotFound) {
    let params = {
        // @ts-ignore
        message: "dataRequest=" + json.serialize({ type: "stockSearch", symbols: [stockSymbol] }),
        protocol: "POST",
        uri: "/dataStream",
        headers: [{ name: "Listener-name", value: "stockRequest" }]
    };
    params.onReceived = response => {
        let resp = json.deserialize(response);
        if (resp && resp.data.length > 0) {
            let stockDatas = [];
            for (let value of resp.data) {
                // Only add to the array if this
                // value actually has a stock data field
                if (value.stockData) {
                    stockDatas.push(value.stockData);
                }
            }
            if (stockDatas.length > 0) {
                storeStockData(stockDatas);
            }
            let stocks = [];
            for (let value of resp.data)
                stocks.push(value.stock);
            onStockFound(stocks);
        }
        else {
            onStockNotFound();
        }
    };
    let request = new HttpRequest(params);
    request.send();
}
export function ezStockSearch(inputElement, onStockFound, onStockNotFound) {
    inputElement.addEventListener("keyup", (e) => {
        if (e.key === 'Enter') {
            // @ts-ignore
            let stockSymbol = inputElement.value;
            stockSearch(stockSymbol, onStockFound, onStockNotFound);
        }
    });
}
