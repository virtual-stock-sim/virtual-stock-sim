<%@tag description="navigation bar" pageEncoding="UTF-8" %>
<style><%@include file="/_view/cssfiles/navbarStyle.css"%></style>
<link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">



<div class = "navbar">
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
    <div class = "home-btn">
        <a href="home"><i class="fa fa-home"></i></a>
    </div>


    <a href="transactionhistory">Transaction History</a>
    <div class = "account-btn">
        <a href="profile">Account</a>
    </div>
</div>