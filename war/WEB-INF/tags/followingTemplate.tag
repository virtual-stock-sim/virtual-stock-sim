<%@tag description="Template for displaying information about a followed stock" pageEncoding="UTF-8"%>
<%@attribute name="followItem" required="true" type="io.github.virtualstocksim.following.Follow" %>
<%@taglib prefix="f" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class = "wrapper">
    <!-- We should link this to where the company description & price history is (buy page) ...... probably-->
    <div class = "followed-item">
        <span class="sub"><b>${followItem.stock.symbol} </b></span>
        <span class= "sub"><b>${followItem.stock.symbol} </b></span>
        <span class = "sub">$${followItem.currentPrice}</span>
        <span class = "percentSub"
                <c:choose>
        <c:when test="${followItem.percentChange ge 0.0}">
            style="color:green";
        </c:when>
        <c:otherwise>
            style="color:red";
        </c:otherwise>
                </c:choose>>
            ${followItem.percentChange} %</span>
    </div>
</div>