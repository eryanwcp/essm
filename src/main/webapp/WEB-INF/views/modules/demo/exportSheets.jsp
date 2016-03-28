<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>多Sheet Excel导出示例</title>
    <%@ include file="/common/meta.jsp"%>
	<script>
		$(function(){
			$('#grid1').datagrid({
				title:'Sheet-1数据',
				iconCls:'icon-save',
				width:700,
				height:250,
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
				]]
			});
		});
		function exportToExcel(){
			var url = "${ctx}/sys/demo/export/sheetsExport";
			window.location.href = url;
		}
	</script>
</head>
<body>
	<h2>多Sheet导出示例</h2>
	点击按钮导出以下两个表格的数据到不同的Sheet：<a href="#" onclick="javascript:exportToExcel()" class="easyui-linkbutton" iconCls="icon-save">导出</a>
	<br><br>
	<table id="grid1"></table>
	<table id="grid2" class="easyui-datagrid" style="width:700px;height:250px"
			title="Sheet-2数据" iconCls="icon-edit" singleSelect="true"
			idField="code" url="${ctx}/sys/demo/export/loadData">
		<thead>
			<tr>
				<th field="code" width="80">编号</th>
				<th field="name" width="100" >姓名</th>
				<th field="sex" width="80" align="right" >性别</th>
				<th field="blood" width="80" align="right" >血型</th>
				<th field="addr" width="250" >地址</th>
			</tr>
		</thead>
	</table>
	
</body>
</html>