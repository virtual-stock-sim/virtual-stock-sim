import { HttpRequest } from "./httprequest.js";
import * as json from "./jsonformats.js";
export function stockSearch(stockSymbol, onStockFound, onStockNotFound) {
    let params = {
        // @ts-ignore
        message: "dataRequest=" + json.serialize({ type: "stock", symbols: [stockSymbol] }),
        protocol: "POST",
        uri: "/dataStream",
        headers: [{ name: "Listener-name", value: "stockRequest" }]
    };
    params.onReceived = response => {
        let resp = json.deserialize(response);
        if (resp && resp.data.length > 0) {
            onStockFound(resp.data[0]);
        }
        else {
            onStockNotFound();
        }
    };
    let request = new HttpRequest(params);
    request.send();
}
export function ezStockSearch(inputElement, onStockFound, onStockNotFound) {
    inputElement.addEventListener("keyup", (e) => {
        if (e.key === 'Enter') {
            // @ts-ignore
            stockSearch(inputElement.value, onStockFound, onStockNotFound);
        }
    });
}
