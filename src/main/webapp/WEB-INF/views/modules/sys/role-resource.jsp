<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var resourceComboboxData = ${resourceComboboxData};
    var resourceIds = ${fns:toJson(resourceIds)};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/role-resource${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="role_resource_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>关联资源:</label>
            <input id="resourceIds" name="resourceIds"  style="width:200px" />
            <label><input id="changeMode" type="checkbox"/>级联模式</label>
        </div>
    </form>
</div>