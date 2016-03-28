<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/meta.jsp" %>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/versionLog${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
         style="padding: 0px; height: 70px;width:100%; overflow-y: hidden;">
        <form id="versionLog_search_form" style="padding: 5px;">
                &nbsp;版本号：<input id="versionName" name="versionName" class="easyui--validatebox textbox eu-input"
                                 maxlength="20" placeholder="版本号"/>
                &nbsp;更新说明：<input id="remark" name="remark" class="easyui--validatebox textbox eu-input"
                                  maxlength="50" placeholder="更新说明"/>
                &nbsp;更新时间：<input type="text" name="startTime" class="easyui-my97" placeholder="更新时间"/>
                            ~ <input type="text" name="endTime" class="easyui-my97"  placeholder="更新时间"/>
            &nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28,onClick:search">查询</a>
            <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:$versionLog_search_form.form('reset');">重置查询</a>
        </form>
        </form>
    </div>

    <%-- 中间部分 列表 --%>
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="versionLog_datagrid"></table>
    </div>

</div>