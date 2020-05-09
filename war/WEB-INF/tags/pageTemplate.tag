<%@ tag description="Overall page tempalte" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ attribute name="header" fragment="true" required="false" %>
<%@ attribute name="account" fragment="false" required="false" type="io.github.virtualstocksim.account.Account" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

    <script src="${pageContext.request.contextPath}/js_files/general.js"></script>
    <script src="${pageContext.request.contextPath}/js_files/redirect.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/cssfiles/generalCSS/generalStyle.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />

    <!--     Fonts and icons     -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />


    <jsp:invoke fragment="header"/>
</head>
<body>

    <nav class="navbar navbar-toggleable-md static-top navbar-transparent" style="border-radius: 0!important;">
        <div class="container">
            <div class="navbar-translate">
                <button class="navbar-toggler navbar-toggler-right navbar-burger" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarToggler" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-bar"></span>
                    <span class="navbar-toggler-bar"></span>
                    <span class="navbar-toggler-bar"></span>
                </button>
                <a class="navbar-brand" data-placement="bottom" onclick="redirectHome()" rel="tooltip" style="cursor:pointer;" title="Home">V S S</a>
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
                    <c:choose>
                        <c:when test= "${empty account}">
                            <li class="nav-item">
                                <a onclick="redirectLogin()" class="btn btn-neutral btn-round">Login</a>
                            </li>
                            <li class="nav-item">
                                <a onclick="redirectAccount()" class="btn btn-neutral btn-round">Create an Account</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <!--Profile menu dropdown, simple logout button for now-->
                            <div class = "dropdown">
                                <button class="btn btn-neutral dropdown-toggle" type="button" data-toggle="dropdown" style="padding: 0 0 0 0; width: 70px; display: inline-block;">
                                    <c:choose>
                                        <c:when test="${account.profilePicture.length() == 0}">
                                            <img class="img-rounded" style="max-height: 40px; max-width: 40px;" alt="avatar"  src="../../_view/resources/images/home/question-mark.jpg">
                                        </c:when>
                                        <c:otherwise>
                                            <img class="img-rounded" style="max-height: 40px; max-width: 40px;" alt="avatar" src="${account.profilePictureWithDir}">
                                        </c:otherwise>
                                    </c:choose>
                                    <!--End IMG Tag-->

                                    <span class="caret" style="color:white;"></span></button>
                                <ul class="dropdown-menu">
                                    <li class="dropdown-header large" style="cursor: pointer;"> Hi, ${account.username}</li>
                                    <li class="dropdown-item" style="cursor: pointer;"><a onclick="redirectProfile()">My Account<i class="fa fa-user" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                                    <li class="dropdown-item" style="cursor: pointer;"><a>Wallet Balance: $${account.walletBalance}<i class="material-icons" style="font-size: 15px;border-style: hidden;padding: 4px;
                                                                                            position:relative;top:2px;">account_balance_wallet</i></a></li>

                                    <li class="dropdown-item" style="cursor:pointer;"><a onclick="logout()">Sign out<i class="fa fa-sign-out" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                                </ul>
                            </div>
                        </c:otherwise>
                    </c:choose>

                </ul>
            </div>
        </div>
    </nav>

    <jsp:doBody/>

</body>
</html>
