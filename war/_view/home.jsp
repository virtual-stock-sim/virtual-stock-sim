<%@ page import="io.github.virtualstocksim.account.Account" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>


    <script src="/js_files/redirect.js"></script>
    <script src="/js_files/general.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">

    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <link rel="stylesheet" href="/cssfiles/homePageStyle.css">

    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>

<t:navbar account="${account}"/>

<div class="wrapper">
    <div class="page-header section-dark" style="background-image: url('http://demos.creative-tim.com/paper-kit-2/assets/img/antoine-barres.jpg'); filter: grayscale(95%);">
        <div class="filter"></div>


        <div class="content-center">
            <div class= "leaderboard-content" id="leaderboard-title">
                <h1 class="presentation-title">Leaderboard</h1>
                <c:forEach var="userName" items="${leaderboardModel.currentRanks}">
                    <p>${userName}</p>
                </c:forEach>
            </div>


            <div class= "top-stocks-content" id="top-stocks-title">
                <h1 class="presentation-title">Top Stocks</h1>
                <c:forEach var="symbol" items="${topStocksModel.topFiveStocks}">
                    <p>${symbol.key} up ${symbol.value}% today</p>
                </c:forEach>
            </div>

            <div class="container">
                <div class="title-brand">
                    <h1 class="presentation-title">VIRTUAL STOCK SIM</h1>
                   <%-- <div class="fog-low">
                        <img src="http://demos.creative-tim.com/paper-kit-2/assets/img/fog-low.png" alt="">
                    </div>
                    <div class="fog-low right">
                        <img src="http://demos.creative-tim.com/paper-kit-2/assets/img/fog-low.png" alt="">
                    </div>--%>
                </div>

                <h2 class="presentation-subtitle text-center">Simulated Investing, Reimagined</h2>
            </div>
        </div>
        <div class="moving-clouds" style="background-image: url('http://demos.creative-tim.com/paper-kit-2/assets/img/clouds.png'); "></div>




    </div>
</div>

<%--<div class="leaderboard">
    <h1>Leading Investors</h1>
    <c:forEach var="userName" items="${leaderboardModel.currentRanks}">
        <p>${userName}</p>
    </c:forEach>
</div>

<div class="top-stocks">
    <h1>Top Stocks</h1>
   <c:forEach var="symbol" items="${topStocksModel.topFiveStocks}">
       <p>${symbol.key} up ${symbol.value}% today</p>
   </c:forEach>
</div>--%>

</body>
</html>
