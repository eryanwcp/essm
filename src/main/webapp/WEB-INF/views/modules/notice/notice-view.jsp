<%@ page language="java" pageEncoding="UTF-8"
         contentType="text/html; charset=UTF-8"%>
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

    <script type="text/javascript">
	    function init() {
	        var parentDIv = window.parent.document.getElementById("notice_view_iframe");
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
        function _download(fileId) {
            var annexFrame = document.getElementById("annexFrame");
            annexFrame.src = '${ctxAdmin}/disk/fileDownload/' + fileId;
        }
    </script>
</head>
<body onload="init();" style="overflow: hidden;margin: 0px;">
<div style="padding: 0px 60px 0px 30px;">
    <div style="width:100%;float: left;margin-top:10px;margin-bottom:10px;padding: 8px 15px 8px;background-color: #f5f5f5;border-top:2px solid #000;border-bottom:2px solid #000;">
        <div align="center">
            <h3>${model.title}</h3>
        </div>
        <div align="right">
            [ <label>发布部门：</label>${model.publishOrganName}&nbsp;&nbsp;<label>发布人：</label>${model.publishUserName}&nbsp;&nbsp;<label>发布时间：</label><fmt:formatDate value="${model.publishTime}" pattern='yyyy-MM-dd HH:mm' />]
        </div>
    </div>
    <div id="content" style="float: left;">
        ${model.content}
    </div>
    <br/>
    <c:if test="${not empty files}">
        <div style="float: left;width:100%;border-top:1px solid #000;padding-top:10px;margin-top:10px;">
            <strong>附件列表：</strong>
            <br/>
            <c:forEach items="${files}" begin="0" var="file" varStatus="i">
                <div style="margin-top: 10px;">${i.index +1}、<a href="javascript:void(0)" onclick="javascript:_download('${file.id}');">${file.name}</a></div>
            </c:forEach>
        </div>
    </c:if>
    <iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 100%;height: 50px;"></iframe>
</div>
</body>
</html>