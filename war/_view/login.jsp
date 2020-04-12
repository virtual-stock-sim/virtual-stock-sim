<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>Login - VSS: Virtual Stock Sim</title>
    <link href='https://fonts.googleapis.com/css?family=Bebas Neue' rel='stylesheet'>
    <link href='//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css' rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style><%@include file="cssfiles/loginPageStyle.css"%></style>

    </head>
    <body>

    <div class = "bg-img"></div>

    <div class ="header">
        <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
        <h2>SIMULATED INVESTING, REIMAGINED</h2>
    </div>

    <h5>Be here, from your computer.</h5>

    <c:if test="${! empty errorMessage}">
        <div class="error">${errorMessage}</div>
    </c:if>
    <div class = "login-credentials">
        <form action="${pageContext.servletContext.contextPath}/login" class="login-form" method="post">
            <p class="login-text">
            <h6>WELCOME BACK</h6>
            </p>
            <input type="text"  class ="username" id="uname" name="uname" required="true" placeholder=" Username" value="${CreateAccountModel.username}" />
            <input type="password" class ="password" id="pword" name="pword"  required="true" placeholder=" Password" />
            <input type="submit" class ="submit" value ="Login"/>
            <button onClick="redirectAccount()">Join the Team</button>
            <script>
                function redirectAccount() {
                    location.href = "createAccount";
                }
            </script>
        </form>
    </div>

</body>
</html>
