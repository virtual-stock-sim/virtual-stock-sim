<%@tag description="navigation bar" pageEncoding="UTF-8" %>
<link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
<script src="/js_files/redirect.js"></script>
<link rel="stylesheet" href="../../cssfiles/generalCSS/generalStyle.css">



<nav class="navbar navbar-toggleable-md fixed-top navbar-brand" color-on-scroll="500" style="width:100%;">
    <div class="container">
        <div class="navbar-translate">
            <button class="navbar-toggler navbar-toggler-right navbar-burger" type="button" data-toggle="collapse" data-target="#navbarToggler" aria-controls="navbarTogglerDemo02" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
                <span class="navbar-toggler-bar"></span>
            </button>
            <a class="navbar-brand" data-placement="bottom" onclick="redirectHome()" rel="tooltip" title="Home">V S S</a>
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
                    <a class="nav-link" rel="tooltip" title="My Account" data-placement="bottom" onclick="redirectProfile()" target="_blank">
                        <i class="fa fa-user"></i>
                        <p class="hidden-lg-up">My Account</p>
                    </a>
                </li>


            </ul>
        </div>
    </div>
</nav>
