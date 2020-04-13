<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function() {
    $('body').on('show.bs.collapse', '.collapse', function(e) {
        $(this).closest('.panel').find('.panel-heading').addClass('active');
    });

    $('body').on('hide.bs.collapse', '.collapse', function(e) {
        $(this).closest('.panel').find('.panel-heading').removeClass('active');
    });
    })

</script>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<style><%@include file="../../_view/cssfiles/stockTemplateStyle.css"%></style>


<div class="stock-template">
    <div class="panel panel-collapse">
        <div class="panel-heading" role="tab" id="comapnyDesc">
            <h2 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseDesc" aria-controls="collapseDesc">
                    <span class="glance-symbol">${stock.symbol}</span>
                    <span class="glance-curr_price">$${stock.currPrice}</span>
                    <span class="glance-pchange">${stock.currPrice.divide(stock.prevClose) * 100} %</span>
                </a>
            </h2>
        </div>
        <div id="collapseDesc" class="collapse" role="tabpanel" aria-labelledby="companyDesc">
            <div class="panel-body">
                <p class="desc">Lorem ipsum dolor sit amet, consectetur adipisicing elit,
                    sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad
                    minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
                    commodo consequat.
                </p><br>
                <table class="table table-condensed" style="width: 100%;">
                    <tr>
                        <th>Current Price:</th>
                        <td><span class="depth-curr_price">$${stock.currPrice}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Closing Price:</th>
                        <td><span class="depth-prev_close">$${stock.prevClose}</span></td>
                    </tr>
                    <tr>
                        <th>Current Volume:</th>
                        <td><span class="depth-curr_volume">${stock.currVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Previous Volume:</th>
                        <td><span class="depth-prev_volume">${stock.prevVolume}</span></td>
                    </tr>
                    <tr>
                        <th>Percent Change:</th>
                        <td><span class="depth-pchange">${stock.currPrice.divide(stock.prevClose) * 100} %</span></td>
                    </tr>
                </table>
                <span class="graph">
                    Graph here
                </span>
            </div>
        </div>
    </div>
</div>