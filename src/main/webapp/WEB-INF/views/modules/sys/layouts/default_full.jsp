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
    <link id="easyuiTheme" rel="stylesheet" type="text/css" href="${ctxStatic}/js/easyui/themes/<c:out value="${cookie.easyuiThemeName.value}" default="bootstrap"/>/easyui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/js/easyui/extend/icon/easyui-icon${yuicompressor}.css" />
    <script type="text/javascript" src="${ctxStatic}/js/easyui/jquery.easyui.mine${yuicompressor}.js" charset="utf-8"></script>
    <script type="text/javascript" src="${ctxStatic}/js/easyui/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>

    <link rel="stylesheet" type="text/css" href="${ctxStatic}/js/easyui/extend/my97/my97${yuicompressor}.css" />
    <script type="text/javascript" src="${ctxStatic}/js/easyui/extend/my97/jquery.easyui.my97${yuicompressor}.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/js/easyui/extend/icon/eu-icon${yuicompressor}.css?_=${sysInitTime}" />
    <%-- easyui自定义表单校验扩展 --%>
    <script type="text/javascript" src="${ctxStatic}/js/easyui/extend/js/validatebox-extend${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
	<sitemesh:head/>
</head>
<body>
<sitemesh:body/>
</body>
</html>