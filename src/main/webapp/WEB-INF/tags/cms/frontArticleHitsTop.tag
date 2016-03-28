office<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ attribute name="category" type="com.eryansky.modules.cms.mapper.Category" required="true" description="栏目对象"%>
<%@ attribute name="pageSize" type="java.lang.Integer" required="false" description="页面大小"%>
<c:forEach items="${fnc:getArticleList(category.site.code, category.code, not empty pageSize?pageSize:8, 'posid:2, orderBy: \"hits desc\"')}" var="content">
	<li><a href="${ctxFront}/view-${content.category.code}-${content.id}${urlSuffix}" style="color:${content.color}" title="${content.title}">${fns:abbr(content.title,16)}</a></li>
</c:forEach>