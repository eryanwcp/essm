<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var noticeId = '${noticeId}';
    $(function() {
        $('#notice_readInfo_datagrid').datagrid({
                    url : ctxAdmin + '/notice/readInfoDatagrid/'+noticeId,
                    fit:true,
                    pagination:true,//底部分页
                    rownumbers:true,//显示行数
                    fitColumns:false,//自适应列宽
                    striped:true,//显示条纹
                    nowrap : true,
                    pageSize:20,//每页记录数
                    idField : 'id',
                    columns : [ [
                        {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80},
                        {field : 'userName', title : '姓名', width : 80},
                        {field : 'organName',title : '部门',width : 260,sortable:true},
                        {field : 'isReadView',title : '状态',width : 60,sortable:true}
                    ]]
                }).datagrid('showTooltip');
    });

</script>
<table id="notice_readInfo_datagrid"></table>
