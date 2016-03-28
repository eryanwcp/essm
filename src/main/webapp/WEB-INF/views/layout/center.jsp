<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctxStatic}/app/layout/center${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div id="layout_center_tabs" fit="true" style="overflow: hidden;">
	<%--<div id="layout_center_tabs_index" title="首页" data-options="href:'${ctxAdmin}/common/layout/portal',iconCls:'icon-application'"></div>--%>
</div>
<div id="layout_center_tabsMenu" style="width: 120px;display:none;">
	<div type="refresh" data-options="iconCls:'easyui-icon-reload'">刷新</div>
	<div class="menu-sep"></div>
	<div type="close" data-options="iconCls:'easyui-icon-cancel'">关闭</div>
	<div type="closeOther">关闭其他</div>
	<div type="closeAll">关闭所有</div>
</div>
<div id="layout_center_tabs_menu" class="easyui-menu" style="width: 120px; display: none;">
    <div data-options="iconCls:'eu-icon-user_red'">
        <span>更换皮肤</span>
        <div style="width:120px;">
            <div onclick="eu.changeTheme('bootstrap');">bootstrap</div>
            <div onclick="eu.changeTheme('default');">蓝色</div>
            <div onclick="eu.changeTheme('gray');">灰色</div>
            <div onclick="eu.changeTheme('black');">黑色</div>
            <div onclick="eu.changeTheme('metro');">metro</div>
        </div>
    </div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'easyui-icon-help'">
        <span>控制面板</span>
        <div style="width:120px;">
            <div onclick="editLoginUserInfo();" iconCls="easyui-icon-edit">个人信息</div>
            <div onclick="editLoginUserPassword();" iconCls="eu-icon-lock">修改密码</div>
            <div class="menu-sep"></div>
            <div onclick="javascript:window.location.href='${ctxFront}';">首页</div>
            <div onclick="toApp();">切换到桌面版</div>
            <div class="menu-sep"></div>
            <div onclick="" data-options="iconCls:'easyui-icon-help'">帮助</div>
            <div onclick="showAbout();" data-options="iconCls:'easyui-icon-essh'">关于</div>
        </div>
    </div>
    <div class="menu-sep"></div>
    <div onclick="logout();" iconCls="eu-icon-user">注销</div>
    <%--<div onclick="logout(true);"  data-options="iconCls:'eu-icon-lock'">切换账号</div>--%>
</div>
<%--全屏工具栏--%>
<div id="layout_center_tabs_full-tools" style="display: none">
    <a href="#" class="easyui-linkbutton easyui-tooltip" title="全屏" data-options="iconCls:'eu-icon-full_screen',plain:true,height:32,width:32"
       onclick="javascript:screenToggle(true);"></a>
    <%--<a href="#" class="easyui-linkbutton easyui-tooltip" title="刷新" data-options="iconCls:'easyui-icon-reload',plain:true"--%>
       <%--onclick="javascript:refresh();"></a>--%>
    <%--<a href="#" class="easyui-linkbutton easyui-tooltip" title="关闭" data-options="iconCls:'easyui-icon-cancel',plain:true"--%>
       <%--onclick="javascript:cancel();"></a>--%>
    <a href="javascript:void(0);" class="easyui-linkbutton" onmousemove="showLayoutTabmenu(this)" onclick="showLayoutTabmenu(this)"
       data-options="iconCls:'eu-icon-more',plain:true,height:32,width:32"></a>
    <span style="width: 20px;">&nbsp;</span>
</div>
<%--退出全屏工具栏--%>
<div id="layout_center_tabs_unfull-tools" style="display: none;">
    <a href="#" class="easyui-linkbutton easyui-tooltip" title="退出全屏" data-options="iconCls:'eu-icon-exit_full_screen',plain:true,height:32,width:32"
       onclick="javascript:screenToggle(false);"></a>
    <%--<a href="#" class="easyui-linkbutton easyui-tooltip" title="刷新" data-options="iconCls:'easyui-icon-reload',plain:true"--%>
       <%--onclick="javascript:refresh();"></a>--%>
    <%--<a href="#" class="easyui-linkbutton easyui-tooltip" title="关闭" data-options="iconCls:'easyui-icon-cancel',plain:true"--%>
       <%--onclick="javascript:cancel();"></a>--%>
    <a href="javascript:void(0);" class="easyui-linkbutton" onmousemove="showLayoutTabmenu(this)" onclick="showLayoutTabmenu(this)"
       data-options="iconCls:'eu-icon-more',plain:true,height:32,width:32"></a>
    <span style="width: 20px;">&nbsp;</span>
</div>