<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Access-Control-Allow-Origin" content="*" />
<link rel="shortcut icon" href="${appURL}/static/img/favicon.ico" />

<%-- 引入jQuery --%>
<script type="text/javascript" src="${appURL}/static/js/jquery/jquery-2.1.4.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${appURL}/static/js/jquery/jquery-extend${yuicompressor}.js" charset="utf-8"></script>

<%-- jQuery Cookie插件 --%>
<script type="text/javascript" src="${appURL}/static/js/jquery/jquery.cookie${yuicompressor}.js" charset="utf-8"></script>

<script type="text/javascript" charset="utf-8" src="${appURL}/static/mobile/af/fastclick.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${appURL}/static/mobile/af/appframework.ui.js"></script>
<%--自定义js--%>
<script type="text/javascript" charset="utf-8" src="${appURL}/static/mobile/mobile${yuicompressor}.js?_=${sysInitTime}"></script>

<%--<link rel="stylesheet" type="text/css" href="${appURL}/static/mobile/af/icons.css" />--%>
<link rel="stylesheet" type="text/css" href="${appURL}/static/mobile/af/af.ui.mine.css" />

<%--自定义css--%>
<link rel="stylesheet" type="text/css" href="${appURL}/static/app/mobile/mobile${yuicompressor}.css?_=${sysInitTime}" />
<script type="text/javascript">
    $.afui.useOSThemes=false;
    var ctx = "${ctx}";
    var ctxAdmin = "${ctxAdmin}";
    var ctxFront = "${ctxFront}";
    var ctxStatic = "${ctxStatic}";
    var ctxMobile = "${ctxMobile}";
    var appURL = "${appURL}";
</script>
