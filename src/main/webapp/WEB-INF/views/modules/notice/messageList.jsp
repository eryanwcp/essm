<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息管理</title>
	<meta name="decorator" content="default_sys"/>
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
		<li class="active"><a href="${ctxAdmin}/notice/message">消息列表</a></li>
		<e:hasPermission name="notice:message:edit"><li><a href="${ctxAdmin}/notice/message/form">消息添加</a></li></e:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="model" action="${ctxAdmin}/notice/message/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>关键字：</label><form:input path="query" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;
		<label>状态：</label><form:radiobuttons onclick="$('#searchForm').submit();" path="status" items="${fns:getDictList('cms_del_flag')}" itemLabel="name" itemValue="value" htmlEscape="false" />
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>应用</th><th>消息内容</th><th>发送者</th><th>发送时间</th><th>状态</th><th>机构</th><e:hasPermission name="notice:message:edit"><th>操作</th></e:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.result}" var="model">
			<tr>
				<%--<td><a href="${ctxAdmin}/notice/message/form?id=${model.id}" title="${model.title}">${fns:abbr(model.title,40)}</a></td>--%>
				<td>${model.appId}</td>
				<td><a href="javascript:" onclick="$('#c_${model.id}').toggle()">${fns:rabbr(model.content,60)}</a></td>
				<td>${model.senderName}</td>
				<td><fmt:formatDate value="${model.sendTime}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td>${model.modeView}</td>
				<td>${model.organName}</td>
				<td>
					<e:hasPermission name="notice:message:edit">
						<%--<a href="${ctxAdmin}/notice/message/form?id=${model.id}">修改</a>--%>
						<a href="${ctxAdmin}/notice/message/info?id=${model.id}" >查看详情</a>
						<a href="${ctxAdmin}/notice/message/delete?id=${model.id}${model.status ne 0?'&isRe=true':''}" onclick="return confirmx('确认要${model.status ne 0?'恢复':''}删除该消息吗？', this.href)" >${model.status ne 0?'恢复':''}删除</a>
					</e:hasPermission>
				</td>

			</tr>
			<tr id="c_${model.id}" style="background:#fdfdfd;display:none;"><td colspan="7">${fns:replaceHtml(model.content)}</td></tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>