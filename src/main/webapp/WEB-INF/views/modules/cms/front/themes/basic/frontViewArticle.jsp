<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${article.title}</title>
	<meta name="decorator" content="cms_default_${site.theme}"/>
	<meta name="description" content="${article.description} ${category.description}" />
	<meta name="keywords" content="${article.keywords} ${category.keywords}" />
    <script type="text/javascript">
		$(document).ready(function() {
			if ("${category.allowComment}"=="1" && "${article.articleData.allowComment}"=="1"){
				$("#comment").show();
				page(1);
			}
		});
		function page(n,s){
			$.get("${ctxFront}/comment",{theme: '${category.site.theme}', 'category.id': '${category.id}',
				contentId: '${article.id}', title: '${article.title}', pageNo: n, pageSize: s, date: new Date().getTime()
			},function(data){
				$("#comment").html(data);
			});
		}
	</script>
	<style type="text/css">
	.span12,.row{margin:0px;}
	</style>
</head>
<body>
<div class="container">
	<%-- <div class="ground">
	   	 <p>栏目列表</p>	 
		 <h4>推荐阅读</h4>
		 <ol>
		 	<cms:frontArticleHitsTop category="${category}"/>
		 </ol>
	</div> --%>
	<div class="span12" style="color:#878787;">
		 <ul class="breadcrumb">
		    <cms:frontCurrentPosition category="${category}"/>
		 </ul>
	</div>
	   <div class="span12" style="min-height:424px;">
	     <div class="row">
	       <div class="span12">
			<h3 style="color:#555555;font-size:20px;text-align:center;border-bottom:1px solid #ddd;padding-bottom:15px;margin:25px 0;">${article.title}</h3>
			<c:if test="${not empty article.description}"><div style="margin-bottom:20px;">摘要：${article.description}</div></c:if>
			<div>${article.articleData.content}</div>
			<%--<div style="border-top:1px solid #ddd;padding:10px;margin:25px 0;">发布者：${article.createUser.name} &nbsp; 点击数：${article.hits} &nbsp; 发布时间：<fmt:formatDate value="${article.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/> &nbsp; 更新时间：<fmt:formatDate value="${article.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></div>--%>
               <div style="border-top:1px solid #ddd;padding:10px 10px 10px 0;margin:25px 0;">发布时间：<fmt:formatDate value="${article.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/> &nbsp;点击数：${article.hits}</div>
  	       </div>
  	     </div>
	     <div class="row">
			<div id="comment" class="hide span10">
				正在加载评论...
			</div>
	     </div>
         <c:if test="${not empty relationList}">
             <div class="row">
                 <div class="span12">
                     <h5>相关文章</h5>
                     <ol><c:forEach items="${relationList}" var="relation">
                         <li style="float:left;width:230px;"><a href="${ctxFront}/view-${relation[0]}-${relation[1]}${urlSuffix}">${fns:abbr(relation[2],30)}</a></li>
                     </c:forEach></ol>
                 </div>
             </div>
         </c:if>

  	  </div>
   </div>
</body>
</html>