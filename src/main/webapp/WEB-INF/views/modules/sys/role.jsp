<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/role${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<%-- 列表右键 --%>
<div id="role_datagrid_menu" class="easyui-menu" style="width:120px;display: none;">
	<div onclick="showDialog();" data-options="iconCls:'easyui-icon-add'">新增</div>
	<div onclick="edit();" data-options="iconCls:'easyui-icon-edit'">编辑</div>
	<div onclick="del();" data-options="iconCls:'easyui-icon-remove'">删除</div>
    <div onclick="editRoleResource();" data-options="iconCls:'eu-icon-folder'">设置资源</div>
    <div onclick="editRoleUser();" data-options="iconCls:'eu-icon-user'">设置用户</div>
</div>

<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
         style="padding: 0px; height: 70px;width:100%; overflow-y: hidden;">
        <form id="role_search_form" style="padding: 5px;">
            &nbsp;角色名称: &nbsp;<input type="text" class="easyui-validatebox textbox eu-input" name="filter_LIKES_name" placeholder="请输入角色名称..."
                              onkeydown="if(event.keyCode==13)search()"  maxLength="25" style="width: 160px" />
            &nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28,onClick:search">查询</a>
            <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:role_search_form.form('reset');">重置查询</a>
        </form>
    </div>
    <%-- 中间部分 列表 --%>
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="role_datagrid"></table>
    </div>
</div>


   