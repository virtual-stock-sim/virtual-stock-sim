<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- TODO: This is just for debug, session should actually be true --%>
<%@ page session="false" %>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Compare Stocks</title>

        <!-- Google Chart API -->
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

        <script type="text/javascript" src="../js_files/datastream.js"></script>
        <script type="text/javascript" src="../js_files/storage.js"></script>
        <script type="text/javascript" src="../js_files/graphs.js"></script>

        <script type="text/javascript">

/*            let stream = new DataStream("stockStream");
            stream.setOnMessage(function(e)
            {
                let output = "";
                let data = JSON.parse(e.data);
                if(data.type === "stocks")
                {

                }
                else if(data.type === "stockDatas")
                {
                    let graphsElem = document.getElementById("graphs");
                    for(let stockStr of data.data)
                    {
                        let stockJson = JSON.parse(stockStr);
                        console.log(stockJson);
                        //storeStockData(stockJson);


                        graphsElem.innerHTML += "<div id=graph-" + stockJson.symbol + "></div>";
                        genPriceHistoryGraph("graph-" + stockJson.symbol, stockJson.symbol, new Date("2010-01-01"), new Date("2020-01-01"));
                        output += "<p>" + JSON.stringify(getStockData(stockJson.symbol)) + "</p>";
                    }

                    genPriceHistoryGraph("graph", "AMZN");
                }
                else
                {

                }
            });*/

/*            stream.sendMessage({msg: "data="+encodeURIComponent("hello!"), protocol: "POST", onReceived: (r) => {
                if(r.readyState === XMLHttpRequest.DONE && r.status === 200)
                {
                    console.log(r.responseText);
                }
            }});*/

/*            let requestObj = {type: "stockDatas", symbols: ["AMZN", "TSLA", "GOOGL"]};
            stream.setOnMessageReceived((e) =>
                                        {
                                            let data = JSON.parse(e.data);
                                            if(data !== null)
                                            {
                                                //console.log(data);
                                            }
                                        });
            stream.sendMessage(new Message("dataRequest=" + JSON.stringify(requestObj), "POST"));*/

            /*getStockData(["AMZN", "TSLA", "GOOGL", "F"], (arr) =>
            {
                for(let obj of arr)
                {
                    console.log(obj);
                }
            });*/

            let req = {type: "stockDatas", symbols: [""]};

            // Wait until window is done loading, otherwise the document elements won't exist yet
            window.addEventListener("load", () =>
            {
                /** @type {GraphConfig[]} */
                let configs = [
                        {elem: document.getElementById("amzn-graph"), stockSymbol: "AMZN", minDate: null, maxDate: null},
                        {elem: document.getElementById("tsla-graph"), stockSymbol: "TSLA", minDate: Date.parse("2016-01-05"), maxDate: Date.now()}
                        ];
                genPriceHistoryGraph(configs)
            });

        </script>
    </head>

    <body>
        <div id="graphs" onload=''></div>

        <div id="amzn-graph"></div>
        <div id="tsla-graph"></div>
    </body>
</html>