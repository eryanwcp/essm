<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<meta name="decorator" content="default_sys"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctxAdmin}/sys/area/">区域列表</a></li>
		<li class="active"><a href="form?id=${area.id}&parent.id=${area.parent.id}">区域<e:hasPermission name="sys:area:edit">${not empty area.id?'修改':'添加'}</e:hasPermission><e:lacksPermission name="sys:area:edit">查看</e:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="area" action="${ctxAdmin}/sys/area/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级区域:</label>
			<div class="controls">
				<tags:treeselect id="area" name="parent.id" value="${area.parent.id}" labelName="parent.name" labelValue="${area.parent.name}"
					title="区域" url="${ctxAdmin}/sys/area/treeData" extId="${area.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区域名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="128" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
				<form:select path="type" class="input-medium">
					<form:options items="${fns:getDictList('sys_area_type')}" itemLabel="name" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区域简称:</label>
			<div class="controls">
				<form:input path="shortName" htmlEscape="false" maxlength="128" class="input-xxlarge"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区域编码:</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="64" class="input-xxlarge required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="3" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<e:hasPermission name="sys:area:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</e:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>