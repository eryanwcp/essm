<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${fns:getAppFullName()}</title>
    <%@ include file="/common/meta.jsp"%>
    <%@ include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript" src="${ctxStatic}/js/session-keep.js?_=${sysInitTime}" charset="utf-8"></script>
    <script type="text/javascript" charset="utf-8">
        var indexLayout;
        $(function() {
            indexLayout = $("#indexLayout").layout();
        });
    </script>
</head>
<body id="indexLayout" class="easyui-layout" style="height: 100%;width: 100%;overflow-y: hidden;">
	<noscript>
		<div style="position: absolute; z-index: 100000; height: 2046px; top: 0px; left: 0px; width: 100%; background: white; text-align: center;">
			<img src="${ctxStatic}/img/noscript.gif" alt='请开启脚本支持!' />
		</div>
	</noscript>
	<%-- north顶部Logo区域 --%>
	<div data-options="region:'north',border:false,split:false,href:'${ctxAdmin}/common/layout/north'"
		style="height: 72px;overflow: hidden;">
	</div>

	<%-- west菜单栏 --%>
	<div data-options="region:'west',title:'导航菜单',split:false,href:'${ctxAdmin}/index/west'"
		style="width: 180px;overflow: hidden;">
	</div>
	<%-- center主面板 --%>
	<div data-options="region:'center',split:false,href:'${ctxAdmin}/common/layout/center'" style="overflow: hidden;">
	</div>
	
	<%-- south底部 --%>
	<div data-options="region:'south',border:false,split:false,href:'${ctxAdmin}/common/layout/south'"
		style="height: 20px;overflow: hidden;">
	</div>
</body>
</html>