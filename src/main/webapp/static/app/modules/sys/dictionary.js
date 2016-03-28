var $dictionary_tree;
var $dictionary_dialog;
var $dictionary_form;

var $dictionaryItem_datagrid;
var editRow = undefined;
var editRowData = undefined;
var $dictionaryItem_search_form;
var currentDictionaryId = undefined;
$(function () {
    $dictionaryItem_search_form = $('#dictionaryItem_search_form').form();
    loadDictionaryTree();
    dictionaryItemDatagrid();
});

/**
 * 数据字典Tree
 */
function loadDictionaryTree() {
    //组织机构树
    $dictionary_tree = $("#dictionary_tree").tree({
        url: ctxAdmin + "/sys/dictionary/tree",
        formatter: function (node) {
            return node.text;
        },
        onClick: function (node) {
            search();
        },
        onBeforeLoad: function (node, param) {
            $("#dictionary_tree").html("数据加载中...");
        },
        onSelect: function (node) {

        },
        onContextMenu: function (e, node) {
            e.preventDefault();
            if (node.id != undefined && node.id != "") {
                $("#treeMenu").menu({
                    onClick: function (item) {
                        if (item.name == "edit") {
                            showDictionaryDialog(node.id);
                        } else if (item.name == "delete") {
                            delDictionary(node.id, node.text);
                        }
                    }
                }).menu('show', {
                    left: e.pageX,
                    top: e.pageY
                });
            }
        },
        onLoadSuccess: function (node, data) {
        }
    });
}
/**
 * 列表
 */

function dictionaryItemDatagrid() {
    //数据列表
    $dictionaryItem_datagrid = $('#dictionaryItem_datagrid').datagrid({
        url: ctxAdmin + '/sys/dictionaryItem/datagrid',
        fit: true,
        pagination: true,//底部分页
        pagePosition: 'bottom',//'top','bottom','both'.
        fitColumns: false,//自适应列宽
        striped: true,//显示条纹
        pageSize: 20,//每页记录数
        singleSelect: false,//单选模式
        rownumbers: true,//显示行数
        checkbox: true,
        nowrap: true,
        border: false,
        remoteSort: false,//是否通过远程服务器对数据排序
        sortName: 'orderNo',//默认排序字段
        sortOrder: 'asc',//默认排序方式 'desc' 'asc'
        idField: 'id',
        frozenColumns: [[
            {field: 'ck', checkbox: true, width: 60}, {
                field: 'dictionaryId',
                title: '数据字典',
                width: 150,
                formatter: function (value, rowData, rowIndex) {
                    return rowData['dictionaryName'];
                },
                editor: {
                    type: 'combobox',
                    options: {
                        url: ctxAdmin + '/sys/dictionary/comboboxGroup',
                        required: true,
                        missingMessage: '请选择字数据字典！',
                        editable: false,
                        groupField: 'group',
                        onSelect: function (record) {
                            currentDictionaryId = record['value'];
                            editRowData["dictionaryCode"] = record['data'];
                            var dictionaryCode = editRowData["dictionaryCode"];
                            initCodeAndValue(dictionaryCode);

                        },
                        onLoadSuccess: function () {
                            var data = $(this).combobox('getData');
                            currentDictionaryId = $(this).combobox('getValue');
                            var dictionaryCode = "";
                            $.each(data, function (i, record) {
                                if (record['value'] == currentDictionaryId) {
                                    dictionaryCode = record['data'];
                                    return false;
                                }
                            });
                            if(editRowData == undefined || editRowData['id'] == undefined || editRowData['id'] ==''){
                                initCodeAndValue(dictionaryCode);
                            }
                        }
                    }
                }
            }, {
                field: 'name',
                title: '名称',
                width: 200,
                editor: {
                    type: 'textbox',
                    options: {
                        required: true,
                        missingMessage: '请输入名称！',
                        validType: ['minLength[1]', 'length[1,64]', 'legalInput']
                    }
                }
            }
        ]],
        columns: [[
            {field: 'id', title: '主键', hidden: true, sortable: true, align: 'right', width: 80}, {
                field: 'parentId',
                title: '上级节点',
                width: 200,
                formatter: function (value, rowData, rowIndex) {
                    return rowData['parentName'];
                },
                editor: {
                    type: 'combotree',
                    options: {
                        url: ctxAdmin + '/sys/dictionaryItem/combotree?selectType=select',
                        onBeforeLoad: function (node, param) {
                            if(currentDictionaryId == undefined && editRowData['dictionaryId'] != ''){
                                currentDictionaryId = editRowData['dictionaryId'];
                            }

                            if (currentDictionaryId != undefined) {
                                param['dictionary.id'] = currentDictionaryId;
                            }
                            if (editRowData != undefined) {
                                param.id = editRowData.id;
                            }
                        }
                    }
                }
            }, {
                field: 'code',
                title: '编码',
                width: 100,
                sortable: true,
                editor: {
                    type: 'textbox',
                    options: {
                        required: true,
                        missingMessage: '请输入编码！',
                        validType: ['minLength[1]', 'length[1,36]', 'legalInput'],
                        onChange: function (newValue, oldValue) {
                            var vallueEditor = $dictionaryItem_datagrid.datagrid('getEditor', {
                                index: editRow,
                                field: 'value'
                            })
                            $(vallueEditor.target).textbox("setValue", newValue);
                        }
                    }
                }
            }, {
                field: 'value',
                title: '属性值',
                width: 120,
                sortable: true,
                editor: {
                    type: 'textbox',
                    options: {}
                }
            }, {
                field: 'remark',
                title: '备注',
                width: 200,
                editor: {
                    type: 'textbox',
                    options: {}
                }
            }, {
                field: 'orderNo',
                title: '排序',
                align: 'right',
                width: 60,
                sortable: true,
                editor: {
                    type: 'numberspinner',
                    options: {
                        required: true
                    }
                }
            },
            {
                field: 'operate', title: '操作', width: 200,
                formatter: function (value, rowData, rowIndex) {
                    var operateHtml = "";
                    if (rowData.editing) {
                        operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-save\"' onclick='saveDictionaryItem(this," + rowIndex + ",\"" + rowData.id + "\")' >保存 </a>"
                            + "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-cancel\"' onclick='rejectChanges(" + rowIndex + ");' >取消  </a>";
                    } else {
                        operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-edit\"' onclick='beginEdit(" + rowIndex + ")'>编辑</a>"
                            + "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='delDictionaryItem(\"" + rowData.id + "\",\"" + rowData.name + "\")'>删除</a>";
                    }
                    return operateHtml;
                }
            }]],
        toolbar: [{
            text: '新增',
            iconCls: 'easyui-icon-add',
            handler: function () {
                addDictionaryItem()
            }
        }, '-', {
            text: '删除',
            iconCls: 'easyui-icon-remove',
            handler: function () {
                delDictionaryItem()
            }
        }],
        onBeforeEdit: function (index, row) {
            editRow = index;
            editRowData = row;
            row.editing = true;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
        },
        onAfterEdit: function (index, row) {
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
            editRow = undefined;
        },
        onCancelEdit: function (index, row) {
            row.editing = false;
            updateActions(index);
            $.parser.parse($(".easyui-linkbutton").parent());
            editRow = undefined;
        },
        onLoadSuccess: function (data) {
            $(this).datagrid('clearSelections');
            $(this).datagrid('clearChecked');
            $(this).datagrid('unselectAll');
            editRow = undefined;

        },
        onRowContextMenu: function (e, rowIndex, rowData) {
            e.preventDefault();
        }
    }).datagrid('showTooltip');
}
/**
 * 初始化 编码 值
 * @param dictionaryCode
 */
function initCodeAndValue(dictionaryCode) {
    var dictionaryParentEditor = $dictionaryItem_datagrid.datagrid('getEditor', {index: editRow, field: 'parentId'});
    $(dictionaryParentEditor.target).combotree('clear').combotree('reload');
    var codeEditor = $dictionaryItem_datagrid.datagrid('getEditor', {index: editRow, field: 'code'});
    var vallueEditor = $dictionaryItem_datagrid.datagrid('getEditor', {index: editRow, field: 'value'})
    $(codeEditor.target).textbox("setValue", dictionaryCode + '_');
    $(vallueEditor.target).textbox("setValue", dictionaryCode + '_');
}

/**
 * 新增
 * @returns {boolean}
 */
function addDictionaryItem() {
    if (editRow != undefined) {
        eu.showMsg("请先保存正在编辑的数据！");
        return false;
        //$dictionaryItem_datagrid.datagrid('endEdit', editRow);
    } else {
        $dictionaryItem_datagrid.datagrid('unselectAll');
        var node = $dictionary_tree.tree('getSelected');
        var dictionaryId = '';
        if (node != null && node['attributes']['groupId'] != null) {
            dictionaryId = node['id'];
        }
        var row = {id: '', dictionaryId: dictionaryId};
        $dictionaryItem_datagrid.datagrid('appendRow', row);
        editRow = $dictionaryItem_datagrid.datagrid('getRows').length - 1;
        $dictionaryItem_datagrid.datagrid('selectRow', editRow);
        beginEdit(editRow);
        var rowIndex = $dictionaryItem_datagrid.datagrid('getRowIndex', row);//返回指定行的索引
        var sortEdit = $dictionaryItem_datagrid.datagrid('getEditor', {index: rowIndex, field: 'orderNo'});
        setDictionaryItemSortValue(sortEdit.target)
    }
}

function updateActions(index) {
    $dictionaryItem_datagrid.datagrid('updateRow', {
        index: index,
        row: {}
    });
}
/**
 * 开始编辑
 * @param index
 */
function beginEdit(index) {
    $dictionaryItem_datagrid.datagrid('beginEdit', index);
}
/**
 *
 * @param index
 */
function rejectChanges(index) {
    $dictionaryItem_datagrid.datagrid('endEdit', index);
    $dictionaryItem_datagrid.datagrid('rejectChanges', index);
}
/**
 * 保持数据字典项
 * @param target
 * @param index
 * @param id
 * @returns {boolean}
 */
function saveDictionaryItem(target, index, id) {
    $dictionaryItem_datagrid.datagrid('unselectAll');
    $dictionaryItem_datagrid.datagrid('selectRow', index);
    $dictionaryItem_datagrid.datagrid('endEdit', index);
    var selectRow = $dictionaryItem_datagrid.datagrid('getSelected');
    var valid = $dictionaryItem_datagrid.datagrid('validateRow', index);
    if (!valid) {
        var validateEdit = $dictionaryItem_datagrid.datagrid('getEditor', {index: index, field: 'name'});
        $(validateEdit.target).focus();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: ctxAdmin + '/sys/dictionaryItem/save',
        data: $.extend({id: selectRow.id}, selectRow),
        traditional: true,
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                $dictionaryItem_datagrid.datagrid('reload');
                eu.showMsg(data.msg);
            } else if (data.code == 2) {
                eu.showMsg(data.msg);
                var validateEdit = $dictionaryItem_datagrid.datagrid('getEditor', {index: index, field: data['obj']});
                if(validateEdit != undefined){
                    $(validateEdit.target).focus();
                }
                beginEdit(index);
            }else {
                eu.showAlertMsg(data.msg);
                rejectChanges(index);
            }
        }

    });
}

/**
 * 数据字典初始化
 */
function dictionaryFormInit() {
    $dictionary_form = $('#dictionary_form').form({
        url: ctxAdmin + '/sys/dictionary/save',
        onSubmit: function (param) {
            $.messager.progress({
                title: '提示信息！',
                text: '数据处理中，请稍后....'
            });
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');
            }
            return isValid;
        },
        success: function (data) {
            $.messager.progress('close');
            var json = $.parseJSON(data);
            if (json.code == 1) {
                $dictionary_dialog.dialog('destroy');//销毁对话框
                $dictionary_tree.tree('reload');//重新加载列表数据
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code == 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    if (json.obj) {
                        $('#dictionary_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        },
        onLoadSuccess: function (data) {
        }
    });
}


/**
 * 新增或修改 数据字典
 * @param dictionaryId
 */
function showDictionaryDialog(dictionaryId) {
    var inputUrl = ctxAdmin + "/sys/dictionary/input";
    if (dictionaryId != undefined) {
        inputUrl += "?id=" + dictionaryId;
    }
    //弹出对话窗口
    $dictionary_dialog = $('<div/>').dialog({
        title: '字典详细信息',
        top: 20,
        width: 500,
        height: 360,
        modal: true,
        maximizable: true,
        href: inputUrl,
        buttons: [{
            text: '保存',
            iconCls: 'easyui-icon-save',
            handler: function () {
                $dictionary_form.submit();
            }
        }, {
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function () {
                $dictionary_dialog.dialog('destroy');
            }
        }],
        onClose: function () {
            $dictionary_dialog.dialog('destroy');
        },
        onLoad: function () {
            dictionaryFormInit();
        }
    });

}


/**
 * 设置字典项排序默认值
 * @param target
 */
function setDictionaryItemSortValue(target) {
    $.get(ctxAdmin + '/sys/dictionaryItem/maxSort', function (data) {
        if (data.code == 1) {
            $(target).numberbox({value: data.obj + 30});
            $(target).numberbox('validate');
        }
    }, 'json');
}


/**
 * 删除数据字典
 * @param dictionaryId
 * @param dictionaryName
 */
function delDictionary(dictionaryId, dictionaryName) {
    $.messager.confirm('确认提示！', '您确定要删除[' + dictionaryName + ']？', function (r) {
        if (r) {
            var ids = new Array();
            ids.push(dictionaryId);
            $.ajax({
                url: ctxAdmin + '/sys/dictionary/remove',
                type: 'post',
                data: {ids: ids},
                dataType: 'json',
                traditional: true,
                success: function (data) {
                    if (data.code == 1) {
                        $dictionaryItem_datagrid.datagrid("reload");
                        $dictionary_tree.tree('reload');
                        eu.showMsg(data.msg);//操作结果提示
                    } else {
                        eu.showAlertMsg(data.msg, 'error');
                    }
                }
            });
        }
    });
}

/**
 * 删除字典项
 * @param dictionaryItemId
 * @param dictionaryItemName
 */
function delDictionaryItem(dictionaryItemId, dictionaryItemName) {
    var rows = $dictionaryItem_datagrid.datagrid('getChecked');
    if (dictionaryItemId != undefined || rows.length > 0) {
        var tipMsg = "您确定要删除";
        if (dictionaryItemName != undefined) {
            tipMsg += "字典项[" + dictionaryItemName + "]?";
        } else {
            tipMsg += "选中的所有字典项?";
        }

        $.messager.confirm('确认提示！', tipMsg, function (r) {
            if (r) {
                var selectedIds = new Array();
                $.messager.progress({
                    title: '提示信息！',
                    text: '数据处理中，请稍后....'
                });
                if (dictionaryItemId != undefined) {
                    selectedIds.push(dictionaryItemId);
                } else {
                    $.each(rows, function (i, row) {
                        selectedIds.push(row.id);
                    });
                }

                $.ajax({
                    url: ctxAdmin + '/sys/dictionaryItem/remove',
                    type: 'post',
                    data: {ids: selectedIds},
                    dataType: 'json',
                    traditional: true,
                    success: function (data) {
                        $.messager.progress('close');
                        if (data.code == 1) {
                            $dictionaryItem_datagrid.datagrid('clearSelections');
                            $dictionaryItem_datagrid.datagrid('load');
                            eu.showMsg(data.msg);//操作结果提示
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

/**
 * 搜索
 */
function search() {
    var node = $dictionary_tree.tree('getSelected');
    var dictionaryId = '';
    if (node != null) {
        dictionaryId = node['id'];
    }
    var queryData = {};
    var dictionaryData = {'dictionary.id': dictionaryId};
    //var formData = $.serializeObject($dictionaryItem_search_form);
    var formData = {name: $("#name_OR_code").val(), code: $("#name_OR_code").val()};
    queryData = $.extend(queryData, dictionaryData, formData);//合并对象


    $dictionaryItem_datagrid.datagrid('load', queryData);
}