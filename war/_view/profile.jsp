<!doctype html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html lang="en">
<head>
    <style><%@include file="cssfiles/profilePageStyle.css"%></style>
    <title>My Account - VSS: Virtual Stock Sim</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <div class="bg-img"></div>
    <script>
        function openTab(evt, tabName) {
            // Declare all variables
            var i, tabcontent, tablinks;

            // Get all elements with class="tabcontent" and hide them
            tabcontent = document.getElementsByClassName("tabcontent");
            for (i = 0; i < tabcontent.length; i++) {
                tabcontent[i].style.display = "none";
            }

            // Get all elements with class="tablinks" and remove the class "active"
            tablinks = document.getElementsByClassName("tablinks");
            for (i = 0; i < tablinks.length; i++) {
                tablinks[i].className = tablinks[i].className.replace(" active", "");
            }

            // Show the current tab, and add an "active" class to the button that opened the tab
            document.getElementById(cityName).style.display = "block";
            evt.currentTarget.className += " active";
        }
    </script>

</head>
<body>
    <t:header/>
    <t:navbar/>


    <div id="cont">
        <div id="menu-fixed">
            <a href="#cont">
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
                <li><i class="material-icons fa fa-lock"></i><a href="#user-update-creds">Change Login</a></li>
                <li><i class="material-icons fa fa-commenting-o"></i><a href="#user-bio">Bio</a></li>
                <li><i class="material-icons fa fa-user"></i><a href="#user-update-picture">Profile Picture</a></li>
                <li><i class="material-icons fa fa-cog"></i><a href="#user-settings">Settings</a></li>
            </ul>
        </div>
    </div>


    <div id ="contents">
        <div id ="user-bio">
            <h2>Edit your bio</h2>
        </div>

        <div id ="user-update-picture">
            <h2>Update Your Profile Picture</h2>
        </div>

        <div id="user-update-creds">
            <h2>Update your Login Credentials</h2>
        </div>

        <div id="user-settings">
            <h2>Update your Settings</h2>
        </div>
    </div>


</body>
</html>