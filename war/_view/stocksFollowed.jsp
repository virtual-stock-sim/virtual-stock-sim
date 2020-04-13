<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page session = "false" %>

<html>
<head>
    <style><%@include file="cssfiles/stocksFollowedStyle.css"%></style>
    <link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
    <title>VSS - Transaction History</title>
</head>
<body>
<div class = "bg-img"></div>

<div class ="navigation">
    <t:navbar/>
</div>

<div class="header">
    <h1>FOLLOWED STOCKS</h1>
    <h3>The stocks you follow, at a glance</h3>
</div>
<div >
    <h4><span class = "subhead">Stock</span><span class = "subhead">Symbol</span> <span class = "subhead">Share Price</span> <span class = "subhead">% Change Since Follow</span></h4>
</div>
    <!-- Following items
    <div class = parent>
    < c:forEach var="followItem" items="$ {model.stocksFollowed}">
        <ul>   < f:followingTemplate followItem="$ {followItem}"></ f:followingTemplate> </ul>
    < /c:forEach>
    </div> -->
<f:stockTemplate stock="${stockModel}"></f:stockTemplate>


</body>
</html>
