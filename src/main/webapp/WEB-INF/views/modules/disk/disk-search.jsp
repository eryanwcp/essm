<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/kendoui.jsp"%>
<script src="${ctxStatic}/js/easyui-${ev}/datagridview/datagrid-bufferview.min.js?_=${sysInitTime}"></script>
<script type="text/javascript">
var file_search_form;
var file_search_datagrid;
var multiSelectUser;
var _dialog;
var isAdmin = undefined; // 是否是管理员权限
$(function() {
    isAdmin = ${not empty isAdmin ? isAdmin: false};
    initSelectUser();
    loadSearchDatagrid();
    loadFolderAuthorize();
    loadFileSize();
    file_search_form = $("#file_search_form").form();
});

function loadSearchDatagrid() {
    var _toolbar = null;
    if(isAdmin) {
        _toolbar  = [{
            text: '批量下载',
            iconCls: 'eu-icon-disk_download',
            handler: function() {
                downloadFile();
            }
        }, {
            text: '删除',
            iconCls: 'easyui-icon-remove',
            handler: function() {
                delFile();
            }
        },{
            text: '级联删除',
            iconCls: 'eu-icon-cancel',
            handler: function() {
                cascadeDelFile();
            }
        }];
    }

    //数据列表
    file_search_datagrid = $('#file_search_datagrid').datagrid({
        url: '${ctxAdmin}/disk/fileSearchDatagrid',
        checkOnSelect: false,
        selectOnCheck: false,
        fit: true,
        fitColumns: false,
        //自适应列宽
        striped: true,
        //显示条纹
        remoteSort: false,
        //是否通过远程服务器对数据排序
        idField: 'id',
        frozen: true,
        //底部分页       
        pagination: true,
        //显示行数
        rownumbers: true,
        toolbar: _toolbar,
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
                width: 260,
                formatter: function(value, rowData, rowIndex) {
                    return "<a onclick='downloadFile(\"" + rowData.id + "\")'>" + value + "</a>";
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
                field: 'ownerName',
                title: '拥有者',
                width: 100
            },
            {
                field: 'createTime',
                title: '创建时间',
                sortable: true,
                width: 200
            },
            {
                field: 'location',
                title: '位置',
                width: 300
            },
            {
                field: 'operate_all',
                title: '操作',
                formatter: function(value, rowData, rowIndex) {
                    var operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"eu-icon-disk_download\"' onclick='downloadFile(\"" + rowData.id + "\")'>下载</a>";
                    if (isAdmin) {
                        operateHtml += "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"easyui-icon-remove\"' onclick='delFile(\"" + rowData.id  + "\")'>删除</a>";
                        operateHtml += "&nbsp;<a class='easyui-linkbutton' data-options='iconCls:\"eu-icon-cancel\"' onclick='cascadeDelFile(\"" + rowData.code + "\")'>级联删除</a>";
                    }
                    return operateHtml;
                }
            }]],
        onLoadSuccess: function() {
            $(this).datagrid('clearSelections'); //取消所有的已选择项
            $(this).datagrid('clearChecked'); //取消所有的选中的择项
            $(this).datagrid('unselectAll'); //取消全选按钮为全选状态
        },
        onHeaderContextMenu:function(e,field){
            e.preventDefault();
        }
    }).datagrid('showTooltip');
}

//下载文件
function downloadFile(pageId) {
    var ids = new Array();
    if (pageId) {
        ids.push(pageId);
    } else {
        var rows = file_search_datagrid.datagrid('getChecked');
        if (rows && true) {
            $.each(rows,
                    function(i, row) {
                        ids.push(row.id);
                    });
        }
    }

    if(ids.length > 0){
        var url  = "${ctxAdmin}/disk/downloadDiskFile?fileIds=" + ids.join(",");
        $("#annexFrame").attr("src", url);
    } else {
        eu.showMsg("请选择要操作的对象！");
        return;
    }
}

/**
 * 删除文件夹下的文件
 * @param fileId fileName
 */
function delFile(fileId, fileName) {
    var ids = new Array();
    var tipMsg = "您确定要删除选中文件吗?";

    if (fileId != undefined && fileId != null) {
        ids.push(fileId);
    } else {
        var rows = file_search_datagrid.datagrid('getChecked');
        if (rows && true) {
            $.each(rows,
                    function(i, row) {
                        ids.push(row.id);
                    });
        }
    }

    if (ids.length > 0) {
        $.messager.confirm('确认提示！', tipMsg,
                function(r) {
                    if (r) {
                        $.messager.progress({
                            title: '提示信息！',
                            text: '数据处理中，请稍后....'
                        });
                        $.ajax({
                            url: '${ctxAdmin}/disk/delFolderFile',
                            type: 'post',
                            data: {
                                fileIds: ids
                            },
                            dataType: 'json',
                            traditional: true,
                            success: function(data) {
                                $.messager.progress('close');
                                if (data.code == 1) {
                                    file_search_datagrid.datagrid("reload");
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

/**
 * 删除文件夹下的文件
 * @param fileCode
 */
function cascadeDelFile(fileCode) {
    var ids = new Array();
    var tipMsg = "引用该选中文件的所有数据将<font color=#D94600>级联删除</font>,您确定要删除吗?";

    if (fileCode != undefined) {
        ids.push(fileCode);
    } else {
        var rows = file_search_datagrid.datagrid('getChecked');
        if (rows && true) {
            $.each(rows,
                    function(i, row) {
                        ids.push(row.code);
                    });
        } else {
            eu.showMsg("请选择要操作的对象！");
            return;
        }
    }

    if (ids.length > 0) {
        $.messager.confirm('确认提示！', tipMsg,
                function(r) {
                    if (r) {
                        $.messager.progress({
                            title: '提示信息！',
                            text: '数据处理中，请稍后....'
                        });
                        $.ajax({
                            url: '${ctxAdmin}/disk/cascadeDelFile',
                            type: 'post',
                            data: {
                                fileCodes: ids
                            },
                            dataType: 'json',
                            traditional: true,
                            success: function(data) {
                                $.messager.progress('close');
                                if (data.code == 1) {
                                    file_search_datagrid.datagrid("reload");
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
    file_search_datagrid.datagrid('load', $.serializeObject($("#file_search_form").form()));
}

function initSelectUser() {
    $.ajax({
        type: "post",
        dataType: 'json',
        contentType: "application/json",
        url: "${ctxAdmin}/sys/user/userList?dataScope=2",
        success: function(data) {
            multiSelectUser = $("#personIds").kendoMultiSelect({
                dataTextField: "name",
                dataValueField: "id",
                dataSource: data,
                dataBound: function(e) {}
            }).data("kendoMultiSelect");
        }
    });

}
function selectUser() {
    var personIds = "";
    var dataItems = multiSelectUser.dataItems();
    if (dataItems && dataItems.length > 0) {
        var num = dataItems.length;
        $.each(dataItems,
                function(n, value) {
                    if (n == num - 1) {
                        personIds += value.id;
                    } else {
                        personIds += value.id + ",";
                    }
                });
    }

    _dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: '${ctxAdmin}/sys/user/select?userIds=' + personIds + "&grade=0",
        width: '700',
        height: '450',
        maximizable: true,
        iconCls: 'easyui-icon-edit',
        modal: true,
        buttons: [{
            text: '确定',
            iconCls: 'easyui-icon-save',
            handler: function() {
                setSelectUser();
                _dialog.dialog('destroy');
            }
        },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function() {
                    _dialog.dialog('destroy');

                }
            }],
        onClose: function() {
            _dialog.dialog('destroy');
        }
    });
}

function setSelectUser() {
    var selectPersonIds = new Array();
    $("#selectUser option").each(function() {
        var txt = $(this).val();
        selectPersonIds.push($.trim(txt));
    });
    multiSelectUser.value(selectPersonIds);
}

function loadFolderAuthorize() {
    $("#folderAuthorize").combobox({
        url: '${ctxAdmin}/disk/folderAuthorizeCombobox?selectType=all&requestType=search',
        editable: false,
        panelHeight: 'auto'
    });
}

function loadFileSize() {
    $("#sizeType").combobox({
        url: '${ctxAdmin}/disk/fileSizeTypeCombobox?selectType=all',
        editable: false,
        panelHeight: 'auto'
    });
}
</script>

<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <table id="file_search_datagrid" > </table>
    </div>

    <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
         style="width: 100%;height:106px; ">
        <form id="file_search_form" style="padding: 5px;">
            <table style="border: 0">
                <tr>
                    <td>文件名称:<input type="text"  name="fileName" placeholder="文件名..."  class="easyui-validatebox textbox eu-input"
                                    maxLength="25" style="width: 160px"/>
                    </td>
                    <td>云盘类型:<input  id="folderAuthorize"  name="folderAuthorize"  style="width: 160px;height: 28px;"/></td>
                    <td>
                        创建时间:<input  id="startTime" name="startTime" class="easyui-my97"  data-options="dateFmt:'yyyy-MM-dd 00:00:00'" style="width: 120px"/>
                        ~
                        <input id="endTime" name="endTime" class="easyui-my97"  data-options="dateFmt:'yyyy-MM-dd 23:59:59'" style="width: 120px"/>
                    </td>
                    <td> <iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 300px;height: 26px;"></iframe>
                    </td>
                </tr>
                <tr >
                    <td colspan="2">
                        <table>
                            <td style="white-space:nowrap;">拥有者:</td>
                            <td ><select id="personIds" name="personIds" style="width: 250px;"></select></td>
                            <td ><a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width: 100px;" onclick="selectUser();">选择</a></td>
                        </table>
                    </td>
                    <td>文件大小:<input  id="sizeType" name="sizeType" style="width: 160px;height: 28px;"/>
                        <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
                        <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-no'" onclick="javascript:file_search_form.form('reset');">重置查询</a>
                    </td>

                </tr>
            </table>
        </form>
    </div>

</div>