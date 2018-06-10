<%@ page import="com.eryansky.common.utils.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp" %>
<html style="background-color: #e6e6e6;">
<head>
    <title>${fns:getAppFullName()}APP下载</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE;chrome=1"/>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0">
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <link rel="shortcut icon" href="${ctxStatic}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/mobile/af/icons.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/mobile/af/af.ui.css" />

    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/js/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/mobile/af/fastclick.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/mobile/af/appframework.ui.js"></script>

    <script src="${ctxStatic}/js/jquery/qrcode/jquery.qrcode.min.js" type="text/javascript"></script>
    <%
        String versionCode = request.getParameter("versionCode");
        String httpPrefix = request.getScheme()+ "://" + request.getServerName() + ":" + request.getServerPort();
//        String httpPrefix ="https://" + request.getServerName() ;
        String downloadUrl_Android = httpPrefix + request.getContextPath() + AppConstants.getMobilePath() + "/downloadApp/1";
        if(StringUtils.isNotBlank(versionCode)){
            downloadUrl_Android += "?versionCode="+versionCode;
        }
//        String downloadUrl_iPhone = "itms-services://?action=download-manifest&url=https://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + AppConstants.getMobilePath() + "/downloadApp/2";
        String downloadUrl_iPhone = "itms-services://?action=download-manifest&url=https://" + request.getServerName() + request.getContextPath() + AppConstants.getMobilePath() + "/downloadApp/3";
        if(StringUtils.isNotBlank(versionCode)){
            downloadUrl_iPhone += "?versionCode="+versionCode;
        }
        request.setAttribute("httpPrefix", httpPrefix);
        request.setAttribute("downloadUrl_Android", downloadUrl_Android);
        request.setAttribute("downloadUrl_iPhone", downloadUrl_iPhone);
    %>
    <script type="text/javascript">
        $.afui.useOSThemes=false;
        var url = window.location.href;
        $(function () {
            $("#qr").qrcode({
                "render": "div",
                "size": 120,
                mode: 4,
                "text": url
            });

        });
        function weixin(){
            if(isWeiXin()){
                $("#tip_msg").addClass("active");
            }

        }

        function isWeiXin() {
            var ua = window.navigator.userAgent.toLowerCase();
            if (ua.match(/MicroMessenger/i) == 'micromessenger') {
                return true;
            } else {
                return false;
            }
        }
    </script>
    <style type="text/css">
        #loginpanel{
            background: url('${ctxStatic}/img/mobile/download_bg2.png') no-repeat;
            background-size: cover;
            -moz-background-size: cover;
            -o-background-size: cover;
            background-size: cover;
            background-size:100% 100%;
            filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='${ctxStatic}/img/mobile/download_bg2.png', sizingMethod='scale');
        }
        .title{
            text-align: center;
            margin:30px 20px 20px;
            padding-bottom: 20px;
            border-bottom: 1px solid #000;
        }
        #tip_msg{
            width: 100%;
            height: 100%;
            position: absolute;
            top:0;
            left:0;
            z-index: 200;
            background-color: rgba(0,0,0,0.6);
            /*display: none;*/
        }

        #tip_msg table{
            width: 90%;
            height: auto;
            margin:15% auto 0;
            line-height: 35px;
            color: #fff;
            font-weight: bold;
            font-size: large;
        }

        #tip_msg img{
            vertical-align: middle;
        }
        .login{
            display: block;
            border: none;
            border-radius: 5px;
            background: #66d354;
            color: #ffffff;
            font-size: 16px;
            font-weight: normal;
        }
    </style>
</head>
<body>
<div class="view">
    <div class="pages">
        <div class="panel active" id="loginpanel">
            <div class="title"><h2 style="color: #66d354;">${fns:getAppFullName()}APP下载</h2></div>
            <div id="qr" align="center" style="margin: 20px;"></div>
            <div align="center">支持Android4.2+以及iOS8.0+</div>
            <div align="center" style="color: red;">建议使用微信扫一扫
                （<a href="${ctxStatic}/help/江西烟草商业系统一线员工移动工作平台用户手册.pdf" data-ignore="true">用户手册</a>）
            </div>

            <c:if test="${likeIOS == false}">
                <a href="${downloadUrl_Android}" onclick="weixin();" class="button login" data-ignore="true">Android版${not empty model.versionName ? '(V'.concat(model.versionName).concat(')'):''}下载</a>
            </c:if>
            <c:if test="${likeIOS == true}">
                <a href="${downloadUrl_iPhone}" onclick="weixin();" class="button login" data-ignore="true">iOS版${not empty model.versionName ? '(V'.concat(model.versionName).concat(')'):''}下载</a>
            </c:if>
        </div>
        <div class="panel" id="tip_msg">
            <img src="${ctxStatic}/app/mobile/img/close.png" onclick="$('#tip_msg').removeClass('active');" style="position: absolute;bottom:5px;right:5px;"/>
            <table>
                <tr>
                    <td colspan="2">链接打不开？</td>
                </tr>
                <tr>
                    <td><img src="${ctxStatic}/app/mobile/img/number1.png" /></td>
                    <td style="position: relative;">请点击右上角<img src="${ctxStatic}/app/mobile/img/rightup.png" style="position: absolute;right:25px;top:-50px;"/></td>
                </tr>
                <tr>
                    <td><img src="${ctxStatic}/app/mobile/img/number2.png" /></td>
                    <td>选择<span style="color: #ff0000;">“在<c:if test="${likeIOS == true}">Safari</c:if>浏览器中打开”</span></td>
                </tr>
            </table>
        </div>
    </div>
    <footer style="height: 30px;background-color: transparent;border:none;">
        <span style="margin:0 auto;line-height: 30px;text-align: center;">版权所有&copy;XXX科技有限公司</span>
    </footer>
</div>
<iframe id="download_iframe" style="display:none"></iframe>
</body>
</html>
