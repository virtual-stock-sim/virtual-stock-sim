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

<%--        <script type="module" src="../js_files/generated/compare.js"></script>--%>
<%--        <script type="text/javascript" src="../js_files/graphs.js"></script>--%>

        <script type="module" src="../js_files/generated/compare.js"></script>
        <script type="text/javascript">

/*            let req = {type: "stockDatas", symbols: [""]};

            /!** @type {GraphConfig} *!/
            let c = {elem: document.getElementById("tsla-graph"), stockSymbol: "TSLA", minDate: Date.parse("2016-01-05"), maxDate: Date.now()};*/

            // Wait until window is done loading, otherwise the document elements won't exist yet

/*            window.addEventListener("load", () =>
            {
                //import {drawPriceHistoryGraph} from "../js_files/generated/graphs.js";
                let configs = [
                        {elem: document.getElementById("amzn-graph"), stockSymbol: "AMZN", minDate: null, maxDate: null},
                        {elem: document.getElementById("tsla-graph"), stockSymbol: "TSLA", minDate: Date.parse("2016-01-05"), maxDate: Date.now()}
                        ];
                drawPriceHistoryGraph(configs);
            });*/

        </script>
    </head>

    <body>
        <div id="graphs" onload=''></div>

        <div id="amzn-graph"></div>
        <div id="tsla-graph"></div>
        <div id="f-graph"></div>
    </body>
</html>