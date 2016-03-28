var organUserCombogridData = organUserCombogridData;
var usersCombogridData = usersCombogridData;

var $managerUser_combogrid;
$(function() {
    loadManagerUser();
});

//加载主管用户
function loadManagerUser(){
    $managerUser_combogrid = $('#managerUserId').combogrid({
        width:360,
        panelWidth:360,
        panelHeight:360,
        idField:'id',
        textField:'name',
        data:organUserCombogridData,
        fitColumns: true,
        striped: true,
        editable:true,
        rownumbers:true,//序号
        collapsible:false,//是否可折叠的
        method:'post',
        columns:[[
            {field:'id',title:'主键ID',width:100,hidden:'true'},
            {field: 'name', title: '姓名', width: 60, sortable: true},
            {field: 'sexView', title: '性别', width: 50},
            {field: 'defaultOrganName', title: '机构', width: 120, sortable: true}
        ]],
        keyHandler : {
            enter: function() {
                var name = $('#managerUserId').combogrid('getText');
                var dg = $('#managerUserId').combogrid("grid");
                dg.datagrid("reload", {'pageType':2,'filter_LIKES_loginName_OR_name':name});
            },
            query : function(q) {
            }
        }
    });
}
//加载分管用户
function selectUser(){
    var checkedUserIds = $("#superManagerUserId").val();
    _dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: ctxAdmin + '/sys/user/organUserTreePage?checkbox=false&checkedUserIds='+checkedUserIds,
        width: '500',
        height: '360',
        maximizable: true,
        iconCls: 'eu-icon-user',
        modal: true,
        buttons: [{
            text: '确定',
            iconCls: 'easyui-icon-save',
            handler: function () {
                setSelectUser();
                _dialog.dialog('destroy');
            }
        },{
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

function setSelectUser(){
    var node = $("#organUserTree").tree("getSelected");
    if(node){
        if("u" ==node.attributes.nType){
            $("#superManagerUserId").val(node['id']);
            $("#superManagerUserName").textbox('setValue',node['text']);
        }else{
            $("#superManagerUserId").val('');
            $("#superManagerUserName").textbox('setValue','');
        }

    }
}