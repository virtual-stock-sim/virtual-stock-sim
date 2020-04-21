<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
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


<t:header/>

<% HttpSession session = request.getSession(false);
if(session!=null){ %>
<t:navbar loggedIn="true"/>

<%} else { %>
<t:navbar loggedIn="false"/>
<% } %>



<div class="leaderboard">
    <h1>Leading Investors</h1>
    <p>1) Jeff Bezos - $98091239.78 </p>
    <p>2) Bill Gates - $3123233.23 </p>
</div>

<div class="top-stocks">
    <h1>Top Stocks</h1>
    <p>1) TSLA - $620.91 - UP 11%</p>
    <p>2) Google - $1214.27 - UP 9%</p>
    <p>3) Amazon - $1785.00 - UP 7%</p>
</div>

</body>
</html>
