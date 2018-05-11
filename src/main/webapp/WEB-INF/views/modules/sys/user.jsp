<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/autocomplete.jsp"%>
<%@ include file="/common/uploadify.jsp"%>
<link href="${ctxStatic}/js/bootstrap/2.3.2/custom/bootstrap-img.css" rel="stylesheet" type="text/css"/>
<%--<%@ include file="/common/zTree.jsp"%>--%>
<script type="text/javascript">
    var sessionInfoUserId = "${sessionInfo.userId}";//当前的登录用户ID
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<%-- 列表右键 --%>
<div id="user_datagrid_menu" class="easyui-menu" style="width:120px;display: none;">
    <div onclick="showDialog();" data-options="iconCls:'easyui-icon-add'">新增</div>
    <div onclick="edit();" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div onclick="del();" data-options="iconCls:'easyui-icon-remove'">删除</div>
    <div onclick="editPassword();" data-options="iconCls:'eu-icon-lock'">修改密码</div>
    <div onclick="editUserOrgan();" data-options="iconCls:'eu-icon-group'">设置机构</div>
    <div onclick="editUserPost();" data-options="iconCls:'eu-icon-group'">设置岗位</div>
    <div onclick="editUserRole();" data-options="iconCls:'eu-icon-group'">设置角色</div>
    <div onclick="editUserResource();" data-options="iconCls:'eu-icon-folder'">设置资源</div>
</div>
<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">

    <%-- 左边部分 菜单树形 --%>
    <div data-options="region:'west',title:'组织机构列表',split:false,collapsed:false,border:false"
         style="width: 180px; text-align: left;padding:5px;">
        <ul id="organ_tree"></ul>
    </div>

    <!-- 中间部分 列表 -->
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
            <div data-options="region:'center',split:true" style="overflow: hidden;">
                <table id="user_datagrid" ></table>
            </div>

            <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
                 style="width: 100%;height:70px;overflow-y: hidden; ">
                <form id="user_search_form" style="padding: 5px;">
                    &nbsp;关键字:<input type="text" id="query" name="query"
                                        class="easyui-validatebox textbox eu-input" placeholder="关键字..."
                                        onkeydown="if(event.keyCode==13)search()" maxLength="25" style="width: 160px"/>
                    &nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28,onClick:search">查询</a>
                    <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:$user_search_form.form('reset');">重置查询</a>
                </form>
            </div>
        </div>
    </div>
</div>