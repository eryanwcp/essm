<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div>
	<form id="user_password_form" class="dialog-form" method="post">
		<input type="hidden" id="user_password_form_id" name="id" value="${model.id}"/>
		<div>
			<label>新密码:</label> 
			<input type="password" id="newPassword" name="newPassword"
				class="easyui-validatebox textbox" required="true" missingMessage="请输入新密码."
				validType="minLength[1]" />
		</div>
		<div>
			<label>确认新密码:</label> 
			<input type="password" id="newPassword2" name="newPassword2"
				class="easyui-validatebox textbox" required="true"
				missingMessage="请再次输入新密码." validType="equalTo['#newPassword']"
				invalidMessage="两次输入密码不匹配." />
		</div>
	</form>
</div>