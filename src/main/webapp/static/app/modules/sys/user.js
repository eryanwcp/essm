var sessionInfoUserId = sessionInfoUserId;//参考父页面 user.jsp
/**
 * 判断是否登录用户是不是超级管理员.
 * @returns {Boolean}
 */
function isSuperUser() {
    //登录用户id
    var sessonUserId = sessionInfoUserId;
    //超级管理员id为"1"
    if (sessonUserId == 1) {
        return true;
    }
    return false;
}
/**
 * 是否是超级用户id.
 */
function isSuperOwner(userId) {
    //超级管理员id为"1"
    if (userId == 1) {
        return true;
    }
    return false;
}

/**
 * 判但是是否允许操作.
 * @param userId
 */
function isOpeated(userId) {
    if (userId == 1 && isSuperUser() == false) {
        return false;
    }
    return true;
}
var $organ_tree;//组织机树(左边)
var $user_datagrid;
var $user_form;
var $user_password_form;
var $user_role_form;
var $user_resource_form;
var $user_organ_form;
var $user_search_form;
var $user_dialog;
var $user_password_dialog;
var $user_role_dialog;
var $user_resource_dialog;
var $user_organ_dialog;

var $user_post_form;
var $user_post_dialog;

$(function () {
    $user_search_form = $('#user_search_form').form();

    //组织机构树
    var selectedNode = null;//存放被选中的节点对象 临时变量
    $organ_tree = $("#organ_tree").tree({
        url: ctxAdmin + "/sys/organ/tree?dataScope=2&cascade=true",
        onClick: function (node) {
            search();
        },
        onBeforeSelect: function (node) {
//                var selected = $(this).tree('getSelected');
//                if(selected){
//                    if(selected.id == node.id){
//                        $(".tree-node-selected", $(this).tree()).removeClass("tree-node-selected");//移除样式
//                        selectedNode = null;
//                        return false;
//                    }
//                }
            selectedNode = node;
            return true;
        },
        onBeforeLoad:function(node, param){
            if(node){
                param.parentId = node['id'];
            }
        },
        onLoadSuccess: function (node, data) {
            if (selectedNode != null) {
                selectedNode = $(this).tree('find', selectedNode.id);
                if (selectedNode != null) {//刷新树后 如果临时变量中存在被选中的节点 则重新将该节点置为被选状态
                    $(this).tree('select', selectedNode.target);
                }
            }
            //$(this).tree("expandAll");
            //$(this).tree("collapseAll");
            var rootNode = $(this).tree("getRoot");
            if(rootNode){//展开第一级
                $(this).tree("expand",rootNode.target);
            }
        }
    });

    //数据列表
    $user_datagrid = $('#user_datagrid').datagrid({
        url: ctxAdmin + '/sys/user/datagrid',
        fit: true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        pageSize: 20,//每页记录数
        pageList:[10,20,50,100,1000,99999],
        remoteSort: false,//是否通过远程服务器对数据排序
        sortName: 'sort',//默认排序字段
        sortOrder: 'asc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozen: true,
        collapsible: true,
        frozenColumns: [
            [
                {field: 'ck', checkbox: true},
                {field: 'loginName', title: '登录名', width: 120, sortable: true,
                    formatter: function (value, rowData, rowIndex) {
                        if (isSuperOwner(rowData.id)) {
                            return $.formatString('<span  style="color:red">{0}</span>', value);
                        }
                        return value;
                    }
                },
                {field: 'name', title: '姓名', width: 100, sortable: true,formatter: function (value, rowData, rowIndex) {
                    var operaterHtml = "<a href='#'  onclick='edit(" + rowIndex + ");' >"+value+"</a>";
                    return operaterHtml;
                }},
                {field:'code',title:'员工编号',width:80,hidden: true,sortable:true}
            ]
        ],
        columns: [
            [
                {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80} ,
                {field:'tel',title:'办公电话',width:100},
                {field:'mobile',title:'手机号',width:82},
                {field:'qq',title:'QQ',width:80},
                {field:'email',title:'公司邮箱',width:160},
                {field:'personEmail',title:'个人邮箱',width:160},
                {field: 'defaultOrganId', title: '默认机构ID', width: 80,hidden: true},
                {field:'defaultOrganName',title:'部门',width:160,sortable:true},
                {field:'companyName',title:'单位',width:200,sortable:true, hidden: true},
                {field:'sexView',title:'性别',width:60,align:'center',sortable:true},
                {field:'birthday',title:'出生日期',width:80,sortable:true,sortable:true},
                {field:'address',title:'地址',width:200},
                {field:'remark',title:'备注',width:200},
                {field:'sort',title:'排序',width:60,sortable:true},
                {field:'statusView',title:'状态',align:'center',width:60,formatter: function (value, rowData, rowIndex) {
                    if (rowData['status'] != 0) {
                        return $.formatString('<span  style="color:red">{0}</span>', value);
                    }
                    return value;
                }},
                {field: 'operater', title: '操作', width: 260, formatter: function (value, rowData, rowIndex) {
                    var operaterHtml = "<a class='easyui-linkbutton' iconCls='easyui-icon-edit'  href='#' " +
                        "onclick='edit(" + rowIndex + ");' >编辑</a>";

                    if(isSuperUser()){//查看用户原始密码
                        operaterHtml += "<a class='easyui-linkbutton' iconCls='easyui-icon-edit'  href='#' onclick='viewUserPassword(\"" + rowData['loginName'] + "\");' >密码</a>";
                    }
                    return operaterHtml;
                }}
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
                text: '修改密码',
                iconCls: 'eu-icon-lock',
                handler: function () {
                    editPassword()
                }
            },
            '-',
            {
                text: '设置机构',
                iconCls: 'eu-icon-group',
                handler: function () {
                    editUserOrgan()
                }
            },
            '-',
            {
                text: '设置岗位',
                iconCls: 'eu-icon-user',
                handler: function () {
                    editUserPost()
                }
            },
            '-',
            {
                text: '设置角色',
                iconCls: 'eu-icon-group',
                handler: function () {
                    editUserRole()
                }
            },
            '-',
            {
                text: '设置资源',
                iconCls: 'eu-icon-folder',
                handler: function () {
                    editUserResource()
                }
            },
            '-',
            {
                text: '上移',
                iconCls: 'eu-icon-up',
                handler: function () {
                    move(true);
                }
            },
            '-',
            {
                text: '下移',
                iconCls: 'eu-icon-down',
                handler: function () {
                    move();
                }
            },
            '-',
            {
                text: '启用',
                iconCls: 'eu-icon-user',
                handler: function () {
                    lock(false);
                }
            },
            '-',
            {
                text: '停用',
                iconCls: 'eu-icon-lock',
                handler: function () {
                    lock(true);
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
            $('#user_datagrid_menu').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        },
        onDblClickRow: function (rowIndex, rowData) {
            edit(rowIndex, rowData);
        }
    }).datagrid('showTooltip');

    $query = $("#query").autocomplete(ctxAdmin+'/sys/user/autoComplete', {
        remoteDataType:'json',
        minChars: 0,
        maxItemsToShow: 10
    });
    var ac = $query.data('autocompleter');
    //添加查询属性
    ac.setExtraParam("rows",ac.options.maxItemsToShow);
});

function viewUserPassword(loginName){
    $.ajax({
        url: ctxAdmin + '/sys/user/viewUserPassword',
        type: 'post',
        data: {loginName: loginName},
        traditional: true,
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                eu.showTopCenterMsg(data.obj);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}

function formInit() {
    $user_form = $('#user_form').form({
        url: ctxAdmin + '/sys/user/save',
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
                $user_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');//重新加载列表数据
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#user_form input[name="' + json.obj + '"]').focus();
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
    var inputUrl = ctxAdmin + "/sys/user/input";
    if (row != undefined && row.id) {
        inputUrl += "?id=" + row.id;
    }else{
        var node = $organ_tree.tree('getSelected');
        var organId = '';
        if (node != null) {
            inputUrl += "?defaultOrganId=" + node['id'];
        }

    }

    //弹出对话窗口
    $user_dialog = $('<div/>').dialog({
        title: '用户详细信息',
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
                    $user_form.submit();
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    $user_dialog.dialog('destroy');
                }
            }
        ],
        onClose: function () {
            $user_dialog.dialog('destroy');
        },
        onLoad: function () {
            formInit();
        }
    });

}

//编辑
function edit(rowIndex, rowData) {
    //响应双击事件
    if (rowIndex != undefined) {
        $user_datagrid.datagrid('unselectAll');
        $user_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $user_datagrid.datagrid('getSelected');
        $user_datagrid.datagrid('unselectRow', rowIndex);
        showDialog(rowData);
        return;
    }
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            row = rows[rows.length - 1];
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }
        //判断是否允许操作
        if (isOpeated(row.id) == false) {
            eu.showMsg("超级管理员用户信息不允许被其他人修改！");
            return;
        }

        showDialog(row);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}
//初始化修改密码表单
function initPasswordForm() {
    $user_password_form = $('#user_password_form').form({
        url: ctxAdmin + '/sys/user/_updateUserPassword',
        onSubmit: function (param) {
            param.upateOperate = '0';
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                var rows = $user_datagrid.datagrid('getSelections');
                var userIds = new Array();
                $.each(rows,function(i,row){
                    userIds.push(row.id);
                })
                param.userIds = userIds;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $user_password_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');	// reload the user data
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}

//修改用户密码
function editPassword() {
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        //判断是否允许操作
        if (isOpeated(row.id) == false) {
            eu.showMsg("超级管理员用户信息不允许被其他人修改！");
            return;
        }

        var modelId = '';
        if(rows.length ==1) {//选中1个
            modelId = row.id;
        }

        $user_password_dialog = $('<div/>').dialog({
            title: '修改用户密码',
            top: 20,
            width: 500,
            height: 200,
            modal: true,
            maximizable: true,
            href: ctxAdmin + '/sys/user/password?id='+modelId,
            buttons: [
                {
                    text: '保存',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        $user_password_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        $user_password_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                $user_password_dialog.dialog('destroy');
            },
            onLoad: function () {
                initPasswordForm();

            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//初始化用户角色表单
function initUserRoleForm() {
    $user_role_form = $('#user_role_form').form({
        url: ctxAdmin+'/sys/user/updateUserRole',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                var rows = $user_datagrid.datagrid('getSelections');
                var userIds = new Array();
                $.each(rows,function(i,row){
                    userIds.push(row.id);
                })
                param.userIds = userIds;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $user_role_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');	// reload the user data
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}

//修改用户角色
function editUserRole() {
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        //判断是否允许操作
        if (isOpeated(row.id) == false) {
            eu.showMsg("超级管理员用户信息不允许被其他人修改！");
            return;
        }
        //判断是否允许操作
        if (row.id == 1) {
            eu.showMsg("超级管理员无需设置角色！");
            return;
        }

        var inputUrl = ctxAdmin+'/sys/user/role';
        if (row != undefined && row.id && rows.length ==1) {
            inputUrl = inputUrl + "?id=" + row.id;
        }
        //弹出对话窗口
        $user_role_dialog = $('<div/>').dialog({
            title: '用户角色信息',
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
                        $user_role_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        $user_role_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                $(this).dialog('destroy');
            },
            onLoad: function () {
                initUserRoleForm();
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}


//初始化用户资源表单
function initUserResourceForm() {
    $user_resource_form = $('#user_resource_form').form({
        url: ctxAdmin + '/sys/user/updateUserResource',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                var rows = $user_datagrid.datagrid('getSelections');
                var userIds = new Array();
                $.each(rows,function(i,row){
                    userIds.push(row.id);
                })
                param.userIds = userIds;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $user_resource_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');	// reload the user data
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//修改用户资源
function editUserResource() {
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        //判断是否允许操作
        if (isOpeated(row.id) == false) {
            eu.showMsg("超级管理员用户信息不允许被其他人修改！");
            return;
        }
        //判断是否允许操作
        if (row.id == 1) {
            eu.showMsg("超级管理员无需设置资源！");
            return;
        }
        //弹出对话窗口
        $user_resource_dialog = $('<div/>').dialog({
            title: '用户资源信息',
            top: 20,
            width: 500,
            height: 360,
            modal: true,
            maximizable: true,
            href: ctxAdmin + '/sys/user/resource?id='+row['id'],
            buttons: [
                {
                    text: '保存',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        $user_resource_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        $user_resource_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                $user_resource_dialog.dialog('destroy');
            },
            onLoad: function () {
                initUserResourceForm();
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//初始化用户机构表单
function initUserOrganForm() {
    $user_organ_form = $('#user_organ_form').form({
        url: ctxAdmin + '/sys/user/updateUserOrgan',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                var rows = $user_datagrid.datagrid('getSelections');
                var userIds = new Array();
                $.each(rows,function(i,row){
                    userIds.push(row.id);
                })
                param.userIds = userIds;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $user_organ_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');	// reload the user data
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}

//设置用户机构
function editUserOrgan() {
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        //判断是否允许操作
        if (isOpeated(row.id) == false) {
            eu.showMsg("超级管理员用户信息不允许被其他人修改！");
            return;
        }

        var inputUrl = ctxAdmin+'/sys/user/organ';
        if (row != undefined && row.id && rows.length ==1) {
            inputUrl = inputUrl + "?id=" + row.id;
        }
        //弹出对话窗口
        $user_organ_dialog = $('<div/>').dialog({
            title: '用户机构信息',
            top: 20,
            width: 500,
            height: 200,
            modal: true,
            maximizable: true,
            href: inputUrl,
            buttons: [
                {
                    text: '保存',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        $user_organ_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        $user_organ_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                $user_organ_dialog.dialog('destroy');
            },
            onLoad: function () {
                initUserOrganForm();
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//初始化用户岗位表单
function initUserPostForm() {
    $user_post_form = $('#user_post_form').form({
        url: ctxAdmin + '/sys/user/updateUserPost',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                var rows = $user_datagrid.datagrid('getSelections');
                var userIds = new Array();
                $.each(rows,function(i,row){
                    userIds.push(row.id);
                })
                param.userIds = userIds;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $user_post_dialog.dialog('destroy');//销毁对话框
                $user_datagrid.datagrid('reload');	// reload the role data
                eu.showMsg(json.msg);//操作结果提示
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//修改用户岗位
function editUserPost() {
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        var node = $organ_tree.tree('getSelected');

        if (rows.length > 1 && node == null) {
            eu.showMsg("批量设置岗位，请选中左侧机构树中的一个机构！");
            return;
        }
        var organParamUrl = "";
        if(node != null){
            organParamUrl = "organId="+node.id;
        }

        var userUrl = ctxAdmin+"/sys/user/post";
        if (row != undefined && row.id) {
            userUrl = userUrl + "?id=" + row.id+"&"+organParamUrl;
        }else{
            userUrl += "?"+organParamUrl;
        }
        //弹出对话窗口
        $user_post_dialog = $('<div/>').dialog({
            title: '用户岗位信息',
            top: 20,
            width: 500,
            height: 200,
            modal: true,
            maximizable: true,
            href: userUrl,
            buttons: [
                {
                    text: '保存',
                    iconCls: 'easyui-icon-save',
                    handler: function () {
                        $user_post_form.submit();
                    }
                },
                {
                    text: '关闭',
                    iconCls: 'easyui-icon-cancel',
                    handler: function () {
                        $user_post_dialog.dialog('destroy');
                    }
                }
            ],
            onClose: function () {
                $user_post_dialog.dialog('destroy');
            },
            onLoad: function () {
                initUserPostForm();
                if(rows.length ==1) {//选中1个
                    $user_post_form.form('load', row);
                }
            }
        });

    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}


//删除用户
function del() {
    var rows = $user_datagrid.datagrid('getSelections');

    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要删除选中的所有行?', function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/sys/user/remove',
                    type: 'post',
                    data: {ids: ids},
                    traditional: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 1) {
                            $user_datagrid.datagrid('load');	// reload the user data
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

/**
 * 移动
 * @param up 是否上移 是：true 否：false
 */
function move(up){
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一次选择的行）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            row = rows[rows.length - 1];
            eu.showMsg("您选择了多个操作对象，请选择一行数据！");
            return;
        }
        var rowIndex = $user_datagrid.datagrid("getRowIndex",row);
        var upUserId,downUserId;
        $.messager.progress({
            title: '提示信息！',
            text: '数据处理中，请稍后....'
        });
        var datagridData = $user_datagrid.datagrid("getData").rows;
        var moveUp;//是否上移动 是：true 否（下移）：false
        if(up){
            moveUp = true;
            upUserId = row.id;
            if(rowIndex == 0){//最上一行
//                $.messager.progress('close');
//                return;
            }else{
                if(datagridData.length>=(rowIndex+1)){
                    downUserId = datagridData[rowIndex-1].id;
                }
            }
        }else{
            moveUp = false;
            downUserId = row.id;
            if (datagridData.length == (rowIndex + 1)) {//最后一行
//                $.messager.progress('close');
//                return;
            } else {
                if (datagridData.length > (rowIndex + 1)) {
                    upUserId = datagridData[rowIndex + 1].id;
                }
            }

        }
        $.ajax({
            url: ctxAdmin+'/sys/user/changeOrderNo',
            type: 'post',
            data: {upUserId: upUserId,downUserId:downUserId,moveUp:moveUp},
            dataType: 'json',
            traditional: true,
            success: function (data) {
                $.messager.progress('close');
                if (data.code == 1) {
                    $user_datagrid.datagrid('reload');	// reload the user data
                } else {
                    eu.showAlertMsg(data.msg, 'error');
                }
            }
        });

    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

function lock(lock){
    //选中的所有行
    var rows = $user_datagrid.datagrid('getSelections');
    //选中的行（第一次选择的行）
    var row = $user_datagrid.datagrid('getSelected');
    if (row) {
        var userIds = new Array();
        $.each(rows,function(i,r){
            userIds.push(r.id);
        })
        var status = 3;//锁定状态位
        if(!lock){//启用
            status = 0;
        }

        $.ajax({
            url: ctxAdmin +'/sys/user/lock',
            type: 'post',
            data: {userIds: userIds,status:status},
            dataType: 'json',
            traditional: true,
            success: function (data) {
                $.messager.progress('close');
                if (data.code == 1) {
                    $user_datagrid.datagrid('reload');
                } else {
                    eu.showAlertMsg(data.msg, 'error');
                }
            }
        });

    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//搜索
function search() {
    var node = $organ_tree.tree('getSelected');//
    var organId = '';
    if (node != null) {
        organId = node.id; //机构ID
    }
    var queryData = {};
    var organData = {organId: organId};
    var formData = $.serializeObject($user_search_form);
    queryData = $.extend(queryData, organData, formData);//合并对象

    $user_datagrid.datagrid('load', queryData);
}
