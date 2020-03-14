<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<form action="${pageContext.servletContext.contextPath}/following" method="get">
    <input type="Submit" name="submit" value="Followed Stocks">
</form>


<head>
    <title>VSS - Transaction History</title>
    <style><%@include file="cssfiles/transactionHistoryStyle.css"%></style>
</head>
<body>

    <h1>Transaction History</h1>
    <h3>A history of your stock purchases through VSS</h3>
    <div class = bodytext>

        <c:forEach var="transaction" items="${model.transactions}">
            <p>hello</p>
            <t:transactionTemplate transaction="${transaction}"></t:transactionTemplate>
        </c:forEach>
    </div>
</form>
</body>
</html>
