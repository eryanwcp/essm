<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var folderId = "${model.id}";
    var sessionId = "${sessionInfo.sessionId}";
    var categorys_combotree;
    var fileSizeLimit = '<%=AppConstants.getPrettyDiskMaxUploadSize()%>';//附件上传大小限制
    $(function(){
        uploadify();
        $(".uploadify").css({'display': 'inline-block', 'height': '24px', 'padding-right': '18px', 'outline': 'none'});

    });
    function loadcategoryType(){
        categorys_combotree = $("#categoryIds").combotree({
            url:'${ctxAdmin}/repository/categoryR/categoryComTree',
            multiple:true,//是否可多选
            editable:false,
            cascadeCheck:false
        });

    }
    /**
     * 文件上传
     */
    function uploadify() {
        $('#file').uploadify({
            method: 'post',
            swf: '${ctxStatic}/js/uploadify/scripts/uploadify.swf',  //FLash文件路径
            buttonText: '上传文件',                                 //按钮文本
            uploader: '${ctxAdmin}/disk/fileUpload;jsessionid='+jsessionid,
            formData:{jsessionid:jsessionid},
            formData:{folderId:folderId},
            fileObjName: 'uploadFile',
            queueSizeLimit: 100,
            uploadLimit: 100,
            removeCompleted: false,
            multi: true,      //是否为多选，默认为true
            fileSizeLimit: fileSizeLimit, //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
            fileTypeDesc: '全部文件',
            fileTypeExts: '*.*',  //上传的文件后缀过滤器
            overrideEvents:["onUploadStart",'onUploadSuccess'],
            onUploadStart : function(file) {
                // Load the swfupload settings
                var settings = this.settings;

                var timer        = new Date();
                this.timer       = timer.getTime();
                this.bytesLoaded = 0;
                if (this.queueData.uploadQueue.length == 0) {
                    this.queueData.uploadSize = file.size;
                }
                var uploadify = this;
            },
            //上传到服务器，服务器返回相应信息到data里
            onUploadSuccess: function (file, data, response) {
                // Load the swfupload settings
                var settings = this.settings;
                var stats    = this.getStats();
                this.queueData.uploadsSuccessful = stats.successful_uploads;
                this.queueData.queueBytesUploaded += file.size;
               if(data != undefined && data != null && data != "" ){
            	   var data = eval("(" + data + ")");
                   if(data.code == "1") {
                        $('#' + file.id).find('.data').html(' - ' + "上传成功!");
                        var uploadify = this;
                        var cancel = $('#file-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
                        if (cancel) {
                            cancel.click(function() {
                                delUpload(data.obj,file.id,uploadify);
                            });
                        }
                   }else {
                       $('#' + file.id).find('.data').html(' - ' + "<font color=#D94600>" + data.msg + "</font>");
                   }
               } else {
                   $('#' + file.id).find('.data').html(' - ' + "<font color=#D94600>上传异常!</font>");
               }
            }
        });
    }
    

 // 删除附件
 function delUpload(attachmentId, pageFileId, uploadify) {
     $.post('${ctxAdmin}/disk/delFolderFile', {
         "fileIds": attachmentId
     },
     function(data) {
         if(data != undefined && data != null && data != ""){
        	// var json = eval('(' + data + ')'); //将后台传递的json字符串转换为javascript json对象
             if (data.code == 1) {
                 delete uploadify.queueData.files[pageFileId]; //删除上传组件中的附件队列
             } else {
                 eu.showAlertMsg(data.msg, 'error');
             }
         } else {
        	 eu.showMsg("删除失败！");
         }
     },'json');
 }
</script>
<div>
    <form id="file_form" method="post"  class="dialog-form" novalidate>
        <div id="tip_msg">
        </div>
        <input type="hidden"  name="id" />
        <!-- 版本控制字段 version -->
        <input type="hidden" id="version" name="version" />
        <div>
            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="单个文件上传限制1GB." ></span>
            <input id="file" name="file" type="file" multiple="true">

            <div id="queue"></div>
            <input id="filePath" name="filePath" type="hidden" value="${filePath}"/>
        </div>

    </form>
</div>