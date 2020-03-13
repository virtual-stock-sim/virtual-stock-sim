<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <title>VSS - Transaction History</title>
    <link rel="stylesheet" href="transactionHistoryCSS.css">
</head>
<body>
<form action="${pageContext.servletContext.contextPath}/transactionHistory" method="post">
    <h1>Transaction History</h1>
    <h3>A history of your stock purchases through VSS</h3>
    <div>
        ${model.firstStock}

    </div>
</form>
</body>
</html>
