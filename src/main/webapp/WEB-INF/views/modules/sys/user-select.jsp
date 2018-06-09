<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<style type="text/css">
    .datagrid-header-rownumber,.datagrid-cell-rownumber{
        width:40px;
    }
</style>
<script type="text/javascript">
    var dataScope = '${dataScope}';
    var userDatagridData = ${userDatagridData};
    var multiple = '${multiple}';
    var cascade = '${cascade}';
    var excludeUserIdStrs = '${excludeUserIdStrs}';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user-select${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div id="select_layout" class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">

    <div data-options="region:'west',title:'选择人员',split:true,collapsed:false,border:false"
         style="width: 200px;text-align: left;padding:0px;">
        <div class="easyui-accordion" data-options="fit:true" style="width:190px;">
            <div title="按部门" iconCls="eu-icon-group" style="overflow:auto;padding:4px;">
                <div id="select_organ_tree"></div>
            </div>
            <%--<div title="按角色" iconCls="eu-icon-group" style="padding:4px;">--%>
                <%--<div id="select_role_tree"></div>--%>
            <%--</div>--%>
        </div>
    </div>


    <!-- 中间部分 列表 -->
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
            <div data-options="region:'center',split:true" style="overflow: hidden;">
                <table id="select_user_datagrid"></table>
            </div>

            <div data-options="region:'north',title:'',split:false,collapsed:false,border:false"
                 style="width: 100%;height:48px; overflow: hidden;">
                <form id="select_user_search_form" style="padding: 10px;">
                    <input class="easyui-textbox" id="query" name="query"
                           data-options="buttonText:' 查 询 ',buttonIcon:'easyui-icon-search',prompt:'登录名或姓名...',onClickButton:search"
                           onkeydown="if(event.keyCode==13)search()" maxLength="25"
                           style="width:250px;height:28px;">
                </form>
            </div>
        </div>
    </div>
    <div data-options="region:'east',title:'已选择人员',split:true,collapsed:false,border:false,tools:[{
                    iconCls:'easyui-icon-cancel',
                    handler:function(){
                        cancelSelectedUser();
                    }
                }]"
         style="width: 130px;text-align: left;padding:1px;">
        <div>
            <select id="selectUser" multiple="multiple" style="width:123px;height:348px;">
                <c:forEach var="user" begin="0" items="${users}">
                    <option value='${user.id}'>${user.name}</option>
                </c:forEach>
            </select>
        </div>
    </div>
</div>