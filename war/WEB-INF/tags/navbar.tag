<%@tag description="navigation bar" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="account" required="true" type="io.github.virtualstocksim.account.Account" %>

<!--Mandatory Scripts-->
<script type="module" src="../../js_files/generated/navbar.js"></script>




<nav class="navbar navbar-toggleable-md static-top " style="border-radius: 0px!important;">
    <div class="container">
        <div class="navbar-translate">
            <button class="navbar-toggler navbar-toggler-right navbar-burger" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarToggler" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
            </button>
            <a class="navbar-brand" data-placement="bottom" onclick="redirectHome()" rel="tooltip" style="cursor:pointer;"title="Home">V S S</a>
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
                    <a class="nav-link" rel="tooltip" title="Followed & Invested Stocks" data-placement="bottom" onClick="redirectFollowing()" target="_blank">
                        <i class="fa fa-line-chart"></i>
                        <p class="hidden-lg-up">Followed & Invested Stocks</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" rel="tooltip" title="Search & Compare Stocks" data-placement="bottom" onclick="redirectCompare()" target="_blank">
                        <i class="fa fa-exchange"></i>
                        <p class="hidden-lg-up">Search & Compare</p>
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
                        <button class="btn btn-neutral dropdown-toggle" type="button" data-toggle="dropdown" style="padding: 0px 0px 0px 0px; width: 70px; display: inline-block;">
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
                        <u1 class="dropdown-menu">
                            <li class="dropdown-header large" style="cursor: pointer;"> Hi, ${account.username}</li>
                            <li class="dropdown-item" style="cursor: pointer;"><a onclick="redirectProfile()">My Account<i class="fa fa-user" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                            <li class="dropdown-item" style="cursor: pointer;"><a>Wallet Balance: $${account.walletBalance}<i class="material-icons" style="font-size: 15px;border-style: hidden;padding: 4px;
                                                                                        position:relative;top:2px;">account_balance_wallet</i></a></li>

                            <li class="dropdown-item" style="cursor:pointer;"><a onclick="logout()">Sign out<i class="fa fa-sign-out" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
                        </u1>
                    </div>
                </c:otherwise>
                    </c:choose>

            </ul>
        </div>
    </div>
</nav>
