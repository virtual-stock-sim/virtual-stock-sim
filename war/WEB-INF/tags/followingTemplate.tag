<%@tag description="Template for displaying information about a followed stock" pageEncoding="UTF-8"%>
<%@attribute name="followItem" required="true" type="io.github.virtualstocksim.following.Follow" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>

<span>
    <ul><b>${followItem.stock.symbol} </b> | Current Price: $${followItem.currentPrice}  |  Percent Change Since Follow ${followItem.percentChange}  </ul>

</span>