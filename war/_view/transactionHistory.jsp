<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>

<head>
    <title>VSS - Transaction History</title>
    <style><%@include file="cssfiles/transactionHistoryStyle.css"%></style>
</head>
<div class = navigation>
<t:header/>
<t:navbar/>
    <h1>Transaction History</h1>

</div>

<div >
    <h4> Type<span class = subhead>Symbol</span> <span class = subhead> Shares</span> <span class = subhead> Date</span> <span class = subhead> Price/Share</span> <span class = subhead>Total</span></h4>
</div>

   <!-- <h3>A history of your stock purchases through VSS</h3> -->
    <div class = parent >
        <c:forEach var="transaction" items="${model.transactions}">
               <t:transactionTemplate transaction="${transaction}"></t:transactionTemplate>
        </c:forEach>
    </div>
</form>

</html>
