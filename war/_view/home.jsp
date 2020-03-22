<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <style><%@include file="cssfiles/homePageStyle.css"%></style>

    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>

<t:header/>

<div class = "createAccountBtn">
    <button onClick="redirectAccount()">Create an Account</button>
    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
    </script>
</div>

<t:navbar/>

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
