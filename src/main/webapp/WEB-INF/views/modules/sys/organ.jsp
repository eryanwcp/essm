<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/organ${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<%-- 列表右键 --%>
<div id="organ_menu" class="easyui-menu" style="width:120px;display: none;">
    <div onclick="showDialog();" data-options="iconCls:'easyui-icon-add'">新增</div>
    <div onclick="edit();" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div onclick="del();" data-options="iconCls:'easyui-icon-remove'">删除</div>
    <div onclick="editOrganUser();" data-options="iconCls:'eu-icon-user'">设置用户</div>
</div>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <%-- 中间部分 列表 --%>
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="organ_treegrid"></table>

    </div>
</div>