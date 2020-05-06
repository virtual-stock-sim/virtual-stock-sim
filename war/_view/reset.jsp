<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>VSS: Virtual Stock Sim - Password Reset</title>
</head>

<body>
<h1>Forgot your password?</h1>
<h3>We can help with that.</h3>
<form action="${pageContext.servletContext.contextPath}/reset?token=${salt}"  method="post">

    <%-- In this case the link parameter is NOT empty(there is an argument AND it is valid in the DB according to checks in ResetToken and ResetManager after the question mark ...../reset?token=XXX --%>
<c:if test="${not empty salt}">
    <p>Thank you for clicking on the reset link!</p>
    <p>Please input the new password </p>
    <label for="newPass1">New password: </label>
    <input type="password" name="newPass1" id="newPass1" required="true" size="12" />

    <label for="newPass2">Confirm password: </label>
    <input type="password" name="newPass2" id="newPass2" required="true" size="12" />
    <br>
    <br>
    <input type="Submit" name="submit" value="Reset my password">

</c:if>

<%-- In this case the link parameter is empty(there is nothing after the question mark ...../reset?token=XXX --%>
<c:if test = "${empty salt}">
    <p>Please provide the username or email for the account who's password you would like to reset</p>
    <p>If an account exists under that username or email, we will send an automated message to the email account associated with that username with instructions on how to reset your password</p>
    <input type="text" name="userInput" required="true" size="12" />
    <input type="Submit" name="submit" value="Send Reset Email">
    <c:if test="${match}">
        <p>THE PASSWORDS DO NOT MATCH</p>
    </c:if>

</c:if>

</form>

</body>
</html>