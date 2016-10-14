<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/sys/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<!--[if lt IE 7]><html class="ie ie6 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 7]><html class="ie ie7 ie-lte9 ie-lte8 ie-lte7 no-js"><![endif]-->
<!--[if IE 8]><html class="ie ie8 ie-lte9 ie-lte8 no-js"><![endif]-->
<!--[if IE 9]><html class="ie9 ie-lte9 no-js"><![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html class="no-js"> <!--<![endif]-->
<html style="background-color: #e6e6e6;">
<head>
    <title><sitemesh:title default="${fns:getAppName()}"/></title>
    <%@ include file="/WEB-INF/views/modules/sys/include/head.jsp" %>
    <%@ include file="/WEB-INF/views/include/dialog.jsp" %>
    <script type="text/javascript" src="${ctxStatic}/js/fancyBox/source/jquery.fancybox.pack.js?v=2.1.5"></script>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/js/fancyBox/source/jquery.fancybox.css?v=2.1.5" media="screen" />
    <link href="${ctxStatic}/css/mobile${yuicompressor}.css?_=${sysInitTime}" type="text/css" rel="stylesheet" />
    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/mobile/mobile.js"></script>
    <meta name="viewport" id="viewport" content="width=device-width, initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
    <sitemesh:head/>
</head>
<body style="background-color: #e6e6e6;">
<sitemesh:body/>
</body>
</html>