var $config_datagrid;
var $config_form;
var $config_search_form;
var $config_dialog;
$(function () {
    $config_search_form = $('#config_search_form').form();
    //数据列表
    $config_datagrid = $('#config_datagrid').datagrid({
        url: ctxAdmin + '/sys/config/datagrid',
        fit: true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        pageSize: 20,//每页记录数
        remoteSort: false,//是否通过远程服务器对数据排序
        sortName: 'id',//默认排序字段
        sortOrder: 'asc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozenColumns: [
            [
                {field: 'ck', checkbox: true},
                {field: 'code', title: '属性名称', width: 200, sortable: true},
                {field: 'value', title: '属性值', width: 360}
            ]
        ],
        columns: [
            [
                {field: 'id', title: '主键', hidden: true, sortable: true, align: 'right', width: 80},
                {field: 'remark', title: '备注', width: 200}
            ]
        ],
        toolbar: [
            {
                text: '新增',
                iconCls: 'easyui-icon-add',
                handler: function () {
                    showDialog()
                }
            },
            '-',
            {
                text: '编辑',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    edit()
                }
            },
            '-',
            {
                text: '删除',
                iconCls: 'easyui-icon-remove',
                handler: function () {
                    del()
                }
            },
            '-',
            {
                text: '读取配置文件',
                iconCls: 'easyui-icon-add',
                handler: function () {
                    syncFromProperties()
                }
            }
        ],
        onLoadSuccess: function () {
            $(this).datagrid('clearSelections');//取消所有的已选择项
            $(this).datagrid('unselectAll');//取消全选按钮为全选状态
        },
        onRowContextMenu: function (e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
            $('#config_datagrid_menu').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        },
        onDblClickRow: function (rowIndex, rowData) {
            edit(rowIndex, rowData);
        }
    }).datagrid('showTooltip');

});
function formInit() {
    $config_form = $('#config_form').form({
        url: ctxAdmin + '/sys/config/save',
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
                $config_dialog.dialog('destroy');//销毁对话框
                $config_datagrid.datagrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#config_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//显示弹出窗口 新增：row为空 编辑:row有值
function showDialog(row) {
    var inputUrl = ctxAdmin + "/sys/config/input";
    if (row != undefined && row.id) {
        inputUrl = inputUrl + "?id=" + row.id;
    }

    //弹出对话窗口
    $config_dialog = $('<div/>').dialog({
        title: '属性配置详细信息',
        top: 20,
        width: 500,
        height: 360,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [
            {
                text: '保存',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    $config_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    $config_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            $config_dialog.dialog('destroy');
        },
        onLoad: function () {
            formInit();
            if (row) {
                $config_form.form('load', row);
            }

        }
    });

}

//编辑
function edit(rowIndex, rowData) {
    //响应双击事件
    if (rowIndex != undefined) {
        showDialog(rowData);
        return;
    }
    //选中的所有行
    var rows = $config_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = $config_datagrid.datagrid('getSelected');
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
function del() {
    var rows = $config_datagrid.datagrid('getSelections');
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要删除选中的所有行？', function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/sys/config/remove',
                    type: 'post',
                    data: {ids: ids},
                    traditional: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 1) {
                            $config_datagrid.datagrid('load');	// reload the user data
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function syncFromProperties() {
    $.ajax({
        url: ctxAdmin + '/sys/config/syncFromProperties',
        type: 'post',
        data: {overrideFromProperties: false},
        traditional: true,
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                $config_datagrid.datagrid('load');	// reload the user data
                eu.showMsg(data.msg);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}

//搜索
function search() {
    $config_datagrid.datagrid('load', $.serializeObject($config_search_form));
}