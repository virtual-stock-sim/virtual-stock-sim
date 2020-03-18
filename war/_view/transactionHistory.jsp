<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>

<head>
    <title>VSS - Transaction History</title>
    <style><%@include file="cssfiles/transactionHistoryStyle.css"%></style>
</head>
<body>
<t:header/>
<t:navbar/>

    <h1>Transaction History</h1>
    <h3>A history of your stock purchases through VSS</h3>
    <div class = bodytext>
        <c:forEach var="transaction" items="${model.transactions}">
            <ul>   <t:transactionTemplate transaction="${transaction}"></t:transactionTemplate> </ul>
        </c:forEach>
    </div>
</form>
</body>
</html>
