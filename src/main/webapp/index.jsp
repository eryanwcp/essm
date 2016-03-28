<%@ page import="com.eryansky.utils.AppConstants" %>
<%
    String ctx = request.getContextPath();
    String adminPath = AppConstants.getAdminPath();
    String frontPath = AppConstants.getFrontPath();
//    response.sendRedirect(ctx + adminPath + "/login/welcome");
    response.sendRedirect(ctx + adminPath);
%>