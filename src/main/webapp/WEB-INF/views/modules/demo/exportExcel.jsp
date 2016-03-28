<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>普通表格导出Demo</title>
        <%@ include file="/common/meta.jsp"%>
	<script>
		$(function(){
			$('#text').datagrid({
				toolbar:[{
					text:'导出',
					iconCls:'icon-save',
					handler:function(){
						var url = "${ctx}/sys/demo/export/exportExcel";
						window.location.href = url;
					}
				}]
			});
		});
	</script>
</head>
<body>
	<h2>普通Excel导出示例</h2>
	<table id="text" style="width:700px;height:350px"
			title="示例数据" iconCls="icon-edit" singleSelect="true"
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