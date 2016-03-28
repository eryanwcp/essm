<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="isSecurityOn" value="<%=AppConstants.getIsSecurityOn()%>"></c:set>
<div>
	<form id="login_password_form" class="dialog-form" method="post" novalidate>
		<input type="hidden" id="login_password_form_id" name="id" value="${sessionInfo.userId}" />
		<div>
			<label>原始密码:</label> <input type="password" id="password" name="password" class="easyui-validatebox textbox"
										data-options="required:true,missingMessage:'请输入原始密码.',validType:['minLength[1]']"/>
			<%--data-options="required:true,missingMessage:'请输入原始密码.',validType:['safepass_yc','minLength[1]']"/>--%>
		</div>
		<div>
			<label>新密码:</label> <input type="password" id="newPassword"
									   name="newPassword" class="easyui-validatebox textbox"
			<c:choose>
				<c:when test="${isSecurityOn}">
				   data-options="required:true,missingMessage:'请输入新密码.',validType:['safepass_yc','minLength[1]']"/>
				</c:when>
				<c:otherwise>
					data-options="required:true,missingMessage:'请输入新密码.',validType:['minLength[1]']"/>
				</c:otherwise>
			</c:choose>

		</div>
		<div>
			<label>确认新密码:</label> <input type="password" name="newPassword2"
										 id="newPassword2" required="true" class="easyui-validatebox textbox"
										 missingMessage="请再次输入确认新密码." validType="equalTo['#newPassword']"
										 invalidMessage="两次输入密码 不一致." />
		</div>
	</form>
</div>