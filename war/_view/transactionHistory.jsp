<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>

<head>
    <link rel="stylesheet" href="../cssfiles/transactionHistoryStyle.css">

    <title>VSS - Transaction History</title>
</head>

<div class="bg-img"></div>


<t:navbar account="${account}"/>

<div class="header">
    <h1>TRANSACTION HISTORY</h1>
    <h3>Your recent transactions</h3>
</div>

<div >
    <h4><span class = subhead>Type</span><span class = subhead>Symbol</span> <span class = subhead>Shares</span> <span class = subhead> Date</span> <span class = subhead>Price/Share</span> <span class = subhead>Total</span></h4>
</div>

   <!-- <h3>A history of your stock purchases through VSS</h3> -->
    <div class = parent >
        <c:forEach var="transaction" items="${model.transactions}">
               <t:transactionTemplate transaction="${transaction}"></t:transactionTemplate>
        </c:forEach>
    </div>


</html>
