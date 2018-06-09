<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${model.title}</title>
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
<br/>
<div class="container-fluid">
	<div>消息内容：${model.content}</div>
	<%--<div style="border-top:1px solid #ddd;padding:10px;margin:25px 0;">发布者：${article.createUser.name} &nbsp; 点击数：${article.hits} &nbsp; 发布时间：<fmt:formatDate value="${article.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/> &nbsp; 更新时间：<fmt:formatDate value="${article.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></div>--%>
	<div>发 布 人：${model.senderName} &nbsp;</div>
	<div>发布时间：<fmt:formatDate value="${model.sendTime}" pattern="yyyy-MM-dd HH:mm"/> &nbsp;</div>
</div>
<form:form id="searchForm" modelAttribute="model" action="${ctxAdmin}/notice/message/info?id=${model.id}" method="post" >
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
</form:form>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead><tr><th>接收人</th><th>所属部门</th><th>状态</th></tr></thead>
	<tbody>
	<c:forEach items="${page.result}" var="model">
		<tr>
			<td>${model.userName}</td>
			<td>${model.organName}</td>
			<td>${model.isRead eq 1 ? '已阅':'未阅'}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
<div class="form-actions">
	<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
</div>
</body>
</html>