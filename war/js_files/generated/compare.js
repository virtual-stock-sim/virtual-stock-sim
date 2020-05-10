import { DataStream } from "./datastream.js";
import { StockRequest } from "./stockrequest.js";
import * as json from "./jsonformats.js";
import { ezStockSearch } from "./stocksearch.js";
import { displayLoadingWheel, removeLoadingWheel } from "./loadingwheel.js";
import { displayModal } from "./modal.js";
// Stocks present in page to keep track of
let stocks = [];
// Set up data stream to handle stock updates
let stream = new DataStream("stockStream", "/dataStream");
stream.onMessageReceived = (event) => {
    let msg = json.Jsonable.deserialize(json.UpdateMessage, event.data);
    if (stocks.length > 0 && msg && msg.type !== json.StockType.FOLLOW) {
        let requestItems = stocks.map(stock => new json.StockRequestItem(msg.type, stock));
        let request = new StockRequest(requestItems, onStockUpdate);
        request.send();
    }
};
// Set up the stock search bar
let inputField = document.getElementById("search-input");
ezStockSearch(inputField, result => {
    // Reset error text
    document.getElementById("error-text").innerText = "";
    // if data is present and code is okay or processing, display stock template
    if (result.code === json.StockResponseCode.OK || result.code === json.StockResponseCode.PROCESSING) {
        stocks.push(result.symbol);
        let listOfStocks = document.getElementById("stocks-in-page");
        if (!listOfStocks.value) {
            listOfStocks.value = result.symbol;
        }
        else {
            console.log(listOfStocks.value);
            if (listOfStocks.value.indexOf(result.symbol) < 0) {
                listOfStocks.value = listOfStocks.value.concat("," + result.symbol);
            }
        }
        if (result.code === json.StockResponseCode.PROCESSING) {
            displayModal("Success!", "We've found '" + result.symbol + "' but it isn't in our system yet. We'll automatically refresh your page within the " +
                "next minute once your data is available. Thank you for your patience");
            displayLoadingWheel("Loading stock information. Please don't refresh your page, this may take up to a minute...");
            let processingStocks = document.getElementById("processing-stocks");
            if (!processingStocks) {
                processingStocks = document.createElement("div");
                processingStocks.id = "processing-stocks";
                processingStocks.hidden = true;
                processingStocks.innerText = result.symbol;
            }
            else {
                processingStocks.innerText += ("," + result.symbol);
            }
            document.body.appendChild(processingStocks);
        }
        else if (result.code === json.StockResponseCode.OK) {
            // submit form with input searched
            let stockForm = document.getElementById("add-stock-form");
            stockForm.submit();
            console.log("Form submitted");
        }
    }
    else if (result.code === json.StockResponseCode.INVALID_STOCK_SYMBOL) {
        let message;
        if (result.stock) {
            message = "Your stock symbol was invalid. It must only contain English letters and be no greater than 10 characters long";
        }
        else {
            message = "The search field cannot be empty";
        }
        displayModal("Houston, We've Got A Problem", message, "Error Code: " + result.code);
    }
    else if (result.symbol && result.code === json.StockResponseCode.SYMBOL_NOT_FOUND) {
        displayModal("Stock not available", "Sorry! '" + result.symbol + "' is not available within our system. We apologize for the inconvenience.", "Error Code: " + result.code);
    }
    else {
        displayModal("Houston, We've Got A Problem", "It seems something went wrong on our end. Please wait a minute and try again," +
            " we apologize for the inconvenience. ", "Error Code: " + result.code);
    }
}, errorCode => {
    document.getElementById("error-text").innerText = inputField.value + " not found. Error Code: " + errorCode;
});
let stockSymbols = findStocksInPage();
// Add event listeners to all follow buttons
for (let symbol of stockSymbols) {
    let button = document.getElementById(symbol + "-follow-btn");
    if (button) {
        button.addEventListener("click", () => {
            let followRequest = new StockRequest([new json.StockRequestItem(json.StockType.FOLLOW, symbol)], resp => {
                if (resp && resp[0]) {
                    let item = resp[0];
                    if (item.code === json.StockResponseCode.OK) {
                        // Submit and refresh the page so the button will update
                        let stockForm = document.getElementById("add-stock-form");
                        stockForm.submit();
                    }
                    else {
                        displayModal("Whoops!", "It seems something went wrong on our end. Please wait a minute and try again," +
                            " we apologize for the inconvenience. ", "Error Code: " + item.code);
                    }
                }
            });
            followRequest.send();
        });
    }
}
function onStockUpdate(response) {
    let processingStocksElem = document.getElementById("processing-stocks");
    if (processingStocksElem) {
        let processingStocks = processingStocksElem.innerText.split(",");
        for (let item of response) {
            if (item.code === json.StockResponseCode.OK || item.code === json.StockResponseCode.PROCESSING) {
                if (processingStocks.indexOf(item.symbol) > -1) {
                    // Submit and refresh the page so the button will update
                    let stockForm = document.getElementById("add-stock-form");
                    stockForm.submit();
                }
            }
            else {
                displayModal("Houston, We've Got A Problem", "It seems something went wrong on our end. Please wait a minute and try again," +
                    " we apologize for the inconvenience. ", "Error Code: " + item.code);
                removeLoadingWheel();
                break;
            }
        }
        processingStocksElem.remove();
    }
}
function findStocksInPage() {
    let stockSymbols = [];
    for (let templateTag of document.getElementsByClassName("stock-template")) {
        let stockSymbolTag = templateTag.getElementsByClassName("stockSymbol")[0];
        stockSymbols.push(stockSymbolTag.innerHTML);
    }
    return stockSymbols;
}
