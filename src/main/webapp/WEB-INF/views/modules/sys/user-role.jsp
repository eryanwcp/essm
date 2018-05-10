<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var roleIds = ${fns:toJson(roleIds)};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user-role${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
	<form id="user_role_form" class="dialog-form" method="post" novalidate>
		<div >
			<label >关联角色:</label>
			<input id="user_role_form-roleIds" name="roleIds"
				   style="width: 260px;height: 28px;"/>
		</div>
	</form>
</div>