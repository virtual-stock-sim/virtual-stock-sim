<!doctype html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">

    <link href='https://fonts.googleapis.com/css?family=Staatliches' rel='stylesheet'>
    <style><%@include file="cssfiles/profilePageStyle.css"%></style>
    <script src="js_files/tabscript.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <title>My Account - VSS: Virtual Stock Sim</title>

    <div class="bg-img"></div>

</head>
<body>

    <t:navbar/>
    <div class="built-by">
        <h1>MY ACCOUNT</h1>
        <h2>BUILT BY YOU, FROM THE GROUND UP.</h2>
    </div>

    <div id="tab-cont">
        <div id="menu-fixed">
            <a href="#tab-cont">
                <i class="material-icons back"></i>
            </a>
            <a href="#menu-fixed">
                <div class="logo">
                    <span></span>
                    <p>V S S</p>
                </div>
                <p class="pmenu">ACCOUNT</p>
            </a>
            <hr>
            <ul class="menu">
                <li onclick="openTab('creds')"><i class="material-icons fa fa-lock"></i><p id="tab-creds">Change Login</p></li>
                <li onclick="openTab('bio')"><i class="material-icons fa fa-commenting-o"></i><p id="tab-bio">Edit Bio</p></li>
                <li onclick="openTab('picture')"><i class="material-icons fa fa-user"></i><p id="tab-picture" >Profile Picture</p></li>
                <li onclick="openTab('settings')"><i class="material-icons fa fa-cog"></i><p id="tab-settings">Settings</p></li>
            </ul>
        </div>
    </div>


    <div id ="contents">
        <div id ="page-bio" class="page">
            <h2>Edit your bio</h2>
            <form action=${pageContext.servletContext.contextPath}/profile class="bio-form" method="post" id="bio-form">
            <textarea class="form-control" form="bio-form" rows="5" cols="100" placeholder="Tell us about yourself" id="bio" name="bio">${bio}</textarea><br>
                <input type="submit" value="SAVE">
            </form>
        </div>

        <div id ="page-picture" class="page">
            <h2>Update Your Profile Picture</h2>
        </div>

        <div id="page-creds" class="page">
            <h2>Update your Login Credentials</h2>

            <c:if test="${! empty errorMsg}">
                <div class="error">${errorMsg}</div>
            </c:if>

            <form action= ${pageContext.servletContext.contextPath}/profile  class="form-group" method="post" id = "creds-form">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name ="username" class="form-control" placeholder="Enter a new username"><br>
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Enter a new password"><br>
                    <input type="submit" value="SAVE">
            </form>


        </div>

        <div id="page-settings" class="page">
            <h2>Update your Settings</h2>
        </div>
    </div>


</body>
</html>