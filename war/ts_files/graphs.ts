import {getStockData} from "./stockstorage.js";

declare let google: any;
export function drawPriceHistoryGraph(configs: GraphConfig[])
{
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(() => { configs.forEach((c) => createGraph(c)) });
}

function createGraph(config: GraphConfig)
{
    getStockData([config.stockSymbol], (stockData) =>
    {
        let history = stockData[0].history;

        // Headers for graph
        let data: any[] = [["Date", "High", "Low", "Adj Close"]];

        // Fill data array with price history
        for(let period of history)
        {
            // Should this period be included in graph
            let include = true;
            let date = Date.parse(period.date);

            if(config.minDate && config.maxDate)
            {
                include = date >= config.minDate && date <= config.maxDate;
            }
            else if(config.minDate)
            {
                include = date >= config.minDate;
            }
            else if(config.maxDate)
            {
                include = date <= config.maxDate;
            }

            if(include)
            {
                data.push
                    ([
                         period.date,
                         parseFloat(period.high),
                         parseFloat(period.low),
                         parseFloat(period.adjclose)
                     ]);
            }
        }

        // Graph visual options
        let options =
                {
                    title: config.stockSymbol + ' - Monthly Share Prices', textStyle:{color: '#FFFFFF'},
                    hAxis: {title: 'Date', titleTextStyle:{color: '#FFFFFF'}, textStyle:{color:'#FFFFFF'}},
                    vAxis: {title: 'Price Per Share', titleTextStyle:{color: '#FFFFFF'}, textStyle:{color:'#FFFFFF'}},
                    seriesType: 'bars',
                    series: {2: {type: 'line'}},
                    backgroundColor: '#222222',
                    titleTextStyle:{color: '#FFFFFF'},
                    legend: {textStyle:{color: '#FFFFFF'}},
                    chartArea:{width: `50%`, height:`70%`},

                };

        // Create and draw the chart
        // @ts-ignore
        let chart = new google.visualization.ComboChart(config.element);
        // @ts-ignore
        let dataTable = google.visualization.arrayToDataTable(data);
        chart.draw(dataTable, options);
    });
}

export interface GraphConfig
{
    /** HTML DOM Element to draw graph to */
    element: HTMLElement;
    /** Symbol of the stock that graph is being drawn for */
    stockSymbol: string;
    /** Minimum date in range to be graphed */
    minDate?: Date | number;
    /** Maximum date in range to be graphed */
    maxDate?: Date | number;
}