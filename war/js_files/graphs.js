/**
 * Generates a pricing history graph
 * @param elementID Element to graph draw to
 * @param stockSymbol Symbol of the stock that graph is being generated for
 */
function genPriceHistoryGraph(elementID, stockSymbol)
{
    // Setup google charts
    google.charts.load('current', {'packages': ['corechart']});
    google.charts.setOnLoadCallback(
        function()
        {
            // Headers for graph
            let data = ['Date', 'High', 'Low', 'Adj Close'];

            // Fill data array with price history
            let priceHistory = JSON.parse(getItem(stockSymbol)).history;
            for(let period of priceHistory)
            {
                data.push
                (
                    period.date,
                    period.high,
                    period.low,
                    period.adjclose
                );
            }


            let options =
                {
                    title: 'Monthly Share Pricing',
                    hAxis: {title: 'Data', titleTextStyle: {color: '#333'}},
                    vAxis: {title: 'Price Per Share'},
                    seriesType: 'bars',
                    series: {2: {type: 'line'}}
                };

            let chart = new google.visualization.ComboChart(document.getElementById(elementID));
            chart.draw(data, options);
        }
    );
}