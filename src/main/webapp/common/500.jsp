<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>页面访问出错</title>
    <script src="${ctxStatic}/js/jquery/jquery.js" type="text/javascript"></script>
</head>
<%
    Throwable ex = null;
    if (exception != null)
        ex = exception;
    if (request.getAttribute("javax.servlet.error.exception") != null)
        ex = (Exception) request.getAttribute("javax.servlet.error.exception");
%>
<body>
<div id="content">
    <div>
        <h3>
            页面访问时发生错误:
            <%
                if (ex != null)
                    out.println(ex.getMessage());
            %>
        </h3>
    </div>
    <div>
        <button onclick="history.back();">返回</button>
        <button onclick="$('#detail_error_msg').toggle();">显示/隐藏详细信息</button>
    </div>
</div>

<div>
    <div id="detail_error_msg" style="display: none">
        <% if (ex != null) { %>
					<pre>
						<% ex.printStackTrace(new java.io.PrintWriter(out)); %>
					</pre>
        <% } %>
    </div>
</div>
</body>
</html>