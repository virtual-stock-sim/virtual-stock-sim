import {getStockData} from "./stockstorage.js";

/**
 * TODO:
 *  Line up range picker better
 *  Date labeling of graph is off by one month. Adding the hAxisTicks fixes this, but the ticks array would need to be updated
 *  on graph range change
 *
 *  TODO: Its also probably a better idea to just use standard "start date" and "end date" date pickers for the range
 */

declare let google: any;
export function drawPriceHistoryGraph(configs: GraphConfig[])
{
    google.charts.load('current', {'packages': ['corechart', 'controls']});
    google.charts.setOnLoadCallback(() => { configs.forEach((c) => createGraph(c)) });
}

function createGraph(config: GraphConfig)
{
    getStockData([config.stockSymbol], (stockData) =>
    {
        let history = stockData[0].history;

        // Headers for graph
        let data: any[] = [["Date", "High", "Low", "Adj Close"]];
        let hAxisTicks: Date[] = [];

        // Fill data array with price history
        for(let period of history)
        {
            // Should this period be included in graph
            let include = true;

            if(config.minDate && config.maxDate)
            {
                include = period.date >= config.minDate && period.date <= config.maxDate;
            }
            else if(config.minDate)
            {
                include = period.date >= config.minDate;
            }
            else if(config.maxDate)
            {
                include = period.date <= config.maxDate;
            }

            if(include)
            {
                hAxisTicks.push(period.date);
                data.push
                    ([
                         period.date,
                         period.high,
                         period.low,
                         period.adjclose
                     ]);
            }
        }

        let chartElem = document.createElement("div");
        chartElem.id = config.stockSymbol + "-chart-wrapper-div";
        config.element.appendChild(chartElem);

        let sliderElem = document.createElement("div");
        sliderElem.id = config.stockSymbol + "-graph-control-wrapper-div";
        config.element.appendChild(sliderElem);

        let dashboard = new google.visualization.Dashboard(config.element);

        let rangeSlider = new google.visualization.ControlWrapper(
                {
                    controlType: "ChartRangeFilter",
                    containerId: sliderElem.id,
                    options:
                            {
                                filterColumnIndex: 0,
                                ui:
                                        {
                                            ticks: hAxisTicks.length,
                                            chartOptions:
                                                    {
                                                        height: 50,
                                                        hAxis: {textPosition: 'none'},
                                                        seriesType: 'bars',
                                                        series: {2: {type: 'line'}},
                                                        backgroundColor: '#222222',
                                                        snapToData: true
                                                    }
                                        }
                            }
                });

        let chartWrapper = new google.visualization.ChartWrapper(
                {
                    chartType: 'ComboChart',
                    containerId: chartElem.id,
                    options:
                            {
                                title: config.stockSymbol + ' - Monthly Share Prices', textStyle:{color: '#FFFFFF'},
                                hAxis: {title: 'Date', /*ticks: hAxisTicks,*/ titleTextStyle:{color: '#FFFFFF'}, textStyle:{color:'#FFFFFF'}},
                                vAxis: {title: 'Price Per Share', titleTextStyle:{color: '#FFFFFF'}, textStyle:{color:'#FFFFFF'}},
                                seriesType: 'bars',
                                series: {2: {type: 'line'}},
                                backgroundColor: '#222222',
                                titleTextStyle:{color: '#FFFFFF'},
                                legend: {textStyle:{color: '#FFFFFF'}},
                            }
                }
        );

        dashboard.bind(rangeSlider, chartWrapper);
        dashboard.draw(data);

/*        // Create and draw the chart
        // @ts-ignore
        let chart = new google.visualization.ComboChart(config.element);
        // @ts-ignore
        let dataTable = google.visualization.arrayToDataTable(data);
        chart.draw(dataTable, options);*/
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