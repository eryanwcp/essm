<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<style>
    label{display:inline-block;padding:10px 0px 0px;width:150px;}
    input{width:250px;}
</style>
<script type="text/javascript">
    var modelId = '${model.id}';
    var modelReceiverEncryptionType = '${model.receiverEncryptionType}';
    var modelSenderEncryptionType = '${model.senderEncryptionType}';
    var modelActivate = '${model.activate}';
    var modelReceiverProtocol = '${model.receiverProtocol}';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/mail/mailAccount-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="mailAccount_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}" />
        <div>
            <label>邮箱地址：</label>
            <input id="mailAddress" name="mailAddress" style="width:350px;" type="text" value="${model.mailAddress}"class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入邮箱地址.',validType:['email']" onchange="onChange ()">
        </div>
        <div>
            <label>邮箱账号：</label>
            <input name="username" style="width:350px;" value="${model.username}"  class="easyui-validatebox textbox"/>
        </div>
        <div>
            <label>邮箱密码：</label>
            <input name="password" style="width:350px;"  value="${model.password}" type="password" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入邮箱密码.'">
        </div>

        <div>
            <label>显示名称：</label>
            <input name="name" style="width:350px;" maxlength="20" value="${model.name}" class="easyui-validatebox textbox"/>
        </div>

        <div>
            <label >邮箱类型：</label>
            <select id="receiverProtocol" name='receiverProtocol' style="width:80px" class="easyui-combobox">
                <option value ="IMAP">IMAP</option>
                <option value ="POP3">POP3</option>
            </select>
        </div>
        <div  style="float:left">
            <label>收件服务器：</label>
            <input name="receiverAddress" style="width:170px;" type="text" value="${model.receiverAddress}" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入接收服务器地址.'"/>
        </div>
        <div style="float:left">
            <label style="text-align: left;width: 50px; padding-left:10px;">
                <input type="hidden" name="receiverEncryptionType" value="${model.receiverEncryptionType}">
                <input type="checkbox" onclick="getReceiverEncryptionType();" id="receiverEncryptionType" style="width: 15px;"/>SSL
            </label>
        </div>
        <div>
            <label style="width:45px; padding-left:10px; ">端口：</label>
            <input name="receiverPort" style="width:60px;" value="${model.receiverPort}"
                   data-options="required:true,missingMessage:'请输入端口号.',validType:['number']"
                   class="easyui-validatebox textbox"/></div>

        <div style="float:left">
            <label>发件服务器：</label>
            <input name="senderAddress" type="text" style="width:170px;" value="${model.senderAddress}" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入接收服务器地址.'"/>
        </div>
        <div style="float:left">
            <label style="text-align: left;width: 50px; padding-left:10px;">
                <input type="hidden" name="senderEncryptionType" value="${model.senderEncryptionType}">
                <input type="checkbox" onclick="getSenderEncryptionType();" id="senderEncryptionType" style="width: 15px;" value="0"/>SSL
            </label>
        </div>
        <div>
            <label style="width:45px;padding-left:10px;" >端口：</label>
            <input name="senderPort"style="width:60px;"value="${model.senderPort}"
                   data-options="required:true,missingMessage:'请输入端口号.',validType:['number']"
                   class="easyui-validatebox textbox"/>
        </div>

        <div>
            <label>账号活动：</label>
            <label style="text-align: left;width: 200px;">
                <label>
                    <input type="radio" name="activate" style="width: 20px;" value="1"/>是
                </label><label>
                    <input type="radio" name="activate" style="width: 20px;" value="0"/> 否
                </label>
            </label>
        </div>
    </form>
</div>