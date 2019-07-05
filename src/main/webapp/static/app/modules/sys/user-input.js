var modelId = modelId;
var modelStatus = modelStatus;
var jsessionid = jsessionid;

var organs_combotree;
$(function() {
    loadOrgan();
    loadSex();
    uploadify();
    $(".uploadify").css({'display': 'inline-block', 'height': '24px', 'padding-right': '18px', 'outline': 'none'});
    if(modelId == ""){  //新增
        setSortValue();
        $("input[name=status]:eq(0)").prop("checked",'checked');//状态 初始化值

        $('#password_div').css('display', 'block');
        $('#repassword_div').css('display', 'block');
    }else{
        $('input[name=status][value='+modelStatus+']').prop("checked",'checked');
        $('#password_div').css('display', 'none');
        $('#repassword_div').css('display', 'none');
        $('#password_div input').removeAttr('class');
        $('#repassword_div input').removeAttr('class');
    }
});

function loadOrgan(){
    organs_combotree = $("#defaultOrganId").combotree({
        url:ctxAdmin + '/sys/organ/tree?dataScope=2&cascade=true',
        multiple:false,
        editable:false
    });
}
//性别
function loadSex(){
    $('#sex').combobox({
        url: ctxAdmin + '/sys/user/sexTypeCombobox?selectType=select',
        editable:false,
        value:'2'
    });
}

//设置排序默认值
function setSortValue() {
    $.get(ctxAdmin + '/sys/user/maxSort', function(data) {
        if (data.code == 1) {
            $('#orderNo').numberspinner('setValue',data.obj+30);
        }
    }, 'json');
}

function uploadify() {
    $('#file').uploadify({
        method: 'post',
        swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',  //FLash文件路径
        buttonText: '浏  览',                                 //按钮文本
        uploader: ctxAdmin + '/sys/user/upload;jsessionid='+jsessionid+'?jsessionid='+jsessionid,
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: false,
        fileSizeLimit: '1MB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
        fileTypeDesc: '全部文件', //文件描述
        fileTypeExts: '*.gif; *.jpg; *.png; *.bmp',  //上传的文件后缀过滤器
        //上传到服务器，服务器返回相应信息到data里
        onUploadSuccess: function (file, data, response) {
            data = eval("(" + data + ")");
            if(data.code == 1){
                $('#photo_pre').attr("src",data['obj']['url']);
                $('#photo_pre').show();
                $("#photo").val(data['obj']['id']);
            }
            $('#' + file.id).find('.data').html(data.msg);


            var uploadify = this;
            var cancel = $('#file-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
            if (cancel) {
                cancel.attr("rel", data['obj']['id']);
                cancel.click(function() {
                    $('#' + file.id).empty();
                    delete uploadify.queueData.files[file.id]; //删除上传组件中的附件队列
                    $('#' + file.id).remove();
                });
            }
        }

    });
}

//登录名 同步成员工编号
function sync(){
    $("#code").val($("#loginName").val());
}