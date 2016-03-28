<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="${ctxStatic}/js/easyui-${ev}/datagridview/datagrid-bufferview-min.js?_=${sysInitTime}"></script>
<script type="text/javascript" src="${ctxStatic}/app/modules/mail/contactGroup${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div id="treeMenu" class="easyui-menu" style="width:120px;">
    <div name="edit" data-options="iconCls:'easyui-icon-edit'">编辑</div>
    <div name="delete" data-options="iconCls:'easyui-icon-remove'">删除</div>
</div>
<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">

    <%-- 左边部分 菜单树形 --%>
    <div data-options="region:'west',title:'联系人组列表',split:true,collapsed:false,border:false"
         style="width: 200px; text-align: left">
        <div id="groupTree-tabs" class="easyui-tabs" style="width:700px;height:250px">
            <div title="用户组">
                <div style="padding: 5px;">
                    <a onclick="showDialog('',0);" class="easyui-linkbutton"
                       data-options="iconCls:'eu-icon-group',toggle:true,selected:true"
                       style="width: 138px;">新建群组</a>
                <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip" data-options="position:'right'"
                  title="选择联系人组，点击鼠标右键即可对联系人组进行【编辑】、【删除】操作." ></span>
                </div>
                <ul id="contactGroup_user_tree"></ul>
            </div>
            <div title="邮件组" data-options="iconCls:'icon-help',closable:false">
                <div style="padding: 5px;">
                    <a onclick="showDialog('',1);" class="easyui-linkbutton"
                       data-options="iconCls:'eu-icon-group',toggle:true,selected:true"
                       style="width: 138px;">新建群组</a>
                <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip" data-options="position:'right'"
                  title="选择联系人组，点击鼠标右键即可对联系人组进行【编辑】、【删除】操作." ></span>
                </div>
                <ul id="contactGroup_mail_tree"></ul>
            </div>
        </div>

    </div>

    <!-- 中间部分 列表 -->
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
            <div data-options="region:'center',split:true" style="overflow: hidden;">
                <table id="contactGroup_datagrid" ></table>
            </div>

            <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
                 style="width: 100%;height:62px; ">
                <form id="contactGroup_search_form" style="padding: 5px;">
                    &nbsp;&nbsp;<span id="search">
                    关键字:<input type="text" id="query" name="query" class="easyui-validatebox textbox eu-input" placeholder="关键字..."
                          onkeydown="if(event.keyCode==13)search()"  maxLength="25" style="width: 160px"/>

                </span>
                    <%--姓名或登录名:<input type="text" id="loginNameOrName" name="loginNameOrName" class="easyui-validatebox textbox eu-input" placeholder="姓名或登录名..."--%>
                          <%--onkeydown="if(event.keyCode==13)search()"  maxLength="25" style="width: 160px"/>--%>
                    <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
                    <%--&lt;%&ndash;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no'" onclick="javascript:$contactGroup_user_search_form.form('reset');">重置查询</a>&ndash;%&gt;--%>
                </form>
            </div>
        </div>
    </div>
</div>