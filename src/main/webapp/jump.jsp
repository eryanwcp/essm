<%@ page import="com.eryansky.common.utils.UserAgentUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%
	boolean isMobile = UserAgentUtils.isMobile(request);
	request.setAttribute("isMobile",isMobile);
%>
<script type="text/javascript">
	var url = "${ctxAdmin}/login/welcome";
	if("true" == "${isMobile}"){
		try {
			top.WebViewPlugin.redirectLogin();
		} catch (e) {
			if(e.toString().indexOf("SecurityError")>-1){
				alert("会话已超时，请重新登录应用!");
			}else{
				alert("会话已超时，请关闭APP后，重新登录应用!");
				top.location.href = url;
			}
		}

	}else{
		top.location.href = url;
	}
</script>