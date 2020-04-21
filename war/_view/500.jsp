<!doctype html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="../cssfiles/500PageStyle.css" rel="stylesheet">
    <script>
        function redirectHome() {
            location.href = "home";
        }
    </script>
    <title>500 - Server Error</title>

</head>

<!-- Idea for this page from user Sheng on CodePen =>  codepen.io/shengslogar-->
<body>
    <div class="vss-500">
        <div class="vss-500-text">
            <span>5</span>
            <span>0</span>
            <span>0</span><br>
        </div>
        <div class="vss-500-terminal">
            <div>$ server debug</div>
            <div>Oh no! Our server did a whoopsie.</div>
            <div>We're working on fixing it as fast as we can!</div>
            <div>Try again in a moment.</div>
            <div>process exited with code -1</div>
        </div>
    </div>


    <button onClick="redirectHome()">Return Home</button>
</body>
</html>