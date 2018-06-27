<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script src="${ctxStatic}/js/easyui/datagridview/datagrid-bufferview.js"></script>
<script type="text/javascript">
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/session${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<%-- 中间部分 列表 --%>
	<div data-options="region:'center',split:false,border:false"
		 style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
		<table id="session_datagrid"></table>
	</div>
</div>