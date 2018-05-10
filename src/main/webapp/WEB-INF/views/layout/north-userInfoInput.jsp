<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctxStatic}/js/uploadify/scripts/jquery.uploadify.mine.js"></script>
<link href="${ctxStatic}/js/uploadify/css/uploadify.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
    var userJson = ${userJson};
    var userinfo_form;
    var jsessionid = "${sessionInfo.sessionId}";
    $(function () {
        up();
        userinfo_form = $('#userinfo_form').form({
            url: ctxAdmin + '/sys/user/saveUserinfo',
            onSubmit: function (param) {
                $.messager.progress({
                    title: '提示信息！',
                    text: '数据处理中，请稍后....'
                });
                var isValid = $(this).form('validate');
                if (!isValid) {
                    $.messager.progress('close');
                }
                return isValid;
            },
            success: function (data) {
                $.messager.progress('close');
                var json = $.parseJSON(data);
                if (json.code == 1) {
                    userinfo_dialog.dialog('destroy');//销毁对话框
                    eu.showMsg(json.msg);//操作结果提示
                } else if (json.code == 2) {
                    $.messager.alert('提示信息！', json.msg, 'warning', function () {
                        if (json.obj) {
                            $('#userinfo_form input[name="' + json.obj + '"]').focus();
                        }
                    });
                } else {
                    eu.showAlertMsg(json.msg, 'error');
                }
            }
        }).form('load', userJson);
        var path = userJson.photo;
        if (path) {
            $('#photo_pre').attr("src", ctx + path);
        }
        $('#photo_pre').show();
        $(".uploadify").css({'display': 'inline-block', 'height': '24px', 'padding-right': '18px', 'outline': 'none'});
        loadSex();
    });

    //性别
    function loadSex(){
        $('#sex').combobox({
            url: ctxAdmin + '/sys/user/sexTypeCombobox',
            height:28,
            width: 120,
            editable:false
        });
    }

    function up() {
        $('#file').uploadify({
            method: 'post',
            swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',  //FLash文件路径
            buttonText: '浏  览',                                 //按钮文本
            uploader: ctxAdmin + '/sys/user/upload;jsessionid='+jsessionid,
            fileObjName: 'uploadFile',
            removeCompleted: false,
            multi: false,
            fileSizeLimit: '2MB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
            fileTypeDesc: '全部文件', //文件描述
            fileTypeExts: '*.gif; *.jpg; *.png; *.bmp',  //上传的文件后缀过滤器
            //上传到服务器，服务器返回相应信息到data里
            onUploadSuccess: function (file, data, response) {
                data = eval("(" + data + ")");
                if(data.code != undefined && data.code == 1){
                    $('#photo_pre').attr("src","${_ctx}" + data.obj);
                    $('#photo_pre').show();
                    $("#photo").val(data.obj);
                }
                $('#' + file.id).find('.data').html(data.msg);


                var uploadify = this;
                var cancel = $('#file-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
                if (cancel) {
                    cancel.attr("rel", data.obj);
                    cancel.click(function() {
                        $('#' + file.id).empty();
                        delete uploadify.queueData.files[file.id]; //删除上传组件中的附件队列
                        $('#' + file.id).remove();
                    });
                }
            }

        });
    }

</script>
<div>
    <form id="userinfo_form" method="post" class="dialog-form" novalidate>
        <input type="hidden" id="id" name="id" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" />
        <div>
            <label>姓名:</label>
            <input name="name" type="text" id="name" maxLength="6" class="easyui-validatebox textbox"
                   data-options="required:true,missingMessage:'请输入姓名.',validType:['CHS','length[2,6]']" />
        </div>

        <div>
            <label>性别:</label>
            <input id="sex" name="sex" />
        </div>
        <div>
            <label>出生日期:</label>
            <input id="birthday" name="birthday" type="text" class="easyui-my97" />
        </div>
        <label>头像:</label>
        <input id="photo" name="photo" readonly="readonly" style="display: none" />
        <img id="photo_pre" src="" alt="头像" style="margin-left: 76px;width: 64px; height: 64px;">
        <input id="file" name="file" type="file" multiple="true">
        <div>
            <label>公司邮箱:</label>
            <input name="email" type="text" class="easyui-validatebox textbox" validType="email" maxLength="64" />
        </div>
        <div>
            <label>个人邮箱:</label>
            <input name="personEmail" type="text" class="easyui-validatebox textbox" validType="email" maxLength="64" />
        </div>
        <div>
            <label>地址:</label>
            <input name="address" type="text" class="easyui-validatebox textbox" validType="legalInput" maxLength="255" />
        </div>
        <div>
            <label>办公电话:</label>
            <input name="tel" type="text" class="easyui-validatebox textbox" validType="phone">
        </div>

        <div>
            <label>手机号:</label>
            <input name="mobile" type="text" class="easyui-validatebox textbox" validType="mobile">
        </div>
        <div>
            <label>QQ:</label>
            <input name="qq" type="text" class="easyui-numberbox" validType="QQ" maxLength="64" style="height:28px;" />
        </div>
    </form>
</div>