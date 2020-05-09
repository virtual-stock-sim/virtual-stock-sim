<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%-- TODO: This is just for debug, session should actually be true --%>
<%@ page session="false" %>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Compare Stocks - VSS: Virtual Stock Sim</title>

        <!-- Google Chart API -->
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="module" src="../js_files/generated/compare.js"></script>

        <!--     Fonts and icons     -->
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

        <link href="../cssfiles/compareStyle.css" rel="stylesheet">
    </head>

    <body>

    <t:navbar account="${account}"/>

    <div class = "bg-img"></div>

    <div class="heading">
        <h1>SEARCH & COMPARE STOCKS</h1>
        <h3>FRESH DATA AT YOUR FINGERTIPS</h3>
    </div>
        <label>
            <input type="text" id="search-input" data-type="data" placeholder="Search for a symbol...">
        </label>
        <p id="error-text"></p>
    </body>
</html>