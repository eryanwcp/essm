var emailId = emailId;

/* boxType UnreadInbox,Inbox, Outbox,Draftbox,RecycleBin //收件箱（未读）、收件箱、发件箱、草稿箱、回收站*/
var $email_datagrid;
var $email_search_form;
var $email_input_dialog;
var $email_view_dialog;
var $selectrecvs_dialog;
var $form_to_MultiSelect,$form_cc_MultiSelect;//email-input.jsp


var $datagrid_query_sender_MultiSelect;
var $datagrid_query_receiver_MultiSelect;

var $datagrid_query_MultiSelect;
var $email_form;

var inbox_linkbutton_text = "收件箱";
var _operateType;

var currentMailAccountId = "";
$(function() {
    receiveObjectIdsDisable();
    $email_search_form = $('#email_search_form').form();

    $("#sta").hide();
    $("#sta1").hide();
    initInboxDatagrid(true);//未读

    initSelectUser();
    if(emailId != "") {
        view("Inbox",emailId);
    }
    mymessage();
});

function refreshMessage(){
    mymessage();
    //刷新 portal消息
    parent.refreshPortal();
}

function mymessage(){
    $.ajax({
        url:ctxAdmin + '/mail/email/myMessage?mailAccountId='+currentMailAccountId,
        type:'get',
        dataType:'json',
        success:function(data) {
            if (data.code==1){
                var obj = data.obj;
                if(obj["inboxs"]>0){
                    var text = inbox_linkbutton_text + "&nbsp;<span style='color: red;'>（"+obj["inboxs"]+"）</span>";
                    $("#inbox_linkbutton").linkbutton({text:text});
                }else{
                    $("#inbox_linkbutton").linkbutton({text:inbox_linkbutton_text});
                }

            }
        }
    });
}



function initEmailDatagrid(boxType) {
    resetSearchForm();
    if (boxType == "Outbox") {//发件箱
        $("#recv").show();
        $("#sta").hide();
        $("#sta1").hide();
        sendObjectIdsDisable();
        receiveObjectIdsEnable();
        initOutboxDatagrid();
    } else if (boxType == "Draftbox") {//草稿箱
        $("#recv").hide();
        $("#sta").hide();
        $("#sta1").hide();
        sendObjectIdsEnable();
        receiveObjectIdsDisable();
        initOutboxDatagrid(true);
    } else if (boxType == "Inbox" || boxType == "UnreadInbox") {//收件箱（包含未读）
        $("#recv").show();
        if(boxType == "UnreadInbox"){
            $("#sta").hide();
            $("#sta1").hide();
        }else{
            $("#sta").show();
            $("#sta1").show();
        }
        sendObjectIdsEnable();
        receiveObjectIdsDisable();
        initInboxDatagrid(boxType == "UnreadInbox")
    } else if (boxType == "RecycleBin") {//回收站
        $("#recv").show();
        $("#sta").hide();
        $("#sta1").hide();
        sendObjectIdsEnable();
        receiveObjectIdsDisable();
        initRecycleBinDatagrid();
    }

}

/**
 * 收件箱（包含未读）
 * @param UnreadInbox 是否未读
 **/
function initInboxDatagrid(UnreadInbox) {
    var boxType = "Inbox";
    if(UnreadInbox != undefined && UnreadInbox == true){
        boxType = "UnreadInbox";
    }
    $email_datagrid = $('#email_datagrid').datagrid({
        url : ctxAdmin + '/mail/email/'+boxType+'Datagrid',
        fit : true,
        pagination : true,//底部分页
        fitColumns : false,//自适应列宽
        striped : true,//显示条纹
        pageSize : 20,//每页记录数
        singleSelect : false,//单选模式
        rownumbers : true,//显示行数
        checkbox : true,
        nowrap : true,
        border : false,
        checkOnSelect:false,
        selectOnCheck:false,
        remoteSort : false,//是否通过远程服务器对数据排序
        //sortName : 'receiveTime',//默认排序字段
        sortName : 'receiveTime',//默认排序字段
        sortOrder : 'desc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        frozenColumns : [ [
            {field : 'ck',checkbox : true},
            {field : 'senderName',title : '发件人',width : 120},
            {field : 'title',
                title : '邮件主题',
                width : 360,
                formatter : function(value, rowData, index) {
                    if(rowData["senderName"] == undefined){
                        value =  value + "[<font color=#D94600>匿名</font>]";
                    }
                    var valueHtml = "<a href='#' onclick='view(\""+boxType+"\",\""+rowData["emailId"]+"\")' >"+value+"</a>";
                    var ur = "";
                    if (rowData["isRead"] == 0) {
                        ur += '<img alt="未读" title="未读邮件" src="'+ctxStatic+'/app/modules/mail/img/new.gif">&nbsp;' + valueHtml;
                    } else {
                        ur += valueHtml;
                    }
                    if (rowData["priority"] == 1) {
                        ur += ' [<span style="color:#ff6600;">' + rowData["priorityView"] + '</span>]';
                    }
                    if (rowData["priority"] == 2) {
                        ur += ' [<span style="color:#ff0000;">' + rowData["priorityView"] + '</span>]';
                    }

                    return ur;
                }

            } ] ],
        columns : [ [ {
            field : 'id',
            title : '主键',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'receiveTime',
            title : '接收时间',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'sendTime',
            title : '发送时间',
            width : 150,
            sortable : true
        }, {
            field : 'isReadView',
            title : '邮件状态',
            width : 80
        }, {
            field : 'priorityView',
            title : '优先级',
            width : 80
        }, {
            field : 'operate',
            title : '操作',
            formatter : function(value, rowData, index) {
                var operateHtml = "<a href='#' class='easyui-linkbutton' data-options='iconCls:\"eu-icon-mail_reply_sender\"' onclick='edit(\"Reply\",\""+rowData["emailId"]+"\")' >回复</a>"
                    +"&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"eu-icon-mail_forward\"' onclick='edit(\"Repeat\",\""+rowData["emailId"]+"\")' >转发</a>";
                return operateHtml;
            }
        } ] ],
        queryParams: {mailAccountId:currentMailAccountId},
        toolbar : [ {
            text : '标记为已读',
            iconCls : 'eu-icon-mail_mark_read',
            handler : function() {
                markReaded();
            }
        }, {
            text : '删除',
            iconCls : 'easyui-icon-remove',
            handler : function() {
                del(boxType);
            }
        } ],
        onLoadSuccess : function() {
            $(this).datagrid('clearSelections');
            $(this).datagrid('clearChecked');
            $(this).datagrid('unselectAll');
            $(this).datagrid('uncheckAll');
        }
    }).datagrid('showTooltip');
}


/**
 * 发件箱（包含草稿）
 * @param isDraft 是否草稿
 **/
function initOutboxDatagrid(isDraft) {
    var boxType = "Outbox";
    var toolbar = [{
        text : '删除',
        iconCls : 'easyui-icon-remove',
        handler : function() {
            del(boxType);
        }
    }];
    if(isDraft != undefined && isDraft == true){
        boxType = "Draftbox";
    }
    $email_datagrid = $('#email_datagrid').datagrid({
        url : ctxAdmin + '/mail/email/'+boxType+'Datagrid',
        fit : true,
        pagination : true,//底部分页
        pagePosition : 'bottom',//'top','bottom','both'.
        fitColumns : false,//自适应列宽
        striped : true,//显示条纹
        pageSize : 20,//每页记录数
        singleSelect : false,//单选模式
        rownumbers : true,//显示行数
        checkbox : true,
        nowrap : true,
        border : false,
        remoteSort : false,//是否通过远程服务器对数据排序
        sortName : 'updateTime',//默认排序字段
        sortOrder : 'desc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        frozenColumns : [ [ {
            field : 'ck',
            checkbox : true
        } ] ],
        columns : [ [ {
            field : 'id',
            title : '主键',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'title',
            title : '邮件主题',
            width : 360,
            formatter:function(value, rowData, index){
                if(rowData['outboxMode'] == 3){
                    value += '<span style="color: red;">'+rowData['outboxModeView']+'</span>';
                }
                var valueHtml = "<a href='#' onclick='view(\""+boxType+"\",\""+rowData["emailId"]+"\")' >"+value+"</a>";
                return valueHtml;
            }
        }, {
            field : 'sendTime',
            title : '时间',
            width : 150,
            sortable : true,
            formatter:function(value, rowData, index){
                if(rowData['sendTime'] == undefined){
                    return rowData['createTime'];
                }
                return value;
            }
        }, {
            field : 'priorityView',
            title : '优先级',
            width : 80
        } , {
            field : 'updateTime',
            title : '更新时间',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'operate',
            title : '操作',
            hidden:false,
            formatter:function(value, rowData, index){
                var operateHtml = "";
                if(isDraft != undefined && isDraft == true){
                    operateHtml += "&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' onclick='edit(null,\"" + rowData["emailId"] + "\");' >编辑</a>"
                        + "&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"eu-icon-mail_forward\"' onclick='sendEmail(\"" + rowData["emailId"] + "\");' >发送</a>";
                }else{
                    operateHtml += "&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"eu-icon-mail_forward\"' onclick='edit(\"Repeat\",\"" + rowData["emailId"] + "\");' >转发</a>"
                }
                operateHtml += "&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='del(\"Outbox\"," + index + ");' >删除</a>";
                if(rowData['isReceivesRead'] == false && rowData['mailType'] ==0 && rowData['mailAccountId'] =='' && rowData['outboxMode'] == 0 ){//未读 站内邮件 并且为已发送
                    operateHtml += "&nbsp;<a href='#' class='easyui-linkbutton' onclick='revoke(" + index + ");' >撤销</a>";
                }
                if(isDraft == undefined || isDraft == false){
                    operateHtml += "&nbsp;<a href='#' class='easyui-linkbutton' data-options='iconCls:\"eu-icon-mail_find\"' onclick='emailReadInfo(\"" + rowData["emailId"] + "\");' >阅读情况</a>";
                }
                return operateHtml;
            }
        }] ],
        queryParams: {mailAccountId:currentMailAccountId},
        toolbar :toolbar,
        onLoadSuccess : function() {
            $(this).datagrid('clearSelections');
            $(this).datagrid('clearChecked');
            $(this).datagrid('unselectAll');
            $(this).datagrid('uncheckAll');
        }
    }).datagrid('showTooltip');
}


/**
 * 回收站
 **/
function initRecycleBinDatagrid() {
    var boxType = "RecycleBin";
    $email_datagrid = $('#email_datagrid').datagrid({
        url : ctxAdmin + '/mail/email/'+boxType+'Datagrid',
        fit : true,
        pagination : true,//底部分页
        pagePosition : 'bottom',//'top','bottom','both'.
        fitColumns : false,//自适应列宽
        striped : true,//显示条纹
        pageSize : 20,//每页记录数
        singleSelect : false,//单选模式
        rownumbers : true,//显示行数
        checkbox : true,
        nowrap : true,
        border : false,
        remoteSort : false,//是否通过远程服务器对数据排序
        sortName : 'delTime',//默认排序字段
        sortOrder : 'desc',//默认排序方式 'desc' 'asc'
        idField : 'id',
        frozenColumns : [ [ {
            field : 'ck',
            checkbox : true
        } ] ],
        columns : [ [ {
            field : 'id',
            title : '主键',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'senderName',
            title : '发件人',
            width : 80
        }, {
            field : 'title',
            title : '邮件主题',
            width : 360,
            formatter:function(value, rowData, index){
                var valueHtml = "<a href='#' onclick='view(\""+boxType+"\",\""+rowData["emailId"]+"\")' >"+value+"</a>";
                return valueHtml;
            }
        }, {
            field : 'delTime',
            title : '删除时间',
            width : 146,
            sortable : true
        } , {
            field : 'updateTime',
            title : '更新时间',
            hidden : true,
            sortable : true,
            align : 'right',
            width : 80
        }, {
            field : 'priorityView',
            title : '优先级',
            width : 100
        }, {
            field : 'fromBoxView',
            title : '来源',
            width : 100
        } ] ],
        queryParams: {mailAccountId:currentMailAccountId},
        toolbar : [ {
            text : '恢复',
            iconCls : 'eu-icon-mail_restore',
            handler : function() {
                reduce();
            }
        }, '-', {
            text : '永久删除',
            iconCls : 'eu-icon-mail_delete_complete',
            handler : function() {
                clearEmail();
            }
        } ],
        onLoadSuccess : function() {
            $(this).datagrid('clearSelections');
            $(this).datagrid('clearChecked');
            $(this).datagrid('unselectAll');
            $(this).datagrid('uncheckAll');
        }
    }).datagrid('showTooltip');
}

function convertValues(MultiSelect) {
    var query = "";
    if(MultiSelect != undefined){
        query = MultiSelect.input.val();

    }
    var data = {};
    data["query"] = query;
    return data;
}
function initSelectUser() {
    var mailAccountId = currentMailAccountId;
    if("1" == currentMailAccountId){//站内邮箱 特殊处理
        mailAccountId = "";
    }
    //发件人
    $datagrid_query_sender_MultiSelect = $("#sendObjectIds").kendoMultiSelect({
        dataTextField : "name",
        dataValueField : "id",
        groupTemplate: "#: data #",
        dataSource: {
            transport: {
                read: {
                    url: ctxAdmin + '/sys/user/customUserList',
                    dataType: 'json'
                },
                parameterMap: function(data, type) {
                    if (type == "read") {
                        return convertValues($datagrid_query_sender_MultiSelect);
                    }
                }
            },
            serverFiltering:true,
            group: { field: "defaultOrganName" }
        }
    }).data("kendoMultiSelect");
    //收件人
    $datagrid_query_receiver_MultiSelect = $("#receiveObjectIds").kendoMultiSelect({
        dataTextField : "name",
        dataValueField : "id",
        groupTemplate: "#: data #",
        dataSource: {
            transport: {
                read: {
                    url: ctxAdmin + '/sys/user/customUserList',
                    dataType: 'json'
                },
                parameterMap: function(data, type) {
                    if (type == "read") {
                        return convertValues($datagrid_query_receiver_MultiSelect);
                    }
                }
            },
            serverFiltering:true,
            group: { field: "defaultOrganName" }
        }

    }).data("kendoMultiSelect");

}


function receiveObjectIdsEnable(){
    $("#receiveObjectIds_label").show();
    $("#receiveObjectIds_input").show();
    $("#receiveObjectIds_op_input").show();
}
function receiveObjectIdsDisable(){
    $("#receiveObjectIds_label").hide();
    $("#receiveObjectIds_input").hide();
    $("#receiveObjectIds_op_input").hide();
}

function sendObjectIdsEnable(){
    $("#sendObjectIds_label").show();
    $("#sendObjectIds_input").show();
    $("#sendObjectIds_op_input").show();
}
function sendObjectIdsDisable(){
    $("#sendObjectIds_label").hide();
    $("#sendObjectIds_input").hide();
    $("#sendObjectIds_op_input").hide();
}

/**
 * 邮件恢复
 **/
function reduce() {
    var rows =  $email_datagrid.datagrid('getChecked');
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要恢复所有行?', function (r) {
            if (r) {
                var ids = new Array();
                $.each(rows, function (i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/mail/email/reduce',
                    type: 'post',
                    data: {recycleBinIds: ids},
                    dataType: 'json',
                    traditional: true,
                    success: function (data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});	// reload the user data
                            eu.showMsg(data.msg);//操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("请选择要操作的对象！");
    }

}

function edit(operateType,emailId,receiveObjectType,receiveObjectId) {
    var mailAccountId = currentMailAccountId;
    if("1" == currentMailAccountId){//站内邮箱 特殊处理
        mailAccountId = "";
    }
    var inputUrl = ctxAdmin + '/mail/email/input?operateType=';
    if (operateType != undefined) {
        inputUrl += operateType;
    }
    if (emailId != undefined) {
        inputUrl += "&id=" + emailId;
    }

    inputUrl += "&mailAccountId="+mailAccountId;

    if("QuickWriteMail" == operateType){//快速写邮件
        if(receiveObjectType != undefined){
            inputUrl += "&receiveObjectType="+receiveObjectType;
        }
        if(receiveObjectId != undefined){
            inputUrl += "&receiveObjectId="+receiveObjectId;
        }
        if($email_view_dialog != undefined){
            $email_view_dialog.dialog("destroy");
        }
    }

    //弹出对话窗口
    $email_input_dialog = $('<div/>').dialog({
        title: '邮件',
        width: document.body.clientWidth,
        height: document.body.clientHeight,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [{
            text: '发送',
            iconCls: 'eu-icon-mail_forward',
            handler: function() {
                _operateType = "Send";
                $email_form.submit();
            }
        },{
            text: '保存草稿',
            iconCls: 'easyui-icon-save',
            handler: function() {
                _operateType = "SaveDraft";
                $email_form.submit();
            }
        },{
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function() {
                if ($email_datagrid) {
                    $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
                }
                $email_input_dialog.dialog('destroy');
            }
        }],
        onClose: function() {
            if ($email_datagrid) {
                $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
            }
            $email_input_dialog.dialog('destroy');
        },
        onLoad: function() {
            formInit();
        }
    });
}

/**
 * 邮件撤销
 **/
function revoke(rowIndex) {
    $email_datagrid.datagrid('unselectAll');
    $email_datagrid.datagrid('selectRow', rowIndex);
    var rowData = $email_datagrid.datagrid('getSelected');
    $email_datagrid.datagrid('unselectRow', rowIndex);
    $.messager.confirm('确认提示！', '您确定要撤销发送邮件[' + rowData['title'] + ']?',
        function(r) {
            if (r) {
                $.ajax({
                    url: ctxAdmin + '/mail/email/revoke/'+rowData['emailId'],
                    type: 'post',
                    data: {},
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId}); // reload the user data
                            eu.showMsg(data.msg); //操作结果提示
                            refreshMessage();
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
}

/**
 * 查看邮件阅读情况
 **/
function emailReadInfo(emailId) {
    var inputUrl = ctxAdmin + '/mail/email/readInfo/' + emailId;
    var _dialog = $('<div/>').dialog({
        title: '邮件阅读情况',
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

/**
 * @param boxType 类型
 * @param emailId 邮件ID
 * 邮件查看
 **/
function view(boxType, emailId,anonymous) {
    var closeObj = {
        text: '关闭',
        iconCls: 'easyui-icon-cancel',
        handler: function() {
            $email_view_dialog.dialog('destroy');
            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
            refreshMessage();
        }
    };
    var buttonsToolbars = new Array();

    var replyObj = {
        text: '回复',
        iconCls: 'eu-icon-mail_reply_sender',
        handler: function() {
            $email_view_dialog.dialog('destroy');
            edit('Reply',  emailId);
        }
    };
    var repeatObj = {
        text: '转发',
        iconCls: 'eu-icon-mail_forward',
        handler: function() {
            $email_view_dialog.dialog('destroy');
            edit('Repeat', emailId);
        }
    };
    if ("Inbox" == boxType || "UnreadInbox" == boxType) {
        if('1'==anonymous){
            buttonsToolbars.push(replyObj);
        }
    }
    buttonsToolbars.push(repeatObj);

    buttonsToolbars.push(closeObj);
    var url = ctxAdmin + '/mail/email/view/' + emailId;
    $email_view_dialog = $('<div/>').dialog({
        title: '查看邮件',
        width: document.body.clientWidth - 90,
        height: document.body.clientHeight - 10,
        modal: true,
        maximizable: true,
        content: '<iframe id="email_view_iframe" scrolling="no" frameborder="0"  src="' + url + '" ></iframe>',
        buttons: buttonsToolbars,
        onClose: function() {
            $(this).dialog('destroy');
            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
            refreshMessage();
        }
    });
}

/**
 * 发送邮件
 **/
function sendEmail(emailId) {
    $.post(ctxAdmin + '/mail/email/sendEmail/'+emailId,{},function(data) {
        var json = $.parseJSON(data);
        if (json.code == 1) {
            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId}); // reload the user data
            eu.showMsg(json.msg); //操作结果提示
        } else {
            eu.showAlertMsg(json.msg, 'error');
        }
    });
}

function formInit() {
    $email_form = $('#email_form').form({
        url: ctxAdmin + '/mail/email/_save',
        //目标地址
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            if ($form_to_MultiSelect && $form_to_MultiSelect.value().length == 0) {
                $.messager.progress('close');
                eu.showTopCenterMsg("收件人不能为空，请设置收件人!");
                $form_to_MultiSelect.focus();
                return false;
            }
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }else{
                param.operateType = _operateType;
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
                $email_input_dialog.dialog('destroy');
                eu.showMsg(json.msg);
            } else if (json.code == 2) {

            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}
//永久删除
function clearEmail() {
    var rows =  $email_datagrid.datagrid('getChecked');
    if (rows.length > 0) {
        var title = '您确定要永久删除选中的邮件?';
        $.messager.confirm('确认提示！', title,function(r) {
            if (r) {
                var ids = new Array();
                $.each(rows,function(i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/mail/email/clearRecycleBin',
                    type: 'post',
                    data: { recycleBinIds: ids},
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('load',{mailAccountId:currentMailAccountId}); // reload the user data
                            eu.showMsg(data.msg); //操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("请选择要操作的对象！");
    }
}

/**
 * 删除
 **/
function del(boxType,rowIndex) {
    var rows = new Array();
    var tipMsg = "您确定要删除选中的所有行?";
    if(rowIndex != undefined){
        $email_datagrid.datagrid('unselectAll');
        $email_datagrid.datagrid('selectRow', rowIndex);
        var rowData = $email_datagrid.datagrid('getSelected');
        rows.push(rowData);
        $email_datagrid.datagrid('unselectRow', rowIndex);
        tipMsg = "您确定要删除["+rowData["title"]+"]？";
    }else{
        rows = $email_datagrid.datagrid('getChecked');
    }
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', tipMsg,function(r) {
            if (r) {
                var ids = new Array();
                $.each(rows,function(i, row) {
                    ids[i] = row.id;
                });
                $.ajax({
                    url: ctxAdmin + '/mail/email/_remove',
                    type: 'post',
                    data: { ids: ids, boxType: boxType},
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('load',{mailAccountId:currentMailAccountId}); // reload the user data
                            eu.showMsg(data.msg); //操作结果提示
                            refreshMessage();
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("请选择要操作的对象！");
    }

}
function markReaded() {
    var rows = $email_datagrid.datagrid('getChecked');
    if (rows.length > 0) {
        $.messager.confirm('确认提示！', '您确定要标记所有行为已读？',function(r) {
            if (r) {
                var ids = new Array();
                $.each(rows,
                    function(i, row) {
                        ids[i] = row.id;
                    });
                $.ajax({
                    url: ctxAdmin + '/mail/email/markReaded',
                    type: 'post',
                    data: {inboxIds: ids},
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        if (data.code == 1) {
                            $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId}); // reload the user data
                            eu.showMsg(data.msg); //操作结果提示
                            refreshMessage();
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });
    } else {
        eu.showMsg("请选择要操作的对象！");
    }
}

//搜索
function search() {
    var queryData = {};
    var mailAccountData = {mailAccountId:currentMailAccountId};
    var formData = $.serializeObject($email_search_form);
    queryData = $.extend(queryData, mailAccountData, formData);//合并对象

    $email_datagrid.datagrid('load', queryData);
}

/**
 * 重置查询条件
 */
function resetSearchForm() {
    $email_search_form.form('reset');
}


function selectSendUser(){
    $datagrid_query_MultiSelect = $datagrid_query_sender_MultiSelect;
    selectUser()
}

function selectPublishUser(){
    $datagrid_query_MultiSelect = $datagrid_query_receiver_MultiSelect;
    selectUser()
}

function selectUser() {
    var userIds = "";
    var dataItems = $datagrid_query_MultiSelect.dataItems();
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
    _dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: ctxAdmin + '/sys/user/organUserTreePage?checkedUserIds='+userIds,
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


function setSelectUser() {
    var selectUserIds = new Array();
    var checkNodes = $("#organUserTree").tree("getChecked");
    $.each(checkNodes,function(i,node){
        if("u" ==node.attributes.nType){
            selectUserIds.push(node.id);
        }
    })
    $datagrid_query_MultiSelect.value(selectUserIds);
}

function toggleMailAccount(mailAccountId,mailAccountName){
    currentMailAccountId = mailAccountId;
    if(mailAccountName){
        $("#btn_mailAccount").menubutton({text:mailAccountName});
    }
    refreshMessage();
    resetSearchForm();
    $email_datagrid.datagrid('reload',{mailAccountId:currentMailAccountId});
    //initInboxDatagrid(true);//未读
    //initSelectUser();
}

/**
 * 收件
 */
function receiveMail(){
    var mailAccountId = currentMailAccountId;
    if("1" == currentMailAccountId){//站内邮箱 特殊处理
        mailAccountId = "";
    }

    $.ajax({
        type: "post",
        dataType: 'json',
        contentType: "application/json",
        url: ctxAdmin + '/mail/email/receiveMail?mailAccountId='+mailAccountId,
        //async: true,
        success: function (data) {
            if (data.code == 1) {
                refreshMessage();
                eu.showMsg(data.msg);
            } else {
                eu.showAlertMsg(data.msg);
            }


        }
    });
}