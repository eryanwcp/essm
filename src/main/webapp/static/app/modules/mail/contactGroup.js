var $contactGroup_tree;
var $contactGroup_user_tree;
var $contactGroup_mail_tree;
var $contactGroup_form;
var $contactGroup_dialog;
var $contactGroup_user_search_form;
var $contactGroup_datagrid;
var $contactGroup_user_datagrid;
var $contactGroup_mail_datagrid;

var $contactGroupMail_form;
var $contactGroupMail_dialog;
var isUser=1;

$(function(){
    $("#groupTree-tabs").tabs({
        onSelect:function(title,index){
            initData(true);
        }
    });
    treeUser();
    treeMail();
});
function treeUser(){
    var refreshDatagrid = false;//tree加载成功后是否刷新联系人列表
    $contactGroup_user_tree = $("#contactGroup_user_tree").tree({
        url : ctxAdmin+"/mail/contactGroup/groupTree?contactGroupType=0",
        formatter:function(node){
            return node.text;
        },
        onClick:function(node){
//                $(this).tree('beginEdit',node.target);
        },
        onSelect:function(node){
            listUser(node['id']);
            initData();
        },
        onContextMenu: function (e, node) {
            e.preventDefault();
            if(node.id != undefined && node.id != ""){
                $("#treeMenu").menu({onClick:function(item){
                    if(item.name == "edit"){
                        showDialog(node.id,0);
                    }else if(item.name == "delete"){
                        del(node.id,node.text);
                    }
                }}).menu('show', {
                    left: e.pageX,
                    top: e.pageY
                });
            }

        },
        onLoadSuccess:function(node, data){
            $(this).tree("expandAll");
        }
    });
}


function treeMail(){
    var refreshDatagrid = false;//tree加载成功后是否刷新联系人列表
    $contactGroup_mail_tree = $("#contactGroup_mail_tree").tree({
        url : ctxAdmin+"/mail/contactGroup/groupTree?contactGroupType=1",
        formatter:function(node){
            return node.text;
        },
        onClick:function(node){
//                $(this).tree('beginEdit',node.target);
        },
        onSelect:function(node){
            listMail(node['id']);
            initData();
        },
        onContextMenu: function (e, node) {
            e.preventDefault();
            if(node.id != undefined && node.id != ""){
                $("#treeMenu").menu({onClick:function(item){
                    if(item.name == "edit"){
                        showDialog(node.id,1);
                    }else if(item.name == "delete"){
                        del(node.id,node.text);
                    }
                }}).menu('show', {
                    left: e.pageX,
                    top: e.pageY
                });
            }

        },
        onLoadSuccess:function(node, data){
            $(this).tree("expandAll");
        }
    });
}
function listUser(contactGroupId){
    var contactGroup_user_toolbar = null;
    if(contactGroupId){
        contactGroup_user_toolbar = [{
            text:'添加',
            iconCls:'easyui-icon-add',
            handler:function(){addContactGroupUserDialog(contactGroupId)}
        },'-',{
            text:'移除',
            iconCls:'easyui-icon-remove',
            handler:function(){removeContactGroupObject('',contactGroupId)}
        }];
    }

    $contactGroup_user_datagrid = $('#contactGroup_datagrid').datagrid({
        url:ctxAdmin + '/mail/contactGroup/contactGroupUserDatagrid',
        fit:true,
        rownumbers:true,//显示行数
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        remoteSort:false,//是否通过远程服务器对数据排序
        idField : 'id',
        frozen:true,
        collapsible: true,
        toolbar:contactGroup_user_toolbar,
        queryParams:{id:contactGroupId},//联系人组ID
        frozenColumns:[[
            {field:'ck',checkbox:true},
            {field:'name',title:'姓名',width:120},
            {field:'loginName',title:'登录名',width:120,hidden:true},
            {field:'sexView',title:'性别',width:60}
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80} ,
            {field:'organNames',title:'部门',width:200},
            {field:'mobilephone',title:'手机号',width:120},
            {field:'tel',title:'电话',width:120},
            {field:'email',title:'邮箱',width:120},
            {field:'operate',title:'操作',hidden:true,
                formatter: function (value, rowData, rowIndex) {
                    return "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='removeContactGroupObject(\""+rowData.id+"\",\""+contactGroupId+"\")'>移除</a>&nbsp;";
                }}
        ]]
    }).datagrid("showColumn","operate");
}

function listMail(contactGroupId){
    var contactGroup_mail_toolbar = null;
    if(contactGroupId){
        contactGroup_mail_toolbar = [{
            text:'新增',
            iconCls:'easyui-icon-add',
            handler:function(){editMail(undefined,contactGroupId)}
        },'-',{
            text:'删除',
            iconCls:'easyui-icon-remove',
            handler:function(){removeContactGroupObject('',contactGroupId)}
        }];
    }

    $contactGroup_mail_datagrid = $('#contactGroup_datagrid').datagrid({
        url:ctxAdmin + '/mail/mailContact/contactGroupMailDatagrid',
        fit:true,
        rownumbers:true,//显示行数
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        remoteSort:false,//是否通过远程服务器对数据排序
        pagination : true,
        pagePosition : 'bottom',//'top','bottom','both'.
        pageSize : 20,
        pageList:[10,20,50,100,1000,99999],
        idField : 'id',
        toolbar:contactGroup_mail_toolbar,
        queryParams:{id:contactGroupId},//联系人组ID
        frozen:true,
        collapsible: true,
        frozenColumns:[[
            {field:'ck',checkbox:true},
            {field:'name',title:'名称',width:120},
            {field:'email',title:'邮箱',width:200}
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80} ,
            {field:'remark',title:'备注',width:350},
            {field:'operate',title:'操作',hidden:true,
                formatter: function (value, rowData, rowIndex) {
                    return "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' " +
                        "onclick='editMail(\""+rowData.id+"\",\""+contactGroupId+"\")'>编辑</a>&nbsp;"+
                        "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' " +
                        "onclick='removeContactGroupObject(\""+rowData.id+"\",\""+contactGroupId+"\")'>删除</a>&nbsp;";
                }}
        ]],
        onRowContextMenu: function (e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
            $('#role_datagrid_menu').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        }
    }).datagrid("showColumn","operate");
}
//编辑
function editMailContact(contactGroupId) {
    //选中的所有行
    var rows = $contactGroup_user_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = $contactGroup_user_datagrid.datagrid('getSelected');
    if (row) {
        if (rows.length > 1) {
            row = rows[rows.length - 1];
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
            $contactGroup_mail_datagrid.datagrid('clearSelections');
            var rowIndex = $contactGroup_mail_datagrid.datagrid('getRowIndex',row);
            $contactGroup_mail_datagrid.datagrid('selectRow',rowIndex);
        }
        editMail(row.id,contactGroupId);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

function formInit(){
    $contactGroup_form = $('#contactGroup_form').form({
        url: ctxAdmin+'/mail/contactGroup/save',
        onSubmit: function(param){
            $.messager.progress({
                title : '提示信息！',
                text : '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }
            return isValid;
        },
        success: function(data){
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code ==1){
                $contactGroup_dialog.dialog('destroy');//销毁对话框
                initData();
                $contactGroup_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            }else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    if(json.obj){
                        $('#$contactGroup_form input[name="'+json.obj+'"]').focus();
                    }
                });
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}
//联系人组 弹窗 新增、修改
function showDialog(contactGroupId,contactGroupType){
    var inputUrl = ctxAdmin + "/mail/contactGroup/input?contactGroupType="+contactGroupType;
    if (contactGroupId != undefined) {
        inputUrl = inputUrl + "&id=" + contactGroupId;
    }

    //弹出对话窗口
    $contactGroup_dialog = $('<div/>').dialog({
        title:'联系人组信息',
        top:20,
        height: 360,
        width : 500,
        modal : true,
        maximizable:true,
        href : inputUrl,
        buttons : [ {
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                $contactGroup_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $contactGroup_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $(this).dialog('destroy');
        },
        onLoad:function(){
            formInit();
        }
    });

}
/**
 * 删除联系人组
 */
function del(contactGroupId,contactGroupName){
    $.messager.confirm('确认提示！', '您确定要删除['+contactGroupName+']？', function (r) {
        if (r) {
            var ids = new Array();
            ids.push(contactGroupId);
            $.ajax({
                url: ctxAdmin + '/mail/contactGroup/remove',
                type: 'post',
                data: {ids: ids},
                dataType: 'json',
                traditional: true,
                success: function (data) {
                    if (data.code == 1) {
                        $contactGroup_user_datagrid.datagrid("reload");
                        refreshDatagrid = true;//特殊处理 tree异步操作时 方法调用异常
                        initData();
                        $contactGroup_tree.tree('reload');
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                }
            });
        }
    });
}


/**
 * 添加联系人 对话框
 */
function addContactGroupUserDialog(contactGroupId){
    _dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: ctxAdmin + '/mail/contactGroup/select?contactGroupId='+contactGroupId,
        width: '700',
        height: '450',
        maximizable: true,
        iconCls: 'eu-icon-user',
        modal: true,
        buttons: [
            {
                text: '确定',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    addContactGroupUser(contactGroupId);
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    _dialog.dialog('destroy');

                }
            }
        ],
        onClose: function () {
            _dialog.dialog('destroy');
        }
    });
}
/**
 * 保存添加联系人
 * @param id
 */
function addContactGroupUser(id){
    var selectds = new Array();
    $("#selectUser option").each(function () {
        var txt = $(this).val();
        selectds.push($.trim(txt));
    });

    $.messager.progress({
        title: '提示信息！',
        text: '数据处理中，请稍后....'
    });
    $.ajax({
        url: ctxAdmin + '/mail/contactGroup/addContactGroupUser?id='+id,
        type: 'post',
        data: {addObjectIds: selectds},
        dataType: 'json',
        traditional: true,
        success: function (data) {
            $.messager.progress('close');
            if (data.code == 1) {
                initData();
                $contactGroup_tree.tree('reload');
                _dialog.dialog('destroy');
                eu.showMsg(data.msg);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}

function mailformInit(){
    $contactGroupMail_form = $('#contactGroupMail_form').form({
        url: ctxAdmin+'/mail/mailContact/_save',
        onSubmit: function(param){
            $.messager.progress({
                title : '提示信息！',
                text : '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }
            return isValid;
        },
        success: function(data){
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code ==1){
                $contactGroupMail_dialog.dialog('destroy');//销毁对话框
                initData();
                $contactGroup_datagrid.datagrid('reload');
                eu.showMsg(json.msg);//操作结果提示
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}

/**
 * 添加邮件联系人
 */
function editMail(mailContactId,contactGroupId){
    var selectedNode = $contactGroup_tree.tree("getSelected");
    var inputUrl=ctxAdmin + "/mail/mailContact/input";
    if (mailContactId != undefined) {
        inputUrl += "?id=" + mailContactId;
        if(contactGroupId){
            inputUrl += "&contactGroupId=" + contactGroupId;
        }
    }else{
        if(contactGroupId){
            inputUrl += "?contactGroupId=" + contactGroupId;
        }
    }
    if(selectedNode != undefined && selectedNode.id != undefined){
        $contactGroupMail_dialog = $("<div/>").dialog({
            title:'邮件联系人',
            top:20,
            height: 360,
            width : 500,
            modal : true,
            maximizable:true,
            href : inputUrl,
            buttons : [ {
                text : '保存',
                iconCls : 'easyui-icon-save',
                handler : function() {
                    $contactGroupMail_form.submit();

                }
            },{
                text : '关闭',
                iconCls : 'easyui-icon-cancel',
                handler : function() {
                    $contactGroupMail_dialog.dialog('destroy');
                }
            }],
            onClose : function() {
                $(this).dialog('destroy');
            },
            onLoad:function(){
                mailformInit();
            }
        });
    }
}

/**
 * 删除关联对象
 * @param objectId 关联对象ID
 * @param contactGroupId 分组ID
 */
function removeContactGroupObject(objectId,contactGroupId) {
    var selectIds = new Array();
    $.messager.progress({
        title: '提示信息！',
        text: '数据处理中，请稍后....'
    });
    if(objectId != undefined&&objectId!=''){
        selectIds.push(objectId);
    }else{
        var rows = $contactGroup_datagrid.datagrid('getSelections');
        var row = $contactGroup_datagrid.datagrid('getSelected');
        $.each(rows, function (i, row) {
            selectIds.push(row.id);
        });
    }
    if(selectIds.length<1){
        $.messager.progress('close');
        eu.showAlertMsg("无法执行该操作.");
        return;
    }
    var url= ctxAdmin + '/mail/contactGroup/removeContactGroupUser?id='+contactGroupId;
    $.ajax({
        url: url,
        type: 'post',
        data: {removeObjectIds: selectIds},
        dataType: 'json',
        traditional: true,
        success: function (data) {
            $.messager.progress('close');
            if (data.code == 1) {
                initData();
                $contactGroup_datagrid.datagrid("reload");
                eu.showMsg(data.msg);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}

function initData(flag){
    var tab = $('#groupTree-tabs').tabs('getSelected');
    var index = $('#groupTree-tabs').tabs('getTabIndex',tab);

    if(index == 0){
        $contactGroup_datagrid = $contactGroup_user_datagrid;
        $contactGroup_tree = $contactGroup_user_tree;
        if(flag){
            listUser();
        }
    }else if(index == 1){
        $contactGroup_datagrid = $contactGroup_mail_datagrid;
        $contactGroup_tree = $contactGroup_mail_tree;
        if(flag){
            listMail();
        }
    }

}


function search(){
    var selectedNode = $contactGroup_tree.tree("getSelected");
    var contactGroupId = '';
    if(selectedNode){
        contactGroupId = selectedNode.id;
    }
    if($contactGroup_datagrid != undefined){
        $contactGroup_datagrid.datagrid({
            queryParams:{id:contactGroupId,query:$("#query").val()}
        });
    }

}