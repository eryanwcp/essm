<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>缓存数据-${key}</title>
	<meta name="decorator" content="default_sys"/>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctxAdmin}/sys/systemMonitor/cacheDetail?region=${region}">缓存明细</a></li>
	<li class="active"><a href="${ctxAdmin}/sys/systemMonitor/cacheKeyDetail?region=${region}&key=${key}">缓存数据</a></li>
</ul>
<form:form id="searchForm" method="post" class="breadcrumb form-search">
	${region}:${key}
</form:form>
<tags:message content="${message}"/>
<div>${data}</div>
</body>
</html>