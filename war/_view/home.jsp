<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<script>
    function logout() {
        location.href = "landing";
    }

    function redirectProfile() {
        location.href="profile";
    }
</script>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link href='https://fonts.googleapis.com/css?family=Staatliches' rel='stylesheet'>


    <link rel="stylesheet" href="../cssfiles/homePageStyle.css">

    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>
<% HttpSession session = request.getSession(false);
    if(session != null) {
        // User is logged in, display menu dropdown
%>
<div class="profile-menu">
   <!--Profile menu dropdown, simple logout button for now-->
    <div class = "dropdown">
        <button class="btn dropdown-toggle" type="button" data-toggle="dropdown"><img class="img-thumbnail" style="width: 50px;" src="${picturepath}">
            <span class="caret"></span>
        </button>
        <u1 class="dropdown-menu">
            <li class="dropdown-header" style="font-size:large;">Hi, ${CreateAccountModel.username}</li>
            <li class="divider"></li>
            <li class="link"><a onclick="redirectProfile()">My Account<i class="fa fa-user" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
            <li><a>Wallet Balance: $${account.walletBalance}<i class="material-icons" style="font-size: 15px;border-style: hidden;padding: 4px;
                                                                                        position:relative;top:2px;">account_balance_wallet</i></a></li>

            <li class="link"><a onclick="logout()">Sign out<i class="fa fa-sign-out" style="border-style: hidden;display: inline-block;padding: 4px;"></i></a></li>
        </u1>
    </div>
</div>

<%
}
else  {
%>
<div class = "createAccountBtn">
    <button onClick="redirectLogin()">LOGIN</button>
    <script>
        function redirectLogin() {
            location.href = "login";
        }
    </script>
    <button onClick="redirectAccount()">CREATE AN ACCOUNT</button>
    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
    </script>
</div>

<%
    }
%>


<t:header/>
<t:navbar/>


<div class="leaderboard">
    <h1>Leading Investors</h1>
    <c:forEach var="userName" items="${model.currentRanks}">
        <p>${userName}</p>
    </c:forEach>
</div>

<div class="top-stocks">
    <h1>Top Stocks</h1>
    <p>1) TSLA - $620.91 - UP 11%</p>
    <p>2) Google - $1214.27 - UP 9%</p>
    <p>3) Amazon - $1785.00 - UP 7%</p>
</div>

</body>
</html>
