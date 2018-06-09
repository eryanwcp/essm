<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/meta_mobile.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>通知公告</title>
    <script type="text/javascript" charset="utf-8" src="${ctxStatic}/app/mobile/modules/notice/notice.mobile${yuicompressor}.js?_=${sysInitTime}"></script>
</head>
<body>
<style rel="stylesheet">
	#markReadAll{background:#2b507d;border-radius:0;color:#fff;padding:6px 8px;margin: 6px 10px 4px 0;border:none;}
</style>
<div class="view">
    <header style="background:#d3e8f5;border-bottom: 1px solid #d3e8f5;">
        <span id="markReadAll" class="button header_span_right" >全部标记为已读</span>
    </header>
    <div class="pages">
        <div class="panel">
            <ul class="list">
            </ul>
        </div>
    </div>
</div>
</body>