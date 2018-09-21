<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统监控-系统日志</title>
	<meta name="decorator" content="default_sys"/>
	<script type="text/javascript">
		$(function(){
			reLoad();
		});
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            reLoad();
            return false;
        }
		function reLoad(){
			$.ajax({
				url: '${ctxAdmin}/sys/systemMonitor/log',
				type: 'post',
				data:$.serializeObject($("#searchForm")),
				cache:false,
				dataType: 'json',
				success: function (data) {
					if (data.code == 1) {
						$("#systemInfo_div").html(data['msg']);
					} else {
						$("#systemInfo_div").html(data['msg']);
					}
				}
			});
		}
        function _download() {
            var annexFrame = document.getElementById("annexFrame");
            annexFrame.src = '${ctxAdmin}/sys/systemMonitor/log?download=true';
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctxAdmin}/sys/systemMonitor">系统监控</a></li>
	<li class="active"><a href="${ctxAdmin}/sys/systemMonitor/log">系统日志</a></li>
	<li><a href="${ctxAdmin}/sys/systemMonitor/cache">缓存管理</a></li>
</ul>
<form:form id="searchForm" method="post" class="breadcrumb form-search">
	&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="刷新" onclick="reLoad();"/>&nbsp;&nbsp;
	&nbsp;&nbsp;<input class="btn btn-primary" type="button" value="下载" onclick="_download();"/>&nbsp;&nbsp;
</form:form>
<div id="systemInfo_div" style="padding: 10px;"></div>
<iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 100%;height: 50px;"></iframe>
</body>
</html>
