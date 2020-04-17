/*
 *   This file does NOT need to be included anywhere, its just
 *   here to define various JSON objects in the project
 *   so editors can perform code inspection and for
 *   human reference
 */

/**
 * Stock JSON Object
 * @typedef Stock
 * TODO:
 */


/**
 * Historical data for a time period starting at `date` and ending at next entry in array or present date
 * @typedef StockData.HistoricalData
 * @property {string} date 'Stringified' starting date of time period
 * @property {string} open Opening share price
 * @property {string} high Share price high
 * @property {string} low Share price low
 * @property {string} close Closing share price
 * @property {string} adjclose Adjusted closing share price
 * @property {string} volume Volume of shares
 */

/**
 *  Stock Data JSON Object
 *  @typedef StockData
 *  @property {string} symbol Stock symbol
 *  @property {string} description Description of the company
 *  @property {StockData.HistoricalData} history Array of historical data
 *  @property {string} lastUpdated 'Stringified' date of when this data is from
 *  @property {int} ttl Time to live of object in milliseconds
 */

/**
 * Configuration Object for a price history graph
 * @typedef GraphConfig
 * @property {HTMLElement} elem Element to draw graph to
 * @property {string} stockSymbol Symbol of the stock that graph is being generated for
 * @property {Date|null} minDate Minimum date in range to be graphed. Null for no minimum
 * @property {Date|null} maxDate Maximum date in range to be graphed. Null for no maximum
 */