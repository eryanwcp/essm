<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<style type="text/css">
.title{
	font-size:16px;
	font-weight:bold;
	padding:20px 10px;
	background:#eee;
	overflow:hidden;
	border-bottom:1px solid #ccc;
}
</style>
<script src="${ctxStatic}/js/easyui/datagridview/datagrid-bufferview.js"></script>
<link href="${ctxStatic}/js/bootstrap/2.3.2/custom/bootstrap.css" rel="stylesheet">
<script type="text/javascript" src="${ctxStatic}/js/jquery/jplayer/jquery.jplayer.min.js?_=${sysInitTime}" charset="utf-8"></script>
<script type="text/javascript" src="${ctxStatic}/app/layout/portal${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div class="easyui-layout" fit="true" style="width:100%;height:100%;overflow: hidden;">
	<div region="center" style="height: 100%;overflow: hidden;">
        <div class="easyui-layout" fit="true" style="width:100%;height:100%;overflow: hidden;">
            <div region="center" style="height: 100%;overflow: hidden;">
                <div id="layout_portal_portal"
                     style="position: relative;">
                    <div></div>
                    <div></div>
                </div>
            </div>
        </div>
	</div>
	<div  style="height: 100%;width:200px;overflow: hidden;"
          data-options="region:'east',title:'日历',href:'${ctxAdmin}/common/layout/portal-east'">
	</div>
</div>
<div id="jplayer"><embed id="tip_new_msg" height="100" autostart="true" width="100" src="" /></div>
