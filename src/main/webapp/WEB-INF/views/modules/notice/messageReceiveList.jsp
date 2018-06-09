<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>我的消息</title>
    <meta name="decorator" content="default_sys"/>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        /**
         * 设置已读 （异步）
         * @param id
         */
        function setRead(id){
            $.ajax({
                url: '${ctxAdmin}/notice/messageReceive/setRead?id='+id,
                type: 'get',
                dataType: 'json',
                success: function (data) {}
            });
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctxAdmin}/notice/messageReceive">消息列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="model" action="${ctxAdmin}/notice/messageReceive" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <form:radiobutton path="isRead" value="0" label="未阅" onclick="$('#searchForm').submit();"/>
    <form:radiobutton path="isRead" value="1" label="已阅" onclick="$('#searchForm').submit();"/>
    <%--<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;--%>
</form:form>
<tags:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>消息内容</th>
        <th>发送者</th>
        <th>发送时间</th>
        <th>阅读状态</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.result}" var="model">
        <tr>
            <%--<td><a href="${ctxAdmin}/notice/messageReceive/info?id=${model.id}" title="${model.message.content}">${fns:rabbr(model.message.content,100)}</a></td>--%>
            <td><a href="javascript:" onclick="setRead('${model.id}');$('#c_${model.id}').toggle()">${fns:rabbr(model.message.content,60)}</a></td>
            <td>${model.message.senderName}</td>
            <td><fmt:formatDate value="${model.message.sendTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${model.isReadView}</td>
        </tr>
        <tr id="c_${model.id}" style="background:#fdfdfd;display:none;"><td colspan="4">${fns:replaceHtml(model.message.content)}</td></tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>