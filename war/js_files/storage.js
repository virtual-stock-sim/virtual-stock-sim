/**
 * Stores stock data locally
 * @param jsonString String that contains the JSON object to be stored
 */
function storeStockData(jsonString)
{
    let json = JSON.parse(jsonString);
    let symbol = Object.keys(json)[0];
    window.localStorage.setItem(symbol, JSON.stringify(json[symbol]));
}

/**
 * Gets a stock from local storage
 * @param symbol Symbol of stock
 * @returns {JSON} JSON object of data for stock
 */
function getStockData(symbol)
{
    return JSON.parse(window.localStorage.getItem(symbol));
}