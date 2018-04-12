<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var modelId = '${model.id}';
    var modelStatus = '${model.status}';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/organ-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="organ_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>所属区域:</label>
            <input id="areaId" name="areaId" value="${model.areaId}" style="width:260px;height: 28px;"/>
        </div>
        <div>
            <label>上级机构:</label>
            <input id="_parentId" name="_parentId" value="${not empty parentId ? parentId:model._parentId}" style="width:260px;height: 28px;"/>
        </div>
        <div>
            <label>机构类型:</label>
            <input id="type" name="type" value="${model.type}"  style="width:120px;height: 28px;"
                   data-options="required:true,missingMessage:'请选择机构类型.'" />
            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="顶级机构只能为[机构(法人单位)]；上级机构的机构类型为[部门]，则机构类型只可以为[部门]或者[小组]；上级机构的机构类型为[小组]，则机构类型只可以为[小组]." ></span>
        </div>
        <div>
            <label>机构名称:</label>
            <input type="text" id="name" name="name" value="${model.name}"
                   maxLength="255" class="easyui-validatebox textbox" placeholder="请输入机构名称..."
                   data-options="required:true,missingMessage:'请输入机构名称.',validType:['minLength[1]']" />
        </div>
        <div>
            <label>机构简称:</label>
            <input type="text" id="shortName" name="shortName" value="${model.shortName}"
                   maxLength="255" class="easyui-validatebox textbox" placeholder="请输入机构简称..."/>
        </div>
        <div>
            <label>机构系统编码:</label>
            <input type="text" id="sysCode" name="sysCode" value="${model.sysCode}"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="请输入机构系统编码..."
                   data-options="required:true,missingMessage:'请输入机构系统编码.',validType:['minLength[1]']" />
        </div>
        <div>
            <label>机构编码:</label>
            <input type="text" id="code" name="code" value="${model.code}"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="请输入机构编码..."
                   data-options="validType:['minLength[1]']" />
        </div>
        <div>
            <label>地址:</label>
            <input type="text" id="address" name="address" value="${model.address}"
                   maxLength="255" class="easyui-validatebox textbox" placeholder="请输入地址..."
                   data-options="validType:['minLength[1]']" />
        </div>
        <div>
            <label>手机号:</label>
            <input type="text" id="mobile" name="mobile" value="${model.mobile}"
                   class="easyui-validatebox textbox" placeholder="请输入手机号..."
                   data-options="validType:['mobile']" />
        </div>
        <div>
            <label>电话号码:</label>
            <input type="text" id="phone" name="phone" value="${model.phone}"
                   class="easyui-validatebox textbox" placeholder="请输入电话号码..."
                   data-options="validType:['phone']" />
        </div>
        <div>
            <label>传真:</label>
            <input type="text" id="fax" name="fax" value="${model.fax}"
                   class="easyui-validatebox textbox" placeholder="请输入传真..."/>
        </div>
        <div>
            <label>排序:</label>
            <input type="text" id="orderNo" name="orderNo" value="${model.orderNo}" class="easyui-numberspinner" STYLE="width: 120px;"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" />
        </div>
        <div>
            <label>状态:</label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status" style="width: 20px;" value="0" /> 启用
            </label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status" style="width: 20px;" value="3" /> 停用
            </label>
        </div>
    </form>
</div>