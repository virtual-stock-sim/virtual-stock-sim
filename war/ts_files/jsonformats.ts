/**
 * The interfaces are here mainly as a reference for the actual JSON formats
 */

export enum StockType
{
    STOCK = "stock",
    STOCK_DATA = "data",
    BOTH = "both",
    FOLLOW = "follow"
}

export namespace StockType
{
    export function deserialize(type: string): StockType
    {
        for(let key of Object.keys(StockType))
        {
            if(StockType[key] == type)
            {
                return StockType[key];
            }
        }
        return undefined;
    }
}

export enum StockResponseCode
{
    // Stock was not already in DB but does exist and is being added in next update cycle
    PROCESSING = "102",
    // Stock was found and returned in response
    OK = "200",
    // Request is bad/malformed
    BAD_REQUEST = "400",
    // Provided stock symbol is valid but could not be found
    SYMBOL_NOT_FOUND = "404",
    // Provided stock symbol is invalid
    INVALID_STOCK_SYMBOL = "412",
    // Internal server error
    SERVER_ERROR = "500",
    // Type parameter for request is invalid
    INVALID_TYPE = "501"
}

export namespace StockResponseCode
{
    export function deserialize(code: string | number): StockResponseCode
    {
        for(let key of Object.keys(StockResponseCode))
        {
            if(StockResponseCode[key] == code)
            {
                return StockResponseCode[key];
            }
        }
        return undefined;
    }
}

export abstract class Jsonable<U>
{
    protected constructor() { }
    protected fromJson(json: string): U { return this.fromJsonObject(JSON.parse(json)); }
    protected abstract fromJsonObject(jsonObj: object): U;
    public abstract _toJsonObject(): object;
    public static deserialize<T extends Jsonable<T>>(type: {new(): T;}, json: string | object): T
    {
        if(type === undefined || json === undefined) return undefined;

        try
        {
            if(typeof json === "string")
            {
                return new type().fromJson(json);
            }
            else if(typeof json === "object")
            {
                return new type().fromJsonObject(json);
            }
            else
            {
                console.error("`json` parameter must be a string or an object that is the result of JSON.parse(). '" + (typeof json) + "' was given instead");
                return undefined;
            }
        }
        catch (e)
        {
            console.error(e);
            return undefined;
        }
    }
    public static serialize<T extends Jsonable<T>>(jsonable: T): string
    {
        return jsonable === undefined ? undefined : JSON.stringify(jsonable._toJsonObject());
    }
}

interface IUpdateMessage
{
    /** Type of update */
    type: string;
}
export class UpdateMessage extends Jsonable<UpdateMessage>
{
    private _type: StockType;
    constructor(type: StockType = undefined)
    {
        super();
        this._type = type;
    }

    get type(): StockType
    {
        return this._type;
    }

    set type(value: StockType)
    {
        this._type = value;
    }

    protected fromJsonObject(jsonObj: object): UpdateMessage
    {
        let obj = jsonObj as IUpdateMessage;
        if(obj && obj.type)
        {
            return new UpdateMessage(StockType.deserialize(obj.type));
        }
        else
        {
            return undefined;
        }
    }

    _toJsonObject(): object
    {
        return {update: this._type};
    }
}


interface IStockRequestItem
{
    type: StockType;
    symbol: string;
}
export class StockRequestItem extends Jsonable<StockRequestItem>
{
    private _type: StockType;
    private _symbol: string;
    constructor(type: StockType = undefined, symbol: string = undefined)
    {
        super();
        this._type = type;
        this._symbol = symbol;
    }

    get type(): StockType
    {
        return this._type;
    }

    set type(value: StockType)
    {
        this._type = value;
    }

    get symbol(): string
    {
        return this._symbol;
    }

    set symbol(value: string)
    {
        this._symbol = value;
    }

    protected fromJsonObject(jsonObj: object): StockRequestItem
    {
        let obj = jsonObj as IStockRequestItem;
        if(obj && obj.type && obj.symbol)
        {
            return new StockRequestItem(obj.type, obj.symbol);
        }
        else
        {
            return undefined;
        }
    }

    _toJsonObject(): object
    {
        return {type: this._type, symbol: this._symbol};
    }
}

interface IStockRequest
{
    items: IStockRequestItem[];
}
export class StockRequest extends Jsonable<StockRequest>
{
    private _items: StockRequestItem[];
    constructor(items: StockRequestItem[] = undefined)
    {
        super();
        this._items = items;
    }

    get items(): StockRequestItem[]
    {
        return this._items;
    }

    set items(value: StockRequestItem[])
    {
        this._items = value;
    }

    protected fromJsonObject(jsonObj: object): StockRequest
    {
        let obj = jsonObj as IStockRequest;
        if(obj && obj.items !== undefined)
        {
            return new StockRequest(obj.items.map(item => Jsonable.deserialize(StockRequestItem, <any>item)));
        }
        else
        {
            return undefined;
        }
    }

    _toJsonObject(): object
    {
        //return `{"items": [${this._items.map(item => Jsonable.serialize(item))}]}`;
        return {items: this._items.map(item => item._toJsonObject())};
    }
}

interface IStockResponseItem
{
    code: StockResponseCode;
    type?: StockType;
    symbol?: string;
    stock?: IStock;
    data?: IStockData;
}

export class StockResponseItem extends Jsonable<StockResponseItem>
{
    private _code: StockResponseCode;
    private _type: StockType;
    private _symbol: string;
    private _stock: Stock;
    private _data: StockData;

    constructor(code: StockResponseCode = undefined, type: StockType = undefined, symbol: string = undefined, stock: Stock = undefined, data: StockData = undefined)
    {
        super();
        this._code = code;
        this._type = type;
        this._symbol = symbol;
        this._stock = stock;
        this._data = data;
    }

    get code(): StockResponseCode
    {
        return this._code;
    }

    set code(value: StockResponseCode)
    {
        this._code = value;
    }

    get type(): StockType
    {
        return this._type;
    }

    set type(value: StockType)
    {
        this._type = value;
    }

    get symbol(): string
    {
        return this._symbol;
    }

    set symbol(value: string)
    {
        this._symbol = value;
    }

    get stock(): Stock
    {
        return this._stock;
    }

    set stock(value: Stock)
    {
        this._stock = value;
    }

    get data(): StockData
    {
        return this._data;
    }

    set data(value: StockData)
    {
        this._data = value;
    }

    protected fromJsonObject(jsonObj: object): StockResponseItem
    {
        let obj = jsonObj as IStockResponseItem;
        if(obj && obj.code)
        {
            return new StockResponseItem(
                    StockResponseCode.deserialize(obj.code),
                    StockType.deserialize(obj.type),
                    obj.symbol,
                    Jsonable.deserialize(Stock, <any>obj.stock),
                    Jsonable.deserialize(StockData, <any>obj.data)
            );
        }
        else
        {
            return undefined;
        }
    }

    _toJsonObject(): object
    {
        return {code: this._code, type: this._type, symbol: this._symbol, stock: this._stock._toJsonObject(), data: this._data._toJsonObject()};
    }
}

interface IStockResponse
{
    code: StockResponseCode;
    // Array of requested data
    items: IStockResponseItem[];
}
export class StockResponse extends Jsonable<StockResponse>
{
    private _code: StockResponseCode;
    private _items: StockResponseItem[];
    constructor(code: StockResponseCode = undefined, items: StockResponseItem[] = undefined)
    {
        super();
        this._code = code;
        this._items = items;
    }


    get code(): StockResponseCode
    {
        return this._code;
    }

    set code(value: StockResponseCode)
    {
        this._code = value;
    }

    get items(): StockResponseItem[]
    {
        return this._items;
    }

    set items(value: StockResponseItem[])
    {
        this._items = value;
    }

    protected fromJsonObject(jsonObj: object): StockResponse
    {
        let obj = jsonObj as IStockResponse;
        if(obj && obj.code && obj.items !== undefined)
        {
            return new StockResponse(
                    StockResponseCode.deserialize(obj.code),
                    obj.items.map(item => Jsonable.deserialize(StockResponseItem, <any>item))
            );
        }
        return undefined;
    }

    _toJsonObject(): object
    {
        return {code: this._code, items: this._items.map(item => item._toJsonObject())};
    }

}

interface IStock
{
    /** Stock symbol */
    symbol: string;
    /** Current price per share */
    currPrice: any;
    /** Last closing of price per share */
    prevClose: any;
    /** Percent change of different between current price and last closing price */
    percentChange: any;
    /** Current market volume */
    currVolume: any;
    /** Last market volume */
    prevVolume: any;
    /** 'Stringified' date of when this data was last updated */
    lastUpdated: string;
}
export class Stock extends Jsonable<Stock>
{
    private _symbol: string;
    private _currPrice: number;
    private _prevClose: number;
    private _percentChange: number;
    private _currVolume: number;
    private _prevVolume: number;
    private _lastUpdated: Date;
    constructor(
            symbol: string = undefined,
            currPrice: number = undefined,
            prevClose: number = undefined,
            percentChange: number = undefined,
            currVolume: number = undefined,
            prevVolume: number = undefined,
            lastUpdated: Date = undefined
    )
    {
        super();
        this._symbol = symbol;
        this._currPrice = isNaN(currPrice) ? undefined : currPrice;
        this._prevClose = isNaN(prevClose) ? undefined : prevClose;
        this._percentChange = isNaN(percentChange) ? undefined : percentChange;
        this._currVolume = isNaN(currVolume) ? undefined : currVolume;
        this._prevVolume = isNaN(prevVolume) ? undefined : prevVolume;
        this._lastUpdated = lastUpdated
    }


    get symbol(): string
    {
        return this._symbol;
    }

    set symbol(value: string)
    {
        this._symbol = value;
    }

    get currPrice(): number
    {
        return this._currPrice;
    }

    set currPrice(value: number)
    {
        this._currPrice = value;
    }

    get prevClose(): number
    {
        return this._prevClose;
    }

    set prevClose(value: number)
    {
        this._prevClose = value;
    }

    get percentChange(): number
    {
        return this._percentChange;
    }

    set percentChange(value: number)
    {
        this._percentChange = value;
    }

    get currVolume(): number
    {
        return this._currVolume;
    }

    set currVolume(value: number)
    {
        this._currVolume = value;
    }

    get prevVolume(): number
    {
        return this._prevVolume;
    }

    set prevVolume(value: number)
    {
        this._prevVolume = value;
    }

    get lastUpdated(): Date
    {
        return this._lastUpdated;
    }

    set lastUpdated(value: Date)
    {
        this._lastUpdated = value;
    }

    protected fromJsonObject(jsonObj: object): Stock
    {
        let obj = jsonObj as IStock;
        if(obj.symbol && !isNaN(obj.currPrice) && !isNaN(obj.prevClose) && !isNaN(obj.percentChange) && !isNaN(obj.currVolume) && !isNaN(obj.prevVolume) && obj.lastUpdated)
        {
            return new Stock(
                    obj.symbol,
                    parseFloat(obj.currPrice),
                    parseFloat(obj.prevClose),
                    parseFloat(obj.percentChange),
                    parseInt(obj.currVolume),
                    parseInt(obj.prevVolume),
                    parseDate(obj.lastUpdated)
            );
        }
        else
        {
            return undefined;
        }
    }

    _toJsonObject(): object
    {
        return {
                    symbol: this._symbol,
                    currPrice: this._currPrice,
                    prevClose: this._prevClose,
                    percentChange: this._percentChange,
                    currVolume: this._currVolume,
                    prevVolume: this._prevVolume,
                    lastUpdated: this._lastUpdated
                };
    }
}

interface IStockData
{
    /** Stock symbol */
    symbol: string;
    /** Company description */
    description: string;
    /** Array of historical data */
    history: IStockHistoricalData[];
    /** 'Stringified' date of when this data was last updated */
    lastUpdated: string;
    /** Time to live of object in milliseconds */
    ttl: any;
}
export class StockData extends Jsonable<StockData>
{
    private _symbol: string;
    private _description: string;
    private _history: StockHistoricalData[];
    private _lastUpdated: Date;
    private _ttl: number;
    constructor(symbol: string = undefined, description: string = undefined, history: StockHistoricalData[] = undefined, lastUpdated: Date = undefined, ttl: number = undefined)
    {
        super();
        this._symbol = symbol;
        this._description = description;
        this._history = history;
        this._lastUpdated = lastUpdated;
        this._ttl = isNaN(ttl) ? undefined : ttl;
    }

    get symbol(): string
    {
        return this._symbol;
    }

    set symbol(value: string)
    {
        this._symbol = value;
    }

    get description(): string
    {
        return this._description;
    }

    set description(value: string)
    {
        this._description = value;
    }

    get history(): StockHistoricalData[]
    {
        return this._history;
    }

    set history(value: StockHistoricalData[])
    {
        this._history = value;
    }

    get lastUpdated(): Date
    {
        return this._lastUpdated;
    }

    set lastUpdated(value: Date)
    {
        this._lastUpdated = value;
    }

    get ttl(): number
    {
        return this._ttl;
    }

    set ttl(value: number)
    {
        this._ttl = value;
    }

    protected fromJsonObject(jsonObj: object): StockData
    {
        let obj = jsonObj as IStockData;
        if(obj && obj.symbol && obj.description && obj.history !== undefined && obj.lastUpdated && !isNaN(obj.ttl))
        {
            return new StockData(
                    obj.symbol,
                    obj.description,
                    obj.history.map(item => Jsonable.deserialize(StockHistoricalData, <any>item)),
                    parseDate(obj.lastUpdated),
                    parseInt(obj.ttl)
            );
        }
        return undefined;
    }

    _toJsonObject(): object
    {
        return {
                    symbol: this._symbol,
                    description: this._description,
                    history: this._history.map(item => item._toJsonObject()),
                    lastUpdated: this._lastUpdated,
                    ttl: this._ttl
                };
    }
}

interface IStockHistoricalData
{
    /** Starting date of time period */
    date: string;
    /** Opening share price */
    open: any;
    /** Share price high */
    high: any;
    /** Share price low */
    low: any;
    /** Closing share price */
    close: any;
    /** Adjusted closing share price */
    adjclose: any;
    /** Volume of shares */
    volume: any;
}
export class StockHistoricalData extends Jsonable<StockHistoricalData>
{
    private _date: Date;
    private _open: number;
    private _high: number;
    private _low: number;
    private _close: number;
    private _adjclose: number;
    private _volume: number;
    constructor(
            date: Date = undefined,
            open: number = undefined,
            high: number = undefined,
            low: number = undefined,
            close: number = undefined,
            adjclose: number = undefined,
            volume: number = undefined
    )
    {
        super();
        this._date = date;
        this._open = isNaN(open) ? undefined : open;
        this._high = isNaN(high) ? undefined : high;
        this._low = isNaN(low) ? undefined : low;
        this._close = isNaN(close) ? undefined : close;
        this._adjclose = isNaN(adjclose) ? undefined : adjclose;
        this._volume = isNaN(volume) ? undefined : volume;
    }

    get date(): Date
    {
        return this._date;
    }

    set date(value: Date)
    {
        this._date = value;
    }

    get open(): number
    {
        return this._open;
    }

    set open(value: number)
    {
        this._open = value;
    }

    get high(): number
    {
        return this._high;
    }

    set high(value: number)
    {
        this._high = value;
    }

    get low(): number
    {
        return this._low;
    }

    set low(value: number)
    {
        this._low = value;
    }

    get close(): number
    {
        return this._close;
    }

    set close(value: number)
    {
        this._close = value;
    }

    get adjclose(): number
    {
        return this._adjclose;
    }

    set adjclose(value: number)
    {
        this._adjclose = value;
    }

    get volume(): number
    {
        return this._volume;
    }

    set volume(value: number)
    {
        this._volume = value;
    }

    protected fromJsonObject(jsonObj: object): StockHistoricalData
    {
        let obj = jsonObj as IStockHistoricalData;
        if(obj && obj.date && !isNaN(obj.open) && !isNaN(obj.high) && !isNaN(obj.low) && !isNaN(obj.close) && !isNaN(obj.adjclose) && !isNaN(obj.volume))
        {
            return new StockHistoricalData(
                    parseDate(obj.date),
                    parseFloat(obj.open),
                    parseFloat(obj.high),
                    parseFloat(obj.low),
                    parseFloat(obj.close),
                    parseFloat(obj.adjclose),
                    parseFloat(obj.volume)
            );
        }
        return undefined;
    }

    _toJsonObject(): object
    {
        return {
                    date: this._date,
                    open: this._open,
                    high: this._high,
                    low: this._low,
                    close: this._close,
                    adjclose: this._adjclose,
                    volume: this._close
                };
    }

}

function parseDate(date: string): Date
{
    let time = Date.parse(date);
    if(!isNaN(time))
    {
        let d = new Date();
        d.setTime(time);
        return d;
    }
    else
    {
        return undefined;
    }
}

interface IStockFollowRequest
{
    symbol: string;
}


interface IStockFollowResponse
{
    code: string;
}