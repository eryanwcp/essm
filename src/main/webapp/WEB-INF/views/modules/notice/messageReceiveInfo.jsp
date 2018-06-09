<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>${model.message.content}</title>
    <meta name="decorator" content="default_sys"/>
</head>
<body>
<br/>
<div>${model.message.content}</div>
<div>发布人：${model.message.senderName} &nbsp;</div>
<div>发布时间：<fmt:formatDate value="${model.message.sendTime}" pattern="yyyy-MM-dd HH:mm"/> &nbsp;</div>
<div>阅读状态：${model.isReadView}</div>
<div class="form-actions">
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
</div>
</body>
</html>