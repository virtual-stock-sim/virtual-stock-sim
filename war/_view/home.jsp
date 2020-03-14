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

<div class = "about">
    <h1>About Us</h1>
    <p>Virtual Stock Sim is a stock investment simulation website that uses real-world stock data and prices
    for users' personal investments in order to provide the most authentic experience possible.</p>

    <h2>The Team</h2>
    <u1>
        <li class="dev-name" type="none">Brett Kearney</li>
        <li class="dev-name" type="none">Dan Palmieri</li>
        <li class="dev-name" type="none">Earl Kennedy</li>
    </u1>
</div>

</body>
</html>
