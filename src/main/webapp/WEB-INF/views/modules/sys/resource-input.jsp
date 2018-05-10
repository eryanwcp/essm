<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var modelId = '${model.id}';
    var modelStatus = '${model.status}';
    var modelType = '${model.type}';
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/resource-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="resource_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <div>
            <label>上级资源:</label>
            <input id="_parentId" name="_parentId" value="${not empty parentId ? parentId:model._parentId}"
                   style="width: 200px;height: 28px;"/>
        </div>
        <div>
            <label>资源类型:</label>
            <input id="type" name="type" value="${model.type}"
                   data-options="required:true,missingMessage:'请选择资源类型.'"
                   style="width: 200px;height: 28px;"/>
            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="上级资源的资源类型为[功能]，则资源类型默认为[功能]，并且不可更改." ></span>
        </div>
        <div>
            <label>资源图标:</label>
            <input id="iconCls" name="iconCls" type="text" value="${model.iconCls}"
                    style="width: 200px;height: 28px;"/>
        </div>
        <div>
            <label>资源名称:</label>
            <input type="text" id="name" name="name" value="${model.name}"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="请输入资源名称..."
                   data-options="required:true,missingMessage:'请输入资源名称.',validType:['minLength[1]']" />
        </div>
        <div>
            <label>资源编码:</label>
            <input type="text" id="code" name="code" value="${model.code}"
                   maxLength="36" class="easyui-validatebox textbox" placeholder="请输入资源编码..."
                   data-options="validType:['minLength[1]']" />
            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="资源识别的唯一标识;主要用于[功能]类型的资源能够根据编码进行权限控制." ></span>
        </div>
        <div>
            <label style="vertical-align: top;">链接地址:</label>
            <%--<textarea maxLength="255" name="url"--%>
            <%--style="position: relative; resize: none; height: 50px; width: 260px;"></textarea>--%>
            <input name="url" class="easyui-textbox" value="${model.url}" maxLength="255" data-options="multiline:true" style="width:260px;height:75px;">
        </div>
        <div>
            <label style="vertical-align: top;">标识地址:</label>
            <%--<textarea maxLength="2000" name="markUrl"--%>
            <%--style="position: relative; resize: none; height: 50px; width: 260px;"></textarea>--%>

            <input name="markUrl" class="easyui-textbox" value="${model.markUrl}"  maxLength="2000"  data-options="multiline:true" style="width:260px;height:75px;">

            <%--提示小图标--%>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="设置标识地址的URL会被拦截器拦截；支持通配符'*';多个标识地址之间以';'分割." ></span>
        </div>
        <div>
            <label>排序:</label>
            <input type="text" id="sort" name="sort"  value="${model.sort}"  class="easyui-numberspinner" style="width:120px;"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" />
        </div>
        <div>
            <label>状态:</label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status"  style="width: 20px;" value="0" /> 启用
            </label>
            <label style="text-align: left;width: 60px;">
                <input type="radio" name="status" style="width: 20px;" value="3" /> 停用
            </label>
        </div>
    </form>
</div>