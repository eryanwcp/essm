<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var resourceComboboxData = ${resourceComboboxData};
    var resourceIds = ${fns:toJson(resourceIds)};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user-resource${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="user_resource_form" class="dialog-form"  method="post" novalidate>
        <div>
            <label style="vertical-align: top;">关联资源:</label>
            <input id="resourceIds" name="resourceIds" style="width:260px;height:200px;" />
            级联模式<input id="changeMode" type="checkbox" />
        </div>
    </form>
</div>