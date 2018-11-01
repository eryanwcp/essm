var $organ_treegrid;
var $organ_form;
var $organ_user_form;
var $organ_dialog;
var $organ_user_dialog;
$(function() {
    //数据列表
    $organ_treegrid = $('#organ_treegrid').treegrid({
        url:ctxAdmin+'/sys/organ/treegrid',
        fit:true,
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        rownumbers:true,//显示行数
        nowrap : false,
        border : false,
        singleSelect:true,
        idField : 'id',
        treeField:"name",
        frozenColumns:[[
            {field:'name',title:'机构名称',width:200},
            {field:'sysCode',title:'机构系统编码',width:120,hidden:true}
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80},
            {field:'managerUserName',title:'主管',width:120},
            {field:'superManagerUserName',title:'分管领导',width:120},
            {field:'code',title:'机构编码',width:120},
            {field:'address',title:'地址',width:120},
            {field:'mobile',title:'手机号',width:120,sortable:true},
            {field:'phone',title:'电话号码',width:120,sortable:true},
            {field:'fax',title:'传真',width:120,sortable:true},
            {field:'typeView',title:'机构类型',width:100},
            {field:'sort',title:'排序',align:'right',width:60,sortable:true},
            {field:'statusView',title:'状态',align:'center',width:60}
        ]],
        toolbar:[{
            text:'新增',
            iconCls:'easyui-icon-add',
            handler:function(){showDialog()}
        },'-',{
            text:'编辑',
            iconCls:'easyui-icon-edit',
            handler:function(){edit()}
        },'-',{
            text:'删除',
            iconCls:'easyui-icon-remove',
            handler:function(){del()}
        },'-',{
            text:'设置用户',
            iconCls:'eu-icon-user',
            handler:function(){editOrganUser()}
        }],
        onBeforeLoad:function(row,param){
            if (row){
                param.parentId = row['id'];
            }
        },
        onContextMenu : function(e, row) {
            e.preventDefault();
            $(this).treegrid('select', row.id);
            $('#organ_menu').menu('show', {
                left : e.pageX,
                top : e.pageY
            });

        },
        onDblClickRow:function(row){
            edit(row);
        },
        onLoadSuccess:function(data){
            //$(this).treegrid("collapseAll");
            var rootNode = $(this).treegrid("getRoot");
            if(rootNode){//展开第一级
                $(this).treegrid("expand",rootNode['id']);
            }
        }
    }).datagrid('showTooltip');

});

function formInit(){
    $organ_form = $('#organ_form').form({
        url: ctxAdmin+'/sys/organ/save',
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
                $organ_dialog.dialog('destroy');//销毁对话框
                $organ_treegrid.treegrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            }else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    if(json.obj){
                        $('#organ_form input[name="'+json.obj+'"]').focus();
                    }
                });
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}
//显示弹出窗口 新增：row为空 编辑:row有值
function showDialog(row){
    var inputUrl = ctxAdmin+"/sys/organ/input";
    if(row != undefined && row.id){//编辑
        inputUrl = inputUrl+"?id="+row.id;
    }else{//新增
        var selectedNode = $organ_treegrid.treegrid('getSelected');
        if(selectedNode != undefined && selectedNode['id'] != undefined){
            inputUrl +="?parentId=" + selectedNode['id'];
        }
    }

    //弹出对话窗口
    $organ_dialog = $('<div/>').dialog({
        title:'机构详细信息',
        top:20,
        width : 500,
        height:360,
        modal : true,
        maximizable:true,
        href : inputUrl,
        buttons : [ {
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                $organ_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $organ_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $organ_dialog.dialog('destroy');
        },
        onLoad:function(){
            formInit();
        }
    });

}


//编辑
function edit(row) {
    if (row == undefined) {
        row = $organ_treegrid.treegrid('getSelected');
    }
    if (row != undefined) {
        showDialog(row);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//初始化机构用户表单
function initOrganUserForm(){
    $organ_user_form = $('#organ_user_form').form({
        url: ctxAdmin+'/sys/organ/updateOrganUser',
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
            if (json.code == 1){
                $organ_user_dialog.dialog('destroy');//销毁对话框
                $organ_treegrid.treegrid('reload');	// reload the organ data
                eu.showMsg(json.msg);//操作结果提示
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}
//修改机构用户
function editOrganUser(){
    //选中的行（第一条）
    var row = $organ_treegrid.treegrid('getSelected');
    if (row){
        var userUrl = ctxAdmin+"/sys/organ/user";
        if(row != undefined && row.id){
            userUrl = userUrl+"?id="+row.id;
        }
        //弹出对话窗口
        $organ_user_dialog = $('<div/>').dialog({
            title:'机构用户信息',
            top:20,
            height : 200,
            width : 600,
            modal : true,
            maximizable:true,
            href : userUrl,
            buttons : [ {
                text : '保存',
                iconCls : 'easyui-icon-save',
                handler : function() {
                    $organ_user_form.submit();
                }
            },{
                text : '关闭',
                iconCls : 'easyui-icon-cancel',
                handler : function() {
                    $organ_user_dialog.dialog('destroy');
                }
            }],
            onClose : function() {
                $organ_user_dialog.dialog('destroy');
            },
            onLoad:function(){
                initOrganUserForm();
            }
        });

    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function del(rowIndex){
    var row;
    if (rowIndex == undefined) {
        row = $organ_treegrid.treegrid('getSelected');
    }
    if (row != undefined) {
        $.messager.confirm('确认提示！','您确定要删除(如果存在子节点，子节点也一起会被删除)？',function(r){
            if (r){
                $.post(ctxAdmin+'/sys/organ/delete/'+row.id,{},function(data){
                    if (data.code==1){
                        $organ_treegrid.treegrid('unselectAll');//取消选择 1.3.6bug
                        $organ_treegrid.treegrid('load');	// reload the user data
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg,'error');
                    }
                },'json');

            }
        });
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}
