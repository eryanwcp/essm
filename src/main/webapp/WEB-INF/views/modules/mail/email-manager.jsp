<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/kendoui.jsp"%>
<script type="text/javascript">
var $email_datagrid = null;
var $search_form = null;
var $sendUserIds_MultiSelect = null;
$(function() {
    $search_form = $("#search_form").form();
    initSelectUser();
    //数据列表
    $email_datagrid = $('#email_datagrid').datagrid({
        url: '${ctxAdmin}/mail/email/emailDatagrid',
        fit:true,
        checkbox: true,
        nowrap: true,
        border: false,
        pagination: true,
        //底部分页
        pagePosition: 'bottom',
        fitColumns: false,
        striped: true,
        pageSize: 20,
        singleSelect: false,
        rownumbers: true,
        remoteSort: false,
        sortName: 'id',
        sortOrder: 'desc',
        idField: 'id',
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        }]],
        columns: [[{
            field: 'id',
            title: '主键',
            hidden: true,
            sortable: true,
            align: 'right',
            width: 80
        },{
            field: 'title',
            title: '邮件主题',
            width: 360,
            formatter : function(value, rowData, index) {
                var valueHtml = "<a href='#' onclick='view(\""+rowData["id"]+"\")' >"+value+"</a>";
                return valueHtml;
            }
        },{
            field: 'priorityView',
            title: '邮件标签',
            width: 100
        },{
            field: 'senderName',
            title: '发件人',
            width: 80
        },{
            field: 'sendTime',
            title: '发送时间',
            width: 146,
            sortable: true
        },{
            field: 'toNames',
            title: '收件人',
            width: 200,
            hidden: true
        },{
            field: 'ccNames',
            title: '抄送人',
            width: 200,
            hidden: true
        },{
            field: 'operate',
            title: '操作',
            formatter : function(value, rowData, index) {
                var valueHtml = "<a href='#' class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='removeEmail("+index+")' >删除</a>"
                        + "<a href='#' class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-search\"' onclick='emailReadInfo(\""+rowData["id"]+"\",\""+rowData["title"]+"\")' >查看阅读情况</a>";
                return valueHtml;
            }
        }]],
        onLoadSuccess: function() {
            $(this).datagrid('clearSelections'); //取消所有的已选择项
            $(this).datagrid('unselectAll'); //取消全选按钮为全选状态
        },
        onDblClickRow: function(rowIndex, rowData) {
            view(rowData['id']);
        },
        toolbar: [{
            text: '批量删除',
            iconCls: 'eu-icon-mail_delete_complete',
            handler: function() {
                removeEmail();
            }
        }]
    }).datagrid('showTooltip');
});



function initSelectUser() {
    $.ajax({
        type: "post",
        dataType: 'json',
        contentType: "application/json",
        url: "${ctxAdmin}/sys/user/userList",
//          async: true,
        success: function (data) {
            var dataSource = {data: data,group: { field: "defaultOrganName" }};
            $sendUserIds_MultiSelect = $("#sendObjectIds").kendoMultiSelect({
                dataTextField : "name",
                dataValueField : "id",
                groupTemplate: "#: data #",
                dataSource:dataSource
            }).data("kendoMultiSelect");


        }
    });

}


function selectUser() {
    var userIds = "";
    var dataItems = $sendUserIds_MultiSelect.dataItems();
    if (dataItems && dataItems.length >0) {
        var num = dataItems.length;
        $.each(dataItems, function (n, value) {
            if (n == num - 1) {
                userIds += value.id;
            } else {
                userIds += value.id + ",";
            }

        });

    }
    var _dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: '${ctxAdmin}/sys/user/organUserTreePage?checkedUserIds=' + userIds,
        width: 500,
        height: 360,
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

function setSelectUser() {
    var selectUserIds = new Array();
    var checkNodes = $("#organUserTree").tree("getChecked");
    $.each(checkNodes,function(i,node){
        if("u" ==node.attributes.nType){
            selectUserIds.push(node.id);
        }
    })
    $sendUserIds_MultiSelect.value(selectUserIds);
}

//查看邮件
function view(emailId) {
    var url = '${ctxAdmin}/mail/email/view/' + emailId;
    var  _dialog = $('<div/>').dialog({
        title: '查看邮件',
        width: document.body.clientWidth - 90,
        height: document.body.clientHeight - 10,
        modal: true,
        maximizable: true,
        content: '<iframe id="email_view_iframe" scrolling="no" frameborder="0"  src="' + url + '" ></iframe>',
        buttons: [{
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function() {
                _dialog.dialog('destroy');
            }
        }],
        onClose: function() {
            $(this).dialog('destroy');
        }
    });
}


//清除查询条件并初始化树节点
function rest() {
    $search_form.form('reset');
}
//搜索
function find() {
    $email_datagrid.datagrid('load', $.serializeObject($search_form));
}

function removeEmail(rowIndex) {
    var rows = new Array();
    var tipMsg = "您确定要删除选中的邮件吗?";
    if(rowIndex != undefined){
        $email_datagrid.datagrid('unselectAll');
        $email_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $email_datagrid.datagrid('getSelected');
        rows.push(rowData);
        $email_datagrid.datagrid('unselectRow', rowIndex);
        tipMsg = "您确定要删除邮件["+rowData["title"]+"]？";
    }else{
        rows = $email_datagrid.datagrid('getSelections');
    }

    if (rows.length > 0) {
        $.messager.confirm('确认提示！', tipMsg,function(r) {
            if (r) {
                var ids = new Array();
                $.each(rows,
                function(i, rows) {
                    ids[i] = rows.id;
                });
                $.ajax({
                    url: '${ctxAdmin}/mail/email/removeEmail',
                    type: 'post',
                    data: { ids: ids},
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('load'); // reload the user data
                            eu.showMsg(data.msg); //操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("请选中需要删除的记录!");
    }
}


/**
 * 查看邮件阅读情况
 **/
function emailReadInfo(emailId,emailTitle) {
    var inputUrl = "${ctxAdmin}/mail/email/readInfo/" + emailId;
    var _dialog = $('<div/>').dialog({
        title: '邮件['+emailTitle+']阅读情况',
        width: 500,
        height: 400,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [{
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function() {
                _dialog.dialog('destroy');
            }
        }],
        onClose: function() {
            _dialog.dialog('destroy');
        }
    });
}
</script>
<div class="easyui-layout" fit="true" style="margin: 0px; border: 0px; overflow: hidden; width: 100%; height: 100%;">
   <div data-options="region:'north',title:'过滤条件',collapsed:false,split:false,border:false"
            style="width: 100%;height:102px;overflow-y: hidden;">
                <form id="search_form" style="padding: 5px;">
	                 <table style="border: 0">
		                <tr>
		                    <td>邮件主题：</td>
		                    <td><input name="title" maxLength="25" placeholder="邮件主题" class="easyui-validatebox textbox eu-input"  style="width: 260px" /></td>
                            <td>&nbsp;</td>
		                    <td>发送时间：</td>
		                    <td><input id="startTime" name="startTime" placeholder="起始时间" type="text" class="easyui-my97" style="width:120px;">
		                        ～
		                        <input id="endTime" name="endTime" placeholder="截止时间"  type="text" class="easyui-my97" style="width:120px;"></td>
		                </tr>
                         <tr>
                             <td>发件人：</td>
                             <td ><select id="sendObjectIds" name="sendObjectIds" placeholder="发件人" style="width: 260px;"></select></td>
                             <td ><a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" onclick="selectUser();">选择</a></td>
                             <td>&nbsp;</td>
                             <td>
                                 <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:find">查询</a>
                                 <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no'" onclick="rest();">重置查询</a>
                             </td>
                         </tr>
	                 </table>
                </form>
                    
            </div>
    <div data-options="region:'center',split:false,border:false"  style="height: 100%; width: 100%; overflow : auto;">
            <div data-options="region:'center',split:false,border:false" style="height: 100%;width:100%; overflow: auto;">
                  <table id="email_datagrid" ></table>
            </div>
          
    </div>
</div>
  