var dataScope = dataScope;//参考user-select.jsp
var userDatagridData = userDatagridData;//参考user-select.jsp
var multiple = multiple;//参考user-select.jsp
var excludeUserIdStrs = excludeUserIdStrs;//参考user-select.jsp

var $select_user_datagrid;
var $select_user_search_form;
var $select_organ_tree;
var $select_role_tree;
var selectUserIds = new Array();
var selectUserDatagridData = userDatagridData;
$(function () {
    selectOrganTree();
    selectRoleTree();
    $.each($("#selectUser option"), function (index, option) {
        selectUserIds.push(option.value);
    });
    userDatagrid();
});
function userDatagrid() {
    $select_user_search_form = $('#select_user_search_form').form();
    var frozenColumns = new Array();
    var multipleColumn = {field: 'ck', checkbox: true};
    if(''==multiple || multiple == 'true'){
        frozenColumns.push(multipleColumn);
    }
    frozenColumns.push({field: 'name', title: '姓名', width: 80, sortable: true});

    //数据列表啊
    $select_user_datagrid = $('#select_user_datagrid').datagrid({
        url: ctxAdmin + "/sys/user/datagridSelectUser",
        data: selectUserDatagridData,
        queryParams: {
            excludeUserIds: excludeUserIdStrs
        },
        fit: true,
        pagination: true,//底部分页
        rownumbers: true,//显示行数
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        remoteSort: false,//是否通过远程服务器对数据排序
//        sortName: 'name',//默认排序字段
//        sortOrder: 'asc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozen: true,
        collapsible: true,
        mode: 'local',
        autoRowHeight: false,
        frozenColumns: [frozenColumns],
        columns: [[
            {field: 'id', title: '主键', hidden: true, sortable: true, width: 10} ,
            {field: 'sexView', title: '性别', width: 50},
            {field: 'defaultOrganName', title: '部门', width: 150}
        ]],
        onCheck: function (rowIndex, rowData) {
            addSelectUser([rowData]);
        },
        onCheckAll: function (rows) {
            addSelectUser(rows);
//            addSelectUser(rows);
        },
        onUncheck: function (rowIndex, rowData) {
            cancelSelectUser([rowData]);
        },
        onUncheckAll: function (rows) {
            cancelSelectUser(rows);
//            cancelSelectUser(rows);
        },
        onDblClickRow: function (rowIndex, rowData) {
//                addSelectUser([rowData]);
        },
        onLoadSuccess: function (data) {
            selectUserDatagridData = data;
            $select_user_datagrid = $(this);
            selectDefault();
        }
    });
//    selectDefault();
}

function selectDefault() {
    var dgData = $select_user_datagrid.datagrid("getData").rows;
    $.each(selectUserIds, function (i, userId) {
        $.each(dgData, function (j, row) {
            if (userId == row.id) {
                $select_user_datagrid.datagrid("selectRow", j);
            }
        });
    });
    sysc();
}

function addSelectUser(rows) {
    $.each(rows, function (i, row) {
        var isSame = false;
        if ($("#selectUser option").length == 0) {
            $("#selectUser").append("<option value='" + row.id + "'>" + row.name + "</option>");
        } else {
            $("#selectUser option").each(function () {
                if ($(this).val() == row.id) {
                    isSame = true;
                    return;
                }
            });
            if (!isSame) {
                $("#selectUser").append("<option value='" + row.id + "'>" + row.name + "</option>");
            }
        }
    });
    sysc();
}

function sysc() {
    selectUserIds = new Array();
    $("#selectUser option").each(function () {
        selectUserIds.push($(this).val());
    });
    var selectUserSize= $("#selectUser option").length;
    $("#select_layout").layout().layout("panel","east").panel("setTitle","已选择<span style='color: red;'>"+selectUserSize+"</span>人");
}

function cancelSelectUser(rows) {
    $.each(rows, function (i, row) {
        $("#selectUser option[value='" + row.id + "']").remove();
    });
    sysc();
}

/**
 * 从已选择人员中去除选择
 */
function cancelSelectedUser(){
    $.each($("#selectUser").find("option:selected"),function(i,option){
        var cancelUserId= $(option).val();
        $(option).remove();
        $.each($select_user_datagrid.datagrid("getSelections"),function(j,row){
            if(row && cancelUserId == row.id && selectUserDatagridData.rows){
                $.each(selectUserDatagridData.rows,function(ii,rowData){
                    if(cancelUserId == rowData.id){
                        $select_user_datagrid.datagrid("uncheckRow",ii);
                    }
                });
            }
        });

    });
    sysc()
}
//部门树形
function selectOrganTree() {
    //组织机构树
    var selectedOrganNode = null;//存放被选中的节点对象 临时变量
    $select_organ_tree = $("#select_organ_tree").tree({
        url: ctxAdmin + "/sys/organ/tree?dataScope="+dataScope+'&cascade='+cascade,
        onClick: function (node) {
            var selectesRoleNode = $select_role_tree.tree('getSelected');
            if (selectesRoleNode) {
                $select_role_tree.tree('unSelect', selectesRoleNode.target);
            }
            search();
        },
        onBeforeLoad: function (node, param) {
            $("#select_organ_tree").html("数据加载中...");
        },
        onBeforeSelect: function (node) {
            selectedOrganNode = node;
            return true;
        },
        onLoadSuccess: function (node, data) {
            $(this).tree("collapseAll");
            var rootNode = $(this).tree("getRoot");
            if(rootNode){//展开第一级
                $(this).tree("expand",rootNode.target);
            }
        }
    });
}
//部门树形
function selectRoleTree() {
    //组织机构树
    var selectedRoleNode = null;//存放被选中的节点对象 临时变量
    $select_role_tree = $("#select_role_tree").tree({
        url: ctxAdmin + "/sys/role/tree?dataScope="+dataScope+'&cascade='+cascade,
        onClick: function (node) {
            var selectesOrganNode = $select_organ_tree.tree('getSelected');
            if (selectesOrganNode) {
                $select_organ_tree.tree('unSelect', selectesOrganNode.target);
            }
            search();
        },
        onBeforeSelect: function (node) {
            var selected = $(this).tree('getSelected');
            if (selected != undefined && node != undefined) {
                if (selected.id == node.id) {
                    $(".tree-node-selected", $(this).tree()).removeClass("tree-node-selected");//移除样式
                    selectedRoleNode = null;
                    return false;
                }
            }
            selectedRoleNode = node;
            return true;
        },
        onLoadSuccess: function (node, data) {
            if (selectedRoleNode != null) {
                selectedRoleNode = $(this).tree('find', selectedRoleNode.id);
                if (selectedRoleNode != null) {//刷新树后 如果临时变量中存在被选中的节点 则重新将该节点置为被选状态
                    $(this).tree('select', selectedRoleNode.target);
                }
            }
            $(this).tree("expandAll");
        }
    });
}
//搜索
function search() {
    var selectOrganNode = $select_organ_tree.tree('getSelected');//
    var organId = '';
    if (selectOrganNode != null) {
        organId = selectOrganNode.id; //搜索 id:主键 即是通过左边组织机构树点击得到搜索结果
    }
    var selectRoleNode = $select_role_tree.tree('getSelected');//
    var roleId = '';
    if (selectRoleNode != null) {
        roleId = selectRoleNode.id; //搜索 id:主键 即是通过左边组织机构树点击得到搜索结果
    }
    $select_user_datagrid.datagrid({
        url: ctxAdmin + "/sys/user/datagridSelectUser",
        queryParams: {organId: organId, roleId: roleId,excludeUserIds: excludeUserIdStrs, query: $("#query").val()}
    });
}