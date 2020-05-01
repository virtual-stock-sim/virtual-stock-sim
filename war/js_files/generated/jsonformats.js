/**
 * The interfaces are here mainly as a reference for the actual JSON formats
 */
export var StockType;
(function (StockType) {
    StockType["STOCK"] = "stock";
    StockType["STOCK_DATA"] = "data";
    StockType["BOTH"] = "both";
})(StockType || (StockType = {}));
(function (StockType) {
    function deserialize(type) {
        for (let key of Object.keys(StockType)) {
            if (StockType[key] == type) {
                return StockType[key];
            }
        }
        return undefined;
    }
    StockType.deserialize = deserialize;
})(StockType || (StockType = {}));
export var StockResponseCode;
(function (StockResponseCode) {
    // Stock was not already in DB but does exist and is being added in next update cycle
    StockResponseCode["PROCESSING"] = "102";
    // Stock was found and returned in response
    StockResponseCode["OK"] = "200";
    // Request is bad/malformed
    StockResponseCode["BAD_REQUEST"] = "400";
    // Provided stock symbol is valid but could not be found
    StockResponseCode["SYMBOL_NOT_FOUND"] = "404";
    // Provided stock symbol is invalid
    StockResponseCode["INVALID_STOCK_SYMBOL"] = "412";
    // Internal server error
    StockResponseCode["SERVER_ERROR"] = "500";
    // Type parameter for request is invalid
    StockResponseCode["INVALID_TYPE"] = "501";
})(StockResponseCode || (StockResponseCode = {}));
(function (StockResponseCode) {
    function deserialize(code) {
        for (let key of Object.keys(StockResponseCode)) {
            if (StockResponseCode[key] == code) {
                return StockResponseCode[key];
            }
        }
        return undefined;
    }
    StockResponseCode.deserialize = deserialize;
})(StockResponseCode || (StockResponseCode = {}));
export class Jsonable {
    constructor() { }
    fromJson(json) { return this.fromJsonObject(JSON.parse(json)); }
    static deserialize(type, json) {
        if (type === undefined || json === undefined)
            return undefined;
        try {
            if (typeof json === "string") {
                return new type().fromJson(json);
            }
            else if (typeof json === "object") {
                return new type().fromJsonObject(json);
            }
            else {
                console.error("`json` parameter must be a string or an object that is the result of JSON.parse(). '" + (typeof json) + "' was given instead");
                return undefined;
            }
        }
        catch (e) {
            console.error(e);
            return undefined;
        }
    }
    static serialize(jsonable) {
        return jsonable === undefined ? undefined : jsonable.toJson();
    }
}
export class UpdateMessage extends Jsonable {
    constructor(type = undefined) {
        super();
        this._type = type;
    }
    get type() {
        return this._type;
    }
    set type(value) {
        this._type = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.type) {
            return new UpdateMessage(StockType.deserialize(obj.type));
        }
        else {
            return undefined;
        }
    }
    toJson() {
        return JSON.stringify({ update: this._type });
    }
}
export class StockRequestItem extends Jsonable {
    constructor(type = undefined, symbol = undefined) {
        super();
        this._type = type;
        this._symbol = symbol;
    }
    get type() {
        return this._type;
    }
    set type(value) {
        this._type = value;
    }
    get symbol() {
        return this._symbol;
    }
    set symbol(value) {
        this._symbol = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.type && obj.symbol) {
            return new StockRequestItem(obj.type, obj.symbol);
        }
        else {
            return undefined;
        }
    }
    toJson() {
        return `{"type": "${this._type}", "symbol": "${this._symbol}"}`;
        // return JSON.stringify({type: this._type, symbol: this._symbol});
    }
}
export class StockRequest extends Jsonable {
    constructor(items = undefined) {
        super();
        this._items = items;
    }
    get items() {
        return this._items;
    }
    set items(value) {
        this._items = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.items !== undefined) {
            return new StockRequest(obj.items.map(item => Jsonable.deserialize(StockRequestItem, item)));
        }
        else {
            return undefined;
        }
    }
    toJson() {
        return `{"items": [${this._items.map(item => Jsonable.serialize(item))}]}`;
        // return JSON.stringify({items: this._items.map(item => Jsonable.serialize(item))});
    }
}
export class StockResponseItem extends Jsonable {
    constructor(code = undefined, type = undefined, symbol = undefined, stock = undefined, data = undefined) {
        super();
        this._code = code;
        this._type = type;
        this._symbol = symbol;
        this._stock = stock;
        this._data = data;
    }
    get code() {
        return this._code;
    }
    set code(value) {
        this._code = value;
    }
    get type() {
        return this._type;
    }
    set type(value) {
        this._type = value;
    }
    get symbol() {
        return this._symbol;
    }
    set symbol(value) {
        this._symbol = value;
    }
    get stock() {
        return this._stock;
    }
    set stock(value) {
        this._stock = value;
    }
    get data() {
        return this._data;
    }
    set data(value) {
        this._data = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.code) {
            return new StockResponseItem(StockResponseCode.deserialize(obj.code), StockType.deserialize(obj.type), obj.symbol, Jsonable.deserialize(Stock, obj.stock), Jsonable.deserialize(StockData, obj.data));
        }
        else {
            return undefined;
        }
    }
    toJson() {
        return JSON.stringify({ code: this._code, type: this._type, symbol: this._symbol, stock: Jsonable.serialize(this._stock), data: Jsonable.serialize(this._data) });
    }
}
export class StockResponse extends Jsonable {
    constructor(code = undefined, items = undefined) {
        super();
        this._code = code;
        this._items = items;
    }
    get code() {
        return this._code;
    }
    set code(value) {
        this._code = value;
    }
    get items() {
        return this._items;
    }
    set items(value) {
        this._items = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.code && obj.items !== undefined) {
            return new StockResponse(StockResponseCode.deserialize(obj.code), obj.items.map(item => Jsonable.deserialize(StockResponseItem, item)));
        }
        return undefined;
    }
    toJson() {
        return JSON.stringify({ code: this._code, items: this._items.map(item => Jsonable.serialize(item)) });
    }
}
export class Stock extends Jsonable {
    constructor(symbol = undefined, currPrice = undefined, prevClose = undefined, percentChange = undefined, currVolume = undefined, prevVolume = undefined, lastUpdated = undefined) {
        super();
        this._symbol = symbol;
        this._currPrice = isNaN(currPrice) ? undefined : currPrice;
        this._prevClose = isNaN(prevClose) ? undefined : prevClose;
        this._percentChange = isNaN(percentChange) ? undefined : percentChange;
        this._currVolume = isNaN(currVolume) ? undefined : currVolume;
        this._prevVolume = isNaN(prevVolume) ? undefined : prevVolume;
        this._lastUpdated = lastUpdated;
    }
    get symbol() {
        return this._symbol;
    }
    set symbol(value) {
        this._symbol = value;
    }
    get currPrice() {
        return this._currPrice;
    }
    set currPrice(value) {
        this._currPrice = value;
    }
    get prevClose() {
        return this._prevClose;
    }
    set prevClose(value) {
        this._prevClose = value;
    }
    get percentChange() {
        return this._percentChange;
    }
    set percentChange(value) {
        this._percentChange = value;
    }
    get currVolume() {
        return this._currVolume;
    }
    set currVolume(value) {
        this._currVolume = value;
    }
    get prevVolume() {
        return this._prevVolume;
    }
    set prevVolume(value) {
        this._prevVolume = value;
    }
    get lastUpdated() {
        return this._lastUpdated;
    }
    set lastUpdated(value) {
        this._lastUpdated = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj.symbol && obj.currPrice && obj.prevClose && obj.percentChange && obj.currVolume && obj.prevVolume && obj.lastUpdated) {
            return new Stock(obj.symbol, parseFloat(obj.currPrice), parseFloat(obj.prevClose), parseFloat(obj.percentChange), parseInt(obj.currVolume), parseInt(obj.prevVolume), parseDate(obj.lastUpdated));
        }
        else {
            return undefined;
        }
    }
    toJson() {
        return JSON.stringify({
            symbol: this._symbol,
            currPrice: this._currPrice,
            prevClose: this._prevClose,
            percentChange: this._percentChange,
            currVolume: this._currVolume,
            prevVolume: this._prevVolume,
            lastUpdated: this._lastUpdated
        });
    }
}
export class StockData extends Jsonable {
    constructor(symbol = undefined, description = undefined, history = undefined, lastUpdated = undefined, ttl = undefined) {
        super();
        this._symbol = symbol;
        this._description = description;
        this._history = history;
        this._lastUpdated = lastUpdated;
        this._ttl = isNaN(ttl) ? undefined : ttl;
    }
    get symbol() {
        return this._symbol;
    }
    set symbol(value) {
        this._symbol = value;
    }
    get description() {
        return this._description;
    }
    set description(value) {
        this._description = value;
    }
    get history() {
        return this._history;
    }
    set history(value) {
        this._history = value;
    }
    get lastUpdated() {
        return this._lastUpdated;
    }
    set lastUpdated(value) {
        this._lastUpdated = value;
    }
    get ttl() {
        return this._ttl;
    }
    set ttl(value) {
        this._ttl = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.symbol && obj.description && obj.history !== undefined && obj.lastUpdated && obj.ttl) {
            return new StockData(obj.symbol, obj.description, obj.history.map(item => Jsonable.deserialize(StockHistoricalData, item)), parseDate(obj.lastUpdated), parseInt(obj.ttl));
        }
        return undefined;
    }
    toJson() {
        return JSON.stringify({
            symbol: this._symbol,
            description: this._description,
            history: this._history.map(item => Jsonable.serialize(item)),
            lastUpdated: this._lastUpdated,
            ttl: this._ttl
        });
    }
}
export class StockHistoricalData extends Jsonable {
    constructor(date = undefined, open = undefined, high = undefined, low = undefined, close = undefined, adjclose = undefined, volume = undefined) {
        super();
        this._date = date;
        this._open = isNaN(open) ? undefined : open;
        this._high = isNaN(high) ? undefined : high;
        this._low = isNaN(low) ? undefined : low;
        this._close = isNaN(close) ? undefined : close;
        this._adjclose = isNaN(adjclose) ? undefined : adjclose;
        this._volume = isNaN(volume) ? undefined : volume;
    }
    get date() {
        return this._date;
    }
    set date(value) {
        this._date = value;
    }
    get open() {
        return this._open;
    }
    set open(value) {
        this._open = value;
    }
    get high() {
        return this._high;
    }
    set high(value) {
        this._high = value;
    }
    get low() {
        return this._low;
    }
    set low(value) {
        this._low = value;
    }
    get close() {
        return this._close;
    }
    set close(value) {
        this._close = value;
    }
    get adjclose() {
        return this._adjclose;
    }
    set adjclose(value) {
        this._adjclose = value;
    }
    get volume() {
        return this._volume;
    }
    set volume(value) {
        this._volume = value;
    }
    fromJsonObject(jsonObj) {
        let obj = jsonObj;
        if (obj && obj.date && obj.open && obj.high && obj.low && obj.close && obj.adjclose && obj.volume) {
            return new StockHistoricalData(parseDate(obj.date), parseFloat(obj.open), parseFloat(obj.high), parseFloat(obj.low), parseFloat(obj.close), parseFloat(obj.adjclose), parseFloat(obj.volume));
        }
        return undefined;
    }
    toJson() {
        return JSON.stringify({
            date: this._date,
            open: this._open,
            high: this._high,
            low: this._low,
            close: this._close,
            adjclose: this._adjclose,
            volume: this._close
        });
    }
}
function parseDate(date) {
    let time = Date.parse(date);
    if (!isNaN(time)) {
        let d = new Date();
        d.setTime(time);
        return d;
    }
    else {
        return undefined;
    }
}
