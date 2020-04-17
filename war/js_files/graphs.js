/**
 * Generates one or more pricing history graphs
 * @param {GraphConfig[]} configs Array of configuration objects for each graph
 */
function genPriceHistoryGraph(configs)
{

    /**
     * Creates a single graph
     * @param {GraphConfig} config
     * @private
     */
    function _createGraph(config)
    {
        getStockData([config.stockSymbol], (stockData) =>
        {
            let priceHistory = stockData[0].history;

            // Headers for graph
            let data = [['Date', 'High', 'Low', 'Adj Close']];
            // Fill data array with price history
            for(let period of priceHistory)
            {
                // Should this period be included in graph
                let include = true;

                let date = Date.parse(period.date);

                // Check that date is between the given range (if any)
                if(config.minDate !== null && config.maxDate !== null)
                {
                    include = date >= config.minDate && date <= config.maxDate;
                }
                else if(config.minDate !== null)
                {
                    include = date >= config.minDate;
                }
                else if(config.maxDate !== null)
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
                        title: config.stockSymbol + ' - Monthly Share Prices',
                        hAxis: {title: 'Date', titleTextStyle: {color: '#333'}},
                        vAxis: {title: 'Price Per Share'},
                        seriesType: 'bars',
                        series: {2: {type: 'line'}}
                    };

            // Create and draw the chart
            let chart = new google.visualization.ComboChart(config.elem);
            let dataTable = google.visualization.arrayToDataTable(data);
            chart.draw(dataTable, options);
        });
    }

    /**
     * Create all graphs
     * @private
     */
    function _loopGraphs()
    {
        for(let config of configs)
        {
            _createGraph(config);
        }
    }

    // Setup google charts
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(_loopGraphs);
}