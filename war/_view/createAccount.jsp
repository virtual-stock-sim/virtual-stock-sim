<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
        <title>Create an Account - Virtual Stock Sim</title>
        <style><%@include file="../cssfiles/accountPageStyle.css"%></style>
        <script src="/js_files/redirect.js"></script>
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
                        <input type="text" id="email" placeholder=" Email" name="email" value=${CreateAccountModel.email}><br><br>

                        <input type="text" id="uname" placeholder=" Username" name="uname" value=${CreateAccountModel.username}><br><br>

                        <input type="password" id="pword" placeholder=" Password " name="pword"><br><br>

                        <input type="password" id="pwordconfirm" placeholder=" Confirm Password" name="pwordconfirm"><br><br><br>

                        <input type="submit" value ="SIGN UP">
                </form>
        </div>

        <div class ="return-to-home">
                <button onClick="redirectHome()">HOME</button>

        </div>
</body>

</html>