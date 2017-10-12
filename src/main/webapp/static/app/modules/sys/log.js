var $log_datagrid;
var $log_search_form;
$(function () {
    $log_search_form = $('#log_search_form').form();
    //数据列表
    $log_datagrid = $('#log_datagrid').datagrid({
        url: ctxAdmin+'/sys/log/datagrid',
        fit:true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        pageSize: 20,//每页记录数
        pageList:[10,20,50,100,1000],
        singleSelect: false,//单选模式
        checkbox: true,
        nowrap: true,
        border: false,
        remoteSort: true,//是否通过远程服务器对数据排序
        //sortName: 'operTime',//默认排序字段
        //sortOrder: 'desc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozenColumns:[[
            {field: 'ck',checkbox: true,width: 60},
            {field: 'typeView',title: '日志类型',width: 80}
        ]],
        columns: [ [
            {field: 'id',title: '主键',hidden: true,sortable: true,align: 'right', width: 100} ,
            {field: 'title',title: '标题',width: 200,hidden:false,formatter: function (value, rowData, rowIndex) {
                return "<a target='_blank' href='"+ctxAdmin+"/sys/log/detail?id="+rowData['id']+"'>"+rowData['title']+"</a>";
            } },
            {field: 'userCompanyName', title: '单位', width: 200, hidden: false},
            {field: 'userOfficeName', title: '部门', width: 150, hidden: false},
            {field: 'userName',title: '姓名',width: 80},
            {field: 'userId',title: '用户ID',width: 60,hidden:true},
            {field: 'ip', title: 'IP地址', width: 100} ,
            {field: 'userAgent', title: '客户端', width: 100,hidden:true} ,
            {field: 'browserType', title: '浏览器', width: 100,hidden:true} ,
            {field: 'deviceType', title: '设备', width: 80,hidden:true} ,
            {field: 'module',title: '模块', width: 200},
            {field: 'action',title: '操作',width: 100,hidden:true},
            {field: 'operTime',title: '操作时间',width: 136,sortable: true} ,
            {field: 'actionTime',title: '操作耗时(ms)',width: 100},
            {field: 'longitude',title: '经度',width: 100,hidden:true},
            {field: 'latitude',title: '纬度',width: 100,hidden:true},
            {field: 'remark',title: '备注',width: 260 ,hidden:true}
        ]],
        toolbar:[{
            text:'删除',
            iconCls:'easyui-icon-remove',
            handler:function(){del()}
        },'-',{
            text:'清空所有',
            iconCls:'easyui-icon-no',
            handler:function(){delAll()}
        }]
    }).datagrid("showTooltip");

    //日志类型 搜索选项
    $('#_type').combobox({
        url:ctxAdmin+'/sys/log/logTypeCombobox?selectType=all',
        editable:false,//是否可编辑
        height:28,
        width:120
    });
});

//删除
function del() {
    var rows = $log_datagrid.datagrid('getSelections');
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要删除当前选中的所有行？', function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows,function(i,row){
                    ids[i] = row.id;
                });
                $.ajax({
                    url:ctxAdmin+'/sys/log/remove',
                    type:'post',
                    data: {ids:ids},
                    traditional:true,
                    dataType:'json',
                    success:function(data) {
                        if (data.code==1){
                            $log_datagrid.datagrid('clearSelections');//取消所有的已选择项
                            $log_datagrid.datagrid('load');//重新加载列表数据
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg,'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

/**
 * 清空所有日志
 */
function delAll(){
    $.messager.confirm('确认提示！', "您确认要清空所有数据？", function (r) {
        if(r){
            $.post(ctxAdmin+'/sys/log/removeAll',
                function (data) {
                    if (data.code == 1) {
                        $log_datagrid.datagrid('clearSelections');//取消所有的已选择项
                        $log_datagrid.datagrid('load');//重新加载列表数据
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                },
                'json');
        }
    });

}

//搜索
function search() {
    $log_datagrid.datagrid('load', $.serializeObject($log_search_form));
}