<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
var disk_notice_datagrid;
$(function() {
    loadSearchDatagrid();
});

function loadSearchDatagrid() {
    //数据列表
    disk_notice_datagrid = $('#disk_notice_datagrid').datagrid({
        url: '${ctxAdmin}/disk/diskNoticeDatagrid',
        checkOnSelect: false,
        selectOnCheck: false,
        fit: true,
        fitColumns: true,
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
        pageSize: 20,
        pageList: [10, 20, 50, 500, 9999],
        frozenColumns: [[
        {
            field: 'fileName',
            title: '最新动态',
            sortable: true,
            formatter: function(value, rowData, rowIndex) {
            	return rowData.createTime + "," + rowData.operateUserName + rowData.operateDesc + "了文件:" +  "<font color='#285e8e' >" +  rowData.fileName + "</font>";
            }
           
        }]],
        onLoadSuccess: function() {
            $(this).datagrid('clearSelections'); //取消所有的已选择项
            $(this).datagrid('clearChecked'); //取消所有的选中的择项
            $(this).datagrid('unselectAll'); //取消全选按钮为全选状态
        }
    }).datagrid('showTooltip');
}


</script>

<div class="easyui-layout" fit="true" style="margin: 0px;border: 0px;overflow: hidden;width:100%;height:100%;">
            <div data-options="region:'center',split:true" style="overflow: hidden;">
                <table id="disk_notice_datagrid" > </table>
            </div>
</div>