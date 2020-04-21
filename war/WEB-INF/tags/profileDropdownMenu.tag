<%@tag description="profile dropdowm menu" pageEncoding="UTF-8" %>
<%@attribute name="loggedIn" required="true" type="java.lang.Boolean"%>
<%@attribute name="picturepath" required="true" type="java.lang.String" %>
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link href='https://fonts.googleapis.com/css?family=Staatliches' rel='stylesheet'>

<div class="profile-menu">
    <!--Profile menu dropdown, simple logout button for now-->
    <div class = "dropdown">
        <button class="btn dropdown-toggle" type="button" data-toggle="dropdown"><img class="img-thumbnail" style="width: 25px;height:25px;" src="${picturepath}">
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
