import {HttpRequest, MessageParams} from "./httprequest.js";
import * as json from "./jsonformats.js";

export function stockSearch(stockSymbol: string, onStockFound: (stock: json.Stock) => void, onStockNotFound: () => void)
{
    let params: MessageParams =
            {
                // @ts-ignore
                message: "dataRequest=" + json.serialize({type: "stock", symbols: [stockSymbol]}),
                protocol: "POST",
                uri: "/dataStream",
                headers: [{name: "Listener-name", value: "stockRequest"}]
            };
    params.onReceived = response =>
    {
        let resp: json.StockRequestResult = json.deserialize(response);
        if(resp && resp.data.length > 0)
        {

            onStockFound(resp.data[0]);
        }
        else
        {
            onStockNotFound();
        }
    }

    let request = new HttpRequest(params);
    request.send();
}

export function ezStockSearch(inputElement: HTMLElement, onStockFound: (stock: json.Stock) => void, onStockNotFound: () => void)
{
    inputElement.addEventListener("keyup", (e) =>
    {
        if(e.key === 'Enter')
        {
            // @ts-ignore
            stockSearch(inputElement.value, onStockFound, onStockNotFound);
        }
    });
}