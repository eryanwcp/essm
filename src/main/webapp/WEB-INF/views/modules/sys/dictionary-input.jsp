<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var modelId = '${model.id}';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/dictionary-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
	<form id="dictionary_form" class="dialog-form" method="post">
		<input type="hidden" id="id" name="id" value="${model.id}" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
	    <div>
			<label>所属分组:</label>
			<input id="_parentId" name="group.id"  value="${model.group.id}" style="width: 120px;height: 28px;"/>
		</div>
		<div>
			<label>字典名称:</label>
			<input type="text" id="name" name="name"  maxLength="20"
				class="easyui-validatebox textbox" value="${model.name}"   placeholder="请输入字典名称..."
				data-options="required:true,missingMessage:'请输入字典名称.'" />
		</div>
		<div>
			<label>字典编码:</label>
			<input type="text" id="code" name="code"
				maxLength="36" class="easyui-validatebox textbox"  value="${model.code}" placeholder="请输入字典编码..."
				data-options="required:true,missingMessage:'请输入字典编码.',validType:['minLength[1]']" />
		</div>
		<div>
			<label>排序:</label>
			<input type="text" id="dictionary_orderNo" name="orderNo" value="${model.orderNo}" class="easyui-numberspinner" style="width: 120px;"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" />
		</div>
        <div>
            <label style="vertical-align: top;">备注:</label>
            <%--<textarea maxLength="255" name="remark"--%>
                      <%--style="position: relative; resize: none; height: 75px; width: 260px;"></textarea>--%>
            <input name="remark" class="easyui-textbox" maxLength="255"  value="${model.remark}" data-options="multiline:true" style="width:260px;height:75px;">
        </div>
	</form>
</div>