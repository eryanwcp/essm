var jsessionid = jsessionid;
var modelId = modelId;
var modelIsPub = modelIsPub;
var modelIsTip = modelIsTip;

$(function () {
    //日志类型 搜索选项
    $('#versionLogType').combobox({
        url:ctxAdmin + '/sys/versionLog/versionLogTypeCombobox?selectType=select',
        editable:false,//是否可编辑
        width:120
    });
    uploadify();

    if(modelId == ""){  //新增
        $("input[name=isPub]:eq(0)").prop("checked",'checked');//状态 初始化值
        $("input[name=isTip]:eq(0)").prop("checked",'checked');//状态 初始化值
    }else{
        $('input[name=isPub][value='+modelIsPub+']').prop("checked",'checked');
        $('input[name=isTip][value='+modelIsTip+']').prop("checked",'checked');
    }
});

function uploadify() {
    $('#uploadify').uploadify({
        method: 'post',
        swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',
        buttonText: '浏  览',
        uploader: ctxAdmin + '/sys/versionLog/upload;jsessionid='+jsessionid,
        formData:{jsessionid:jsessionid},
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: true,
        fileSizeLimit: '100MB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
        fileTypeDesc: '全部文件',
        fileTypeExts: '*.*',
        onUploadSuccess: function (file, data, response) {
            data = eval("(" + data + ")");
            var fileId="";
            if(data.code != undefined && data.code == 1){
                fileId=data.obj;
            }
            $('#' + file.id).find('.data').html(data.msg);
            $("#fileId").val(fileId);
            var uploadify = this;

        }

    });
}

function loadOrOpen(fileId) {
    $('#annexFrame').attr('src', ctxAdmin + '/disk/fileDownload/' + fileId);
}

function delUpload(id){
    $("#fileId").val("");
    $("#"+id).parent().remove();
}