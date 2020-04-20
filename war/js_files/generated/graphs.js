import { getStockData } from "./stockstorage.js";
export function drawPriceHistoryGraph(configs) {
    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.setOnLoadCallback(() => { configs.forEach((c) => createGraph(c)); });
}
function createGraph(config) {
    getStockData([config.stockSymbol], (stockData) => {
        let history = stockData[0].history;
        // Headers for graph
        let data = [["Date", "High", "Low", "Adj Close"]];
        // Fill data array with price history
        for (let period of history) {
            // Should this period be included in graph
            let include = true;
            let date = Date.parse(period.date);
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
                data.push([
                    period.date,
                    parseFloat(period.high),
                    parseFloat(period.low),
                    parseFloat(period.adjclose)
                ]);
            }
        }
        // Graph visual options
        let options = {
            title: config.stockSymbol + ' - Monthly Share Prices', textStyle: { color: '#FFFFFF' },
            hAxis: { title: 'Date', titleTextStyle: { color: '#FFFFFF' }, textStyle: { color: '#FFFFFF' } },
            vAxis: { title: 'Price Per Share', titleTextStyle: { color: '#FFFFFF' }, textStyle: { color: '#FFFFFF' } },
            seriesType: 'bars',
            series: { 2: { type: 'line' } },
            backgroundColor: '#222222',
            titleTextStyle: { color: '#FFFFFF' },
            legend: { textStyle: { color: '#FFFFFF' } }
        };
        // Create and draw the chart
        // @ts-ignore
        let chart = new google.visualization.ComboChart(config.element);
        // @ts-ignore
        let dataTable = google.visualization.arrayToDataTable(data);
        chart.draw(dataTable, options);
    });
}
