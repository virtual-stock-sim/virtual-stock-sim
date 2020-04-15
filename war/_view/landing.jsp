<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html>
<head>
    <title>VSS: Virtual Stock Sim</title>
    <link rel="stylesheet" href="../cssfiles/landingPageStyle.css">
    <div class = "header">
          <h1>Virtual Stock Sim: Deadly Accurate Investments</h1>
          <h2>Simulated Investing, Reimagined</h2>
  </div>


</head>
<body>
<div class = "bg-img"></div>

<div class = "invest">
    <h1>Invest in something you can track</h1>

</div>

<div class = "buttons">
    <h2>All your investments, in one spot</h2>
    <button onClick="redirectAccount()" style=" font-familyamily:Segoe UI,Arial,sans-serif;
    font-weight:400;letter-spacing: 4px;">JOIN THE TEAM</button>
    <button onClick="redirectLogin()"style=" font-familyamily:Segoe UI,Arial,sans-serif;
    font-weight:400;letter-spacing: 4px;">LOGIN</button>

    <script>
        function redirectAccount() {
            location.href = "createAccount";
        }
        function redirectLogin() {
            location.href = "login";
        }
    </script>
</div>

</body>
</html>
