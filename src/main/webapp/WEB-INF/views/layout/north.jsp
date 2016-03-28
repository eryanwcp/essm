<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%--<script type="text/javascript" src="${ctxStatic}/app/layout/north.js" charset="utf-8"></script>--%>
<div style="height: 100%;background-image: url(${ctxStatic}/img/top_bg.png); background-repeat: repeat-x;">
    <div title="${fns:getAppFullName()}" style="float: left;padding-left: 10px;padding-top:10px;">
        <img title="锦峰软件" alt="锦峰软件" src="${ctxStatic}/img/logo_jfit.png" style="height: 51px;vertical-align: middle;">
        <span style="font-size:24px;vertical-align: middle;">${fns:getAppFullName()}</span>
    </div>
	<%--<div style="float: right; position: absolute; bottom: 20px; right: 10px">--%>
	    <%--<div style="text-align: right;">您好,<span style="color: red;">${sessionInfo.loginName}</span>[${sessionInfo.roleNames}] 欢迎您！</div>--%>
		<%--<a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_pfMenu" iconCls="eu-icon-user_red">更换皮肤</a>--%>
		<%--<div id="layout_north_pfMenu" style="width: 120px; display: none;">--%>
			<%--<div onclick="eu.changeTheme('bootstrap');">bootstrap</div>--%>
			<%--<div onclick="eu.changeTheme('default');">蓝色</div>--%>
			<%--<div onclick="eu.changeTheme('gray');">灰色</div>--%>
			<%--<div onclick="eu.changeTheme('black');">黑色</div>--%>
			<%--<div onclick="eu.changeTheme('metro');">metro</div>--%>
		<%--</div>--%>

	    <%--<a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_kzmbMenu" iconCls="easyui-icon-help">控制面板</a>--%>
		<%--<div id="layout_north_kzmbMenu" style="width: 100px; display: none;">--%>
            <%--<div onclick="editLoginUserInfo();" iconCls="easyui-icon-edit">修改个人信息</div>--%>
			<%--<div onclick="editLoginUserPassword();" iconCls="easyui-icon-edit">修改密码</div>--%>
			<%--<div class="menu-sep"></div>--%>
            <%--<div onclick="toApp();">切换到桌面版</div>--%>
            <%--<div class="menu-sep"></div>--%>
			<%--<div onclick="" data-options="iconCls:'easyui-icon-help'">帮助</div>--%>
			<%--<div onclick="showAbout();" data-options="iconCls:'easyui-icon-essh'">关于</div>--%>
		<%--</div>--%>

		<%--<a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_logoutMenu" iconCls="easyui-icon-back">安全退出</a>--%>
		<%--<div id="layout_north_logoutMenu" style="width: 100px; display: none;">--%>
			<%--<div onclick="logout(true);"  data-options="iconCls:'eu-icon-lock'">切换账号</div>--%>
			<%--<div onclick="logout();"  data-options="iconCls:'eu-icon-lock'">注销</div>--%>
		<%--</div>--%>
	<%--</div>--%>
</div>
