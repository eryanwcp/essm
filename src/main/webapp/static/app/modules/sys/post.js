var $organ_tree;//组织机树(左边)
var $post_datagrid;
var $post_form;
var $post_search_form;
var $post_dialog;

var $post_user_form;
var $post_user_dialog;
$(function() {
    $post_search_form = $('#post_search_form').form();


    //组织机构树
    var selectedNode = null;//存放被选中的节点对象 临时变量
    $organ_tree = $("#organ_tree").tree({
        url : ctxAdmin+"/sys/organ/tree?dataScope=2&cascade=true",
        onClick:function(node){
            search();
        },
        onBeforeSelect:function(node){
            var selected = $(this).tree('getSelected');
            if(selected){
                if(selected.id == node.id){
                    $(".tree-node-selected", $(this).tree()).removeClass("tree-node-selected");//移除样式
                    selectedNode = null;
                    return false;
                }
            }
            selectedNode = node;
            return true;
        },
        onLoadSuccess:function(node, data){
            if(selectedNode !=null){
                selectedNode = $(this).tree('find', selectedNode.id);
                if(selectedNode !=null){//刷新树后 如果临时变量中存在被选中的节点 则重新将该节点置为被选状态
                    $(this).tree('select', selectedNode.target);
                }
            }
            $(this).tree("expandAll");
        }
    });

    //数据列表
    $post_datagrid = $('#post_datagrid').datagrid({
        url:ctxAdmin+'/sys/post/_datagrid',
        fit:true,
        pagination:true,//底部分页
        rownumbers:true,//显示行数
        fitColumns:false,//自适应列宽
        striped:true,//显示条纹
        pageSize:20,//每页记录数
        remoteSort:false,//是否通过远程服务器对数据排序
        sortName:'id',//默认排序字段
        sortOrder:'asc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        frozen:true,
        collapsible: true,
        frozenColumns:[[
            {field:'ck',checkbox:true},
            {field:'name',title:'岗位名称',width:200,sortable:true}
        ]],
        columns:[[
            {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80} ,
            {field:'organName',title:'部门',width:120,hidden:false},
            {field:'code',title:'岗位编码',width:120,sortable:true},
            {field:'remark',title:'备注',width:200}
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
            handler:function(){editPostUser()}
        }],
        onLoadSuccess:function(){
            $(this).datagrid('clearSelections');//取消所有的已选择项
            $(this).datagrid('unselectAll');//取消全选按钮为全选状态
        },
        onRowContextMenu : function(e, rowIndex, rowData) {
            e.preventDefault();
            $(this).datagrid('unselectAll');
            $(this).datagrid('selectRow', rowIndex);
            $('#post_datagrid_menu').menu('show', {
                left : e.pageX,
                top : e.pageY
            });
        },
        onDblClickRow:function(rowIndex, rowData){
            edit(rowIndex, rowData);
        }
    }).datagrid('showTooltip');
});

function formInit(){
    $post_form = $('#post_form').form({
        url: ctxAdmin+'/sys/post/_save',
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
                $post_dialog.dialog('destroy');//销毁对话框
                $post_datagrid.datagrid('reload');//重新加载列表数据
                $organ_tree.tree("reload");
                eu.showMsg(json.msg);//操作结果提示
            }else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    if(json.obj){
                        $('#post_form input[name="'+json.obj+'"]').focus();
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
    var inputUrl = ctxAdmin+"/sys/post/input";
    if(row != undefined && row.id){
        inputUrl += "?id="+row.id;
    }else{
        var node = $organ_tree.tree('getSelected');//
        if(node != null){
            inputUrl += "?organId="+node['id'];
        }

    }

    //弹出对话窗口
    $post_dialog = $('<div/>').dialog({
        title:'岗位详细信息',
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
                $post_form.submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                $post_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            $post_dialog.dialog('destroy');
        },
        onLoad:function(){
            formInit();

        }
    });

}

//编辑
function edit(rowIndex, rowData){
    //响应双击事件
    if(rowIndex != undefined) {
        showDialog(rowData);
        return;
    }
    //选中的所有行
    var rows = $post_datagrid.datagrid('getSelections');
    //选中的行（最后一次选择的行）
    var row = $post_datagrid.datagrid('getSelected');
    if (row){
        if(rows.length>1){
            row = rows[rows.length-1];
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }

        showDialog(row);
    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}



//初始化岗位用户表单
function initPostUserForm(){
    $post_user_form = $('#post_user_form').form({
        url: ctxAdmin+'/sys/post/updatePostUser',
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
                $post_user_dialog.dialog('destroy');//销毁对话框
                $post_datagrid.datagrid('reload');	// reload the role data
                eu.showMsg(json.msg);//操作结果提示
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}
//修改岗位用户
function editPostUser(){
    //选中的所有行
    var rows = $post_datagrid.datagrid('getSelections');
    //选中的行（第一条）
    var row = $post_datagrid.datagrid('getSelected');
    if (row){
        if(rows.length>1){
            eu.showMsg("您选择了多个操作对象，默认操作最后一次被选中的记录！");
        }
        var userUrl = ctxAdmin+"/sys/post/user";
        if(row != undefined && row.id){
            userUrl = userUrl+"?id="+row.id;
        }
        //弹出对话窗口
        $post_user_dialog = $('<div/>').dialog({
            title:'岗位用户信息',
            top:20,
            width : 600,
            height:200,
            modal : true,
            maximizable:true,
            href : userUrl,
            buttons : [ {
                text : '保存',
                iconCls : 'easyui-icon-save',
                handler : function() {
                    $post_user_form.submit();
                }
            },{
                text : '关闭',
                iconCls : 'easyui-icon-cancel',
                handler : function() {
                    $post_user_dialog.dialog('destroy');
                }
            }],
            onClose : function() {
                $post_user_dialog.dialog('destroy');
            },
            onLoad:function(){
                initPostUserForm();
                $post_user_form.form('load', row);
            }
        });

    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}


//删除
function del(){
    var rows = $post_datagrid.datagrid('getSelections');

    if(rows.length >0){
        $.messager.confirm('确认提示！','您确定要删除选中的所有行?',function(r){
            if (r){
                var ids = new Array();
                $.each(rows,function(i,row){
                    ids[i] = row.id;
                });
                $.ajax({
                    url:ctxAdmin+'/sys/post/remove',
                    type:'post',
                    data: {ids:ids},
                    dataType:'json',
                    traditional:true,
                    success:function(data) {
                        if (data.code==1){
                            $post_datagrid.datagrid('load');	// reload the user data
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg,'error');
                        }
                    }
                });
            }
        });
    }else{
        eu.showMsg("您未选择任何操作对象，请选择一行数据！");
    }
}

//搜索
function search(){
    var node = $organ_tree.tree('getSelected');//
    var organId = '';
    if(node != null){
        organId = node.id; //搜索 id:主键 即是通过左边组织机构树点击得到搜索结果
    }

    var queryData = {};
    var organData = {organId: organId};
    var formData = $.serializeObject($post_search_form);
    queryData = $.extend(queryData, organData, formData);//合并对象


    $post_datagrid.datagrid('load',queryData);
}