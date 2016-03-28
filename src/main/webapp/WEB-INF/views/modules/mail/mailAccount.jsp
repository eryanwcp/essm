<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="${ctxStatic}/js/easyui-${ev}/datagridview/datagrid-bufferview-min.js?_=${sysInitTime}"></script>
<script type="text/javascript" src="${ctxStatic}/app/modules/mail/mailAccount${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<!-- 中间部分 列表 -->
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="mailAccount_datagrid" ></table>
    </div>
</div>