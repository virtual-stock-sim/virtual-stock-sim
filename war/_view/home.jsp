<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <style><%@include file="cssfiles/homePageStyle.css"%></style>

    <title>Home - VSS: Virtual Stock Sim</title>
</head>
<body>
<div class ="header">
    <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
    <h2>Simulated Investing, Reimagined</h2>
</div>

<div class = "createAccountBtn">
    <button onClick="redirectAccount()">Create an Account</button>
    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
    </script>
</div>



</body>
</html>
