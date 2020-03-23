<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <style><%@include file="cssfiles/aboutPageStyle.css"%></style>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script type="text/javascript" src="https://platform.linkedin.com/badges/js/profile.js" async defer></script>
    <title>About - VSS: Virtual Stock Sim</title>
</head>
<body>

<div class = container style="padding:10px 16px" id ="about">
    <h3 class = "center">ABOUT VSS</h3>
    <p class ="center">What we bring to the table</p>
    <div class = "row center" style="margin-top: 64px">
        <div class = "quarter">
        <i class="fa fa-crosshairs center jumbo"></i>
            <p class="large">Deadly Accurate</p>
            <p>We know that making deadly accurate investments requires deadly accurate stock data. That's why we get
            our stock data directly from real-world stock markets via a combination of the IEX API,
            and Yahoo! Finance so you always know that your investments are as accurate as they can be.
            </p>
        </div>
        <div class = "quarter">
            <i class="fa fa-line-chart center jumbo"></i>
            <p class="large">Tracking</p>
            <p>What's the fun of investing in something if you can't see how you're doing? We let you track your
                investments in real time so you can see if you should buy more or sell fast. You can also track a stock
                that you might want to invest in, if it does well enough.
            </p>
        </div>
        <div class = "quarter">
            <i class="fa fa-exchange center jumbo"></i>
            <p class="large">Investing</p>
            <p>The best part about investing is you get to make money. We let you buy and sell stocks as you please, so
                you can capitalize on that surging (or plummeting) stock.
            </p>
        </div>
        <div class = "quarter">
            <i class="fa fa-edit center jumbo"></i>
            <p class="large">Leaderboard</p>
            <p>We're not saying it's a competition, but who wouldn't want to be featured as one of the top 3 investors
                on the leaderboard? You can flex on Cheryl from accounting.
            </p>
        </div>
    </div>
</div>


<div class = "container" style="padding:50px 16px" id ="team">
    <h3 class ="center">THE TEAM</h3>
    <p class ="center large">The Creators of Virtual Stock Sim</p>
    <div class = "row grayscale" style="margin-top: 34px">
        <div class="col margin-bottom">
            <div class = "card">
            <img src="../_view/resources/images/about/earl.jpg" alt="Earl" style="width:100%">
                <div class="container">
                    <h3>Earl Kennedy</h3>
                    <p class="opacity">Project Manager & Full Stack Developer</p>
                    <p>The glue that held the team together, Earl served as the team's lead as he delegated
                        tasks and managed deadlines while also creating the database backend from the ground up.</p>
                    <p><button onclick="earlLinkedin()" class="button light-grey block"><i class="fa fa-envelope"></i> Contact</button></p>
                    <script>
                        function earlLinkedin() {
                            location.href="https://www.linkedin.com/in/earlkennedyiv/"
                        }
                    </script>
            </div>
        </div>
        </div>
            <div class="col margin-bottom">
                <div class = "card">
                    <img src="../_view/resources/images/about/dan.jpg" alt="Earl" style="width:100%">
                    <div class="container">
                        <h3>Dan Palmieri</h3>
                        <p class="opacity">Full Stack Developer</p>
                        <p>Dan's interest in web development led him to taking on the bulk of designing and building the
                            front-end user interface. He also was responsible for developing the Account and Encryption backend.
                        </p>
                        <p><button onclick="danLinkedin()" class="button light-grey block"><i class="fa fa-envelope"></i> Contact</button></p>
                        <script>
                            function danLinkedin() {
                                location.href="https://www.linkedin.com/in/dpalmieri5/"
                            }
                        </script>
                    </div>
                </div>
            </div>
                <div class="col margin-bottom">
                    <div class = "card">
                        <img src="../_view/resources/images/about/brett.jpg" alt="Brett" style="width:100%">
                        <div class="container">
                            <h3>Brett Kearney</h3>
                            <p class="opacity">Full Stack Developer</p>
                            <p>Brett took on the majority of the backend, developing the transaction history and followed
                                stocks. Brett also took on web scraping to get the company history and price history for a stock.
                            </p>
                            <p><button onclick="brettLinkedin()" class="button light-grey block"><i class="fa fa-envelope"></i> Contact</button></p>
                            <script>
                                function brettLinkedin() {
                                    location.href="https://www.linkedin.com/in/brett-kearney-bbb947160/"
                                }
                            </script>
                        </div>
                    </div>
                </div>
            </div>
</div>


</body>
</html>
