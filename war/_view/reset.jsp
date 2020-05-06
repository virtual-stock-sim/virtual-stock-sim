<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>

<style>
    h1 {text-align: center}
    p {text-align: center}
    form {text-align: center}
</style>


<title>VSS-Password Reset</title>
<h1>Forgot your password?</h1>
<form action="${pageContext.servletContext.contextPath}/reset?token=${salt}"  method="post">

    <%-- In this case the link parameter is NOT empty(there is an argument AND it is valid in the DB according to checks in ResetToken and ResetManager after the question mark ...../reset?token=XXX --%>
<c:if test="${not empty salt}">
    <p>Thank you for clicking on the reset link!</p>
    <p>Please input the new password </p>

    <p><div class="error">the error message is: ${model.message}</div></p>

    <c:if test="${! empty model.message}">
        <div class="error">${model.message1}</div>
    </c:if>

    <p>New password: </p>
    <input type="password" name="newPass1" required="true" size="12" />

    <p>Confirm new password: </p>
    <input type="password" name="newPass2" required="true" size="12" />
    <br/>
    <br/>
    <input type="Submit" name="submit" value="Reset my password">

</c:if>

<%-- In this case the link parameter is empty(there is nothing after the question mark ...../reset?token=XXX --%>
<c:if test = "${empty salt}">
    <p>Please provide the username or email for the account who's password you would like to reset</p>
    <p>If an account exists under that username or email, we will send an automated message to the email account associated with that username with instructions on how to reset your password</p>

    <c:if test="${! empty model.message}">
        <div class="error">${model.message}</div>
    </c:if>

    <input type="text" name="userInput" required="true" size="12" />
    <input type="Submit" name="submit" value="send reset email">


</c:if>

</form>


</html>