<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
</script>
<div>
    <form id="config_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" />
        <div>
            <label>属性名称:</label>
            <input name="code" type="text" class="easyui-validatebox textbox"
                   maxLength="100" data-options="required:true,missingMessage:'请输入属性名称.',validType:['minLength[1]']" />
        </div>
        <div>
            <label>属性值:</label>
            <input name="value" type="text" class="easyui-validatebox textbox"
                   maxLength="2048" >
        </div>
        <div>
            <label style="vertical-align: top;">备注:</label>
            <input name="remark" class="easyui-textbox" data-options="multiline:true" style="width:260px;height:100px;">
        </div>
    </form>
</div>