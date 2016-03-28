var jsessionid = jsessionid;
var toIds = toIds;
var ccIds = ccIds;
var modelPriority = modelPriority;
var PREFIX_USER = PREFIX_USER;
var fileSizeLimit = fileSizeLimit;

var $form_to_MultiSelect = undefined;
var $form_cc_MultiSelect = undefined;
var $form_CurrentUser_MultiSelect = undefined;
var current_input_values = new Array();
var $form_selectUser_dialog = undefined;
var _currentMailAccountId = _mailAccountId;

function isString(s) {
    return typeof(s) === 'string' || s instanceof String;
}
var inputTOValue = '';
var inputCCValue = '';
var toValues = toIds;
var ccValues = ccIds;
if(toValues == undefined || isString(toValues) || toValues.length == 0){
    toValues = new Array();
}
if(ccValues == undefined || isString(ccValues) || ccValues.length == 0){
    ccValues = new Array();
}

$(function() {
//        loadMailAccount();
    loadMailPriority();
    initFormUser();
    uploadify();
    editor();
});

function editor(){
    $("#editor").kendoEditor({
        encoded: false,
        resizable: {
            content: true,
            toolbar: true
        }
    });
}

function setCurrentUserMultiSelect(type){
    if (type == 'cc') {
        $form_CurrentUser_MultiSelect = $form_cc_MultiSelect;
        current_input_values = ccValues;
    } else {
        $form_CurrentUser_MultiSelect = $form_to_MultiSelect;
        current_input_values = toValues;
    }

}
/*选择收件人或者抄送人*/
function selectFormUser(type) {
    setCurrentUserMultiSelect(type);
    var dataItem = $form_CurrentUser_MultiSelect.dataItems();

    var userIds = '';
    if (dataItem != null) {
        var num = dataItem.length;
        var i = 0;
        $.each(dataItem,function(n, value) {
            var _id = value.id;
            if(_id.substring(0,3) == PREFIX_USER){
                _id = _id.substring(3,_id.length);
                i++;
                if (i == 0) {
                    userIds += _id;
                } else {
                    userIds += "," + _id ;
                }
            }

        });
    }

    $form_selectUser_dialog = $("<div/>").dialog({
        title: "选择",
        top: 10,
        href: ctxAdmin + '/sys/user/select?dataScope=1&cascade=true&userIds=&excludeUserIds='+userIds.toString(),
        width: '760',
        height: '450',
        maximizable: true,
        iconCls: 'easyui-icon-edit',
        modal: true,
        buttons: [{
            text: '确定',
            iconCls: 'easyui-icon-save',
            handler: function() {
                _setSelectUser();
            }
        },{
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function() {
                $form_selectUser_dialog.dialog('destroy');
            }
        }],
        onClose: function() {
            $form_selectUser_dialog.dialog('destroy');
        },
        onLoad: function() {
        }
    });
}
function _setSelectUser() {
    var values = $form_CurrentUser_MultiSelect.value().slice();
    $("#selectUser option").each(function() {
        var txt = $(this).val();
        var id = PREFIX_USER+$.trim(txt);
        $form_CurrentUser_MultiSelect.dataSource.add({id:id,name:id,group:id});
        current_input_values.push(id);
        values.push(id);
    });

    $form_CurrentUser_MultiSelect.dataSource.filter({});
    $form_CurrentUser_MultiSelect.value(values);

    $form_selectUser_dialog.dialog('destroy'); //销毁对话框;
}

function convertValues(MultiSelect,values) {
    values = $.isArray(values) ? values : [values];
    var query = "";
    var includeIds = "";
    if(MultiSelect != undefined){
        query = MultiSelect.input.val();
        var inputValues = MultiSelect.value();
        if(inputValues != undefined && inputValues.length > 0){
            values = $.unique($.merge(values,inputValues));
        }

    }
    if(values != undefined){
        includeIds = values.join(",");
    }
    var data = {};
    data["mailAccountId"] = _currentMailAccountId;
    data["query"] = query;
    data["includeIds"] = includeIds;
    return data;
}

function initFormUser() {
    //接收人
    $form_to_MultiSelect = $("#_toIds").kendoMultiSelect({
        dataTextField: "name",
        dataValueField: "id",
        groupTemplate: "#: data #",
        itemTemplate:kendo.template($("#itemTemplate").html()),
        tagTemplate:kendo.template($("#itemTemplate").html()),
        autoSync: true,
        dataSource: {
            transport: {
                read: {
                    url: ctxAdmin + '/mail/mailContact/multiSelectPrefix',
                    type: "post",
                    dataType: 'json'
                },
                parameterMap: function(data, type) {
                    if (type == "read") {
                        return convertValues($form_to_MultiSelect,toValues);
                    }
                }
            },
            serverFiltering:true,
            group: { field: "group" }
        },
        value: toIds,
        dataBound: function(e) {
            inputTOValue = $form_to_MultiSelect.input.val();
            dataBound($form_to_MultiSelect,toValues);
        }
    }).data("kendoMultiSelect");

    // Bind  blur event
    $form_to_MultiSelect.input.bind("blur", function() {
        autoAddEmail($form_to_MultiSelect,inputTOValue,toValues);
    });
    //抄送人
    $form_cc_MultiSelect = $("#ccIds").kendoMultiSelect({
        dataTextField: "name",
        dataValueField: "id",
        groupTemplate: "#: data #",
        itemTemplate:kendo.template($("#itemTemplate").html()),
        tagTemplate:kendo.template($("#itemTemplate").html()),
        autoSync: true,
        dataSource: {
            transport: {
                read: {
                    url: ctxAdmin + '/mail/mailContact/multiSelectPrefix',
                    type: "post",
                    dataType: 'json'
                },
                parameterMap: function(data, type) {
                    if (type == "read") {
                        return convertValues($form_cc_MultiSelect,ccValues);
                    }
                }
            },
            serverFiltering:true,
            group: { field: "group" }
        },
        value: ccIds,
        dataBound: function(e) {
            inputCCValue = $form_cc_MultiSelect.input.val();
            dataBound($form_cc_MultiSelect,ccValues);
        }
    }).data("kendoMultiSelect");
    // Bind  blur event
    $form_cc_MultiSelect.input.bind("blur", function() {
        autoAddEmail($form_cc_MultiSelect,inputCCValue,ccValues);
    });
}
function dataBound(MultiSelect,inputValues){
    if("" == _currentMailAccountId){
        return;
    }

    if(!MultiSelect){
        return;
    }
    var inputDom = MultiSelect.input;
    var inputStr = inputDom.val();
    var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if (inputStr.charAt(inputStr.length - 1) == ';' || inputStr.charAt(inputStr.length - 1) == '；') {
        inputDom.val('');
        var email = inputStr.substring(0, inputStr.length - 1);

        $.ajax({
            type: "post",
            dataType: 'json',
            contentType: "application/json",
            url: ctxAdmin + '/mail/mailContact/autoAdd?email='+email,
            async: true,
            success: function (data) {
                if (data.code == 1) {
                    var obj = data.obj;
                    MultiSelect.dataSource.add(obj);
                    var values = MultiSelect.value().slice();
                    values.push(obj["id"]);
                    inputValues.push(obj["id"]);
                    MultiSelect.dataSource.filter({});
                    MultiSelect.value(values);
                } else {
                    inputDom.val(email);
                    eu.showMsg(data.msg);
                }


            }
        });
    }
}

function autoAddEmail(MultiSelect,inputValue,inputValues){
    if("" == _currentMailAccountId){
        return;
    }
    if(!MultiSelect){
        return;
    }
    var inputDom = MultiSelect.input;
    var inputStr = inputValue;
    var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if (inputStr != undefined && reg.test(inputStr)) {
        $.ajax({
            url: ctxAdmin + '/mail/mailContact/autoAdd',
            data:{email:inputStr},
            type: "post",
            dataType: 'json',
            success: function (data) {
                if (data.code == 1) {
                    var obj = data.obj;
                    MultiSelect.dataSource.add(obj);
                    MultiSelect.dataSource.sync();
                    var values = MultiSelect.value().slice();
                    values.push(obj["id"]);
                    inputValues.push(obj["id"]);
                    MultiSelect.dataSource.filter({});
                    MultiSelect.value(values);

                }else {
                    $.afui.toast({
                        message:data.msg,
                        position:"tc",
                        autoClose:true, //have to click the message to close
                        type:"warning"
                    });
                    MultiSelect.focus();
                }
            }
        });
    }
}
/**
 * 邮件优先级
 */
function loadMailPriority() {
    $('#priority').combobox({
        url: ctxAdmin + '/mail/email/mailPriorityCombobox',
        editable: false,
        value: modelPriority
    });
}

/**
 * 账号
 */
function loadMailAccount() {
    $('#mailAccountId').combobox({
        url: ctxAdmin + '/mail/mailAccount/combobox?selectType=站内邮箱',
        editable: false,
        value:_currentMailAccountId
    });
}

var $uploadify;
var fileIdArray = new Array();
var fileIds = $("#fileIds").val();
if(fileIds != ""){
    fileIdArray = fileIds.split(",");
}
function uploadify() {
    $uploadify = $('#uploadify').uploadify({
        swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',
        buttonText: '浏  览',
        uploader: ctxAdmin + '/mail/email/upload;jsessionid='+jsessionid,
        queueSizeLimit: 10,
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: true,
        fileSizeLimit: fileSizeLimit,
        fileTypeDesc: '所有文件',
        fileTypeExts: '*.*',
        onUploadSuccess: function(file, data, response) {
            data = eval("(" + data + ")");
            if (data.code != undefined && data.code == "1") {
                $('#' + file.id).find('.data').html(' - ' + "上传成功!");
                if(data.code != undefined && data.code == 1){
                    fileIdArray.push(data.obj);
                }
                var _fileIds = fileIdArray.join(",");
                $("#fileIds").val(_fileIds);
                var uploadify = this;
                var cancel = $('#uploadify-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
                if (cancel) {
                    cancel.attr("rel", data.obj);
                    cancel.click(function() {
                        delUpload(data.obj,file.id,uploadify);
                    });
                }
            } else {
                $('#' + file.id).find('.data').html(' - ' + "<font color=#D94600>" + data.msg + "</font>");
            }
        }
    });

};
//下载附件
function loadOrOpen(fileId) {
    $('#annexFrame').attr('src', ctxAdmin + '/disk/fileDownload/' + fileId);
}

/**
 * 删除附件 页面删除
 * @param fileId 后台File ID
 * @param pageFileId uploadify页面ID'
 * @param uploadify
 */
function delUpload(fileId,pageFileId,uploadify) {
    fileIdArray.splice($.inArray(fileId,fileIdArray),1)
    var _fileIds = fileIdArray.join(",");
    $("#fileIds").val(_fileIds);
    $('#' + fileId).remove();
    if(pageFileId){
        $('#' + pageFileId).empty();
        delete uploadify.queueData.files[pageFileId]; //删除上传组件中的附件队列
        $('#' + pageFileId).remove();
    }

}