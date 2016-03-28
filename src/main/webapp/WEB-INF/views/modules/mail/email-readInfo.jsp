<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    $(function() {
         $('#readInfo_datagrid').datagrid({
            url : '${ctxAdmin}/mail/email/readInfoDatagrid/${emailId}',
            fit : true,
            pagination : true,
            fitColumns : false,
            striped : true,
            pageSize : 20,
            pageList:[10,20,50,100,1000,99999],
            singleSelect : false,
            checkbox : true,
            nowrap : false,
            border : false,
            remoteSort : false,
            sortName : 'id',
            sortOrder : 'desc',
            idField : 'id',
            columns : [ [
                {field:'id',title:'主键',hidden:true,sortable:true,align:'right',width:80},
                {field : 'userName',title : '收件人',width : 150},
                {field : 'isReadView',title : '阅读状态',width : 120},
                {field : 'readTime',title : '阅读时间',width : 150,sortable : true}
            ]]
        }).datagrid('showTooltip');
    });

</script>
<table id="readInfo_datagrid"></table>
           
       

