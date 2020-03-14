<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <style><%@include file="cssfiles/homePageStyle.css"%></style>

    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>
<div class ="header">
    <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
    <h2>Simulated Investing, Reimagined</h2>
</div>

<div class = "createAccountBtn">
    <button onClick="redirectAccount()">Create an Account</button>
    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
    </script>
</div>

<div class = "navbar">
    <a href="home">Home</a>
    <div class = "dropdown">
        <button class = "dropBtn">Stocks
            <i class="arrow down"></i>
        </button>
        <div class = "dropdown-content">
            <a href="following">View</a>
            <a href="compare">Compare</a>
        </div>
    </div>
    <a>Portfolio</a>
    <a href="transactionhistory">Transaction History</a>
    <a>Account</a>
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
