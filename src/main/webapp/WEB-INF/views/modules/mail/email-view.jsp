<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE;chrome=1" />
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <title>${model.title}</title>
    <%-- 引入jQuery --%>
    <script type="text/javascript" src="${ctxStatic}/js/jquery/jquery-1.10.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${ctxStatic}/js/jquery/jquery-migrate-1.2.1.min.js" charset="utf-8"></script>
    <link id="easyuiTheme" rel="stylesheet" type="text/css" href="${ctxStatic}/js/easyui-${ev}/themes/<c:out value="${cookie.easyuiThemeName.value}" default="bootstrap"/>/easyui.css" />
    <script type="text/javascript" src="${ctxStatic}/js/easyui-${ev}/jquery.easyui.mine.js" charset="utf-8"></script>
    <style type="text/css">
        #layer{width:100%;height:100%;background:#FFFFFF;position:absolute;top:0;left:0;z-index:100;filter:alpha(opacity=0);opacity:0;display:none;}
    </style>

    <script type="text/javascript">
    
        function init() {
            var parentDIv = window.parent.document.getElementById("email_view_iframe");
            var contentY = document.getElementById("content");
            if (parentDIv) {
                parentDIv.style.height = document.body.clientHeight + "px";
                var panelWidth = parseInt(parentDIv.parentNode.style.width);
                var contentWidth = contentY.clientWidth + 60;
                var setWidth = (contentWidth > panelWidth ? contentWidth: panelWidth);
                parentDIv.style.width = setWidth + "px";
                contentY.style.width = (setWidth - 200) + "px";

            }
            //修复Chrome浏览器不显示 border小于0.75pt的边框问题
            var tdDoms = document.getElementsByTagName("td");
            if (tdDoms) {
                for (var i = 0; i < tdDoms.length; i++) {
                    var border = tdDoms[i].style.border;
                    if (border && border.indexOf("0.5pt") == 0) {
                        tdDoms[i].style.border = border.replace("0.5pt", "0.75pt");
                    }
                }
            }


        }
        function loadOrOpen(attachmentId) {
            var annexFrame = document.getElementById("annexFrame");
            annexFrame.src = '${ctxAdmin}/disk/fileDownload/' + attachmentId;
            //$('#annexFrame').attr('src', '${ctxAdmin}/mail/email/download?attachmentId=' + attachmentId);
        }

        function tooltip(domId,receiveObjectId,receiveObjectType){
            $('#'+domId).tooltip({
                showEvent:'click',
                hideEvent:'',
                content: $('<div></div>'),
                onShow: function(){
                    $(this).tooltip('arrow').css('left', 20);
                    $(this).tooltip('tip').css('left', $(this).offset().left);
                    $("#layer").show();
                    $("#layer").bind("click",function(){
                        $(".tooltip").hide();
                        $("#layer").hide();
                    });
                },
                onUpdate: function(cc){
                    cc.panel({
                        width: 360,
                        height: 'auto',
                        border: false,
                        queryParams:{receiveObjectId:receiveObjectId,receiveObjectType:receiveObjectType},
                        href: '${ctxAdmin}/mail/email/receiveObject'
                    });
                }
            });
        }

        //办理流程
        function deal(pdf, taskId) {
            top.eu.addTab(window.parent.parent.layout_center_tabs,'办公管理','${ctxAdmin}/oa/process?flag=deal&pdId='+encodeURIComponent(encodeURIComponent(pdf))+'&tId='+taskId, true,'', '', true);
        }
    </script>
</head>
<body onload="init();" style="overflow: hidden;margin: 0px;">
<div style="padding: 0px 30px 0px 30px;">
    <div style="width:100%;float: left;margin-top:10px;margin-bottom:10px;font-size: 16px;padding: 8px 15px 8px;border-top:2px solid #000;border-bottom:2px solid #000;background-color: #f5f5f5;">
        <div>
            <label><strong>邮件主题：</strong></label> ${model.title} 【${model.priorityView}】 &nbsp;&nbsp;<fmt:formatDate value="${model.sendTime}" pattern='yyyy-MM-dd HH:mm' />
        </div>
        <div>
            <label><strong>发 件 人：</strong></label> ${model.senderName}
        </div>
        <%--接收人--%>
        <c:set var="toContacts" value="${fnc:getEmailContacts(model.id,0)}" ></c:set>
        <div>
            <label><strong>收 件 人：</strong></label>
            <c:forEach items="${toContacts}" var="iContact" varStatus="i">
                <span id="${iContact.id}" class="">
                    ${iContact.nameView}
                </span><c:if test="${i.index+1 < fn:length(toContacts)}">,</c:if>
                <script type="text/javascript">
                    tooltip('${iContact.id}','${iContact.receiveObjectId}','${iContact.receiveObjectType}');
                </script>
            </c:forEach>
        </div>
        <%--抄送人--%>
        <c:set var="ccContacts" value="${fnc:getEmailContacts(model.id,1)}"></c:set>
        <c:if test="${not empty ccContacts}">
            <div>
                <label><strong>抄 送 人：</strong></label>
                <c:forEach items="${ccContacts}" var="iContact" varStatus="i">
                <span id="${iContact.id}" class="">
                        ${iContact.nameView}
                </span><c:if test="${i.index+1 < fn:length(ccContacts)}">,</c:if>
                    <script type="text/javascript">
                        tooltip('${iContact.id}','${iContact.receiveObjectId}','${iContact.receiveObjectType}');
                    </script>
                </c:forEach>
            </div>
        </c:if>
        <div>
            <label><strong>大&nbsp;&nbsp;小：</strong></label>
            <span>${model.emailSizeView}</span>
        </div>
    </div>
    <br/>
    <div id="content" style="float: left;">${model.content}</div>
    <c:if test="${not empty files}">
        <div style="float: left;width:100%;border-top:1px solid #000;padding-top:10px;margin-top:10px;">
            <label>附件列表：</label> <br />
            <c:forEach items="${files}" begin="0" var="fl" varStatus="status">
                <div style="margin-top: 10px;">${status.index +1}、<a href="javascript:void(0)" onclick="javascript:loadOrOpen('${fl.id}');">${fl.name}</a></div>
            </c:forEach>

        </div>
    </c:if>
    <iframe id="annexFrame" frameborder="no" style="padding: 0;border: 0;width: 100%;height: 50px;"></iframe>
</div>
<div id="layer"></div>
</body>
</html>