<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="transaction" required="true" type="io.github.virtualstocksim.transaction.Transaction" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<span>
    <span>${transaction.type.text} | </span>   <span><b>${transaction.stock.symbol}</b>   </span><span>Date: ${transaction.timestamp}  Shares: ${transaction.numShares} Price Per Share: ${transaction.pricePerShare}  Total : ${transaction.volumePrice}</span>
    <t:stockTemplate stock="${stock}"></t:stockTemplate>
</span>