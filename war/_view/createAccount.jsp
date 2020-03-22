<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head>
        <title>Create an Account - Virtual Stock Sim</title>
        <style><%@include file="cssfiles/accountPageStyle.css"%></style>
</head>

<body>
        <div class = "create-an-account">
                <h1>Create an Account</h1>
                <h3>Sign up to start investing with us.</h3>
                <h6 style="font-size: 15px;">(And make a ton of money)</h6>
        </div>


        <c:if test="${! empty errorMessage}">
                <div class="error">${errorMessage}</div>
        </c:if>

        <div class = "sign-up">
                <form action="${pageContext.servletContext.contextPath}/createAccount" method="post">
                        <label for="email">E-MAIL:</label><br>
                        <input type="text" id="email" name="email"><br><br>

                        <label for="uname">USERNAME:</label><br>
                        <input type="text" id="uname" name="uname"><br><br>

                        <label for="pword">PASSWORD:</label><br>
                        <input type="password" id="pword" name="pword"><br><br>

                        <label for="pwordconfirm">CONFIRM PASSWORD:</label><br>
                        <input type="password" id="pwordconfirm" name="pwordconfirm"><br><br><br>

                        <input type="submit" value ="SIGN UP">
                </form>
        </div>

        <div class ="return-to-home">
                <button onClick="redirectHome()">HOME</button>
                <script>
                        function redirectHome() {
                                location.href = "landing";
                        }
                </script>
        </div>
</body>

</html>