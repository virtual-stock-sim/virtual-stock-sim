<%@tag description="navigation bar" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="account" required="true" type="io.github.virtualstocksim.account.Account"%>

<link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<script>
    function logout() {
        location.href = "landing";
    }

    function redirectProfile() {
        location.href="profile";
    }
</script>


<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link href='https://fonts.googleapis.com/css?family=Staatliches' rel='stylesheet'>
<link rel="stylesheet" href="../../cssfiles/navbarStyle.css">




<div class = "navigationbar">
    <a href="about">About Us</a>
    <div class = "dropdown1">
        <button class = "dropBtn">Stocks
            <i class="arrow down"></i>
        </button>
        <div class = "dropdown1-content">
            <a href="following">Following</a>
            <a href="compare">Compare</a>
        </div>
    </div>
    <div class = "home-btn">
        <a href="home"><i class="fa fa-home"></i></a>
    </div>
    <a>Portfolio</a>
    <div class="transaction-history-btn">
        <a href="transactionhistory">Transaction History</a>
    </div>
    <c:choose>
        <c:when test="${not empty account}" >
            <div class="profile-menu">
                <!--Profile menu dropdown, simple logout button for now-->
                <div class = "dropdown">
                    <button class="btn dropdown-toggle" type="button" data-toggle="dropdown"><img class="img-thumbnail" style="width: 25px;height:25px;"
                            <% if(account.getProfilePicture().length() > 0) { %>
                            src=""
                            <% }else{ %>
                            src="../../_view/resources/images/home/question-mark.jpg"
                            <% } %>

                    >
                        <span class="caret"></span>
                    </button>
                    <u1 class="dropdown-menu">
                        <li class="dropdown-header" style="font-size:large;">Hi, ${account.username}</li>
                        <li class="divider"></li>
                        <li class="link"><a onclick="redirectProfile()">My Account<i class="fa fa-user" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                        <li><a>Wallet Balance: $${account.walletBalance}<i class="material-icons" style="font-size: 15px;border-style: hidden;padding: 4px;
                                                                                        position:relative;top:2px;">account_balance_wallet</i></a></li>

                        <li class="link"><a onclick="logout()">Sign out<i class="fa fa-sign-out" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                    </u1>
                </div>
            </div>

        </c:when>
        <c:otherwise>
            <div class="profile-menu">
                <!--Profile menu dropdown, simple logout button for now-->
                <div class = "dropdown">
                    <button class="btn dropdown-toggle" type="button" data-toggle="dropdown"><img class="img-thumbnail" style="width: 25px;height:25px;" src="../../_view/resources/images/home/question-mark.jpg">
                        <span class="caret"></span>
                    </button>
                    <u1 class="dropdown-menu">
                        <li class="dropdown-header" style="font-size:large;">Welcome Back</li>
                        <li class="divider"></li>
                        <li class="link"><a onclick="redirectLogin()">Login<i class="fa fa-user" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                        <li class="link"><a onclick="redirectAccount()">Create an Account <i class="fa fa-sign-out" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                    </u1>
                </div>
            </div>

        </c:otherwise>
    </c:choose>>
</div>