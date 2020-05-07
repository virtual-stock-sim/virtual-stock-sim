<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>VSS: Virtual Stock Sim</title>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

    <script src="/js_files/redirect.js"></script>
    <script src="/js_files/general.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
    <link rel="stylesheet" href="/cssfiles/generalCSS/generalStyle.css">
</head>
<body>
<nav class="navbar navbar-toggleable-md fixed-top navbar-transparent" color-on-scroll="500">
    <div class="container">
        <div class="navbar-translate">
            <button class="navbar-toggler navbar-toggler-right navbar-burger" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarTogglerDemo02" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
            </button>
            <a class="navbar-brand" style="cursor:pointer;" rel="tooltip" title="Home" onclick="redirectHome()">V S S</a>
        </div>
        <div class="collapse navbar-collapse" id="navbarToggler">
            <ul class="navbar-nav ml-auto">
                    <li class="nav-item">
                        <a class="nav-link" rel="tooltip" title="About VSS" data-placement="bottom" onclick="redirectAbout()" target="_blank">
                            <i class="fa fa-info-circle"></i>
                            <p class="hidden-lg-up">About Us</p>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" rel="tooltip" title="Followed Stocks" data-placement="bottom" onClick="redirectFollowing()" target="_blank">
                            <i class="fa fa-line-chart"></i>
                            <p class="hidden-lg-up">Following</p>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" rel="tooltip" title="Compare Stocks" data-placement="bottom" onclick="redirectCompare()" target="_blank">
                            <i class="fa fa-exchange"></i>
                            <p class="hidden-lg-up">Compare</p>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" rel="tooltip" title="Transaction History" data-placement="bottom" onclick="redirectTransHist()" target="_blank">
                            <i class="fa fa-list"></i>
                            <p class="hidden-lg-up">Transaction History</p>
                        </a>
                    </li>
                <li class="nav-item">
                    <a onclick="redirectLogin()" class="btn btn-neutral btn-round">Login</a>
                </li>
                <li class="nav-item">
                    <a onclick="redirectAccount()" class="btn btn-neutral btn-round">Create an Account</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="wrapper">
    <div class="page-header section-dark" style=" background-image: url('https://ak6.picdn.net/shutterstock/videos/16504666/thumb/1.jpg'); filter: grayscale(95%);">
        <div class="filter"></div>
        <div class="content-center">
            <div class="container">
                <div class="title-brand">
                    <h1 class="presentation-title">VIRTUAL STOCK SIM</h1>
                </div>

                <h2 class="presentation-subtitle text-center">Simulated Investing, Reimagined</h2>
            </div>
        </div>
        <div class="moving-clouds" style="background-image: url('http://demos.creative-tim.com/paper-kit-2/assets/img/clouds.png'); ">

        </div>
    </div>
</div>
</body>
</html>
