<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/meta.jsp" %>
<%@ include file="/common/kendoui.jsp"%>
<%@ include file="/common/uploadify.jsp"%>
<script type="text/javascript">
    var noticeId = '${noticeId}';
    var hasRepeatPermission = false;
    <e:hasPermission name="notice:repeat">
        hasRepeatPermission = true;
    </e:hasPermission>
    isSuperUser = function() {
        var isSuperUser = "${sessionInfo.superUser}";
        if(isSuperUser == "true"){
            return true;
        }
        return false;
    }
    var isSuperUser = isSuperUser();
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/notice/notice${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true"
     style="margin: 0px; border: 0px; overflow: hidden; width: 100%; height: 100%;">
    <%-- 左边部分 菜单树形 --%>
    <div data-options="region:'west',title:'我的通知',split:true,collapsed:false,border:false"
         style="width: 180px; text-align: left; padding: 2px;">
        <div style="padding:5px">
            <a  id="read_linkbutton" href="#" class="easyui-linkbutton" onclick="initReadDatagrid()"
                data-options="iconCls:'eu-icon-notice_user_comment',toggle:true,group:'notice',selected:true"
                style="width: 150px">我的通知</a>
        </div>
        <e:hasPermission name="notice:publish">
        <div style="padding:5px">
                <a href="#" class="easyui-linkbutton" onclick="initDatagrid()"
                   data-options="iconCls:'eu-icon-notice_publish',toggle:true,group:'notice'"
                   style="width: 150px">发布管理</a>
        </div>
        </e:hasPermission>
    </div>
    <!-- 中间部分 列表 -->
    <div data-options="region:'center',split:true"
         style="overflow: hidden;">
        <div class="easyui-layout" fit="true"
             style="margin: 0px; border: 0px; overflow: hidden; width: 100%; height: 100%;">
            <%-- 中间部分 列表 --%>
            <div data-options="region:'center',split:false,border:false"
                 style="padding: 0px; height: 100%; width: 100%; overflow-y: hidden;">
                <table id="notice_datagrid"></table>
            </div>

            <div id="layout_north" data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
                 style="padding: 0px; height: 96px; width: 100%; overflow-y: auto;">
                <form id="notice_search_form" style="padding: 5px;">
                    <table style="border: 0">
                        <tr>
                            <td>标&nbsp;题：</td>
                            <td><input type="text" name="title" placeholder="标题" class="easyui-validatebox textbox eu-input" maxLength="25" style="width: 260px" /></td>
                            <!-- <td>内&nbsp;容：</td>
                            <td><input type="text" name="content" maxLength="25" style="width: 260px"/></td> -->
                            <td>&nbsp;</td>
                            <td>发布日期：</td>
                            <td><input name="startTime" type="text" placeholder="起始时间" class="easyui-my97"  style="width:120px;">
                                ~
                                <input name="endTime" type="text"  placeholder="截止时间"  class="easyui-my97"  style="width:120px;"></td>
                        </tr>
                        <tr id="publishUserIds_tr" style="display: none;">
                            <td>发布人：</td>
                            <td ><select id="publishUserIds" name="publishUserIds" style="width: 260px;"></select></td>
                            <td >&nbsp;</td>
                            <%--<td ><a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" onclick="selectQueryUser();">选择</a></td>--%>
                        </tr>

                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
                                <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no'" onclick="javascript:$notice_search_form.form('reset');">重置查询</a>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>

        </div>
    </div>
</div>