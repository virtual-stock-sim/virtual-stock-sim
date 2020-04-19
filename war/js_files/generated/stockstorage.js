import * as json from "./jsonformats.js";
import { HttpRequest } from "./httprequest.js";
/**
 * Stores the given stock data objects with the symbol as the key
 * @param dataArr Array of Json stock data objects
 */
export function storeStockData(dataArr) {
    dataArr.forEach((data) => window.localStorage.setItem(data.symbol, json.serialize(data)));
}
class StockDataResp {
}
/**
 * Retrieve an array of stock data objects and perform onDataRetrieved(stockData[]) on the array
 * @param symbolArr
 * @param onDataRetrieved
 */
export function getStockData(symbolArr, onDataRetrieved) {
    let invalidStocks = [];
    let validStocks = [];
    // Determine which stock symbols have a valid stored value and which don't
    for (let symbol of symbolArr) {
        let data = window.localStorage.getItem(symbol);
        if (data) {
            let stockData = json.deserialize(data);
            // Has the date expired
            if (Date.parse(stockData.lastUpdated) + parseInt(stockData.ttl) >= Date.now()) {
                validStocks.push(stockData);
            }
            else {
                invalidStocks.push(symbol);
            }
        }
        else {
            invalidStocks.push(symbol);
        }
    }
    // Only query server for data if there is invalid stock data
    if (invalidStocks.length > 0) {
        let params = {
            message: "dataRequest=" + json.serialize({ type: "stockData", symbols: invalidStocks }),
            protocol: "POST",
            uri: "/dataStream",
            headers: [{ name: "Listener-name", value: "stockRequest" }]
        };
        params.onReceived = (response) => {
            let resp = json.deserialize(response);
            // Check that response contents are valid
            if (!resp && resp.type !== "stockData") {
                throw "Data request failed - Wrong type returned. 'stockData' should be returned but '" + resp.type + "' was instead";
            }
            else {
                storeStockData(resp.data);
                validStocks.push(...resp.data);
                onDataRetrieved(validStocks);
            }
        };
        let request = new HttpRequest(params);
        request.send();
    }
    else {
        onDataRetrieved(validStocks);
    }
}
