<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>
<head>
    <link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
    <link href="../cssfiles/stocksFollowedStyle.css" rel="stylesheet">
    <title>VSS - Transaction History</title>
</head>
<body>
<div class = "bg-img"></div>

<div class = "navigation">
    <t:navbar/>
</div>

<div class ="followed-heading">
    <h1>FOLLOWED STOCKS</h1>
    <h3>THE STOCKS YOU FOLLOW, AT A GLANCE</h3>
</div>


<div class="parent">
    <c:forEach var="followItem" items="${model.stocksFollowed}">
        <f:stockTemplate stock="${followItem.stock}"/>
    </c:forEach>
</div>

</body>
</html>
