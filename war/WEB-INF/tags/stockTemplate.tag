<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function() {
    $('body').on('show.bs.collapse', '.collapse', function(e) {
        $(this).closest('.panel').find('.panel-heading').addClass('active-${stock.symbol}');
    });

    $('body').on('hide.bs.collapse', '.collapse', function(e) {
        $(this).closest('.panel').find('.panel-heading').removeClass('active-${stock.symbol}');
    });
    })

</script>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="module" src="../../js_files/generated/stocktemplate.js"></script>


<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">

<link rel="stylesheet" href="../../cssfiles/stockTemplateStyle.css">

<div class="stock-template" style="margin-left: 5%;">
    <div hidden class="stockSymbol">${stock.symbol}</div>
    <div class="dropdown" id="stock-invest-dropdown">
        <button class="btn btn-default dropdown-toggle dropdown-title" id="invest-btn" data-toggle="dropdown" style="position:relative;
    top:95px;right:60px;">
            INVEST<i class="material-icons" style="border-style:hidden;display:inline-block;position:relative;top:4px;">
            import_export</i>
            <span class="caret" style="display: inline-block;"></span></button>
        <u1 class="dropdown-menu" style="margin-top:5%;margin-left: -5%;">
            <li class="link"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-buy-modal" style="cursor:pointer;">BUY</a></li>
            <li class="divider"></li>
            <li class="link"><a type="button" data-toggle="modal" data-target="#${stock.symbol}-sell-modal" style="cursor:pointer;">SELL</a></li>
        </u1>
    </div>
    <div class="panel panel-collapse">
        <div class="panel-heading" role="tab">
            <h2 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseDesc" aria-controls="collapseDesc">

                    <span class="${stock.symbol}-glance-symbol">${stock.symbol}</span>
                    <span class="${stock.symbol}-glance-curr_price">$${stock.currPrice}</span>
                    <span class="${stock.symbol}-glance-pchange"<c:choose>
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
        <div id="collapseDesc" class="collapse" role="tabpanel" >
            <div class="panel-body">
                <p id="${stock.symbol}-desc"></p><br>
                <table class="table table-condensed" style="width: 100%;">
                    <tr>
                        <th>Current Price:</th>
                        <td><span class="${stock.symbol}-depth-curr_price">$${stock.currPrice}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Closing Price:</th>
                        <td><span class="${stock.symbol}-depth-prev_close">$${stock.prevClose}</span></td>
                    </tr>
                    <tr>
                        <th>Current Volume:</th>
                        <td><span class="${stock.symbol}-depth-curr_volume">${stock.currVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Volume:</th>
                        <td><span class="${stock.symbol}-depth-prev_volume">${stock.prevVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Percent Change:</th>
                        <td><span class="${stock.symbol}-depth-pchange"<c:choose>
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
                <span id="${stock.symbol}-depth-graph">
                </span>
            </div>
        </div>
    </div>
</div>


<!--Buy Modal-->
<div id="${stock.symbol}-buy-modal" class="modal fade" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">

    <!-- Modal Content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h2 class="modal-title">Buy ${stock.symbol} Stock</h2>
        </div>

        <div class="modal-body">
            <form action=${pageContext.servletContext.contextPath}/following class="${stock.symbol}-buy-form" method="post" id="${stock.symbol}-buy-form">
                <textarea class="form-control" form="${stock.symbol}-buy-form" rows="1" cols="5" placeholder="Enter the number of shares you'd like to buy" name="shares-to-buy"></textarea><br>
                <input class="btn btn-default" type="submit" value="Buy" onclick="this.value=''">
            </form>
        </div>
    </div>
</div>

<!--Sell Modal-->
<div class="modal fade" id="${stock.symbol}-sell-modal" role="form" style="width:50%;margin-top: 10%; margin-left: 25%;color:black">
        <!-- Modal Content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h2 class="modal-title">Sell ${stock.symbol} Stock</h2>
            </div>

            <div class="modal-body">
                <form action=${pageContext.servletContext.contextPath}/following class="${stock.symbol}-sell-form" method="post" id="${stock.symbol}-sell-form">
                    <textarea class="form-control" form="${stock.symbol}-sell-form" rows="1" cols="5" placeholder="Enter the number of shares you'd like to sell" name="shares-to-sell"></textarea><br>
                    <input class="btn btn-default" type="submit" value="Sell" onclick="this.value=''">
                </form>
            </div>
        </div>
</div>
