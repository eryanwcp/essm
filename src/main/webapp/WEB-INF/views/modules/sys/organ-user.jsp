<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var usersCombogridData = [];
    var organUserCombogridData = ${organUserCombogridData};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/organ-user${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="organ_user_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" value="${model.id}" />
        <%--<div>--%>
            <%--<label>机构用户:</label>--%>
            <%--<input type="select" id="userIds" name="userIds" style="width: 260px;height:28px;"/>--%>
        <%--</div>--%>
        <div>
            <label>主管:</label>
            <input type="select" id="managerUserId" name="managerUserId" value="${model.managerUserId}"
                   style="width: 260px;height:28px;"/>
        </div>
        <div>
            <label>分管领导:</label>
            <input type="hidden" id="superManagerUserId" name="superManagerUserId" value="${model.superManagerUserId}">
            <input class="easyui-textbox" id="superManagerUserName" name="superManagerUserName" value="${model.superManagerUserName}"
                   data-options="editable:false,buttonText:' 选 择 ',buttonIcon:'eu-icon-user',prompt:'分管领导',onClickButton:selectUser"
                   style="width:200px;height:28px;">

            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="点击选择." ></span>
        </div>
    </form>
</div>