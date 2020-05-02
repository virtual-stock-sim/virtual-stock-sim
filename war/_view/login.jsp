<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>Login - VSS: Virtual Stock Sim</title>
    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <link rel="stylesheet" href="/cssfiles/generalCSS/generalStyle.css">
    <link rel="stylesheet" href= "../cssfiles/loginPageStyle.css">
    <script src="/js_files/redirect.js"></script>

    </head>
    <body>

    <div class = "bg-img"></div>

    <div class ="heading">
        <h1>VSS: Virtual Stock Sim</h1>
        <h2>Simulated Investing, Reimagined</h2>
    </div>

    <h5>Experience Wall Street, from your computer</h5>

    <c:if test="${! empty errorMessage}">
        <div class="error">${errorMessage}</div>
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
