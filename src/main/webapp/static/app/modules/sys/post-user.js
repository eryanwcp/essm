var organId = organId;
var userIds = userIds;

var $user_combogrid;
$(function() {
    loadUser();
});

//加载用户
function loadUser(){
    $user_combogrid = $('#userIds').combogrid({
        url:ctxAdmin + '/sys/user/combogridOrganUser',
        width:360,
        height:96,
        panelWidth:360,
        panelHeight:200,
        idField:'id',
        textField:'name',
        multiple:true,
        mode: 'remote',
        fitColumns: true,
        striped: true,
        editable:false,
        rownumbers:true,//序号
        collapsible:false,//是否可折叠的
        method:'post',
        columns:[[
            {field:'ck',checkbox:true},
            {field:'id',title:'主键ID',width:100,hidden:'true'},
            {field: 'name', title: '姓名', width: 60, sortable: true},
            {field: 'sexView', title: '性别', width: 50},
            {field: 'defaultOrganName', title: '部门', width: 120, sortable: true}
        ]],
        onBeforeLoad:function(param){
            param.organId = organId;
        }
    });
    $user_combogrid.combogrid("setValues",userIds);

}