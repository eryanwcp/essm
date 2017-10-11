<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctxStatic}/js/bootstrap/2.3.2/custom/bootstrap-img.css" />
<script type="text/javascript" src="${ctxStatic}/app/layout/west${yuicompressor}.js" charset="utf-8"></script>
<!-- 导航菜单 -->
<%--<div class="easyui-accordion" data-options="animate:false,fit:true,border:true" style="overflow: auto">--%>
    <%--<ul id="menu_tree"></ul>--%>
<%--</div>--%>
<div id="tt" class="easyui-tabs" data-options="fit:true,tabHeight:36,tabWidth:76" style="width:100%;padding: 0px;">
    <div data-options="title:'菜单',iconCls: 'eu-icon-folder'" style="overflow:auto;padding:10px;">
        <ul id="menu_tree"></ul>
    </div>
    <div data-options="title:'我&nbsp;',iconCls: 'eu-icon-user'" style="padding:10px;">
        <img src="${userPhoto}" class="img-circle" style="width: 48px;height: 48px;">
        欢迎您，<strong>${user.name}</strong>！
    </div>
</div>