<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>序列号管理</title>
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
	<li class="active"><a href="${ctxAdmin}/sys/systemSerialNumber/">序列号列表</a></li>
	<e:hasPermission name="sys:systemSerialNumber:edit"><li><a href="${ctxAdmin}/sys/systemSerialNumber/form">序列号添加</a></li></e:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="model" action="${ctxAdmin}/sys/systemSerialNumber/" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
</form:form>
<tags:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<tr><th>模块名称</th><th>模块编码</th><th>备注</th> <e:hasPermission name="sys:systemSerialNumber:edit"><th>操作</th></e:hasPermission></tr>
	<c:forEach items="${page.result}" var="model">
		<tr>
			<td><a href="${ctxAdmin}/sys/systemSerialNumber/form?id=${model.id}">${model.moduleName}</a></td>
			<td>${model.moduleCode}</td>
			<td>${model.remark}</td>
			<td>
				<e:hasPermission name="sys:systemSerialNumber:edit">
					<a href="${ctxAdmin}/sys/systemSerialNumber/form?id=${model.id}" >修改</a>
					<a href="${ctxAdmin}/sys/systemSerialNumber/delete?id=${model.id}"
					   onclick="return confirmx('要删除表单数据吗？', this.href)">删除</a>
				</e:hasPermission>
			</td>
		</tr>
	</c:forEach>
</table>
<div class="pagination">${page}</div>
</body>
</html>