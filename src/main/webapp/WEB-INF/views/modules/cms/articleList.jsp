<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文章管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function viewComment(href){
			top.$.jBox.open('iframe:'+href,'查看评论',$(top.document).width()-220,$(top.document).height()-120,{
				buttons:{"关闭":true},
				loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".nav,.form-actions,[class=btn]", h.find("iframe").contents()).hide();
					$("body", h.find("iframe").contents()).css("margin","10px");
				}
			});
			return false;
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctxAdmin}/cms/article/?category.id=${article.category.id}">文章列表</a></li>
		<e:hasPermission name="cms:article:edit"><li><a href="<c:url value='${fns:getAdminPath()}/cms/article/form?id=${article.id}&category.id=${article.category.id}'><c:param name='category.name' value='${article.category.name}'/></c:url>">文章添加</a></li></e:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="article" action="${ctxAdmin}/cms/article/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>栏目：</label><tags:treeselect id="category" name="category.id" value="${article.category.id}" labelName="category.name" labelValue="${article.category.name}"
					title="栏目" url="/cms/category/treeData" module="article" notAllowSelectRoot="false" cssClass="input-small"/>
		<label>标题：</label><form:input path="title" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
		<label>状态：</label><form:radiobuttons onclick="$('#searchForm').submit();" path="status" items="${fns:getDictList('cms_del_flag')}" itemLabel="name" itemValue="value" htmlEscape="false"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>栏目</th><th>标题</th><th>权重</th><th>点击数</th><th>发布者</th><th>更新时间</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.result}" var="content">
			<tr>
				<td><a href="javascript:" onclick="$('#categoryId').val('${content.category.id}');$('#categoryName').val('${content.category.name}');$('#searchForm').submit();return false;">${content.category.name}</a></td>
				<td><a href="${ctxAdmin}/cms/article/form?id=${content.id}" title="${content.title}">${fns:abbr(content.title,40)}</a></td>
				<td>${content.weight}</td>
				<td>${content.hits}</td>
				<td>${content.createUserName}</td>
				<td><fmt:formatDate value="${content.updateTime}" type="both"/></td>
				<td>
					<a href="${pageContext.request.contextPath}${fns:getFrontPath()}/view-${content.category.code}-${content.id}${fns:getUrlSuffix()}" target="_blank">访问</a>
					<e:hasPermission name="cms:article:edit">
						<c:if test="${content.category.allowComment eq '1'}"><e:hasPermission name="cms:comment:view">
							<a href="${ctxAdmin}/cms/comment/?module=article&contentId=${content.id}&status=2" onclick="return viewComment(this.href);">评论</a>
						</e:hasPermission></c:if>
	    				<a href="${ctxAdmin}/cms/article/form?id=${content.id}">修改</a>
	    				<e:hasPermission name="cms:article:audit">
							<a href="${ctxAdmin}/cms/article/delete?id=${content.id}${content.status ne 0?'&isRe=true':''}&categoryId=${content.category.id}" onclick="return confirmx('确认要${content.status ne 0?'发布':'删除'}该文章吗？', this.href)" >${content.status ne 0?'发布':'删除'}</a>
						</e:hasPermission>
					</e:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>