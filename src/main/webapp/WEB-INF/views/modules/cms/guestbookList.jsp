<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>留言管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<li class="active"><a href="${ctxAdmin}/cms/guestbook/">留言列表</a></li><%--
		<e:hasPermission name="cms:guestbook:edit"><li><a href="${ctxAdmin}/cms/guestbook/form?id=${guestbook.id}">留言添加</a></li></e:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="guestbook" action="${ctxAdmin}/cms/guestbook/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>分类：</label><form:select id="type" path="type" class="input-small"><form:option value="" label=""/><form:options items="${fns:getDictList('cms_guestbook')}" itemValue="value" itemLabel="name" htmlEscape="false"/></form:select>
		<label>内容 ：</label><form:input path="content" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
		<label>状态：</label><form:radiobuttons onclick="$('#searchForm').submit();" path="status" items="${fns:getDictList('cms_del_flag')}" itemLabel="name" itemValue="value" htmlEscape="false" />
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>留言分类</th><th>留言内容</th><th>留言人</th><th>留言时间</th><th>回复人</th><th>回复内容</th><th>回复时间</th><e:hasPermission name="cms:guestbook:edit"><th>操作</th></e:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.result}" var="guestbook">
			<tr>
				<td>${fns:getDictionaryNameByDV('cms_guestbook',guestbook.type,  '无分类')}</td>
				<td><a href="${ctxAdmin}/cms/guestbook/form?id=${guestbook.id}">${fns:abbr(guestbook.content,40)}</a></td>
				<td>${guestbook.name}</td>
				<td><fmt:formatDate value="${guestbook.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${guestbook.reUser.name}${guestbook.status}</td>
				<td>${fns:abbr(guestbook.reContent,40)}</td>
				<td><fmt:formatDate value="${guestbook.reDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<e:hasPermission name="cms:guestbook:edit"><td>
					<c:if test="${guestbook.status ne '2'}"><a href="${ctxAdmin}/cms/guestbook/delete?id=${guestbook.id}${guestbook.status ne 0?'&isRe=true':''}"
						onclick="return confirmx('确认要${guestbook.status ne 0?'恢复审核':'删除'}该留言吗？', this.href)">${guestbook.status ne 0?'恢复审核':'删除'}</a></c:if>
					<c:if test="${guestbook.status eq '2'}"><a href="${ctxAdmin}/cms/guestbook/form?id=${guestbook.id}">审核</a></c:if>
				</td></e:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>