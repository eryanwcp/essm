<%@ page language="java" pageEncoding="UTF-8"   contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%--<%@ include file="/common/meta_mobile.jsp" %>--%>
<!DOCTYPE html>
<html>
<head>
    <title>${model.title}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE;chrome=1" />
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0">
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <script type="text/javascript" src="${ctxStatic}/js/jquery/jquery-2.1.4.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/app/mobile/modules/platform/news-view${yuicompressor}.css?_=${sysInitTime}" />
    <script type="text/javascript">
        $(function(){
            var maxWidth = $(window).width();
            $.each($("#content").find("img"),function(i,img){
                var imgSrc = $(img).attr("src");
                getImageWidth(imgSrc,function(w,h){
                    if(w > maxWidth){
                        $(img).css({"width":"100%"});
                    }
                });
            });
            $("#content").find("img").parent().css({"text-indent":0});
            $("#content").find("table").css({"width":"100%","margin-left":0});
        });

        function getImageWidth(url,callback){
            var img = new Image();
            img.src = url;
            // 如果图片被缓存，则直接返回缓存数据
            if(img.complete){
                callback(img.width, img.height);
            }else{
                // 完全加载完毕的事件
                img.onload = function(){
                    callback(img.width, img.height);
                }
            }
        }
    </script>
</head>
<body >
    <h3 class="newstitle">${model.title}</h3>
    <h4 class="newstime">${model.publishOrganName}${model.publishUserName}<fmt:formatDate value="${model.publishTime}" pattern='yyyy-MM-dd HH:mm' /></h4>
    <div id="content">${model.content}</div>
    <c:if test="${not empty files}">
        <div id="notice_files" style="float: left;width:100%;border-top:1px solid #D2D2D2;padding-top:10px;margin-top:10px;">
            <h3>附件列表：</h3>
            <c:forEach items="${files}" begin="0" var="file" varStatus="i">
                <div style="margin-top: 10px;">${i.index +1}、<a href="${appURL}/a/disk/fileDownload/${file.id}" target="_blank">${file.name}</a></div>
            </c:forEach>
        </div>
    </c:if>
    <iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 100%;height: 50px;"></iframe>
</body>
</html>