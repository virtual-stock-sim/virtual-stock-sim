<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page session = "false" %>

<html>
<head>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>


    <script src="/js_files/redirect.js"></script>

    <script type="text/javascript" src="https://platform.linkedin.com/badges/js/profile.js" async defer></script>

    <link rel="stylesheet" href="../cssfiles/aboutPageStyle.css">

    <title>About - VSS: Virtual Stock Sim</title>
</head>
<body>

<t:navbar account="${account}"/>

<div class = vss-container style="padding:10px 16px">

    <div class="github-link">
        <p class="vss-center">You can view the project's GitHub repository by clicking here <a class="vss-xtra-large" href="https://github.com/virtual-stock-sim/virtual-stock-sim"><i class="fa fa-github"></i></a></p>
    </div>

    <h3 class = "vss-center">ABOUT VSS</h3>
    <p class ="vss-center">What we bring to the table</p>

    <div class = "vss-row vss-center" >
        <div class = "vss-quarter">
        <i class="fa fa-crosshairs vss-center vss-jumbo"></i>
            <p class="vss-large">Deadly Accurate</p>
            <p>We know that making deadly accurate investments requires deadly accurate stock data. That's why we get
                our stock data directly from real-world stock markets via Yahoo! Finance so you always know that your
                investments are as accurate as they can be.
            </p>
        </div>
        <div class = "vss-quarter">
            <i class="fa fa-line-chart vss-center vss-jumbo"></i>
            <p class="vss-large">Tracking</p>
            <p>What's the fun of investing in something if you can't see how you're doing? We let you track your
                investments in real time so you can see if you should buy more or sell fast. You can also track a stock
                that you might want to invest in, if it does well enough.
            </p>
        </div>
        <div class = "vss-quarter">
            <i class="fa fa-exchange vss-center vss-jumbo"></i>
            <p class="vss-large">Investing</p>
            <p>The best part about investing is you get to make money. We let you buy and sell stocks as you please, so
                you can capitalize on that surging (or plummeting) stock.
            </p>
        </div>
        <div class = "vss-quarter">
            <i class="fa fa-edit vss-center vss-jumbo"></i>
            <p class="vss-large">Leaderboard</p>
            <p>We're not saying it's a competition, but who wouldn't want to be featured as one of the top 3 investors
                on the leaderboard? You can flex on Cheryl from accounting.
            </p>
        </div>
    </div>
</div>


<div class = "vss-container" style="padding:50px 16px" id ="team">
    <h3 class ="vss-center">MEET THE TEAM</h3>
    <p class ="vss-center vss-large">The Creators of Virtual Stock Sim</p>
    <div class = "vss-row vss-grayscale" style="margin-top: 34px">
        <div class="vss-col vss-margin-bottom">
            <div class = "vss-card">
            <img src="../_view/resources/images/about/earl.jpg" alt="Earl" style="width:100%">
                <div class="vss-container">
                    <h3>Earl Kennedy</h3>
                    <p class="vss-opacity">Project Manager & Full Stack Developer</p>
                    <p>The glue that held the team together, Earl served as the team's lead as he delegated
                        tasks and managed deadlines while also creating the database backend from the ground up,
                        featuring HTTPS client-server communication with realtime data & scheduled updates.</p>
                    <p><button onclick="earlLinkedin()" class="vss-button vss-light-grey vss-block"><i class="fa fa-envelope"></i> Contact</button></p>
                    <script>
                        function earlLinkedin() {
                            location.href="https://www.linkedin.com/in/earlkennedyiv/"
                        }
                    </script>
            </div>
        </div>
        </div>
            <div class="vss-col vss-margin-bottom">
                <div class = "vss-card">
                    <img src="../_view/resources/images/about/dan.jpg" alt="Earl" style="width:100%">
                    <div class="vss-container">
                        <h3>Dan Palmieri</h3>
                        <p class="vss-opacity">Frontend Lead & Full Stack Developer</p>
                        <p>Dan's interest in web development led him to taking on designing and building the
                            frontend user interface. He also developed the stock template, as well as the Account, AccountController,
                            and Encryption classes and the Account database on the backend.
                        </p>
                        <p><button onclick="danLinkedin()" class="vss-button vss-light-grey vss-block"><i class="fa fa-envelope"></i> Contact</button></p>
                        <script>
                            function danLinkedin() {
                                location.href="https://www.linkedin.com/in/dpalmieri5/"
                            }
                        </script>
                    </div>
                </div>
            </div>
                <div class="vss-col vss-margin-bottom">
                    <div class = "vss-card">
                        <img src="../_view/resources/images/about/brett.jpg" alt="Brett" style="width:100%">
                        <div class="vss-container">
                            <h3>Brett Kearney</h3>
                            <p class="vss-opacity">Full Stack Developer</p>
                            <p>Brett took on the majority of the backend, developing the transaction history, followed stocks,
                                and investments. Brett also used web scraping & JSON to get the company history and price history for a stock.
                            </p>
                            <p><button onclick="brettLinkedin()" class="vss-button vss-light-grey vss-block"><i class="fa fa-envelope"></i> Contact</button></p>
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
