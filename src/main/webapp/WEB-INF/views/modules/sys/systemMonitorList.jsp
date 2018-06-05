<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统监控</title>
	<meta name="decorator" content="default_sys"/>
	<script type="text/javascript">
		$(function(){
			reLoad();
		});
		function reLoad(){
			$.ajax({
				url: ctxAdmin + '/sys/systemMonitor',
				type: 'get',
				cache:false,
				dataType: 'json',
				success: function (data) {
					if (data.code == 1) {
						var html = Mustache.render($("#systemList").html(),data['obj']);
						$("#systemInfo_div").html(html);
					} else {
						$("#systemInfo_div").html("加载异常");
					}
				}
			});
		}
	</script>
	<script type="text/template" id="systemList">
		<table class="table table-striped table-bordered table-condensed">
			<tbody>
			<tr><td>名称</td><td>{{serverName}}</td></tr>
			<%--<tr><td>IP地址</td><td>{{ip}}</td></tr>--%>
			<tr><td>操作系统</td><td>{{serverOs}}</td></tr>
			<tr><td>服务器时间</td><td>{{serverTime}}</td></tr>
			<%--<tr><td>deployPath</td><td>{{deployPath}}</td></tr>--%>
			<tr><td>javaHome</td><td>{{javaHome}}</td></tr>
			<tr><td>javaVersion</td><td>{{javaVersion}}</td></tr>
			<%--<tr><td>javaServer</td><td>{{javaServer}}</td></tr>--%>
			<tr><td>javaTmpPath</td><td>{{javaTmpPath}}</td></tr>
			<tr><td>jvmTotalMem</td><td>{{jvmTotalMem}} M</td></tr>
			<tr><td>jvmMaxMem</td><td>{{jvmMaxMem}} M</td></tr>
			<tr><td>jvmFreeMem</td><td>{{jvmFreeMem}} M</td></tr>

			<tr><td>内存</td><td>{{usedMem}} M / {{totalMem}}M</td></tr>

			<tr><td>内存交换区</td><td>{{usedSwap}} M / {{totalSwap}} M</td></tr>

			<tr><td>CPU使用率</td><td>{{cpuUsage}}%</td></tr>

			<tr><td>CPU</td><td>
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
					{{#cpuInfos}}
					<tr><td>{{vendor}} {{model}} {{used}}</td></tr>
					{{/cpuInfos}}
					</tbody>
				</table>

			</td></tr>

			<tr><td>磁盘</td><td>
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
					{{#diskInfos}}
					<tr><td>{{dirName}}({{devName}}) {{sysTypeName}} {{usedSize}} G / {{totalSize}} G {{usePercent}}</td></tr>
					{{/diskInfos}}
					</tbody>
				</table>

			</td></tr>


			</tbody>
		</table>
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctxAdmin}/sys/systemMonitor">系统监控</a></li>
	<li><a href="${ctxAdmin}/sys/systemMonitor/log">系统日志</a></li>
</ul>
<form:form id="searchForm" method="post" class="breadcrumb form-search">
	&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="刷新" onclick="reLoad();"/>&nbsp;&nbsp;
	<e:hasPermission name="sys:systemMonitor:edit">
		<a class="btn btn-link" type="button" value="清空缓存" href="${ctxAdmin}/sys/systemMonitor/clearCache"/>&nbsp;&nbsp;
	</e:hasPermission>
</form:form>
<div id="systemInfo_div"></div>
</body>
</html>