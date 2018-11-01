<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/session${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
		 style="padding: 0px; height: 70px;width:100%; overflow-y: hidden;">
		<form id="session_search_form" style="padding: 5px;">
			&nbsp;关键字: &nbsp;<input type="text" class="easyui-validatebox textbox eu-input" name="query" placeholder="请输入关键字..."
									onkeydown="if(event.keyCode==13)search()"  maxLength="36" style="width: 160px" />
			&nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28,onClick:search">查询</a>
			<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:$session_search_form.form('reset');">重置查询</a>
		</form>
	</div>
	<%-- 中间部分 列表 --%>
	<div data-options="region:'center',split:false,border:false"
		 style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
		<table id="session_datagrid"></table>
	</div>
</div>