import { getStockData } from "./stockstorage.js";
export function drawPriceHistoryGraph(configs) {
    google.charts.load('current', { 'packages': ['corechart', 'controls'] });
    google.charts.setOnLoadCallback(() => { configs.forEach((c) => createGraph(c)); });
}
function createGraph(config) {
    getStockData([config.stockSymbol], (stockData) => {
        let history = stockData[0].history;
        // Headers for graph
        let data = [["Date", "High", "Low", "Adj Close"]];
        let hAxisTicks = [];
        // Fill data array with price history
        for (let period of history) {
            // Should this period be included in graph
            let include = true;
            let date = new Date();
            date.setTime(Date.parse(period.date));
            if (config.minDate && config.maxDate) {
                include = date >= config.minDate && date <= config.maxDate;
            }
            else if (config.minDate) {
                include = date >= config.minDate;
            }
            else if (config.maxDate) {
                include = date <= config.maxDate;
            }
            if (include) {
                hAxisTicks.push(date);
                data.push([
                    date,
                    parseFloat(period.high),
                    parseFloat(period.low),
                    parseFloat(period.adjclose)
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
        let rangeSlider = new google.visualization.ControlWrapper({
            controlType: "ChartRangeFilter",
            containerId: sliderElem.id,
            options: {
                filterColumnIndex: 0,
                ui: {
                    ticks: hAxisTicks.length,
                    chartOptions: {
                        height: 50,
                        hAxis: { textPosition: 'none' },
                        seriesType: 'bars',
                        series: { 2: { type: 'line' } },
                        backgroundColor: '#222222',
                        snapToData: true
                    }
                }
            }
        });
        let chartWrapper = new google.visualization.ChartWrapper({
            chartType: 'ComboChart',
            containerId: chartElem.id,
            options: {
                title: config.stockSymbol + ' - Monthly Share Prices', textStyle: { color: '#FFFFFF' },
                hAxis: { title: 'Date', /*ticks: hAxisTicks,*/ titleTextStyle: { color: '#FFFFFF' }, textStyle: { color: '#FFFFFF' } },
                vAxis: { title: 'Price Per Share', titleTextStyle: { color: '#FFFFFF' }, textStyle: { color: '#FFFFFF' } },
                seriesType: 'bars',
                series: { 2: { type: 'line' } },
                backgroundColor: '#222222',
                titleTextStyle: { color: '#FFFFFF' },
                legend: { textStyle: { color: '#FFFFFF' } },
            }
        });
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
