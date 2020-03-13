<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <style><%@include file="../_view/homePageStyle.css"%></style>

    <title>VSS: Virtual Stock Sim</title>
</head>
<body>
    <div class ="header">
        <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
        <h2>Simulated Investing, Reimagined</h2>
    </div>

    <div class = "login-credentials">
        <h2>Login</h2>
        <form>
            <label for="uname">Username:</label><br>
            <input type="text" id="uname" name="uname"><br>
            <label for="pword">Password:</label><br>
            <input type="text" id="pword" name="pword"><br><br>
            <input type="submit" value ="Enter">
        </form>
    </div>

    <div class = "create-account">
        <h1>Don't have an account?</h1>
        <h2>You can't invest without one. Let's fix that.</h2>
        <form action="${pageContext.servletContext.contextPath}/createAccount" method="get">
            <input type="Submit" value="Join the Team">
        </form>
    </div>


</body>
</html>
