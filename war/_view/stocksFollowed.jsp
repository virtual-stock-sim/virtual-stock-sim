<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<form action="${pageContext.servletContext.contextPath}/transactionhistory" method="get">
    <input type="Submit" name="submit" value="Transaction History">
</form>
<head>
    <title>VSS - Transaction History</title>
</head>
<body>
    <style><%@include file="cssfiles/stocksFollowedStyle.css"%></style>
    <h1>Followed Stocks</h1>
    <h3>Gain/loss of investments and followed stocks</h3>
    <div>
    ${model.followedStockHTML}
        <t:stockTemplate stock="${stock}"></t:stockTemplate>

    </div>
</form>
</body>
</html>
