import {DataStream} from "./datastream.js";
import {drawPriceHistoryGraph, GraphConfig} from "./graphs.js";
import {HttpRequest, MessageParams} from "./httprequest.js";
import * as json from "./jsonformats.js";
import {ezStockSearch} from "./stocksearch.js";
import {storeStockData} from "./stockstorage.js";

// Stocks present in page to keep track of
let stocks: string[] = [];

let stream = new DataStream("stockStream", "/dataStream");
stream.onMessageReceived = (event) =>
{
    let msg: json.UpdateMessage = json.deserialize(event.data);
    if(stocks.length > 0 && msg && msg.update === 'stockData')
    {
        let params: MessageParams =
                {
                    message: "dataRequest=" + json.serialize({type: "stockData", symbols: stocks}),
                    protocol: "POST",
                    uri: "/dataStream",
                    headers: [{name: "Listener-name", value: "stockRequest"}],
                    onReceived: onStockUpdate
                }

        let request = new HttpRequest(params);
        request.send();
    }
}

let inputField: HTMLInputElement = document.getElementById("search-input") as HTMLInputElement;
let graphsInPage = new Map();
ezStockSearch(inputField,
              results =>
              {
                  // Reset error text
                  document.getElementById("error-text").innerText = "";

                  let stock: json.Stock = results[0];
                  stocks.push(stock.symbol);
                  let element = document.getElementById(stock.symbol + "-graph");
                  if(!element)
                  {
                      element = document.createElement("div");
                      element.id = stock.symbol + "-graph";
                      document.body.appendChild(element);
                  }
                  let config = {element: element, stockSymbol: stock.symbol, minDate: null, maxDate: null};
                  drawPriceHistoryGraph([config]);
                  graphsInPage.set(element.id, config);
              },
              () =>
              {
                  document.getElementById("error-text").innerText = inputField.value + " not found";
              });

function onStockUpdate(response: string)
{
    console.log(response);
    let resp: json.StockDataRequestResult = json.deserialize(response);
    if(resp && resp.type === "stockData")
    {
        storeStockData(resp.data);
        drawPriceHistoryGraph(Array.from(graphsInPage.values()));
    }



}