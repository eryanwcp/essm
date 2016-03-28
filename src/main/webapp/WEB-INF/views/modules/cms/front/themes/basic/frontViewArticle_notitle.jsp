<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${article.title} - ${category.name}</title>
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
			$.get("${ctx}/comment",{theme: '${category.site.theme}', 'category.id': '${category.id}',
				contentId: '${article.id}', title: '${article.title}', pageNo: n, pageSize: s, date: new Date().getTime()
			},function(data){
				$("#comment").html(data);
			});
		}
	</script>
</head>
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
	<div class="span10" style="padding-top:15px;margin-left:4px;" id="frontViewArticle_notitle_span10_height">
	     <div class="row" style="margin-left:6px;">
			<%-- <h3 style="color:#555555;font-size:20px;text-align:center;border-bottom:1px solid #ddd;padding-bottom:15px;margin:25px 0;">${article.title}</h3> --%>
			<c:if test="${not empty article.description}"><div style="padding:0 60px 10px;">摘要：${article.description}</div></c:if>
			<div style="overflow:hidden;word-break:break-all;padding:0px 60px 20px;">${article.articleData.content}</div>
			<%-- <div style="border-top:1px solid #ddd;padding:10px;margin:25px 0;">点击数：${article.hits} &nbsp; 发布时间：<fmt:formatDate value="${article.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></div> --%>

  	     </div>
	     <%-- <div class="row" style="margin-left:6px;">
			<div id="comment" class="hide span10">
				正在加载评论...
			</div>
	     </div>
	     <div class="row" style="margin-left:6px;">
			<h5>相关文章</h5>
			<ol><c:forEach items="${relationList}" var="relation">
				<li style="float:left;width:230px;"><a href="${ctxFront}/view-${relation[0]}-${relation[1]}${urlSuffix}">${fns:abbr(relation[2],30)}</a></li>
			</c:forEach></ol>
  	    </div>
  --%>
	      
	</div>
  </div>
 </div>
 <script type="text/javascript">
	$(document).ready(function(){
		$(".tanhuang_center").height($("#frontViewArticle_notitle_span10_height").height()+65);
	});
</script>
</body>
</html>