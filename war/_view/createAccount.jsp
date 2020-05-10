<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
        <title>Create an Account - Virtual Stock Sim</title>

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
                <div class="alert alert-danger alert-dismissible" id="error-message" style="width:50%;margin-left: 25%;">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        <strong>Whoops! </strong>${errorMessage}</div>
        </c:if>

        <div class = "sign-up">
                <form action="${pageContext.servletContext.contextPath}/createAccount" method="post">
                        <input type="email" id="email" placeholder=" Email" name="email" value=${CreateAccountModel.email}><br><br>

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