<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="transaction" required="true" type="io.github.virtualstocksim.transaction.Transaction" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class = "wrapper">
    <!-- We should link this to where the company description & price history is (buy page) ...... probably-->
    <div class = "transactionItem">
        <span class="sub"> ${transaction.type.text}</span>
<%--        <span class="sub"><a href="this will point to company">${transaction.stock.symbol}</a></span>--%>
        <span class = "sub">${transaction.numShares} </span>
        <span class = "sub"> ${transaction.truncatedDate}</span>
        <span class = "sub">$${transaction.pricePerShare} </span>
        <span class = "sub"> $${transaction.volumePrice}</span></div>
</div>
