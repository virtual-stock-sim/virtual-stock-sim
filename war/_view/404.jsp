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
    <link href="../cssfiles/404PageStyle.css" rel="stylesheet">
    <title>404 - Page Not Found</title>

</head>
<body>
<div class="page">
    <div class="head">
        <h1>404 Error</h1>
    </div>
    <h2>Oh no! Something went wrong on one of our ends...</h2>
    <h3>Our pages aren't as accurate as our investments</h3>
<div class="back">
    <button onClick="javascript:history.go(-1)" >Go Back</button>
</div>

</div>

</body>
</html>