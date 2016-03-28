var $mailAccount_datagrid;
var $mailAccount_form;
var $mailAccount_dialog;

var refreshDatagrid = false;//tree加载成功后是否刷新联系人列表
$(function() {
    //组织机构树
    $mailAccount_datagrid = $('#mailAccount_datagrid').datagrid({
        url:ctxAdmin + '/mail/mailAccount/mailAccountDatagrid',
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
                text: '删除',
                iconCls: 'easyui-icon-remove',
                handler: function () {
                    del()
                }
            }
        ],
        frozenColumns: [
            [
                {field: 'ck', checkbox: true},
                {field: 'name', title: '名称', width: 120},
                {field: 'mailAddress', title: '邮件地址', width: 280}
            ]
        ],
        columns: [
            [
                {field: 'id', title: '主键', hidden: true, sortable: true, align: 'right', width: 80},
                {field: 'username', title: '邮箱账号', width: 280},
                {field: 'activateString', title: '邮箱活动', width: 120},
                {field: 'operate',title:'操作',hidden:true,
                    formatter: function (value, rowData, rowIndex) {
                        return  "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-ok\"' " +
                            "onclick='test(\""+rowData.id+"\")'>测试</a>&nbsp;"+
                            "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' " +
                            "onclick='showDialog(\""+rowData.id+"\")'>编辑</a>&nbsp;"+
                            "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' " +
                            "onclick='del(\""+rowData.id+"\")'>删除</a>&nbsp;";
                    }}
            ]
        ],
        onRowContextMenu: function (e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
        }
    }).datagrid("showColumn","operate");
});

//联系人组 弹窗 新增、修改
function showDialog(mailAccountId){
    var inputUrl = ctxAdmin + "/mail/mailAccount/input";
    if (mailAccountId != undefined) {
        inputUrl = inputUrl + "?id=" + mailAccountId;
    }
    //弹出对话窗口
    $mailAccount_dialog = $('<div/>').dialog({
        title :'邮箱账号',
        top   : 20,
        height: 450,
        width : 600,
        modal : true,
        maximizable:true,
        href : inputUrl,
        buttons : [ {
            text : '测试',
            iconCls : 'easyui-icon-save',
            handler : function() {
                testformInit();
                $mailAccount_form.submit();
            }
        },{
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                formInit();
                $mailAccount_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $mailAccount_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $(this).dialog('destroy');
        }
    });
}

function testformInit(){
    $mailAccount_form = $('#mailAccount_form ').form({
        url: ctxAdmin+'/mail/mailAccount/testForm',
        onSubmit: function(param){
            $.messager.progress({
                title : '提示信息！',
                text : '正在验证中，请稍后....'
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
            var code=json.code;
            if (code >0){
                var msg=json.msg;
                $.messager.alert('测试结果',msg);
                if(code==1 ){
                    $("input[name='activate']").get(0).checked=true;
                }else{
                    $("input[name='activate']").get(1).checked=true;
                }
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}

function formInit(){
    $mailAccount_form = $('#mailAccount_form ').form({
        url: ctxAdmin+'/mail/mailAccount/_save',
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
                $mailAccount_datagrid.datagrid('reload');
                $mailAccount_dialog.dialog('destroy');//销毁对话框
                eu.showMsg(json.msg);//操作结果提示
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}

function test(mailAccountId){
    $.ajax({
        url: ctxAdmin + "/mail/mailAccount/test?id="+mailAccountId,
        type: 'post',
        dataType: 'json',
        traditional: true,
        success: function (data) {
            $.messager.progress('close');
            if (data.code == 1) {
                $mailAccount_datagrid.datagrid("reload");
                eu.showMsg(data.msg);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}

/**
 * 删除
 */
function del(mailAccountId){
    var selectUserIds = new Array();
    $.messager.progress({
        title: '提示信息！',
        text: '数据处理中，请稍后....'
    });
    if(mailAccountId != undefined){
        selectUserIds.push(mailAccountId);
    }else{
        var rows = $mailAccount_datagrid.datagrid('getSelections');
        $.each(rows, function (i, row) {
            selectUserIds.push(row.id);
        });
    }
    if(selectUserIds.length<1){
        $.messager.progress('close');
        eu.showAlertMsg("无法执行该操作.");
        return;
    }
    $.ajax({
        url: ctxAdmin+'/mail/mailAccount/remove',
        type: 'post',
        data: {ids: selectUserIds},
        dataType: 'json',
        traditional: true,
        success: function (data) {
            $.messager.progress('close');
            if (data.code == 1) {
                $mailAccount_datagrid.datagrid("reload");
                eu.showMsg(data.msg);//操作结果提示
            } else {
                eu.showAlertMsg(data.msg, 'error');
            }
        }
    });
}