<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息</title>
	<meta name="decorator" content="default_sys"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			$("#receiveObjectType").change(function(){
				$("#objectIdsId").val("");
				$("#objectIdsName").val("");
				//TODO 其它方法 取消表单校验
				$("#content").removeClass("required");
				$("#objectIdsId").removeClass("required");
				$("#objectIdsName").removeClass("required");

				$("#inputForm").attr({'action':'${ctxAdmin}/notice/message/form'}).submit();
			});
//			$("#receiveObjectType").trigger("change");


		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctxAdmin}/notice/message">消息列表</a></li>
		<li class="active"><a href="${ctxAdmin}/notice/message/form?id=${model.id}">消息<e:hasPermission name="notice:message:edit">${not empty model.id?'修改':'添加'}</e:hasPermission><e:lacksPermission name="notice:message:edit">查看</e:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="model" action="${ctxAdmin}/notice/message/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="title"/>
		<tags:message content="${message}"/>
		<div id="user_div" class="control-group">
			<label class="control-label">接收对象:</label>
			<div class="controls">
				<form:select id="receiveObjectType" path="receiveObjectType" cssStyle="width: 100px;" itemValue="user" >
					<form:options items="${messageReceiveObjectTypes}" itemLabel="description" itemValue="value" htmlEscape="false" cssClass="required"/>
				</form:select>
				<c:choose>
					<c:when test="${model.receiveObjectType eq 'organ'}">
						<tags:treeselect id="objectIds" name="objectIds" value="" labelName="organName" labelValue=""
										 title="部门" url="${ctxAdmin}/sys/organ/treeData" checked="true" cssClass="required"/>
					</c:when>
					<c:otherwise>
						<tags:treeselect id="objectIds" name="objectIds" value="" labelName="senderName" labelValue=""
										 title="用户" url="${ctxAdmin}/sys/user/organUserTree" checked="true" cssClass="required"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<%--<div class="control-group">--%>
			<%--<label class="control-label">标题:</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="title" htmlEscape="false" maxlength="200" class="input-xlarge"/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="control-group">
			<label class="control-label">消息内容:</label>
			<div class="controls">
				<form:textarea id="content" htmlEscape="true" path="content" rows="6" maxlength="512" class="input-xxlarge required"/>
				<%--<tags:ckeditor replace="content" uploadPath="/notice/message" height="200" />--%>
			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">图片:</label>--%>
			<%--<div class="controls">--%>
				<%--<input type="hidden" id="image" name="image" value="${model.image}" />--%>
				<%--<tags:ckfinder input="image" type="images" uploadPath="/notice/message"  selectMultiple="false"/>--%>
				<%--<span class="help-inline">建议首页图大小：300 × 400（像素）</span>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="control-group">
			<label class="control-label">链接地址:</label>
			<div class="controls">
				<form:input path="url" htmlEscape="false" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<e:hasPermission name="notice:message:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="发送"/>&nbsp;
			</e:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>