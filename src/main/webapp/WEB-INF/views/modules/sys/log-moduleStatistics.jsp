<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>模块访问统计</title>
	<meta name="decorator" content="default_sys"/>
	<script type="text/javascript">
		$(function(){
			$("#organIdId").change(function(){
				if($("#organIdId").val()){
					$("#userIdId").val('');
					$("#userIdName").val('');
                    userIdSetAjaxURL("${ctxAdmin}/sys/user/organUserTree?parentId="+$("#organIdId").val())
				}
			});

            $("#onlyCompany").change(function(){
                if($("#onlyCompany").prop('checked')){
                    $("#userIdId").val('');
                    $("#userIdName").val('');
                }
            });
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
		}

		//导出excel表格
		function exportExcel(){
			var param = $.serializeObject($("#searchForm"));
			param['userId'] = $("#userIdId").val();
			param['organId'] = $("#organIdId").val();
			$('#module_temp_iframe').attr('src', '${ctxAdmin}/sys/log/report/moduleStatisticsExportExcel?'+ $.param(param));
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctxAdmin}/sys/log/report/moduleStatistics">模块访问统计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="paramMap" action="${ctxAdmin}/sys/log/report/moduleStatistics" method="post" class="breadcrumb form-search">

		<div>
		<label>机构选择：</label><tags:treeselect id="organId" name="organId" value="${paramMap.organId}" labelName="organName" labelValue="${paramMap.organName}"
											 title="机构" url="${ctxAdmin}/sys/organ/ownerAndChildsCompanysData" allowClear="true" />
			<label><input type="checkbox" id="onlyCompany" name="onlyCompany" value="true" <c:if test="${paramMap.onlyCompany}">checked</c:if>>本级</label>

			<label>人员选择：</label><tags:treeselect id="userId" name="userId" value="${paramMap.userId}" labelName="userName" labelValue="${paramMap.userName}"
										 title="人员" url="${ctxAdmin}/sys/user/organUserTree" notAllowSelectParent="true" notAllowSelectRoot="true" allowClear="true" cssClass="required"/>
			<label>开始日期：</label><input id="startTime" name="startTime" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
							   value="${paramMap.startTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
	<label>结束日期：</label><input id="endTime" name="endTime" type="text" readonly="readonly" maxlength="20" class="input-small Wdate"
							   value="${paramMap.endTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>&nbsp;&nbsp;
	<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	<button type="button" class="btn btn-primary" onclick="exportExcel();">导出excel</button>
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>模块</th><th>访问次数</th>
		<tbody>
		<c:forEach items="${page.result}" var="m">
			<tr>
				<td>${m.module}</td>
				<td>${m.moduleCount}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<%-- 隐藏iframe --%>
	<iframe id="module_temp_iframe" style="display: none;"></iframe>
</body>
</html>