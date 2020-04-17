/**
 * Stores the given JSON stock data objects with the symbol as the key
 * @param {StockData[]} dataArr Array of JSON stock data objects
 */
function storeStockData(dataArr)
{
    for(let data of dataArr)
    {
        window.localStorage.setItem(data.symbol, JSON.stringify(data));
    }
}

/**
 * Callback for when stock data is retrieved
 * @callback dataRetrieved
 * @param {StockData[]} JSON Array of stock data
 */

/**
 * Retrieve an array of stock data objects and perform onDataRetrieved() on the array
 *
 * @param {string[]} symbolArr Array of stock symbols
 * @param {dataRetrieved} onDataRetrieved
 */
function getStockData(symbolArr, onDataRetrieved)
{
    /** @type {string[]} Array of stock data symbols that are either outdated or don't exist */
    let invalidStocks = [];
    /** @type {StockData[]} Array of stock data objects that are valid and were successfully retrieved */
    let validStocks = [];

    // Determine which stock symbols have a valid stored value and which don't
    for(let symbol of symbolArr)
    {
        let data = window.localStorage.getItem(symbol);
        if(data !== null)
        {
            let dataObj = JSON.parse(data);
            // Has the data expired
            if(Date.parse(dataObj.lastUpdated) + dataObj.ttl >= Date.now())
            {
                validStocks.push(JSON.parse(data));
            }
            else
            {
                invalidStocks.push(symbol);
            }
        }
        else
        {
            invalidStocks.push(symbol);
        }
    }

    // Only query server for data if there is invalid stock data
    if(invalidStocks.length !== 0)
    {
        let dataStream = new DataStream("stockStream", "/dataStream");
        dataStream.setOnMessageReceived((e) =>
                                        {
                                            let response = JSON.parse(e.data);
                                            // Check that response contents are valid
                                            if(response === null || response.type !== 'stockDatas')
                                            {
                                                throw "Data request failed - Wrong type returned. 'stockDatas' should be returned but '" + response.type + "' was instead";
                                            }
                                            else
                                            {
                                                /** @type {StockData[]} Array of stock data objects that were returned in response */
                                                let returnedStockData = [];
                                                // Iterate over each stock data returned in response
                                                for(let data of response.data)
                                                {
                                                    returnedStockData.push(data);
                                                }

                                                this.storeStockData(returnedStockData);

                                                // Add returned stocks to the list of valid stock data objects
                                                validStocks.push(...returnedStockData);
                                                dataStream.close();

                                                // Finally, call the callback function with the returned stocks
                                                onDataRetrieved(validStocks);
                                            }
                                        });
        // Ask the server for the data of symbols with invalid stored data
        let dataReq = {type: "stockDatas", symbols: invalidStocks};
        dataStream.sendMessage(new DataRequestMessage(JSON.stringify(dataReq)));
    }
    else
    {
        onDataRetrieved(validStocks);
    }
}