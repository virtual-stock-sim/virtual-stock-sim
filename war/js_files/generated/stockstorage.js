import * as json from "./jsonformats.js";
import { StockRequest } from "./stockrequest.js";
import { StockData, StockRequestItem } from "./jsonformats.js";
/**
 * Stores the given stock data objects with the symbol as the key
 * @param dataArr Array of Json stock data objects
 */
export function storeStockData(dataArr) {
    dataArr.forEach((data) => window.localStorage.setItem(data.symbol, json.Jsonable.serialize(data)));
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
            let stockData = json.Jsonable.deserialize(StockData, data);
            // Is the data still unexpired
            if (stockData.lastUpdated.getTime() + stockData.ttl >= Date.now()) {
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
    if (invalidStocks.length <= 0) {
        onDataRetrieved(validStocks);
    }
    else {
        console.log("??");
        // Assemble a list of StockRequestItems from the invalid stocks
        let requestItems = invalidStocks.map(stock => new StockRequestItem(json.StockType.STOCK_DATA, stock));
        let responseReceived = (response) => {
            try {
                for (let item of response) {
                    // Make sure response is either okay or processing
                    // Since this is just storing/retrieving stock data, we don't care if
                    // the dynamic stock data isn't available yet
                    if (item.data && (item.code === json.StockResponseCode.OK || item.code === json.StockResponseCode.PROCESSING)) {
                        storeStockData([item.data]);
                        validStocks.push(item.data);
                    }
                    else {
                        console.log(item);
                        // TODO: Handle error
                    }
                }
            }
            catch (e) {
                console.log(e);
                // TODO: Handle error
            }
            onDataRetrieved(validStocks);
        };
        let request = new StockRequest(requestItems, responseReceived);
        request.send();
    }
}
