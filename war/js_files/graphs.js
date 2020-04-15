/**
 * Generates a pricing history graph
 * @param {string} elementID ID of element to graph draw to
 * @param {string} stockSymbol Symbol of the stock that graph is being generated for
 * @param {Date} minDate Minimum date in range to be graphed. Null for no minimum.
 * @param {Date} maxDate Maximum date in range to be graphed. Null for no maximum
 */
function genPriceHistoryGraph(elementID, stockSymbol, minDate, maxDate)
{
    // Setup google charts
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(
        function()
        {
            // Headers for graph
            let data = [['Date', 'High', 'Low', 'Adj Close']];

            // Fill data array with price history
            let priceHistory = getStockData(stockSymbol).history;
            for(let period of priceHistory)
            {
                // Should this period be included in graph
                let include = true;

                let date = Date.parse(period.date);

                // Check that date is between the given range (if any)
                if(minDate !== null && maxDate !== null)
                {
                    include = date >= minDate && date <= maxDate;
                }
                else if(minDate !== null)
                {
                    include = date >= minDate;
                }
                else if(maxDate !== null)
                {
                    include = date <= maxDate;
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
                    title: stockSymbol + ' - Monthly Share Prices',
                    hAxis: {title: 'Date', titleTextStyle: {color: '#333'}},
                    vAxis: {title: 'Price Per Share'},
                    seriesType: 'bars',
                    series: {2: {type: 'line'}}
                };

            // Create and draw the chart
            let chart = new google.visualization.ComboChart(document.getElementById(elementID));
            let dataTable = google.visualization.arrayToDataTable(data);
            chart.draw(dataTable, options);
        }
    );
}