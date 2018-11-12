var $resource_treegrid;
var $resource_form;
var $resource_dialog;

$(function() {
    //数据列表
    $resource_treegrid = $('#resource_treegrid').treegrid({
        url:ctxAdmin+'/sys/resource/treegrid',
        fit:true,
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        rownumbers:true,//显示行数
        nowrap : false,
        border : false,
        singleSelect:true,
        remoteSort:false,//是否通过远程服务器对数据排序
        sortName:'sort',//默认排序字段
        sortOrder:'asc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        treeField:"name",
        frozenColumns:[[
            {field:'name',title:'资源名称',width:200},
            {field:'code',title:'资源编码',width:120}
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80},
            {field:'url',title:'链接地址',width:260},
            {field:'markUrl',title:'标识地址',width:260},
            {field:'sort',title:'排序',align:'right',width:60,sortable:true},
            {field:'typeView',title:'资源类型',align:'center',width:100},
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
        }],
        onContextMenu : function(e, row) {
            e.preventDefault();
            $(this).treegrid('select', row.id);
            $('#resource_menu').menu('show', {
                left : e.pageX,
                top : e.pageY
            });

        },
        onDblClickRow:function(row){
            edit(row);
        }
    }).datagrid('showTooltip');

});

function formInit(){
    $resource_form = $('#resource_form').form({
        url: ctxAdmin+'/sys/resource/save',
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
                $resource_dialog.dialog('destroy');//销毁对话框
                $resource_treegrid.treegrid('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            }else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    if(json.obj){
                        $('#resource_form input[name="'+json.obj+'"]').focus();
                    }
                });
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        },
        onLoadSuccess:function(data){
            if(data != undefined && data._parentId != undefined){
                //$('#_parentId')是弹出-input页面的对象 代表所属分组
                $('#_parentId').combotree('setValue',data._parentId);
            }
        }
    });
}
//显示弹出窗口 新增：row为空 编辑:row有值
function showDialog(row){
    var inputUrl = ctxAdmin+"/sys/resource/input";
    if(row != undefined && row.id){
        inputUrl = inputUrl+"?id="+row.id;
    }else{
        var selectedNode = $resource_treegrid.treegrid('getSelected');
        if(selectedNode != undefined && selectedNode.type != undefined){
            inputUrl +="?parentId="+selectedNode['id'];
        }
    }

    //弹出对话窗口
    $resource_dialog = $('<div/>').dialog({
        title:'资源详细信息',
        top:20,
        width : 500,
        height:360,
        modal : true,
        maximizable:true,
        resizable:true,
        href : inputUrl,
        buttons : [ {
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                $resource_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $resource_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $resource_dialog.dialog('destroy');
        },
        onLoad:function(){
            formInit();
        }
    });

}

//编辑
function edit(row) {
    if (row == undefined) {
        row = $resource_treegrid.treegrid('getSelected');
    }
    if (row != undefined) {
        showDialog(row);
    } else {
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//删除
function del(rowIndex){
    var row;
    if (rowIndex == undefined) {
        row = $resource_treegrid.treegrid('getSelected');
    }
    if (row != undefined) {
        $.messager.confirm('确认提示！','您确定要删除(如果存在子节点，子节点也一起会被删除)？',function(r){
            if (r){
                $.post(ctxAdmin+'/sys/resource/delete/'+row.id,{},function(data){
                    if (data.code==1){
                        $resource_treegrid.treegrid('unselectAll');//取消选择 1.3.6bug
                        $resource_treegrid.treegrid('load');	// reload the user data
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