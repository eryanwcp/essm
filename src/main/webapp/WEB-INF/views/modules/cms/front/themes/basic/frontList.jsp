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
		<%--  <h4>推荐阅读</h4>
		 <ol>
		 	<cms:frontArticleHitsTop category="${category}"/>
		 </ol> --%>
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
    <div class="span10" style="padding-top:15px;margin-left:4px;" id="frontList_span10_height">
		  <h4>${category.name}</h4>
		  <c:if test="${category.module eq 'article'}">
			<ul><c:forEach items="${page.result}" var="content">
				<li><span class="pull-right"><fmt:formatDate value="${content.updateTime}" pattern="yyyy.MM.dd"/></span><a href="${content.url}" style="color:${content.color}">${fns:abbr(content.title,96)}</a></li>
			</c:forEach></ul>
			<div class="pagination">${page}</div>
			<script type="text/javascript">
				function page(n,s){
					location="${ctxFront}/list-${category.code}${urlSuffix}?pageNo="+n+"&pageSize="+s;
				}
			</script>
		  </c:if>
		  <c:if test="${category.module eq 'link'}">
			<ul><c:forEach items="${page.result}" var="link">
				<li><a href="${link.href}" target="_blank" style="color:${link.color}"><c:out value="${link.title}" /></a></li>
			</c:forEach></ul>
		  </c:if>
    </div>
  </div>
   </div>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		$(".tanhuang_center").height($("#frontList_span10_height").height()+65);
	});
</script>
</body>
</html>