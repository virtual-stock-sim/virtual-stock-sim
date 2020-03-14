<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
        <title>Create an Account - Virtual Stock Sim</title>
        <style><%@include file="cssfiles/accountPageStyle.css"%></style>

</head>

<body>
        <h1>Create an Account</h1>
        <h2>Sign up to start investing with us.</h2>

        <div class = "sign-up">
                <form>
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

        <div class ="header">
                <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
                <h2>Simulated Investing, Reimagined</h2>
                <button onClick="redirectHome()">Back to Login</button>
                <script>
                        function redirectHome() {
                                location.href = "login";
                        }
                </script>
        </div>
</body>

</html>