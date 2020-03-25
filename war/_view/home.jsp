<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <style><%@include file="cssfiles/homePageStyle.css"%></style>
    <link href='https://fonts.googleapis.com/css?family=Staatliches' rel='stylesheet'>


    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>

<div class = "header">
    <t:header/>
</div>


<div class = "createAccountBtn">
    <button onClick="redirectAccount()">CREATE AN ACCOUNT</button>
    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
    </script>
</div>

<div class = "navbar">
    <t:navbar/>
</div>


<div class="leaderboard">
    <h1>Leading Investors</h1>
    <p>1) Jeff Bezos - $98091239.78 </p>
    <p>2) Bill Gates - $3123233.23 </p>
</div>

<div class="top-stocks">
    <h1>Top Stocks</h1>
    <p>1) TSLA - $620.91 - UP 11%</p>
    <p>2) Google - $1214.27 - UP 9%</p>
    <p>3) Amazon - $1785.00 - UP 7%</p>
</div>

</body>
</html>
