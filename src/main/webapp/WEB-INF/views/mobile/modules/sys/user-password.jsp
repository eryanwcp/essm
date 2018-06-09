<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/meta_mobile.jsp" %>
<!DOCTYPE html>
<html>

<script type="text/javascript">
    var activate='${model.activate}';
    var receiverEncryption='${model.receiverEncryptionType}';
    var senderEncryption='${model.senderEncryptionType}';
    var receiverProtocol='${model.receiverProtocol}';
    var id='${model.id}';
</script>

<head>
    <title>用户密码修改</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/app/mobile/modules/mail/email.mobile${yuicompressor}.css" />
    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/app/mobile/modules/mail/mailAccount-input.mobile${yuicompressor}.js?_=${sysInitTime}"></script>
    <style type="text/css">
        .error { box-shadow: 0 0 10px #ff4136; }
    </style>
</head>
<body>
<div class="view">
    <header>
        <span class="button yellow header_span_right" onclick="validate('Save')">保存</span>
    </header>
    <div class="pages">
        <div class="panel">
            <form id="mailAccount_form">
                <input type="hidden" id="id" name="id" value = "${model.id}"/>
                <div class="dinput-group">
                    <i>原始密码：</i>
                    <input type="email" id="password" name="password"  required/>
                </div>
                <div class="dinput-group">
                    <i>新密码：</i>
                    <input type="email" id="newPassword" name="newPassword" required/>
                </div>
                <div class="dinput-group">
                    <i>确认新密码：</i>
                    <input type="text"  name="newPassword2" required/>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript" charset="utf-8" src="${ctxStatic}/mobile/jquery-html5Validate.js"></script>
