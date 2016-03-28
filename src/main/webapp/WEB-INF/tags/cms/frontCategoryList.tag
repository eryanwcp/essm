<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ attribute name="categoryList" type="java.util.List" required="true" description="栏目列表"%>
<%-- <%@ attribute name="category" type="com.jfit.modules.cms.entity.Category" required="true" description="栏目对象"%> --%>
<c:forEach items="${categoryList}" var="tpl" varStatus="status">
  	<%--<c:if test="${category.inList eq '1'}"> --%>
   		<c:choose>
   			<c:when test="${not empty tpl.href}">
    			<c:choose>
	    			<c:when test="${fn:indexOf(tpl.href, '://') eq -1 && fn:indexOf(tpl.href, 'mailto:') eq -1}">
	    			<c:set var="url" value="${ctxFront}${tpl.href}"/></c:when>
	    			<c:otherwise><c:set var="url" value="${tpl.href}"/></c:otherwise>
	    		</c:choose>
   			</c:when>
   			<c:otherwise><c:set var="url" value="${ctxFront}/list-${tpl.code}${urlSuffix}"/></c:otherwise>
   		</c:choose>
		<li>
			<c:choose><c:when test="${fn:length(tpl.name) gt 12}">
				<a href="${url}" target="${tpl.target}" style="line-height:16px;padding-top:3px;">${fn:substring(tpl.name,0,8)}<br/>${fn:substring(tpl.name,8,18)}</a>
			</c:when><c:otherwise>
				<a href="${url}" target="${tpl.target}" ${fn:length(tpl.name) gt 10?'style="font-size:12px;"':''}>
					<c:choose>
            		<c:when test="${tpl.id eq category.id}">
             			<span class="active">
            		</c:when>
            		<c:otherwise>
            		 	<span>
            		</c:otherwise>
            	</c:choose>
						${tpl.name}
					</span>
				</a>
			</c:otherwise></c:choose></li>
	<%--</c:if> --%>
</c:forEach>
<style type="text/css">
	.active{color: #2586ab;text-decoration: none;font-weight:bold;}
</style>