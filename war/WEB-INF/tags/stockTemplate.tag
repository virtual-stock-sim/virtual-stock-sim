<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>
<%@taglib prefix="f" tagdir="../../WEB-INF/tags" %>
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<style><%@include file="/war/_view/cssfiles/stockTemplateStyle.css"%></style>


<div class="panel panel-default">
    <div class="panel-heading">
        <div class="panel-title">
            <a data-toggle="collapse" data-parent="#accordion" data-target="companyInfo"></a>
            <span id="symbol">${stock.symbol}</span>
            <span id="currPrice"> ${stock.currPrice}</span>
            <span id="prevClose">${stock.currVolume}</span>
            <span id="prevVolume">${stock.prevVolume}</span>
            <span id="stockDataId">${stock.stockDataId}</span>
            <span id="timeUpdated">${stock.lastUpdated}</span>
            <div id="companyInfo" class="collapse">
            <span id="companyDesc" class="panel-body">Lorem ipsum dolor sit amet, consectetur adipisicing elit,
                sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad
                minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
                commodo consequat.
            </span>
            <span id="graph">
                Graph Here
            </span>
            </div>
        </div>
    </div>
</div>