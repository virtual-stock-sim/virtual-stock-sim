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
    <script src="/js_files/general.js"></script>


    <link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
    <link href="../cssfiles/stocksFollowedStyle.css" rel="stylesheet">

    <title>VSS - Stocks Followed</title>
</head>
<body>
<div class = "bg-img"></div>

<t:navbar account="${account}"/>


<div class ="followed-heading">
    <h1>FOLLOWED STOCKS</h1>
    <h3>THE STOCKS YOU FOLLOW, AT A GLANCE</h3>
</div>
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


<div class="parent">
    <c:forEach var="followItem" items="${model.stocksFollowed}">
        <t:stockTemplate stock="${followItem.stock}"/>
    </c:forEach>
</div>
</body>
</html>
