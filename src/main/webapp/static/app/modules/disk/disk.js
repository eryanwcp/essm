var $folder_tree;
var contextMenuNode;
var $folder_form;
var $folder_dialog;
var $folder_file_search_form;
var $folder_file_datagrid;
var defaultSelectedNode = null; //存放被选中的节点对象 临时变量
var editRow = undefined;
var editRowData = undefined;
var folder_file_toolbar = {
    text: '上传文件',
    iconCls: 'eu-icon-upload',
    handler: function() {
        addFolderFile();
    }
};
$(function() {
    $folder_file_search_form = $("#folder_file_search_form").form();
    loadDiskTree();
    loadFileDatagrid();
});

function loadFileDatagrid() {
    //数据列表
    $folder_file_datagrid = $('#folder_file_datagrid').datagrid({
        url: '',
        checkOnSelect: false,
        selectOnCheck: false,
        fit: true,
        fitColumns: false,         //自适应列宽
        striped: true,             //显示条纹
        remoteSort: false,         //是否通过远程服务器对数据排序
        idField: 'id',
        frozen: true,
        collapsible: true,
        showFooter: true,
        pagination: true,          //底部分页
        rownumbers: true,          //显示行数
        pageSize: 20,
        pageList: [10, 20, 50, 500, 9999],
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        },
        {
            field: 'name',
            title: '文件名',
            sortable: true,
            width: 360,
            formatter: function(value, rowData, rowIndex) {
               if(value != "总大小") {
            	   return "<a onclick='downloadFile(\"" + rowData.fileId + "\")'>" + value + "</a>";
               } else {
            	   return value;
               }
            },
            editor: {
                type: 'validatebox',
                options: {
                    required: true,
                    missingMessage: '请输入名称！',
                    validType: ['minLength[1]', 'length[1,200]']
                }
            }
        }]],
        columns: [[{
            field: 'id',
            title: '主键',
            hidden: true,
            sortable: true,
            align: 'right',
            width: 80
        },
        {
            field: 'prettyFileSize',
            title: '文件大小',
            sortable: true,
            align: 'right',
            width: 110
        },
        {
            field: 'userName',
            title: '上传人',
            width: 100
        },{
            field: 'createTime',
            title: '上传时间',
            sortable: true,
            width: 200
        },
        {
            field: 'operate_all',
            title: '操作',
            formatter: function(value, rowData, rowIndex) {
            	var operateHtml = "";
            	if (rowData.id) {
            	    if (rowData.editing) {
            	        operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-save\"' onclick='saveFileName(this," + rowIndex + ",\"" + rowData.id + "\")' >保存 </a>";
            	        operateHtml += "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-cancel\"' onclick='rejectChanges(" + rowIndex + ");' >取消  </a>";
            	    } else {
            	        operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"eu-icon-disk_download\"' onclick='downloadFile(\"" + rowData.fileId + "\")'>下载</a>";
                        var editHtml = "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' onclick='beginEdit(" + rowIndex + ")'>更名</a>";
                        var delHtml = "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='delFolderFile(\"" + rowData.id + "\",\"" + rowData.name + "\")'>删除</a>&nbsp";
                        operateHtml +=  editHtml;
                        operateHtml +=  delHtml;
            	    }
            	}
            	return operateHtml;
            }
        }]],
        onBeforeEdit: function(index, row) {
            editRow = index;
            editRowData = row;
            row.editing = true;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
        },
        onAfterEdit: function(index, row) {
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
            editRow = undefined;
        },
        onCancelEdit: function(index, row) {
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
            editRow = undefined;
        },
        onLoadSuccess: function() {
            $(this).datagrid('clearSelections'); //取消所有的已选择项
            $(this).datagrid('clearChecked'); //取消所有的选中的择项
            $(this).datagrid('unselectAll'); //取消全选按钮为全选状态
            editRow = undefined;
        },
        onHeaderContextMenu:function(e,field){
            e.preventDefault();
        }
    }).datagrid('showTooltip');
}

function loadDiskTree() {
    //组织机构树
    $folder_tree = $("#folder_tree").tree({
        url: ctxAdmin + "/disk/diskTree",
        formatter: function(node) {
            return node.text;
        },
        onClick: function(node) {
            //  $(this).tree('beginEdit',node.target);
        },
        onBeforeSelect: function(node) {
            defaultSelectedNode = node;
            return true;
        },
        onBeforeLoad: function(node, param) {
            $("#folder_tree").html("数据加载中...");
        },
        onSelect: function(node) {
        	if (true && $folder_file_datagrid) {
        	    var nType = node.attributes["nType"];
        	    var organType = node.attributes['type'];

                var _toolbar =  [{
                    text: '批量下载',
                    iconCls: 'eu-icon-disk_download',
                    handler: function() {
                        downloadFile();
                    }
                }];
                var _queryParams = {};

                if ("FolderAuthorize" == nType &&'0' == node.id) { //我的云盘
                    _toolbar.unshift(folder_file_toolbar);
                } else {
                    var operate_all = node.attributes["operate"];
                    var parentNode = $folder_tree.tree("getParent", node.target);
                    while ((operate_all == undefined || operate_all == null) && parentNode != null) { //查找父级权限控制
                        operate_all = parentNode.attributes["operate"];
                        parentNode = $folder_tree.tree("getParent", parentNode.target);
                    }
                    if (true == operate_all) {
                        _toolbar.unshift(folder_file_toolbar);
                    }
                }

        	    if ("Folder" == nType) {
        	        _queryParams = {
        	            folderId: node.id
        	        } //文件夹ID
        	    }else if ("FolderAuthorize" == nType) {
                    _queryParams = {
                        folderAuthorize: node.id
                    } //文件夹隶属云盘类型
                }
                $folder_file_datagrid.datagrid({
        	        toolbar: _toolbar,
        	        queryParams: _queryParams,
        	        url: ctxAdmin + '/disk/folderFileDatagrid'
        	    }).datagrid('showTooltip');
        	}
        },
        onContextMenu: function(e, node) {
            e.preventDefault();
            contextMenuNode = node;
            var nType = node.attributes["nType"];
            var organType = node.attributes['type'];
            var treeName = 'folder_treeMenu_add';
            if ("Folder" == nType) {
                if (true == node.attributes.operate) {
                    treeName = 'folder_treeMenu_all';
                } else {
                    treeName = '';
                }
            }
            if ('' != treeName) {
                $("#" + treeName).menu({
                    onClick: function(item) {
                        if ("addFolder" == item.name) {
                            showFolderDialog();
                        } else if ("editFolder" == item.name) {
                            showFolderDialog(node.id);
                        } else if ("deleteFolder" == item.name) {
                            delFolder(node.id, node.text);
                        }
                    }
                }).menu('show', {
                    left: e.pageX,
                    top: e.pageY
                });
            }
        },
        onLoadSuccess: function(node, data) {
            contextMenuNode = undefined;
            if (defaultSelectedNode != null) {
                defaultSelectedNode = $(this).tree('find', defaultSelectedNode.id);
                if (defaultSelectedNode != null) { //刷新树后 如果临时变量中存在被选中的节点 则重新将该节点置为被选状态
                    $(this).tree('select', defaultSelectedNode.target);
                }
            }
            var rootNodes = $(this).tree("getRoots");
            if (defaultSelectedNode == null) {
            	$folder_tree.tree("select", rootNodes[0].target);
            }
        }
    });
}


function saveFileName(target, index, id) {
    $folder_file_datagrid.datagrid('unselectAll');
    $folder_file_datagrid.datagrid('selectRow', index);
    $folder_file_datagrid.datagrid('endEdit', index);
    var selectRow = $folder_file_datagrid.datagrid('getSelected');
    var valid = $folder_file_datagrid.datagrid('validateRow', index);
    if (!valid) {
        var validateEdit = $folder_file_datagrid.datagrid('getEditor', {
            index: index,
            field: 'name'
        });
        $(validateEdit.target).focus();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: ctxAdmin + '/disk/fileSave',
        data: $.extend({
            id: selectRow.id,
            modelType: 'File'
        },
        selectRow),
        traditional: true,
        dataType: 'json',
        success: function(data) {
            if (data.code == 1) {
                $folder_file_datagrid.datagrid('reload');
                eu.showMsg(data.msg); //操作结果提示
            }
        }

    });
}
function updateActions(index) {
    $folder_file_datagrid.datagrid('updateRow', {
        index: index,
        row: {}
    });
    $folder_file_datagrid.datagrid('refreshRow',index);
}
//开始编辑
function beginEdit(index) {
    $folder_file_datagrid.datagrid('beginEdit', index);
}
//撤销
function rejectChanges(index) {
    $folder_file_datagrid.datagrid('endEdit', index);
    $folder_file_datagrid.datagrid('rejectChanges', index);
}

//下载文件
function downloadFile(pageId) {
    var ids = new Array();
    if (pageId) {
        ids.push(pageId);
    } else {
        var rows = $folder_file_datagrid.datagrid('getChecked');
        if (rows && true) {
            $.each(rows,
                function(i, row) {
                    ids.push(row.fileId);
                });
        }
    }

    if(ids.length > 0){
        var url  = ctxAdmin  + "/disk/downloadDiskFile?fileIds=" + ids.join(",");
        $("#annexFrame").attr("src", url);
    } else {
        eu.showMsg("请选择要操作的对象！");
        return;
    }
}


//文件夹 弹窗 新增、修改
function showFolderDialog(folderId) {
    var inputUrl = ctxAdmin + "/disk/folderInput";
    if (folderId != undefined) {
        inputUrl += "?folderId=" + folderId;
    } else {
        var selectedNode = $folder_tree.tree("getSelected");
        selectedNode = (contextMenuNode != undefined) ? contextMenuNode: selectedNode;
        if (selectedNode != undefined && selectedNode != null) {
            var folderAuthorize = '';
            var nType = selectedNode.attributes['nType'];
            var nodeId = selectedNode.id;
            var parentFolderId = '';
            if ("FolderAuthorize" == nType && ("0" == nodeId) ) { //一级目录
                folderAuthorize = nodeId;
            } else if ("Folder" == nType) { //文件夹目录
                parentFolderId = nodeId;
                var parentNode = $folder_tree.tree("getParent", selectedNode.target);
                var nodeLevel = $folder_tree.tree("getLevel", selectedNode.target);
                while (parentNode != undefined && nodeLevel != 2) {
                    nodeLevel = $folder_tree.tree("getLevel", parentNode.target);
                    parentNode = $folder_tree.tree("getParent", parentNode.target);
                }
                if (parentNode != undefined) {
                    folderAuthorize = parentNode.id;
                }
            }
            inputUrl += "?folderAuthorize=" + folderAuthorize + "&parentFolderId=" + parentFolderId;
        }

    }

    //弹出对话窗口
    $folder_dialog = $('<div/>').dialog({
        title: '文件夹信息',
        top: 20,
        width: 500,
        height: 360,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [{
            text: '保存',
            iconCls: 'easyui-icon-save',
            handler: function() {
                $folder_form.submit();
            }
        },
        {
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function() {
                $folder_dialog.dialog('destroy');
            }
        }],
        onClose: function() {
            $(this).dialog('destroy');
        },
        onLoad: function() {
            folderFormInit();
        }
    });

}



function folderFormInit() {
    $folder_form = $('#folder_form').form({
        url: ctxAdmin + '/disk/saveFolder',
        onSubmit: function(param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            } else {
                param.modelType = "Folder";
            }
            return isValid;
        },
        success: function(data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $folder_dialog.dialog('destroy'); //销毁对话框
                $folder_tree.tree("reload");
                eu.showMsg(json.msg); //操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning',
                function() {
                    if (json.obj) {
                        $('#$folder_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}


/**
 * 删除文件夹
 */
function delFolder(folderId, folderName) {
    $.messager.confirm('确认提示！', '您确定要删除[' + folderName + '],包含下级文件夹以及文件?',
    function(r) {
        if (r) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            $.ajax({
                url: ctxAdmin + '/disk/folderRemove/' + folderId,
                type: 'post',
                dataType: 'json',
                traditional: true,
                success: function(data) {
                    $.messager.progress('close');
                    if (data.code == 1) {
                        $folder_tree.tree('reload');
                        $folder_file_datagrid.datagrid('reload');
                        eu.showMsg(data.msg); //操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                }
            });
        }
    });
}

/**
 * 上传文件,若未指定文件夹则上传至当前选中云盘的默认文件夹中
 */
function addFolderFile() {
    var selectedNode = $folder_tree.tree("getSelected");
    if (selectedNode != undefined && selectedNode.id != undefined) {
        var text = selectedNode.text;
        var title = "上传文件";
        if (text != undefined && text != null && text != "") {
            title += ":" + text;
        }
        var url = ctxAdmin + '/disk/fileInput';
        var folderAuthorize = null;
        var organId = null;
        var folderId = null;
        var nType = selectedNode.attributes["nType"];
        var organType = selectedNode.attributes['type'];
        folderId = selectedNode.id;
        url += "?folderId=" + folderId;
        _dialog = $("<div/>").dialog({
            title: title,
            top: 10,
            href: url,
            width: 500,
            height: 360,
            maximizable: true,
            iconCls: 'eu-icon-file',
            modal: true,
            buttons: [{
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function() {
                    _dialog.dialog('destroy');
                    $folder_file_datagrid.datagrid("reload");
                }
            }],
            onClose: function() {
                _dialog.dialog('destroy');
            }
        });
    }

}
/**
 * 删除文件夹下的文件
 * @param fileId
 */
function delFolderFile(fileId, fileName) {
    var rows = $folder_file_datagrid.datagrid('getChecked');
    if (fileId != undefined || rows.length > 0) {
        var tipMsg = "您确定要删除";
        if (fileName != undefined) {
        	tipMsg += "文件[" + fileName + "]?";
        } else {
            tipMsg += "选中的所有文件?";
        }

        $.messager.confirm('确认提示！', tipMsg,
        function(r) {
            if (r) {
                var selectedFileIds = new Array();
                $.messager.progress({
                    title: '提示信息！',
                    text: '数据处理中，请稍后....'
                });
                if (fileId != undefined) {
                    selectedFileIds.push(fileId);
                } else {
                    $.each(rows,
                    function(i, row) {
                        selectedFileIds.push(row.id);
                    });
                }

                $.ajax({
                    url: ctxAdmin + '/disk/delFolderFile',
                    type: 'post',
                    data: {
                        fileIds: selectedFileIds
                    },
                    dataType: 'json',
                    traditional: true,
                    success: function(data) {
                        $.messager.progress('close');
                        if (data.code == 1) {
                            $folder_file_datagrid.datagrid("reload");
                            eu.showMsg(data.msg); //操作结果提示
                        } else {
                            eu.showAlertMsg(data.msg, 'error');
                        }
                    }
                });
            }
        });

    } else {
        eu.showMsg("请选择要操作的对象！");
    }

}

function search() {
    var selectedNode = $folder_tree.tree("getSelected");
    var folderId = '';
    if (selectedNode) {
        folderId = selectedNode.id;
    }
    var queryParam = $.extend($.serializeObject($folder_file_search_form),{folderId:folderId})
    $folder_file_datagrid.datagrid("load",queryParam);
}