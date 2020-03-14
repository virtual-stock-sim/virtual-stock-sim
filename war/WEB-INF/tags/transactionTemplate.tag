<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="transaction" required="true" type="io.github.virtualstocksim.transaction.Transaction" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<span>
    <span>${transaction.type.text} | </span><span>${transaction.date} - Shares: ${transaction.numShares} Price Per Share: ${transaction.pricePerShare}</span>
    <t:stockTemplate stock="${stock}"></t:stockTemplate>
</span>