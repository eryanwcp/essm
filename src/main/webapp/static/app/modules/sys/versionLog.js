var $versionLog_datagrid;
var $versionLog_search_form;
var $versionLog_dialog;
var $versionLog_form;
$(function () {
    initDatagrid();
    $versionLog_search_form = $('#versionLog_search_form').form();
});
/**
 * 数据列表
 */
function initDatagrid(){
    $versionLog_datagrid = $('#versionLog_datagrid').datagrid({
        url: ctxAdmin+'/sys/versionLog/datagrid',
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
        remoteSort: true,//是否通过远程服务器对数据排序
        idField: 'id',
        frozenColumns:[[
            {field: 'ck',checkbox: true,width: 60},
            {field: 'app',title: 'APP',width: 80},
            {field: 'versionName',title: '版本号',width: 120,
                formatter: function (value, rowData, rowIndex) {
                    return "<a href='#' onclick='view(\"" + rowData["id"]+"\")' >"+value+"</a>";
                }
            }
        ]],
        columns: [ [
            {field: 'id',title: '主键',hidden: true,width: 120 } ,
            {field: 'versionCode',title: '内部版本号',width: 100,align: 'right'},
            {field: 'versionLogTypeView',title: '类型',width: 120},
            {field: 'remark',title: '更新说明',width: 360 },
            {field: 'isPubView',title: '是否发布',width: 120},
            {field: 'pubTime', title: '发布时间', width: 146,sortable: true} ,
            {field: 'operate', title: '操作', formatter:function(value,rowData,rowIndex){
                    var operaterHtml = "<a class='easyui-linkbutton' iconCls='easyui-icon-edit' onclick='edit(" + rowIndex + ");' >编辑</a>";
                    operaterHtml += "<a class='easyui-linkbutton' iconCls='easyui-icon-remove' onclick='del(" + rowIndex + ");' >删除</a>";
                    return operaterHtml;
                }
            }
        ]],
        toolbar:[{
            text:'新增',
            iconCls:'easyui-icon-add',
            handler:function(){showDialog()}
        },{
            text:'删除',
            iconCls:'easyui-icon-remove',
            handler:function(){del()}
        },'-',{
            text:'清空所有',
            iconCls:'easyui-icon-no',
            handler:function(){delAll()}
        }]
    }).datagrid("showTooltip");
}

function view(versionLogId){
    var inputUrl = ctxAdmin+"/sys/versionLog/view/" + versionLogId;
    var versionLog_view_dialog = $('<div/>').dialog({
        title : '查看系统',
        width : 600,
        height : 320,
        modal : true,
        maximizable : true,
        href:inputUrl,
        //content : '<iframe id="versionLog_view_iframe" scrolling="no" frameborder="0"  src="'+inputUrl+'" ></iframe>',
        buttons : [{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                versionLog_view_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            versionLog_view_dialog.dialog('destroy');
        }
    });
}

function formInit() {
    $versionLog_form = $('#versionLog_form').form({
        url: ctxAdmin + '/sys/versionLog/save',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $versionLog_dialog.dialog("destroy");
                $versionLog_datagrid.datagrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#versionLog_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//显示弹出窗口 新增：row为空
function showDialog(row) {
    var inputUrl = ctxAdmin + "/sys/versionLog/input";
    if(row){
        inputUrl += "?id=" + row['id'];
    }
    //弹出对话窗口
    $versionLog_dialog = $('<div/>').dialog({
        title: '系统更新详细信息',
        top: 20,
        width: 600,
        height: 360,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [{
                text: '保存',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    $versionLog_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    $versionLog_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            $versionLog_dialog.dialog('destroy');
        },
        onLoad: function () {
            formInit();
            if (row) {
                $versionLog_form.form('load', row);
            }

        }
    });

}

//编辑
function edit(rowIndex, rowData) {
    //响应双击事件
    if (rowIndex != undefined) {
        $versionLog_datagrid.datagrid('unselectAll');
        $versionLog_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $versionLog_datagrid.datagrid('getSelected');
        $versionLog_datagrid.datagrid('unselectRow', rowIndex);
        showDialog(rowData);
        return;
    }
    //选中的所有行
    var rows = $versionLog_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = $versionLog_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            row = rows[rows.length - 1];
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }
        showDialog(row);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function del(rowIndex) {
    var rows = new Array();
    var tipMsg = "您确定要删除";
    if(rowIndex != undefined){
        $versionLog_datagrid.datagrid('unselectAll');
        $versionLog_datagrid.datagrid('selectRow', rowIndex);
        var row = $versionLog_datagrid.datagrid('getSelected');
        $versionLog_datagrid.datagrid('unselectRow', rowIndex);
        rows = new Array();
        rows.push(row);
        tipMsg += "更新说明["+row["versionName"]+"]？";
    }else{
        tipMsg += "选中的所有记录？";
        rows = $versionLog_datagrid.datagrid('getChecked');
    }
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', tipMsg, function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows,function(i,row){
                    ids[i] = row.id;
                });
                $.ajax({
                    url:ctxAdmin+'/sys/versionLog/remove',
                    type:'post',
                    data: {ids:ids},
                    traditional:true,
                    dataType:'json',
                    success:function(data) {
                        if (data.code==1){
                            $versionLog_datagrid.datagrid('clearSelections');//取消所有的已选择项
                            $versionLog_datagrid.datagrid('load');//重新加载列表数据
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
        if (r){
            $.post(ctxAdmin+'/sys/versionLog/removeAll',
                function (data) {
                    if (data.code == 1) {
                        $versionLog_datagrid.datagrid('clearSelections');//取消所有的已选择项
                        $versionLog_datagrid.datagrid('load');//重新加载列表数据
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                },
                'json');
        }
    });
}

/**
 * 搜索
 */
function search() {
    $versionLog_datagrid.datagrid('load', $.serializeObject($versionLog_search_form));
}