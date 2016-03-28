<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script src="${ctxStatic}/js/jquery/qrcode/jquery.qrcode.min.js" type="text/javascript"></script>
<script type="text/javascript">
    <%
    String httpPrefix = request.getScheme()+ "://" + request.getServerName() + ":" + request.getServerPort();
    request.setAttribute("httpPrefix",httpPrefix);
    %>
    $(function(){
        if($("#qr")){
            $("#qr").qrcode({
                "render": "div",
                "size": 100,
                "color": "#3a3",
                "text": '${httpPrefix}'+$("#link_download").attr("href")
            });
        }
    });
</script>

<div>
    <form id="versionLog_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" />
        <div>
            <label>版本号:</label>
            ${model.versionName}
        </div>
        <div>
            <label>内部编号:</label>
            ${model.versionCode}
        </div>
        <div>
            <label>类型:</label>
            ${model.versionLogTypeView}</div>
        <div>
            <label style="vertical-align: top;">更新说明:</label>
            ${model.remark}
        </div>
        <c:if test="${not empty file}">
            <div style="float: left;width:100%;border-top:1px solid #000;padding-top:10px;margin-top:10px;">
                <strong>附件列表：</strong>
                <br/>
                <div style="margin-top: 10px;"><a id="link_download" href="${ctxAdmin}/disk/fileDownload/${file.id}">${file.name}</a></div>
                <div id="qr"></div>
            </div>
        </c:if>
    </form>
</div>