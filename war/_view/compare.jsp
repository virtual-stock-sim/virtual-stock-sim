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

        <!-- jQuery library -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

        <!-- Popper JS -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

        <!-- Latest compiled JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>


        <script src="/js_files/redirect.js"></script>

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">


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
            <input type="text" id="search-input" data-type="stock" placeholder="Search for a symbol...">
        </label>

    <form hidden id="add-stock-form" method="POST" action="${pageContext.servletContext.contextPath}/compare">
        <input hidden id="stocks-in-page" name="stocks-in-page" value="${stocksSearched}"/>
    </form>
        <p id="error-text"></p>


    <div class="invested-templates">
        <!--Invested stocks templates-->
        <c:forEach var="investedItem" items="${investedList}">
            <t:stockTemplate stock="${investedItem.stock}" investItem="${investedItem}"/>
        </c:forEach>
    </div>


    <div class = "followed-templates">
        <!--Followed stocks templates-->
        <c:forEach var="followItem" items="${followedList}">
            <t:stockTemplate stock="${followItem.stock}" followItem="${followItem}"/>
        </c:forEach>
    </div>

    <div class="not-following-or-invested-templates">
        <!--Not followed Or invested stocks templates-->
        <c:forEach var="stock" items="${notFollowedOrInvestedList}">
            <t:stockTemplate stock="${stock}"/>
        </c:forEach>
    </div>



    </body>
</html>