import * as json from "./jsonformats.js";
import { storeStockData } from "./stockstorage.js";
import { StockRequest } from "./stockrequest.js";
export function stockSearch(stockSymbol, searchType, onStockFound, onStockNotFound) {
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
    let requestItem = new json.StockRequestItem(searchType, stockSymbol);
    let request = new StockRequest([requestItem], onSearchResult);
    request.send();
}
export function ezStockSearch(inputElement, onStockFound, onStockNotFound) {
    inputElement.addEventListener("keyup", (e) => {
        if (e.key === 'Enter') {
            // @ts-ignore
            let stockSymbol = inputElement.value;
            let type = json.StockType.deserialize(inputElement.dataset.type);
            if (type !== undefined) {
                stockSearch(stockSymbol, type, onStockFound, onStockNotFound);
            }
            else {
                console.error("Undefined search type: " + inputElement.dataset.type);
            }
        }
    });
}
