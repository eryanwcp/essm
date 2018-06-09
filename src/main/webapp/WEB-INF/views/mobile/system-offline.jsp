<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html style="background-color: #e6e6e6;">
<head>
    <title>${fns:getAppFullName()}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE;chrome=1"/>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0">
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <link rel="shortcut icon" href="${ctxStatic}/img/favicon.ico"/>

    <%-- 引入jQuery --%>
    <script type="text/javascript" src="${ctxStatic}/js/jquery/jquery-1.10.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${ctxStatic}/js/jquery/jquery-migrate-1.2.1.min.js" charset="utf-8"></script>
    <link href="${ctxStatic}/js/toastr/toastr.min.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/toastr/toastr.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        try {
            toastr.options = {
                "closeButton":true,
                "positionClass": "toast-top-center"}
        } catch (e) {
        }
        $(function(){
            toastr.error("系统正在维护，请稍后再试！!");
        });
    </script>
</head>
<body>
</body>
</html>
