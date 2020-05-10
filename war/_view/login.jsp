<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>Login - VSS: Virtual Stock Sim</title>

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>


    <script src="/js_files/redirect.js"></script>

    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <link rel="stylesheet" href="/cssfiles/generalCSS/generalStyle.css">
    <link rel="stylesheet" href= "../cssfiles/loginPageStyle.css">

    </head>
    <body>

    <div class = "bg-img"></div>

    <div class ="heading">
        <h1>VSS: Virtual Stock Sim</h1>
        <h2>Simulated Investing, Reimagined</h2>
    </div>

    <div class="subheading">
         <h5>Experience Wall Street, from your computer</h5>
    </div>
    <c:if test="${! empty errorMessage}">
        <div class="alert alert-danger alert-dismissible" id="error-message" style="width:50%;margin-left: 25%;margin-bottom: 2%;">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Whoops! </strong>${errorMessage}</div>
    </c:if>
    <div class = "login-form">
        <form action="${pageContext.servletContext.contextPath}/login" method="post">
            <p class="login-text">
            <h6>WELCOME BACK</h6>
            </p>
            <input type="text"  class ="username" id="uname" name="uname" required="true" placeholder=" Username" value="${CreateAccountModel.username}" />
            <input type="password" class ="password" id="pword" name="pword"  required="true" placeholder=" Password" />
            <input type="submit" class ="submit" value ="Login"/>
            <button onClick="redirectAccount()">Join the Team</button>
        </form><br>
        <a class="login-text" href="/reset" style="color:white; font-size:14px;text-decoration: none">FORGOT PASSWORD?
        </a>
    </div>

</body>
</html>
