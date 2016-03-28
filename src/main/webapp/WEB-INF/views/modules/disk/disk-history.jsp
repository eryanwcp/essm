<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
var $file_search_form;
var $file_history_datagrid;
$(function() {
    $file_search_form = $("#file_search_form").form();
    $file_history_datagrid = $('#file_history_datagrid').datagrid({
        url: '${ctxAdmin}/disk/fileHistoryDatagrid',
        checkOnSelect: false,
        selectOnCheck: false,
        fit: true,
        fitColumns: false,
        striped: true,
        remoteSort: false,
        idField: 'id',
        frozen: true,
        pagination: true,
        rownumbers: true,
        pageSize: 20,
        pageList: [10, 20, 50, 500, 9999],
        frozenColumns: [[{
            field: 'ck',
            checkbox: true
        },
        {
            field: 'fileName',
            title: '文件名',
            sortable: true,
            width: 260
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
            field: 'fileUserName',
            title: '拥有者',
            hidden: true,
            width: 100
        },{
            field: 'operateTypeDesc',
            title: '最后操作',
            width: 200
        },
        {
            field: 'operateTime',
            title: '操作时间',
            sortable: true,
            width: 200
        },
        {
            field: 'isActive',
            title: '操作',
            formatter: function(value, rowData, rowIndex) {
            	var operateHtml = "";
            	if(true == value){
            		operateHtml = "<a class='easyui-linkbutton' data-options='iconCls:\"eu-icon-disk_download\"' onclick='downloadFile(\"" + rowData.fileId + "\")'>下载</a>";
            	}
            	return  operateHtml;
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
});



//下载文件
function downloadFile(pageId) {
   if(!pageId){
	   eu.showMsg("请选择操作的对象！");
   } else {
	    var url  = "${ctxAdmin}/disk/downloadDiskFile?fileId=" + pageId;
	   $("#annexFrame").attr("src", url);
   }
}


function search() {
	$file_history_datagrid.datagrid("load", $.serializeObject($file_search_form));
}

</script>

<%-- easyui-layout布局 --%>
<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
    <div data-options="region:'center',split:true" style="overflow: hidden;">
        <table id="file_history_datagrid" > </table>
    </div>

    <div data-options="region:'north',title:'过滤条件',split:false,collapsed:false,border:false"
         style="width: 100%;height:76px; ">
        <form id="file_search_form" style="padding: 5px;">
            &nbsp;&nbsp;文件名:<input type="text" id="fileName" name="fileName" placeholder="文件名..." class="easyui-validatebox textbox eu-input"
                                   onkeydown="if(event.keyCode==13)search()"  maxLength="25" style="width: 160px"/>
            <a class="easyui-linkbutton" href="#" data-options="iconCls:'easyui-icon-search',onClick:search">查询</a>
            <iframe id="annexFrame" src="" frameborder="no" style="padding: 0;border: 0;width: 300px;height: 26px;"></iframe>
        </form>
    </div>
</div>