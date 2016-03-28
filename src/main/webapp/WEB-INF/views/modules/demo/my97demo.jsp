<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>后台管理主界面</title>
<%@ include file="/common/meta.jsp"%>
<%--<script type="text/javascript">--%>
<%--//$(function() {--%>
<%--//	$.parser.parse();--%>
<%--//});--%>
<%--</script>--%>
</head>
<body>
	<!-- 整合my97作为easyui插件 -->
	easyui日期:<br>
	<input type="text" class="easyui-datebox" validateType="isAfter['@today','Today']" >
		<br><br>
	整合my97到easyui的日期控件(包含时间选择范围限制):<br>
	<input type="text" id="date-first" class="easyui-my97" validateType="isAfter['@today','Today']"
		data-options="required:true,missingMessage:'请设置时间...'">
    <br/>

    <input type="text" class="easyui-my97"
           data-options="required:true,missingMessage:'请设置时间...',validateType:'isAfter[\'#date-first\',\'Date first\']'">

</body>
</html>
