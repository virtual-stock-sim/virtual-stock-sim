<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head>
        <title>Create an Account - Virtual Stock Sim</title>
        <style><%@include file="cssfiles/accountPageStyle.css"%></style>
        <t:header/>
</head>

<body>
        <h1>Create an Account</h1>
        <h2>Sign up to start investing with us.</h2>

        <c:if test="${! empty errorMessage}">
                <div class="error">${errorMessage}</div>
        </c:if>

        <div class = "sign-up">
                <form action="${pageContext.servletContext.contextPath}/createAccount" method="post">>
                        <label for="email">E-mail:</label><br>
                        <input type="text" id="email" name="email"><br>

                        <label for="uname">Username:</label><br>
                        <input type="text" id="uname" name="uname"><br>

                        <label for="pword">Password:</label><br>
                        <input type="text" id="pword" name="pword"><br>

                        <label for="pwordconfirm">Confirm Password:</label><br>
                        <input type="text" id="pwordconfirm" name="pwordconfirm"><br><br>

                        <input type="submit" value ="Sign Up">
                </form>
        </div>

        <div class ="return-to-home">
                <button onClick="redirectHome()">Back to Login</button>
                <script>
                        function redirectHome() {
                                location.href = "login";
                        }
                </script>
        </div>
</body>

</html>