var $session_datagrid;
$(function() {
    $session_datagrid = $('#session_datagrid').datagrid({
        url : ctxAdmin + '/sys/session/onLineSessions',
        fit : true,
        nowarp : false,
        border : false,
        view:bufferview,
//        autoRowHeight:false,
        idField : 'id',
        pagination: true,
        rownumbers: true,
        fitColumns: false,
        striped: true,
        pageSize: 20,
        frozenColumns: [[
            {field: 'ck', checkbox: true}
        ]],
        columns : [ [{
            title : 'SessionID',
            field : 'id',
            width : 360,
            hidden:true
        },{
            title : 'SessionID',
            field : 'sessionId',
            width : 360,
            hidden:false
        },{
            title : '员工类型',
            field : 'bizUserType',
            width : 120,
            hidden:true
        },{
            title : '姓名',
            field : 'name',
            width : 120
        },{
            title : '登录名',
            field : 'loginName',
            width : 120,
            hidden:true
        },{
            title : '部门',
            field : 'loginOrganName',
            width : 200,
            hidden:true
        },{
            title : '登录IP',
            field : 'ip',
            width : 120,
            formatter:function(value, rowData, rowIndex){
                return value;
            }
        },{
            title : '设备类型',
            field : 'sysTemDeviceType',
            width : 120,
            hidden:true
        },{
            title : '设备编码',
            field : 'deviceCode',
            width : 200,
            hidden:true
        },{
            title : 'APP客户端版本',
            field : 'appVersion',
            width : 120,
            hidden:true
        },{
            title : '登录时间',
            field : 'loginTime',
            width : 146,
            formatter:function(value, rowData, rowIndex){
                return value;
            }
        },{
            title : '操作',
            field : 'operate',
            width : 100,
            formatter:function(value, rowData, rowIndex){
                var operaterHtml = "";
                var editHtml = "<a class='easyui-linkbutton' iconCls='easyui-icon-cancel'  href='#' " +
                    "onclick='offline(" + rowIndex + ");' >下线</a>";
                operaterHtml += editHtml;
                return operaterHtml;
            }
        }
        ] ],
        toolbar: [
            {
                text: '下线',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    offline();
                }
            }
        ],
        onClickRow : function(rowIndex, rowData) {
        },
        onLoadSuccess : function(data) {
            $.parser.parse($(".easyui-linkbutton").parent());
            $(this).datagrid('clearSelections');
            $(this).datagrid('clearChecked');
            $(this).datagrid('unselectAll');
            $(this).datagrid('uncheckAll');
        }
    }).datagrid('showTooltip');
});

/**
 * 用户下线
 */
function offline(rowIndex){
    var rows = new Array();
    var tipMsg = "您确定要将选中用户强制下线？";
    if(rowIndex != undefined){
        $session_datagrid.datagrid('unselectAll');
        $session_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $session_datagrid.datagrid('getSelected');
        rows.push(rowData);
        $session_datagrid.datagrid('unselectRow', rowIndex);
        tipMsg = "您确定要将用户["+rowData["name"]+"]强制下线？";
    }else{
        rows = $session_datagrid.datagrid('getChecked');
    }
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', tipMsg, function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/sys/session/offline',
                    type: 'post',
                    data: {sessionIds: ids},
                    traditional: true,
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == 1) {
                            $session_datagrid.datagrid('load');	// reload the user data
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