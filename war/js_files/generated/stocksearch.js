import * as json from "./jsonformats.js";
import { storeStockData } from "./stockstorage.js";
import { StockRequest } from "./stockrequest.js";
import { displayLoadingWheel, removeLoadingWheel } from "./loadingwheel.js";
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
    try {
        inputElement.addEventListener("keyup", (e) => {
            if (e.key === 'Enter') {
                displayLoadingWheel("Loading stock information. This may take up to a minute...");
                // @ts-ignore
                let stockSymbol = inputElement.value;
                let type = json.StockType.deserialize(inputElement.dataset.type);
                if (type !== undefined) {
                    stockSearch(stockSymbol, type, (stock) => {
                        removeLoadingWheel();
                        onStockFound(stock);
                    }, (err) => {
                        removeLoadingWheel();
                        onStockNotFound(err);
                    });
                }
                else {
                    console.error("Undefined search type: " + inputElement.dataset.type);
                }
            }
        });
    }
    catch (e) {
        removeLoadingWheel();
        console.error(e);
    }
}
