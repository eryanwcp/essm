<%@ tag language="java" pageEncoding="UTF-8"%>
<%--<c:set var="ctx" value="${pageContext.request.contextPath}" />--%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="name" type="java.lang.String" required="true" description="验证码输入框名称"%>
<%@ attribute name="inputCssStyle" type="java.lang.String" required="false" description="验证框样式"%>
<input type="text" id="${name}" name="${name}" maxlength="5" class="txt required" style="font-weight:bold;width:50px;${inputCssStyle}"/>
<img id="${name}Image" src="${ctx}/servlet/ValidateCodeServlet" onclick="$('.${name}Refresh').click();" class="mid ${name}"/>
<a href="javascript:" onclick="$('.${name}').attr('src','${ctx}/servlet/ValidateCodeServlet?'+new Date().getTime());" class="mid ${name}Refresh">看不清</a>