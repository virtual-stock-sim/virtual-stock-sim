<!doctype html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>


    <script src="/js_files/redirect.js"></script>
    <script src="js_files/tabscript.js"></script>


    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />
    <link href="../cssfiles/profilePageStyle.css" rel="stylesheet">

    <title>My Account - VSS: Virtual Stock Sim</title>
</head>
<body>

<t:navbar account="${account}"/>

<div class="bg-img"></div>

    <div class="built-by">
        <h1>MY ACCOUNT</h1>
        <h2>BUILT BY YOU, FROM THE GROUND UP.</h2>
    </div>

    <c:forEach var="errorMsg" items="${errorMsgs}">
        <c:if test="${! empty errorMsg}">
            <div class="alert alert-danger alert-dismissible" style="width:50%;margin-left: 25%;">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                <strong>Whoops! </strong>${errorMsg}</div>
        </c:if>
    </c:forEach>

    <c:if test="${bioUpdateSuccess==true}">
        <div class="alert alert-success alert-dismissible" id="bio-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your bio was successfully updated.
        </div>
    </c:if>

    <c:if test="${pictureUpdateSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="profile-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your profile was successfully updated.
        </div>
    </c:if>

    <c:if test="${credentialUpdateSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="credential-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your login credentials were successfully updated.
        </div>
    </c:if>

    <c:if test="${resetTransHistSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="reset-transaction-history-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your Transaction History was successfully cleared.
        </div>
    </c:if>

    <c:if test="${resetFollowedSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="reset-followed-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your followed stocks were successfully reset.
        </div>
    </c:if>

    <c:if test="${optInSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="opt-in-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> Your have opted into the leaderboard. Check the home page!
        </div>
    </c:if>

    <c:if test="${optOutSuccess==true}">
        <div class="alert alert-success alert-dismissible" id ="opt-out-success" style="width:50%; margin-left:25%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> You have opted out of the leaderboard.
        </div>
    </c:if>


    <div id="tab-cont">
        <div id="menu-fixed">
            <a href="#tab-cont">
                <i class="material-icons back"></i>
            </a>
            <a href="#menu-fixed">
                <div class="logo">
                    <span></span>
                    <p>V S S</p>
                </div>
                <p class="pmenu">ACCOUNT</p>
            </a>
            <hr>
            <ul class="menu">
                <li onclick="openTab('creds')"><i class="material-icons fa fa-lock"></i><p id="tab-creds">Change Login</p></li>
                <li onclick="openTab('bio')"><i class="material-icons fa fa-commenting-o"></i><p id="tab-bio">Edit Bio</p></li>
                <li onclick="openTab('picture')"><i class="material-icons fa fa-user"></i><p id="tab-picture" >Profile Picture</p></li>
                <li onclick="openTab('settings')"><i class="material-icons fa fa-cog"></i><p id="tab-settings">Settings</p></li>
                <li onclick="logout()"><i class="material-icons fa fa-sign-out"></i><p>Sign out</p></li>
            </ul>
        </div>
    </div>


    <div id ="contents">
        <div id ="page-bio" class="page">
            <h2>Edit your bio</h2>
            <form action=${pageContext.servletContext.contextPath}/profile class="bio-form" method="post" id="bio-form">
            <textarea class="form-control" form="bio-form" rows="5" cols="100" placeholder="Tell us about yourself" id="bio" name="bio">${account.bio}</textarea><br>
                <input type="submit" value="Save">
            </form>
        </div>

        <div id ="page-picture" class="page">
            <h2>Update Your Profile Picture</h2>
            <form action=${pageContext.servletContext.contextPath}/profile class="form-group" method="post" enctype="multipart/form-data">
                <input type="file" name="file" />
                <input type="submit" value="Upload"/>
            </form>
        </div>

        <div id="page-creds" class="page">
            <h2>Update your Login Credentials</h2>

            <!--Login Credentials form -->
            <form action= ${pageContext.servletContext.contextPath}/profile class="form-group" method="post" id = "creds-form">
                    <label for="new-email">New Email:</label>
                    <input type="email" id="new-email" name="new-email" class="form-control" placeholder="Enter a new email:"><br>
                    <label for="username">Username:</label>
                    <input type="text" id="username" name ="username" class="form-control" placeholder="${account.username}"><br>
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Enter a new password"><br>
                    <label for="confirmPassword">Confirm Password:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Confirm password"><br>
                    <input type="submit" value="Save">
            </form>
        </div>

        <!--Clear transaction history or followed stocks-->
        <div id="page-settings" class="page">
            <h2>Update your Settings</h2>
            <form action="${pageContext.servletContext.contextPath}/profile" class="form-group" method="post" id="settings-form">
                <input type="checkbox" id="reset-followed" name="reset-followed" value="followed">
                <label for="reset-followed">Reset Followed Stocks</label><br>
                <input type="checkbox" id="reset-transaction-history" name="reset-transaction-history" value="transaction-history">
                <label for="reset-transaction-history">Reset Transaction History</label><br>
                <input type="submit" value="Submit">
            </form>

                <h3>Opt in or out to our site's investor leaderboard</h3>
                <h5>Others will be able to see where you stand</h5>
                <h5>(You are opted out by default)</h5>
                <form action="${pageContext.servletContext.contextPath}/profile" class="form-group" method="post" id="opt-in-form">
                    <input type="radio" id="opt-in" name="leaderboard-opt-in" value="in">
                    <label for="opt-in">Opt in</label><br>
                    <input type="radio" id="opt-out" name="leaderboard-opt-in" value="out">
                    <label for="opt-out">Opt out</label><br>
                    <input type="submit" value="Submit">
                </form>
        </div>
    </div>


</body>
</html>