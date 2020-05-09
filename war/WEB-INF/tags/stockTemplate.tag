<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>
<%@attribute name="followItem" required="false" type="io.github.virtualstocksim.following.Follow" %>
<%@attribute name="investItem" required="false" type="io.github.virtualstocksim.transaction.Investment" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script type="module" src="../../js_files/generated/stocktemplate.js"></script>



<div class="stock-template" style="margin-left: 10%;">
    <div hidden class="stockSymbol">${stock.symbol}</div>
    <!--Determine which buttons to display-->
    <c:choose>
        <c:when test="${empty followItem and empty investItem}">
            <button class="btn btn-neutral" id="${stock.symbol}-follow-btn" style="position:relative; top:95px;right:80px;z-index: 1000;">FOLLOW</button>
        </c:when>
        <c:otherwise>
            <div class="dropdown" id="stock-invest-dropdown">
                <button class="btn btn-default dropdown-toggle dropdown-title" id="invest-btn" data-toggle="dropdown" style="position:relative;top:95px;right:80px;z-index: 1000;">
                    INVEST<i class="material-icons" style="border-style:hidden;display:inline-block;position:relative;top:4px;">
                    import_export</i>
                </button>
                <u1 class="dropdown-menu">
                    <li class="dropdown-item"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-buy-modal" style="cursor:pointer; font-weight: bold;">BUY</a></li>
                    <li class="divider"></li>
                    <li class="dropdown-item"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-sell-modal" style="cursor:pointer;font-weight: bold;">SELL</a></li>
                    <li class="divider"></li>
                    <li class="dropdown-item"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-unfollow-modal" style="cursor:pointer;font-weight: bold;">UNFOLLOW</a></li>
                </u1>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="vss-panel vss-panel-collapse">
        <div class="vss-panel-heading" role="tab">
            <h2 class="vss-panel-title">
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
                                ${stock.percentChange}%</span>
                </a>
            </h2>
        </div>
        <div id="${stock.symbol}-dropdown" class="collapse" role="tabpanel" >
            <div class="vss-panel-body">
                <p id="${stock.symbol}-desc" style="padding-top:20px;"></p><br>
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
                        <th>Percent Change Since Last Close:</th>
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
                    <c:choose>
                        <c:when test="${(empty followItem) && (not empty investItem)}">
                            <tr>
                                <th>Current Shares Owned: </th>
                                <td><span name="${stock.symbol}-shares-owned">${investItem.numShares()}</span></td>
                            </tr>
                            <tr>
                                <th>Total Invested:</th>
                                <td><span name="${stock.symbol}-total-invested">$${investItem.totalHoldings}</span></td>
                            </tr>
                        </c:when>
                        <c:when test="${(not empty followItem) && (empty investItem)}">
                            <tr>
                                <th>Percent Change Since Following:</th>
                                <td><span name="${followItem.stock.symbol}-pchange-since-follow"
                                        <c:choose>
                                        <c:when test="${followItem.percentChange  ge 0.0}">
                                            style="color:green!important;"
                                        </c:when>
                                        <c:otherwise>
                                            style="color:red!important;"
                                        </c:otherwise>
                                        </c:choose>>${followItem.percentChange} %</span></td>
                            </tr>
                        </c:when>
                    </c:choose>
                </table>
                <div id="${stock.symbol}-depth-graph">
                </div>
            </div>
        </div>
    </div>
</div>


<!--Buy Modal-->
<div id="${stock.symbol}-buy-modal" class="modal" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">

    <!-- Modal Content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class= "close" data-dismiss="modal" aria-label="Close">&times;</button>
            <h2 class="modal-title">Buy ${stock.symbol} Stock</h2>
        </div>

        <div class="modal-body">
            <form action=${pageContext.servletContext.contextPath}/following name="${stock.symbol}-buy-form" method="post" id="${stock.symbol}-buy-form">
                <textarea class="form-control" form="${stock.symbol}-buy-form" onsubmit="this.value=''" rows="1" cols="5" placeholder="Enter the number of shares you'd like to buy" name="shares-to-buy"></textarea><br>
                <input type="hidden" name="stock-name" value="${stock.symbol}">
                <input class="btn btn-default" type="submit" value="Buy">
            </form>
        </div>
    </div>
</div>



<!--Sell Modal-->
<div class="modal" id="${stock.symbol}-sell-modal" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">
    <!-- Modal Content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class= "close" data-dismiss="modal" aria-label="Close">&times;</button>
            <h2 class="modal-title">Sell ${stock.symbol} Stock</h2>
        </div>

        <div class="modal-body">
            <form action=${pageContext.servletContext.contextPath}/following name="${stock.symbol}-sell-form" method="post" id="${stock.symbol}-sell-form">
                <textarea class="form-control" form="${stock.symbol}-sell-form" onsubmit="this.value=''" rows="1" cols="5" placeholder="Enter the number of shares you'd like to sell" name="shares-to-sell"></textarea><br>
                <input type="hidden" name="stock-name" value="${stock.symbol}">
                <input class="btn btn-default" type="submit" value="Sell">
            </form>
        </div>
    </div>
</div>





<!--Unfollow Modal-->
<div class="modal" id="${stock.symbol}-unfollow-modal" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">
    <!-- Modal Content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class= "close" data-dismiss="modal" aria-label="Close">&times;</button>
            <h2 class="modal-title">Unfollow ${stock.symbol} Stock</h2>
        </div>

        <div class="modal-body">
            <div class="alert alert-warning">
                <strong>Warning!</strong> Unfollowing a stock will sell all your currently owned shares. Do you wish to proceed?
            </div>
            <form action=${pageContext.servletContext.contextPath}/following name="${stock.symbol}-unfollow-form" method="post" id="${stock.symbol}-unfollow-form">
                <label for="unfollow-confirm">Yes, unfollow ${stock.symbol} and sell my current shares.</label>
                <input type="checkbox" name="unfollow-confirm" id="unfollow-confirm" value="${stock.symbol}"><br>
                <input class="btn btn-default" type="submit" value="Confirm">
            </form>
        </div>
    </div>
</div>
