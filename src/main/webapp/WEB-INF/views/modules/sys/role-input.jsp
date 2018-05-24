<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var organIds = ${fns:toJson(organIds)};
    var isSystem = "${model.isSystem}";
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/role-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>

<div>
    <form id="role_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>是否系统角色:</label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="isSystem" style="width: 20px;" value="1" /> 是
            </label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="isSystem" style="width: 20px;" value="0" /> 否
            </label>
        </div>
        <div id="div_organId" style="${model.isSystem eq 0 ? '':'display: none;'}">
            <label>所属机构:</label>
            <input id="organId" name="organId" value="${model.organId}"
            <%--data-options="validType:combotreeRequired['#organId']"--%>
                   style="width: 260px;height: 28px;">
        </div>
        <div>
            <label>角色名称:</label>
            <input name="name" type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="100" data-options="required:true,missingMessage:'请输入角色名称.',validType:['minLength[1]','legalInput']">
        </div>
        <div>
            <label>角色编码:</label>
            <input name="code" type="text" value="${model.code}" class="easyui-validatebox textbox"
                   maxLength="36" >
        </div>

        <div>
            <label>数据范围:</label>
            <input id="dataScope" name="dataScope" value="${model.dataScope}" style="width: 200px;height: 28px;">
        </div>
        <div id="div_organIds" style="${model.dataScope eq 9 ? '':'display: none;'}">
            <label>授权机构:</label>
            <input id="organIds" name="organIds"
                   style="width: 200px;height: 28px;">
            <label><input id="changeMode" type="checkbox"/>级联模式</label>
        </div>
        <div>
            <label>权限类型:</label>
            <select id="roleType" name="roleType" class="easyui-combobox" style="width: 120px;height: 28px;" >
                <c:forEach items="${roleTypes}" var="roleType">
                    <option value="${roleType.value}" <c:if test="${model.roleType eq roleType.value}">selected</c:if>>${roleType.description}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label style="vertical-align: top;">备注:</label>
            <input name="remark" class="easyui-textbox" value="${model.remark}" data-options="multiline:true" style="width:260px;height:100px;">
        </div>
    </form>
</div>