<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var modelId = "${model.id}";
    var contactGroupType = "${model.contactGroupType}";

    $(function(){
        if (modelId == "") {
            setSortValue();
        }
//        loadContactGroupType();
    });

    function loadContactGroupType(){
        $('#contactGroupType').combobox({
            url:'${ctxAdmin}/mail/contactGroup/contactGroupTypeCombobox',
            editable:false,//是否可编辑
            value:contactGroupType
        });
    }

    //设置排序默认值
    function setSortValue() {
        $.get('${ctxAdmin}/mail/contactGroup/maxSort', function(data) {
            if (data.code == 1) {
                $('#orderNo').numberspinner('setValue',data.obj+1);
            }
        }, 'json');
    }
</script>
<div>
    <form id="contactGroup_form" class="dialog-form" method="post" novalidate>
        <input type="hidden"  name="id" value="${model.id}" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <input type="hidden" id="contactGroupType" name="contactGroupType" value="${model.contactGroupType}"/>
        <div>
            <label>名称：</label>
            <input name="name"  type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="10" data-options="required:true,missingMessage:'请输入名称.',validType:['minLength[1]']">
        </div>
        <%--<div>--%>
            <%--<label>类型:</label>--%>
            <%--<input id="contactGroupType" name="contactGroupType" style="height: 28px;width: 120px;" />--%>
        <%--</div>--%>
        <div>
            <label>排序号：</label>
            <input type="text" id="orderNo" name="orderNo" class="easyui-numberspinner" style="width:120px;"
                   data-options="min:1,max:99999999,size:9,maxlength:9,height:28" value="${model.orderNo}"/>
        </div>
        <div>
            <label style="vertical-align: top;">备注：</label>
            <input name="remark" class="easyui-textbox" data-options="multiline:true" style="width:260px;height:100px;">
        </div>
    </form>
</div>