<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
var folder_from_organ_div  = undefined;
var parentId_combotree = undefined;
var folderAuthorize_combobox = undefined;
var organId_combobox = undefined;
var defaultFolderIdValue = undefined; //文件夹Id
var defaultParentIdValue = undefined; //父文件夹Id
var defaultOrganIdValue = undefined; //所属部门Id
var defaultFolderAuthorizeValue = undefined; //所属归类Id
    $(function () {
    	    folder_from_organ_div = $("#organ_div");
    	    defaultFolderAuthorizeValue = '${not empty folderAuthorize ? folderAuthorize : model.folderAuthorize}';
    	    defaultFolderIdValue = '${not empty folderId ? folderId : model.id}';
    	    defaultParentIdValue = '${not empty parentFolderId ? parentFolderId : model.parentId}';
    	    defaultOrganIdValue = '${not empty organId ? organId : model.organId}';
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
                toggole(record.value, '', true);
            },
            onLoadSuccess: function() {
                var selectFolderAuthorizeValue = $(this).combobox('getValue');
                toggole(defaultFolderAuthorizeValue, defaultOrganIdValue, false);
            }
        });
    }
  
  //加载父级文件夹下拉框
    function loadParent(folderAuthorizeValue, folderIdValue, organIdValue) {
        parentId_combotree = $("#parentId").combotree({
            url: '${ctxAdmin}/disk/folderTree?selectType=select&folderAuthorize=' + folderAuthorizeValue + '&excludeFolderId=' + folderIdValue + "&organId=" + organIdValue,
            onLoadSuccess: function() {
                //如果默认归类与选中归类相同时
                if (defaultFolderAuthorizeValue == folderAuthorizeValue) {
                    if ('2'  == folderAuthorizeValue) { //机构归类下还需判断选中机构与默认机构是否相同
                        var organId = organId_combobox.combotree('getValue'); //选中的机构下拉框Id
                        if ( defaultOrganIdValue == organId ) {
                            parentId_combotree.combotree('setValue', defaultParentIdValue);
                        }
                    } else {
                        parentId_combotree.combotree('setValue', defaultParentIdValue);
                    }
                }
            }
        });

    }

    function toggole(selectFolderAuthorizeValue,organIdValue, clear) {
        if (clear) {
            if (organId_combobox != undefined) {
                organId_combobox.combobox("clear");
            }
        }
        if ('2' == selectFolderAuthorizeValue) { //机构
            folder_from_organ_div.show();
            loadOrgan(selectFolderAuthorizeValue);
        } else {
            folder_from_organ_div.hide();
            loadParent(selectFolderAuthorizeValue, defaultFolderIdValue, organIdValue);
         }
    }

    function loadOrgan(selectFolderAuthorizeValue) {
        organId_combobox = $("#organId").combotree({
            url: "${ctxAdmin}/sys/organ/tree?selectType=select&dataScope=2&cascade=true",
            valueField: 'id',
            textField: 'text',
            value: defaultOrganIdValue,
//            validType: ['combotreeRequired[\'#organId\']'],
            onSelect: function(record) {
                loadParent(selectFolderAuthorizeValue, defaultFolderIdValue, record.id);
            },
            onLoadSuccess: function() {
                var organId = organId_combobox.combotree('getValue');
                loadParent(selectFolderAuthorizeValue, defaultFolderIdValue, organId);
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
        <div id="organ_div" style="display: none;">
            <label>所属部门:</label>
            <input id="organId" name="organId" style="width: 260px;height: 28px;" />
        </div>
        <div>
            <label>上级文件夹:</label>
            <input id="parentId" name="parentId" style="width: 260px;height: 28px;" />
        </div>
        <div>
            <label>备注:</label>
            <%--<textarea maxLength="255" name="remark"--%>
                      <%--style="position: relative;resize: none;height: 75px;width: 260px;">${model.remark}</textarea>--%>
            <input name="remark" maxlength="120" class="easyui-textbox" value="${model.remark}" data-options="multiline:true" style="width:260px;height:100px;">
        </div>

    </form>
</div>