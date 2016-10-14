<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta name="author" content="锦峰软件"/>
<link rel="shortcut icon" href="${ctxStatic}/img/favicon.ico" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<c:if test="${not empty yuicompressor}">
    <script src="${ctxStatic}/js/all.js" type="text/javascript"></script>
    <link href="${ctxStatic}/css/all.css" type="text/css" rel="stylesheet" />
</c:if>
<c:if test="${empty yuicompressor}">
    <script src="${ctxStatic}/js/jquery/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/js/jquery/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <link href="${ctxStatic}/js/jquery-validation/1.11.1/jquery.validate.min.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/jquery-validation/1.11.1/jquery.validate.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/js/jquery-validation/1.11.1/jquery.validate.method.min.js" type="text/javascript"></script>
    <link href="${ctxStatic}/js/bootstrap/2.3.2/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
    <link href="${ctxStatic}/js/bootstrap/2.3.2/css/bootstrap-responsive.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/bootstrap/2.3.2/js/bootstrap.min.js" type="text/javascript"></script>
    <!--[if lte IE 6]><link href="${ctxStatic}/js/bootstrap/bsie/css/bootstrap-ie6.min.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/bootstrap/bsie/js/bootstrap-ie.min.js" type="text/javascript"></script><![endif]-->
    <script src="${ctxStatic}/js/select2/select2.js" type="text/javascript"></script>
    <script src="${ctxStatic}/js/select2/select2_locale_zh-CN.js" type="text/javascript"></script>
    <link href="${ctxStatic}/js/select2/select2.css" type="text/css" rel="stylesheet" />
    <link href="${ctxStatic}/js/common/common.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/common/common.js" type="text/javascript"></script>
    <script src="${ctxStatic}/js/common/mustache.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <link href="${ctxStatic}/js/fancyBox/source/jquery.fancybox.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/fancyBox/source/jquery.fancybox.js" type="text/javascript"></script>

    <%--兼容区--%>
    <!--[if IE 7]>
    <link href="${ctxStatic}/img/jfit_css/font-awesome-ie7.min.css" rel="stylesheet" type="text/css" />
    <![endif]-->
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <link href="${ctxStatic}/img/jfit_css/bootstrap-ie78.css" rel="stylesheet" type="text/css" />
    <script src="${ctxStatic}/img/jfit_css/respond.src.js" type="text/javascript"></script>
    <script src="${ctxStatic}/img/jfit_css/html5.js" type="text/javascript"></script>
    <![endif]-->

    <script src="${ctxStatic}/js/bootstrap/dropdown/bootstrap-hover-dropdown.js" type="text/javascript"></script>
    <!--[if lt IE 7 ]> <script src="${ctxStatic}/js/dd_belatedpng.js"></script> <script> DD_belatedPNG.fix('img, .png_bg'); //fix any <img> or .png_bg background-images </script> <![endif]-->

</c:if>
<script type="text/javascript">
    var ctx = "${ctx}";
    var ctxAdmin = "${ctxAdmin}";
    var ctxFront = "${ctxFront}";
    var ctxStatic = "${ctxStatic}";
    var appURL = "${appURL}";
</script>
