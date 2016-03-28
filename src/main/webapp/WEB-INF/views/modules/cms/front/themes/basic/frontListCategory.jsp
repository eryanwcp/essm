<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${category.name}</title>
	<meta name="decorator" content="cms_default_${site.theme}"/>
	<meta name="description" content="${category.description}" />
	<meta name="keywords" content="${category.keywords}" />
</head>
<body>
<div class="container backgroundcontainer">
  <div class="background_top">
	<div class="ground">
	   	 <p>${category.name}</p>		 
		<%-- <h4>推荐阅读</h4>
		 <ol>
		 	<cms:frontArticleHitsTop category="${category}"/>
		 </ol>  --%>
	</div>
	<div class="ground1">
		 <ul class="breadcrumb">
		    <cms:frontCurrentPosition category="${category}"/>
		 </ul>
	</div>
  </div>
  <div class="container">
	<div class="span2" style="margin:0px;padding:0 4px 0 10px;background:url(${ctxStatic}/img/jfit/category_bg.png) repeat-y;">
		<ol class="tanhuang_center">
		 	<cms:frontCategoryList categoryList="${categoryList}"/>
		</ol>
	</div>
	<div class="span10" style="padding-top:15px;margin-left:4px;" id="frontListCategory_span10_height">
	      <c:set var="index" value="1"/>
		  <c:forEach items="${categoryList}" var="tpl">
			<c:if test="${tpl.inList eq '1' && tpl.module ne ''}">
				<c:set var="index" value="${index+1}"/>
				 ${index % 2 eq 0 ? '<div class="row" style="margin:0px;">':''}
		    	<div class="span5" style="margin:0px 0px 0px 10px;">
		    		<h4><small><a href="${ctxFront}/list-${tpl.code}${urlSuffix}" class="pull-right">更多&gt;&gt;</a></small>${tpl.name}</h4>
					<c:if test="${tpl.module eq 'article'}">
		    			<ul><c:forEach items="${fnc:getArticleList(site.code, tpl.code, 5, '')}" var="content">
							<li><span class="pull-right"><fmt:formatDate value="${content.updateTime}" pattern="yyyy.MM.dd"/></span><a href="${ctxFront}/view-${content.category.code}-${content.id}${urlSuffix}" style="color:${content.color}">${fns:abbr(content.title,40)}</a></li>
						</c:forEach></ul>
					</c:if>
					<c:if test="${tpl.module eq 'link'}">
		    			<ul><c:forEach items="${fnc:getLinkList(site.code, tpl.code, 5, '')}" var="link">
							<li><span class="pull-right"><fmt:formatDate value="${link.updateTime}" pattern="yyyy.MM.dd"/></span><a target="_blank" href="${link.href}" style="color:${link.color}">${fns:abbr(link.title,40)}</a></li>
						</c:forEach></ul>
					</c:if>
		    	</div>
		    	${index % 2 ne 0 ? '</div>':''}
			</c:if>
		  </c:forEach>
	</div>
  </div>
	</div>
<script type="text/javascript">
	$(document).ready(function(){
		$(".tanhuang_center").height($("#frontListCategory_span10_height").height()+65);
	});
</script>
</body>
</html>