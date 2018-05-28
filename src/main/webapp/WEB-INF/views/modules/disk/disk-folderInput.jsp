<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var parentId_combotree = undefined;
    var folderAuthorize_combobox = undefined;
    var defaultFolderIdValue = undefined; //文件夹Id
    var defaultParentIdValue = undefined; //父文件夹Id
    var defaultFolderAuthorizeValue = undefined; //所属归类Id
    $(function () {
    	    defaultFolderAuthorizeValue = '${not empty folderAuthorize ? folderAuthorize : model.folderAuthorize}';
    	    defaultFolderIdValue = '${not empty folderId ? folderId : model.id}';
    	    defaultParentIdValue = '${not empty parentFolderId ? parentFolderId : model.parentId}';
            loadFolderAuthorize();
    });

  //加载归类下拉框
    function loadFolderAuthorize() {
        folderAuthorize_combobox = $("#folderAuthorize").combobox({
            url: '${ctxAdmin}/disk/folderAuthorizeCombobox',
            disabled: ${not empty model.id ? true : false},
            value: defaultFolderAuthorizeValue,
            editable: false,
            onSelect: function(record) {
                loadParent(record.value,  defaultFolderIdValue);
            },
            onLoadSuccess: function() {
                var selectFolderAuthorizeValue = $(this).combobox('getValue');
                loadParent(selectFolderAuthorizeValue,  defaultFolderIdValue);
            }
        });
    }
  
    //加载父级文件夹下拉框
    function loadParent(folderAuthorizeValue, folderIdValue, organIdValue) {
        parentId_combotree = $("#parentId").combotree({
            url: '${ctxAdmin}/disk/folderTree?selectType=select&folderAuthorize=' + folderAuthorizeValue + '&excludeFolderId=' + folderIdValue,
            onLoadSuccess: function() {
                //如果默认归类与选中归类相同时
                if (defaultFolderAuthorizeValue == folderAuthorizeValue) {
                    parentId_combotree.combotree('setValue', defaultParentIdValue);
                }
            }
        });

    }

</script>
<div>
    <form id="folder_form" method="post" class="dialog-form" novalidate>
        <input type="hidden" name="id" value="${model.id}"/>
        <!-- 版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>

        <div>
            <label>名称:</label>
            <input name="name" type="text" class="easyui-validatebox textbox" value="${model.name}"
                   maxLength="12"
                   data-options="required:true,missingMessage:'请输入名称.',validType:['minLength[1]','legalInput']">
        </div>
        <div>
            <label>归类:</label>
            <input id="folderAuthorize" name="folderAuthorize" style="width: 120px;height: 28px;"/>
                   <%--data-options="validType:['comboboxRequired[\'#folderAuthorize\']']"/>--%>
        </div>
        <div>
            <label>上级文件夹:</label>
            <input id="parentId" name="parent.id" style="width: 260px;height: 28px;" />
        </div>
        <div>
            <label>备注:</label>
            <%--<textarea maxLength="255" name="remark"--%>
                      <%--style="position: relative;resize: none;height: 75px;width: 260px;">${model.remark}</textarea>--%>
            <input name="remark" maxlength="120" class="easyui-textbox" value="${model.remark}" data-options="multiline:true" style="width:260px;height:100px;">
        </div>

    </form>
</div>