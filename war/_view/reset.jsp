<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>
<style>h1 {text-align: center}
p {text-align: center}
form {text-align: center}
</style>


<title>VSS-Password Reset</title>
<h1>Forgot your password?</h1>
<p>Please provide the username or email for the account who's password you would like to reset</p>
<p>If an account exists under that username or email, we will send an automated message to the email account assoicated with that username with instructions on how to reset your password</p>

<form action="${pageContext.servletContext.contextPath}/reset"  method="post">
   <input type="text" name="userInput" size="12" />
    <input type="Submit" name="submit" value="send reset email">
</form>


</html>