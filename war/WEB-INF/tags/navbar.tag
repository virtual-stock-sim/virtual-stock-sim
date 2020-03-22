<%@tag description="navigation bar" pageEncoding="UTF-8" %>
<style><%@include file="/_view/cssfiles/navbarStyle.css"%></style>


<div class = "navbar">
    <a href="home">Home</a>
    <a href="about">About Us</a>
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