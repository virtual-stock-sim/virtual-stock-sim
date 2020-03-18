<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <title>VSS - Transaction History</title>
</head>
<body>
<t:header/>
<t:navbar/>


    <style><%@include file="cssfiles/stocksFollowedStyle.css"%></style>
    <h1>Followed Stocks</h1>
    <h3>Gain/loss of investments and followed stocks</h3>
    <div class =bodytext>

    <c:forEach var="followItem" items="${model.following}">
        <ul>   <f:followingTemplate followItem="${followItem}"></f:followingTemplate> </ul>

    </c:forEach>
    </div>
</form>
</body>
</html>
