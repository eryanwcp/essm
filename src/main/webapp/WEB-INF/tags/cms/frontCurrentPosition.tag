<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ attribute name="category" type="com.eryansky.modules.cms.mapper.Category" required="true" description="栏目对象"%>
<li><span>当前位置：</span><a href="${ctxFront}/index-${site.code}${urlSuffix}" style="color:#878787;">首页</a></li><c:forEach items="${fnc:getCategoryListByIds(category.parentIds)}" var="tpl">
	<c:if test="${tpl.code ne '1'}"><li><span class="divider">/</span> <a href="${ctxFront}/list-${tpl.code}${urlSuffix}" style="color:#878787;">${tpl.name}</a></li></c:if>
</c:forEach><li><span class="divider">/</span> <a href="${ctxFront}/list-${category.code}${urlSuffix}" style="color:#878787;">${category.name}</a></li>