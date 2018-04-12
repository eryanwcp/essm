<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<meta name="decorator" content="default_sys"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "${rootId}";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 3});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if (row['parentId'] == pid){
					$(list).append(Mustache.render(tpl, {pid: (root?0:pid), row: row}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctxAdmin}/sys/area">区域列表</a></li>
		<e:hasPermission name="sys:area:edit"><li><a href="${ctxAdmin}/sys/area/form">区域添加</a></li></e:hasPermission>
	</ul>
	<tags:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>区域名称</th><th>区域编码</th><th>区域类型</th><th>备注</th><e:hasPermission name="sys:area:edit"><th>操作</th></e:hasPermission></tr></thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctxAdmin}/sys/area?parent.id={{row.id}}">{{row.name}}</a></td>
			<td>{{row.code}}</td>
			<td>{{row.typeView}}</td>
			<td>{{row.remark}}</td>
			<e:hasPermission name="sys:area:edit"><td>
				<a href="${ctxAdmin}/sys/area/form?id={{row.id}}">修改</a>
				<a href="${ctxAdmin}/sys/area/delete?id={{row.id}}" onclick="return confirmx('要删除该区域及所有子区域项吗？', this.href)">删除</a>
				<a href="${ctxAdmin}/sys/area/form?parent.id={{row.id}}">添加下级区域</a>
			</td></e:hasPermission>
		</tr>
	</script>
</body>
</html>