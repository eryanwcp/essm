<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var defaultOrganComboboxData = ${defaultOrganComboboxData};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/user-organ${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="user_organ_form" class="dialog-form" method="post" novalidate>
        <div>
            <label>所属组织机构:</label>
            <input type="select" id="organIds" name="organIds"
                   style="width: 260px;height: 28px;"/>
        </div>
        <div>
            <label>默认组织机构:</label>
            <input type="select" id="defaultOrganId" name="defaultOrganId"
                   style="width: 260px;height: 28px;"/>
        </div>
    </form>
</div>