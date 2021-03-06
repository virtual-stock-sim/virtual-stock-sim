<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>
<head>
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
    <link href="../cssfiles/stocksFollowedStyle.css" rel="stylesheet">

    <title>Stocks Followed - VSS: Virtual Stock Sim</title>
</head>
<body>

<t:navbar account="${account}"/>

<div class = "bg-img"></div>


<c:if test="${! empty buySuccessMsg}">
    <div class="alert alert-success alert-dismissible" id="buy-success" style="width:50%; margin-left: 25%;">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <strong>Success! </strong>${buySuccessMsg}</div>
</c:if>
<c:if test="${! empty sellSuccessMsg}">
    <div class="alert alert-success alert-dismissible" id="sell-success" style="width:50%;margin-left: 25%;">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <strong>Success! </strong>${sellSuccessMsg}</div>
</c:if>

<c:if test="${! empty stockUnfollowSuccess}">
    <div class="alert alert-success alert-dismissible" id="unfollow-success" style="width:50%;margin-left: 25%;">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <strong>Success! </strong>${stockUnfollowSuccess}</div>
</c:if>

<c:if test="${! empty errorMsg}">
    <div class="alert alert-danger alert-dismissible" id="error-message" style="width:50%;margin-left: 25%;">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <strong>Whoops! </strong>${errorMsg}</div>
</c:if>


    <div id="followed-stocks">
        <div class ="heading">
            <h1>FOLLOWED STOCKS</h1>
            <h3>THE STOCKS YOU FOLLOW, AT A GLANCE</h3>
        </div>
        <%--@elvariable id="followedModel" type="io.github.virtualstocksim.following.FollowedStocks"--%>
        <c:forEach var="followItem" items="${followedModel.followedStocks.values()}">
            <t:stockTemplate stock="${followItem.stock}" followItem="${followItem}"/>
        </c:forEach>
    </div>

    <div id="invested-stocks">
        <div class ="heading">
            <h1>INVESTED STOCKS</h1>
            <h3>WHAT'S MAKING YOU MONEY</h3>
        </div>
        <c:forEach var="investedItem" items="${investModel.investments}">
            <t:stockTemplate stock="${investedItem.stock}" investItem="${investedItem}"/>
        </c:forEach>
    </div>


</body>
</html>
