<%@tag description="Template for displaying a Stock" pageEncoding="UTF-8"%>
<%@attribute name="stock" required="true" type="io.github.virtualstocksim.stock.Stock" %>

<span>
    <span>${stock.symbol}</span><span> ${stock.currPrice}</span>
</span>