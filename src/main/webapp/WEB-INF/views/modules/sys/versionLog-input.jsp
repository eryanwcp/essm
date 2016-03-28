<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/uploadify.jsp"%>

<script type="text/javascript">
    var jsessionid = '<%=session.getId()%>';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/versionLog-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="versionLog_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" />
        <div>
            <label>版本号:</label>
            <input type="text" name="versionName"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="版本号"
                   data-options="required:true,missingMessage:'请输入版本号....',validType:['minLength[1]']" />
        </div>
        <div>
            <label>内部编号:</label>
            <input type="text" name="versionCode"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="内部编号"
                   data-options="required:true,missingMessage:'请输入内部编号....',validType:['minLength[1]']" />
        </div>
        <div>
            <label>类型:</label>
            <input id="versionLogType" name="versionLogType" style="height: 28px;" /></div>
        <div>
            <label style="vertical-align: top;">更新说明:</label>
            <input name="remark" class="easyui-textbox" data-options="multiline:true" placeholder="更新说明" style="width:420px;height:200px;">
        </div>
        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="display: inline-block; width: 96px; vertical-align: top;">附件：</td>
                <td><input id="uploadify" name="file" type="file" multiple="true">

                    <div id="queue"></div>
                    <input id="fileId" name="fileId" type="hidden" />
                </td>
            </tr>
        </table>
    </form>
</div>