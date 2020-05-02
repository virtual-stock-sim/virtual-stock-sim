<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script type="module" src="../../js_files/generated/stocktemplate.js"></script>
<script src="../../js_files/general.js"></script>

<link rel="stylesheet" href="../../cssfiles/stockTemplateStyle.css">



<div class="stock-template" style="margin-left: 5%;">
    <div hidden class="stockSymbol">${stock.symbol}</div>
    <div class="dropdown" id="stock-invest-dropdown">
        <button class="btn btn-default dropdown-toggle dropdown-title" id="invest-btn" data-toggle="dropdown" style="position:relative;
    top:95px;right:60px;">
            INVEST<i class="material-icons" style="border-style:hidden;display:inline-block;position:relative;top:4px;">
            import_export</i>
            </button>
        <u1 class="dropdown-menu">
            <li class="dropdown-item"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-buy-modal" style="cursor:pointer;">BUY</a></li>
            <li class="divider"></li>
            <li class="dropdown-item"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-sell-modal" style="cursor:pointer;">SELL</a></li>
        </u1>
    </div>
    <div class="panel panel-collapse">
        <div class="panel-heading" role="tab">
            <h2 class="panel-title">
                <a id="${stock.symbol}-dropdown-toggle" data-toggle="collapse" data-target="#${stock.symbol}-dropdown" aria-controls="#${stock.symbol}-dropdown">

                    <span name="${stock.symbol}-symbol">${stock.symbol}</span>
                    <span name="${stock.symbol}-curr_price">$${stock.currPrice}</span>
                    <span name="${stock.symbol}-pchange"<c:choose>
                        <c:when test="${stock.percentChange  ge 0.0}">
                            style="color:green!important;"
                        </c:when>
                        <c:otherwise>
                            style="color:red!important;"
                        </c:otherwise>
                    </c:choose>>
                                ${stock.percentChange} %</span>
                </a>
            </h2>
        </div>
        <div id="${stock.symbol}-dropdown" class="collapse" role="tabpanel" >
            <div class="panel-body">
                <p id="${stock.symbol}-desc"></p><br>
                <table class="table table-condensed" style="width: 100%;">
                    <tr>
                        <th>Current Price:</th>
                        <td><span name="${stock.symbol}-curr_price">$${stock.currPrice}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Closing Price:</th>
                        <td><span name="${stock.symbol}-prev_close">$${stock.prevClose}</span></td>
                    </tr>
                    <tr>
                        <th>Current Volume:</th>
                        <td><span name="${stock.symbol}-curr_volume">${stock.currVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Volume:</th>
                        <td><span name="${stock.symbol}-prev_volume">${stock.prevVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Percent Change:</th>
                        <td><span name="${stock.symbol}-pchange"<c:choose>
                            <c:when test="${stock.percentChange  ge 0.0}">
                                style="color:green!important;"
                            </c:when>
                            <c:otherwise>
                                style="color:red!important;"
                            </c:otherwise>
                        </c:choose>>
                                ${stock.percentChange} %</span></td>
                    </tr>
                </table>
                <div id="${stock.symbol}-depth-graph">
                </div>
            </div>
        </div>
    </div>
</div>


<!--Buy Modal-->
<div id="${stock.symbol}-buy-modal" class="modal fade" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">

    <!-- Modal Content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class= "close" data-dismiss="modal" aria-label="Close">&times;</button>
            <h2 class="modal-title">Buy ${stock.symbol} Stock</h2>
        </div>

        <div class="modal-body">
            <form action=${pageContext.servletContext.contextPath}/following name="${stock.symbol}-buy-form" method="post" id="${stock.symbol}-buy-form">
                <textarea class="form-control" form="${stock.symbol}-buy-form" onsubmit="this.value=''" rows="1" cols="5" placeholder="Enter the number of shares you'd like to buy" name="shares-to-buy"></textarea><br>
                <input class="btn btn-default" type="submit" value="Buy">
            </form>
        </div>
    </div>
</div>

<!--Sell Modal-->
<div class="modal fade" id="${stock.symbol}-sell-modal" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">
        <!-- Modal Content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class= "close" data-dismiss="modal" aria-label="Close">&times;</button>
                <h2 class="modal-title">Sell ${stock.symbol} Stock</h2>
            </div>

            <div class="modal-body">
                <form action=${pageContext.servletContext.contextPath}/following name="${stock.symbol}-sell-form" method="post" id="${stock.symbol}-sell-form">
                    <textarea class="form-control" form="${stock.symbol}-sell-form" onsubmit="this.value=''" rows="1" cols="5" placeholder="Enter the number of shares you'd like to sell" name="shares-to-sell"></textarea><br>
                    <input class="btn btn-default" type="submit" value="Sell">
                </form>
            </div>
        </div>
</div>
