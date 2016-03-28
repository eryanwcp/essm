var role_datagrid;
var role_form;
var role_search_form;
var role_resource_form
var role_user_form;
var role_dialog;
var role_resource_dialog;
var role_user_dialog;
$(function () {
    role_form = $('#role_form').form();
    role_search_form = $('#role_search_form').form();
    //数据列表
    role_datagrid = $('#role_datagrid').datagrid({
        url: ctxAdmin + '/sys/role/datagrid',
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
                {field: 'name', title: '角色名称', width: 200},
                {field: 'code', title: '角色编码', width: 120}
            ]
        ],
        columns: [
            [
                {field: 'id', title: '主键', hidden: true, sortable: true, align: 'right', width: 80},
                {field: 'resourceNames', title: '关联资源', width: 420},
                {field: 'remark', title: '描述', width: 200}
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
                text: '设置资源',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    editRoleResource()
                }
            },
            '-',
            {
                text: '设置用户',
                iconCls: 'easyui-icon-edit',
                handler: function () {
                    editRoleUser()
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
            $('#role_datagrid_menu').menu('show', {
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
    role_form = $('#role_form').form({
        url: ctxAdmin + '/sys/role/save',
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
                role_dialog.dialog('destroy');//销毁对话框
                role_datagrid.datagrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#role_form input[name="' + json.obj + '"]').focus();
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
    var inputUrl = ctxAdmin + "/sys/role/input";
    if (row != undefined && row.id) {
        inputUrl = inputUrl + "?id=" + row.id;
    }

    //弹出对话窗口
    role_dialog = $('<div/>').dialog({
        title: '角色详细信息',
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
                    role_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    role_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            role_dialog.dialog('destroy');
        },
        onLoad: function () {
            formInit();
            if (row) {
                role_form.form('load', row);
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
    var rows = role_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = role_datagrid.datagrid('getSelected');
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


//初始化角色角色表单
function initRoleResourceForm() {
    role_resource_form = $('#role_resource_form').form({
        url: ctxAdmin + '/sys/role/updateRoleResource',
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
                role_resource_dialog.dialog('destroy');//销毁对话框
                role_datagrid.datagrid('reload');	// reload the role data
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//修改角色角色
function editRoleResource() {
    //选中的所有行
    var rows = role_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = role_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }
        //弹出对话窗口
        role_resource_dialog = $('<div/>').dialog({
            title: '角色资源信息',
            top: 20,
            width: 500,
            height: 200,
            modal: true,
            maximizable: true,
            href: ctxAdmin + '/sys/role/resource',
            buttons: [
                {
                    text: '保存',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        role_resource_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        role_resource_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                role_resource_dialog.dialog('destroy');
            },
            onLoad: function () {
                initRoleResourceForm();
                role_resource_form.form('load', row);
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//修改角色用户
function editRoleUser() {
    //选中的所有行
    var rows = role_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = role_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }
        var userUrl = ctxAdmin + "/sys/role/user";
        if (row != undefined && row.id) {
            userUrl = userUrl + "?id=" + row.id;
        }
        //弹出对话窗口
        role_user_dialog = $('<div/>').dialog({
            title: '角色用户信息',
            top: 20,
            width: 600,
            height: 360,
            modal: true,
            maximizable: true,
            href: userUrl,
            buttons: [
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        role_user_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                role_user_dialog.dialog('destroy');
            },
            onLoad: function () {
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function del() {
    var rows = role_datagrid.datagrid('getSelections');
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要删除选中的所有行？', function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/sys/role/_remove',
                    type: 'post',
                    data: {ids: ids},
                    traditional: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 1) {
                            role_datagrid.datagrid('load');	// reload the user data
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

//搜索
function search() {
    role_datagrid.datagrid('load', $.serializeObject(role_search_form));
}