import * as json from "./jsonformats.js";
import { storeStockData } from "./stockstorage.js";
import { StockRequest } from "./stockrequest.js";
export function stockSearch(stockSymbol, onStockFound, onStockNotFound) {
    let onSearchResult = (responseItems) => {
        if (responseItems) {
            let data = responseItems[0].data;
            if (data)
                storeStockData([data]);
            onStockFound(responseItems[0]);
        }
        else {
            onStockNotFound(json.StockResponseCode.SERVER_ERROR);
        }
    };
    // TODO: Allow different type types. Maybe embed an attribute in the input element for ezStockSearch to read
    let requestItem = new json.StockRequestItem(json.StockType.BOTH, stockSymbol);
    let request = new StockRequest([requestItem], onSearchResult);
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
