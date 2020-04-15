/**
 * Stores stock data locally
 * @param json String that contains the JSON object to be stored
 */
function storeStockData(json)
{
    window.localStorage.setItem(json.symbol, JSON.stringify(json));
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