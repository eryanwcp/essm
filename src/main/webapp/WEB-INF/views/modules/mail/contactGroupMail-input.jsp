<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    $(function(){
    });
</script>
<div>
    <form id="contactGroupMail_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}" />
        <input type="hidden"  name="contactGroupId" value="${contactGroupId}"/>
        <div>
            <label>名称：</label>
            <input name="name" id="mname" type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="20" data-options="required:true,missingMessage:'请输入名称.',validType:['minLength[1]']">
        </div>
        <div>
            <label style="vertical-align: top;">邮箱：</label>
            <input name="email" id="memail" value="${model.email}" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入邮箱地址.',validType:['email']">
        </div>
        <div>
            <label style="vertical-align: top;">电话：</label>
            <input name="mobile" value="${model.mobile}" class="easyui-validatebox textbox" />
        </div>
        <div>
            <label style="vertical-align: top;">备注：</label>
            <%--<textarea maxLength="255" name="remark"--%>
            <%--style="position: relative; resize: none; height: 75px; width: 260px;">${model.remark}</textarea>--%>
            <input name="remark" value="${model.remark}" class="easyui-textbox"
                   data-options="multiline:true" style="width:260px;height:100px;">
        </div>
    </form>
</div>