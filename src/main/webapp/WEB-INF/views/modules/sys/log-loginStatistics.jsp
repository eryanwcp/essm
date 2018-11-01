<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
    var $log_datagrid;
    var $log_search_form;
    $(function () {
        $log_search_form = $('#log_search_form').form();
        //数据列表
        $log_datagrid = $('#log_datagrid').datagrid({
            url: ctxAdmin+'/sys/log/report/loginStatisticsData',
            fit:true,
            pagination: true,//底部分页
            rownumbers: true,//显示行数
            fitColumns: false,//自适应列宽
            striped: true,//显示条纹
            pageSize: 20,//每页记录数
            singleSelect: false,//单选模式
            checkbox: true,
            nowrap: true,
            border: false,
            //idField: 'id',
            columns: [ [
                {field: 'company', title: '单位', width: 235, hidden: false},
                {field: 'department', title: '部门', width: 235, hidden: false},
                {field: 'name', title: '姓名', width: 100},
                {field: 'userName', title: '账号', width: 120, hidden: false},
                {field: 'count', title: '登录次数', width: 100,align:'right', hidden: false}
            ]],

        }).datagrid("showTooltip");

    });

    //搜索
    function search() {
        $log_datagrid.datagrid('load', $.serializeObject($log_search_form));
    }
</script>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
         style="padding: 0px; height: 70px;width:100%; overflow-y: hidden;">
        <form id="log_search_form" style="padding: 5px;">

            &nbsp;账号或姓名：<input type="text" id="name" name="name"
                               class="easyui-validatebox textbox eu-input" placeholder="账号或姓名..."  onkeydown="if(event.keyCode==13)search()"
                               maxLength="25" style="width: 160px" />
            &nbsp;起始时间：<input type="text" id="startTime" name="startTime" class="easyui-my97" placeholder="起始时间..."/>
            &nbsp;结束时间：<input type="text" id="endTime" name="endTime" class="easyui-my97"  placeholder="结束时间..."/>
            &nbsp;<a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',width:100,height:28, onClick:search">查询</a>
            <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no',width:100,height:28" onclick="javascript:$log_search_form.form('reset');">重置查询</a>
            <a href="#" class="easyui-linkbutton" id="exportExcel" data-options="width:100,height:28, onClick:exportExcel">导出excel</a>
        </form>
    </div>

    <%-- 中间部分 列表 --%>
    <div data-options="region:'center',split:false,border:false"
         style="padding: 0px; height: 100%;width:100%; overflow-y: hidden;">
        <table id="log_datagrid"></table>
    </div>

    <%-- 隐藏iframe --%>
    <iframe id="login_temp_iframe" style="display: none;"></iframe>
</div>

<script>
    //导出excle表格
    function exportExcel(){
       var name =  document.getElementById("name").value;
       var startTime =  document.getElementById("startTime").value;
       var endTime =  document.getElementById("endTime").value;
       $('#login_temp_iframe').attr('src', ctxAdmin+'/sys/log/report/loginStatisticsExportExcel?name='+name+'&startTime='+startTime+'&endTime='+endTime);
    }
</script>