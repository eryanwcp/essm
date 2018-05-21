<%@ page import="com.eryansky.utils.AppConstants" %>
<%@ page import="com.eryansky.common.web.utils.WebUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="e" uri="/essm-tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="ctxFront" value="${pageContext.request.contextPath}${fns:getFrontPath()}"/>
<c:set var="ctxAdmin" value="${pageContext.request.contextPath}${fns:getAdminPath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<c:set var="urlSuffix" value="${fns:getUrlSuffix()}"/>
<%
    long sysInitTime = AppConstants.SYS_INIT_TIME;
    //系统启动时间
    request.setAttribute("sysInitTime",sysInitTime);
    if(!AppConstants.isdevMode()){
        request.setAttribute("yuicompressor",".min");
    }
//    request.setAttribute("yuicompressor",".min");
    request.setAttribute("appURL", WebUtils.getAppURL(request));
%>