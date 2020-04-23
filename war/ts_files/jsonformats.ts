
export function deserialize<T>(json: string): T
{
    return {...JSON.parse(json)} as T;
}

export function serialize(obj: object)
{
    return JSON.stringify(obj);
}

export interface DataRequest
{
    type: string;
    symbols: string[];
}

export interface StockRequestResult
{
    type: string;
    data: Stock[];
}

export interface StockDataRequestResult
{
    type: string;
    data: StockData[];
}

export interface Stock
{
    /** Stock symbol */
    symbol: string;
    /** Current price per share */
    currPrice: string;
    /** Last closing of price per share */
    prevClose: string;
    /** Percent change of different between current price and last closing price */
    percentChange: string;
    /** Current market volume */
    currVolume: string;
    /** Last market volume */
    prevVolume: string;
    /** 'Stringified' date of when this data was last updated */
    lastUpdated: string;
}

export interface StockData
{
    /** Stock symbol */
    symbol: string;
    /** Company description */
    description: string;
    /** Array of historical data */
    history: HistoricalData[];
    /** 'Stringified' date of when this data was last updated */
    lastUpdated: string;
   /** Time to live of object in milliseconds */
    ttl: string;
}

export interface HistoricalData
{
    /** Starting date of time period */
    date: string;
    /** Opening share price */
    open: string;
    /** Share price high */
    high: string;
    /** Share price low */
    low: string;
    /** Closing share price */
    close: string;
    /** Adjusted closeing share price */
    adjclose: string;
    /** Volume of shares */
    volume: string;
}