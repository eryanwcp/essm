<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/sys/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<!--[if lt IE 7]><html class="ie ie6 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 7]><html class="ie ie7 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 8]><html class="ie ie8 ie-lte9 ie-lte8 no-js"><![endif]-->
<!--[if IE 9]><html class="ie9 ie-lte9 no-js"><![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html class="no-js"> <!--<![endif]-->
<html>
<head>
	<title><sitemesh:title default="${fns:getAppName()}"/></title>
    <%@ include file="/WEB-INF/views/modules/sys/include/head.jsp" %>
    <%@ include file="/WEB-INF/views/include/dialog.jsp" %>

	<sitemesh:head/>
</head>
<body>
<sitemesh:body/>
</body>
</html>