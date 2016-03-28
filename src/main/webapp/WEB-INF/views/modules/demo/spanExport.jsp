<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>普通Excel导出</title>
    <%@ include file="/common/meta.jsp"%>
	<script>
		$(function(){
			$('#test').datagrid({
				title:'示例数据',
				iconCls:'icon-save',
				width:700,
				height:350,
				nowrap: false,
				striped: true,
				collapsible:true,
				url:'${ctx}/sys/demo/export/loadData',
				idField:'code',
				columns:[[
	                {title:'编号',field:'code',width:50, rowspan:2},
			        {title:'基本信息',colspan:3},
					{field:'addr',title:'地址',width:150,align:'center', rowspan:2}
				],[
					{field:'name',title:'姓名',width:100},
					{field:'sex',title:'性别',width:80,rowspan:2},
					{field:'blood',title:'血型',width:100,rowspan:2}
				]],
				toolbar:[{
					id:'btnsave',
					text:'导出',
					iconCls:'icon-save',
					handler:function(){
						var url = "${ctx}/sys/demo/export/spanExport";
						window.location.href = url;
					}
				}]
			});
		});
	</script>
</head>
<body>
	<h2>普通Excel导出示例</h2>
	<table id="test"></table>
</body>
</html>