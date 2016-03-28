<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/kendoui.jsp"%>
<%@ include file="/common/uploadify.jsp"%>
<script type="text/javascript">
    var emailId = "${emailId}";
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/mail/email${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div id="menu_operate" style="width:150px;">
    <div onclick="receiveMail();"
         data-options="iconCls:''">收取</div>
    <div onclick="javascript:eu.addTab(window.parent.layout_center_tabs, '邮箱账号','${ctxAdmin}/mail/mailAccount', true,'eu-icon-user','',false);"
         data-options="iconCls:'eu-icon-user'">邮箱账号</div>
    <div onclick="javascript:eu.addTab(window.parent.layout_center_tabs, '我的联系人','${ctxAdmin}/mail/contactGroup', true,'eu-icon-user','',false);"
         data-options="iconCls:'eu-icon-user'">我的联系人</div>
    <e:hasPermission name="mail:manager">
        <div onclick="javascript:eu.addTab(window.parent.layout_center_tabs, '邮件管理','${ctxAdmin}/mail/email/manager', true,'eu-icon-mail_emails','',false);"
             data-options="iconCls:'eu-icon-mail_emails'">邮件管理</div>
    </e:hasPermission>


</div>
<div id="menu_mailAccount" style="width:160px;">
    <div onclick="toggleMailAccount('','全部邮箱')">全部邮箱</div>
    <div onclick="toggleMailAccount('1','站内邮箱')">站内邮箱</div>
    <c:forEach items="${mailAccounts}" var="mailAccount">
        <c:set var="displayName" value="${not empty mailAccount.name ? mailAccount.name:mailAccount.username}"></c:set>
        <div onclick="toggleMailAccount('${mailAccount.id}','${fns:abbr(displayName,11)}')">${fns:abbr(displayName,21)}</div>
    </c:forEach>
</div>
<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true"
	style="margin: 0px; border: 0px; overflow: hidden; width: 100%; height: 100%;">
	<%-- 左边部分 菜单树形 --%>
	<div data-options="region:'west',title:'邮箱管理',split:true,collapsed:false,border:false"
		style="width: 200px; text-align: left; padding: 2px;">
        <a href="#" class="easyui-splitbutton" data-options="menu:'#menu_operate',iconCls:'easyui-icon-edit'">菜单</a>
        <a id="btn_mailAccount" href="#" class="easyui-menubutton" data-options="menu:'#menu_mailAccount',iconCls:'eu-icon-user'">全部邮箱</a>
		<br>
		<br>
        <div style="padding: 5px;">
			<a onclick="initEmailDatagrid('UnreadInbox');" class="easyui-linkbutton"
				data-options="iconCls:'eu-icon-mail_unread',toggle:true,group:'mail_main',selected:true"
				style="width: 76px;">未读</a>
            <a onclick="edit();" class="easyui-linkbutton"
				data-options="iconCls:'eu-icon-mail_email_edit',toggle:true,group:'mail_main'"
				style="width: 76px;">写信</a>
		</div>
		<hr>

		<div style="padding: 5px 20px 0px 20px;">
			<a id="inbox_linkbutton" href="#" class="easyui-linkbutton" onclick="initEmailDatagrid('Inbox')"
				data-options="iconCls:'eu-icon-mail_reply',toggle:true,group:'mail'"
				style="width: 130px">收件箱</a><br />
            <a href="#" class="easyui-linkbutton" onclick="initEmailDatagrid('Outbox')"
				data-options="iconCls:'eu-icon-mail_send',toggle:true,group:'mail'"
				style="width: 130px; margin-top: 5px;">发件箱</a><br />
            <a href="#" class="easyui-linkbutton" onclick="initEmailDatagrid('Draftbox')"
				data-options="iconCls:'eu-icon-mail_purple',toggle:true,group:'mail'"
				style="width: 130px; margin-top: 5px;">草稿箱</a><br />
            <a href="#"
				class="easyui-linkbutton" onclick="initEmailDatagrid('RecycleBin')"
				data-options="iconCls:'eu-icon-mail_trash',toggle:true,group:'mail'"
				style="width: 130px; margin-top: 5px;">垃圾箱</a>
		</div>

	</div>


	<!-- 中间部分 列表 -->
	<div data-options="region:'center',split:true"
		style="overflow: hidden;">
		<div class="easyui-layout" fit="true"
			style="margin: 0px; border: 0px; overflow: hidden; width: 100%; height: 100%;">
            <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
                    style="width: 100%; height: 136px;">
                <form id="email_search_form" style="padding: 5px;">
                    <table style="border: 0">
                        <tr>
                            <td>邮件主题：</td>
                            <td><input type="text" id="title"  name="title" placeholder="邮件主题" class="easyui-validatebox textbox eu-input" maxLength="25" style="width: 260px" />
                            </td>
                            <!-- <td>邮件内容：</td>
                            <td><input type="text" name="emailContent" maxLength="25"
                                style="width: 260px" id="emailContent" />
                            </td> -->
                            <td>&nbsp;</td>
                            <td>日&nbsp;&nbsp;期：</td>
                            <td>
                                <input id="startTime" name="startTime" placeholder="起始时间" type="text" class="easyui-my97" style="width: 120px;">
                                ～
                                <input id="endTime" name="endTime" placeholder="截止时间"  type="text" class="easyui-my97" style="width: 120px;">
                            </td>
                        </tr>

                        <tr id="recv">
                            <td id="sendObjectIds_label">发件人：</td>
                            <td id="sendObjectIds_input">
                                <select id="sendObjectIds" name="sendObjectIds" style="width: 260px;"></select>
                            </td>
                            <td id="sendObjectIds_op_input">
                                &nbsp;
                                <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'"  onclick="selectSendUser();">选择</a>--%>
                            </td>
                            <td id="receiveObjectIds_label">收件人：</td>
                            <td id="receiveObjectIds_input">
                                <select id="receiveObjectIds" name="receiveObjectIds" style="width: 260px;"></select>
                            </td>
                            <td id="receiveObjectIds_op_input">
                                &nbsp;
                                <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" onclick="selectPublishUser();">选择</a>--%>
                            </td>
                            <td id="sta">邮件状态：</td>
                            <td id="sta1">
                                <input type="text" name="inboxReadStatus" class="easyui-combobox" style="width: 100px;height: 28px;"
                                       data-options="editable:false,valueField: 'value',displayField: 'text',url: '${ctxAdmin}/mail/email/emailReadStatusCombobox?selectType=all'" />
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
                                <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no'"
                                   onclick="javascript:resetSearchForm();">重置查询</a>
                            </td>
                        </tr>

                    </table>
                </form>
            </div>

			<div data-options="region:'center',split:true"
				style="overflow: hidden;">
				<table id="email_datagrid"></table>
			</div>
		</div>
	</div>
</div>