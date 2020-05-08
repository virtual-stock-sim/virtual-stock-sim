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

    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <link rel="stylesheet" href="../cssfiles/transactionHistoryStyle.css">
    <link rel="stylesheet" href ="../cssfiles/generalCSS/generalStyle.css">

    <title>VSS - Transaction History</title>
</head>

<t:navbar account="${account}"/>

<div class="bg-img"></div>

<div class="header">
    <h1>TRANSACTION HISTORY</h1>
    <h3>Your recent transactions</h3>
</div>

<div>
    <h4><span class = subhead>Type</span><span class = subhead>Symbol</span> <span class = subhead>Shares</span> <span class = subhead> Date</span> <span class = subhead>Price/Share</span> <span class = subhead>Total</span></h4>
</div>

   <!-- <h3>A history of your stock purchases through VSS</h3> -->
    <div class = parent >
        <c:forEach var="transaction" items="${model.transactions}">
               <t:transactionTemplate transaction="${transaction}"></t:transactionTemplate>
        </c:forEach>
    </div>


</html>
