<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var organIds = ${fns:toJson(model.organIds)};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/post-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="post_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>所属机构:</label>
            <input id="organId" name="organId" value="${model.organId}"
                   <%--data-options="validType:combotreeRequired['#organId']"--%>
                   style="width: 260px;height: 28px;">
        </div>
        <div>
            <label>附属机构:</label>
            <input id="organIds" name="organIds"
                   style="width: 200px;height: 28px;">
            <label><input id="changeMode" type="checkbox"/>级联模式</label>
        </div>
        <div>
            <label>岗位名称:</label>
            <input name="name" type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="100" data-options="required:true,missingMessage:'请输入岗位名称.',validType:['minLength[1]','legalInput']">
        </div>
        <div>
            <label>岗位编码:</label>
            <input name="code" type="text" value="${model.code}"
                   class="easyui-validatebox textbox"
                   maxLength="36" >
        </div>
        <div>
            <label style="vertical-align: top;">描述:</label>
            <input name="remark" maxLength="255" value="${model.remark}"
                   class="easyui-textbox" maxLength="2000"  data-options="multiline:true" style="width:260px;height:75px;">
        </div>
    </form>
</div>