<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<link rel="stylesheet" type="text/css" href="${ctxStatic}/app/modules/mail/email${yuicompressor}.css" />
<script type="text/javascript">
    var _mailAccountId = '${mailAccountId}';
    var sessionUserId = '${sessionInfo.userId}';
    var toIds = ${toIds};
    var ccIds = ${ccIds};
    var jsessionid = '<%=session.getId()%>';
    var modelPriority = '${model.priority}';
    var PREFIX_USER = '${PREFIX_USER}';
    var fileSizeLimit = '<%=AppConstants.getPrettyEmailMaxUploadSize()%>';//附件上传大小限制
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/mail/email-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="email_form" class="dialog-form" method="post" novalidate>
        <input type="hidden" name="id" value = "${model.id}"/>
        <input id="fileIds" name="_fileIds" type="hidden" value="${fileIds}"/>

        <input type="hidden" id="version" name="version" value="${model.version}"/>

        <input type="hidden" id="mailAccountId" name="mailAccountId" value="${mailAccountId}"/>
        <%--<div style="clear:both">--%>
            <%--<label>发件账号：</label>--%>
            <%--<input id="mailAccountId" name="mailAccountId" style="width: 260px;height:28px;"/>--%>
        <%--</div>--%>

        <div id="toIds_div">
            <label style="display:block;float:left;">收件人：</label>
            <select id="_toIds" name="toIds" multiple="true"
                    style="width:70%; float:left;margin-left:1px;margin-right:2px;"> </select>
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width: 100px;"
               onclick="selectFormUser();">选择</a>

        </div>
        <div id="ccIds_div" style="clear:both">
            <label style="display:block;float:left;">抄送人：</label>
            <select id="ccIds" name="ccIds" multiple="true"
                    style="width:70%;float:left;margin-left:1px;margin-right:2px;"> </select>
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width: 100px;"
               onclick="selectFormUser('cc');">选择</a>
        </div>
        <div style="clear:both">
            <label>邮件主题：</label>
            <input id="title" name="title" type="text" class="easyui-validatebox textbox" maxLength="125" style="width:70%;"
                   value="${model.title}"
                   data-options="required:true,missingMessage:'请输入邮件主题.',validType:['minLength[1]']" />
            <input id="priority" name="priority" style="width: 100px;height:28px;"/>
        </div>

        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="text-align: left; width: 96px; vertical-align: top;">邮件内容：</td>
                <td><textarea id="editor" name="content">${fns:escapeHtml(model.content)}</textarea></td>
                <%--<td><textarea id="editor" name="content">${model.content}</textarea></td>--%>
            </tr>
        </table>


        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="display: inline-block; text-align: left; width: 96px; vertical-align: top;">附件：</td>
                <td><input id="uploadify" name="file" type="file" multiple="true">

                    <div id="queue"></div>
                    <c:if test="${not empty files}">
                        <div>
                            <c:forEach items="${files}" begin="0" var="fl" varStatus="status">
                                <div id='${fl.id}' style="font-size: 14px;">
                                    <a href="#"  onclick="loadOrOpen('${fl.id}');" style="color: #0000ff;">${fl.name}</a>&nbsp;&nbsp;
                                    <a href="#" onclick="delUpload('${fl.id}');" style="color: red;">删除</a></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>


    </form>
    <iframe id="annexFrame" style="display:none" src=""></iframe>
</div>
<script id="itemTemplate" type="text/x-kendo-template">
    <div class="#:id#">#: name #</div>
</script>