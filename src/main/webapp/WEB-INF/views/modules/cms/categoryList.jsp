<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>栏目管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
    	function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctxAdmin}/cms/category/updateSort");
	    	$("#listForm").submit();
    	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctxAdmin}/cms/category/">栏目列表</a></li>
		<e:hasPermission name="cms:category:edit"><li><a href="${ctxAdmin}/cms/category/form">栏目添加</a></li></e:hasPermission>
	</ul>
	<tags:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr><th>栏目名称</th><th>归属机构</th><th>栏目模型</th><th style="text-align:center;">排序</th><th title="是否在导航中显示该栏目">导航菜单</th><th title="是否在分类页中显示该栏目的文章列表">栏目列表</th><th>展现方式</th><th>操作</th></tr>
			<c:forEach items="${list}" var="tpl">
                <c:if test="${fns:isPermitted(tpl.id)}"><%--栏目权限--%>
                    <tr id="${tpl.id}" pId="${tpl.parent.id ne '1' ? tpl.parent.id : '0'}">
                        <td><a href="${ctxAdmin}/cms/category/form?id=${tpl.id}">${tpl.name}</a></td>
                        <td>${tpl.organName}</td>
                        <td>${fns:getDictionaryNameByDV('cms_module',tpl.module,  '公共模型')}</td>
                        <td style="text-align:center;">
                            <e:hasPermission name="cms:category:edit">
                                <input type="hidden" name="ids" value="${tpl.id}"/>
                                <input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
                            </e:hasPermission><e:lacksPermission name="cms:category:edit">
                                ${tpl.sort}
                        </e:lacksPermission>
                        </td>
                        <td>${fns:getDictionaryNameByDV('show_hide',tpl.inMenu,  '隐藏')}</td>
                        <td>${fns:getDictionaryNameByDV('show_hide', tpl.inList, '隐藏')}</td>
                        <td>${fns:getDictionaryNameByDV('cms_show_modes',tpl.showModes,  '默认展现方式')}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}${fns:getFrontPath()}/list-${tpl.code}${fns:getUrlSuffix()}" target="_blank">访问</a>
                            <e:hasPermission name="cms:category:edit">
                                <a href="${ctxAdmin}/cms/category/form?id=${tpl.id}">修改</a>
                                <a href="${ctxAdmin}/cms/category/delete?id=${tpl.id}" onclick="return confirmx('要删除该栏目及所有子栏目项吗？', this.href)">删除</a>
                                <a href="${ctxAdmin}/cms/category/form?parent.id=${tpl.id}">添加下级栏目</a>
                            </e:hasPermission>
                        </td>
                    </tr>
                </c:if>
			</c:forEach>
		</table>
		<e:hasPermission name="cms:category:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></e:hasPermission>
	</form>
</body>
</html>