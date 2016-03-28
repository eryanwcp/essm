<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>后台管理主界面</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
$(function() {
});
function unreadonly(){
	$('#form').form('disable',false); 
}
function readonly(){
	$('#form').form('disable',true); 
}
</script>
</head>
<body>
<a href="#" id="btn" onclick="unreadonly();" class="easyui-linkbutton" iconCls="icon-search">启用</a> 
<a href="#" id="btn" onclick="readonly();" class="easyui-linkbutton" iconCls="icon-search">禁用</a> 
<form id="form" action="">
easyui-validatebox<input class="easyui-validatebox" ><br><br>
easyui-combo<input class="easyui-combo" ><br><br>
easyui-combobox<input class="easyui-combobox" ><br><br>
easyui-combotree<input class="easyui-combotree" ><br><br>
easyui-combogrid<input class="easyui-combogrid" ><br><br>
easyui-numberbox<input class="easyui-numberbox" ><br><br>
easyui-datebox<input class="easyui-datebox" ><br><br>
easyui-datetimebox<input class="easyui-datetimebox" ><br><br>
easyui-spinner<input class="easyui-spinner" ><br><br>
easyui-numberspinner<input class="easyui-numberspinner" ><br><br>
easyui-timespinner<input class="easyui-timespinner" ><br><br>
	整合my97到easyui的日期控件(包含时间选择范围限制):
	<input class="easyui-my97"
		data-options="required:true,missingMessage:'请设置时间...',minDate:'2013-05-01',maxDate:'2013-05-31'">
</form>
</body>
</html>
