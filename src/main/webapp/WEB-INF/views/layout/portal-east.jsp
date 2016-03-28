<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
$(function() {
	$('#layout_east_calendar').calendar({
		fit : true,
		current : new Date(),
		border : false,
		onSelect : function(date) {
			$(this).calendar('moveTo', new Date());
		}
	});
	
	$('#layout_east_onlineDatagrid').datagrid({
		url : '${ctxAdmin}/login/onlineDatagrid',
		fit : true,
		fitColumns : true,
		nowarp : false,
		border : false,
        view:bufferview,
        autoRowHeight:false,
		idField : 'loginName',
		columns : [ [ {
			title : '姓名',
			field : 'name',
			width : 60,
			formatter : function(value, rowData, rowIndex) {
				var title = $.formatString('登录名:{0},登录IP:{1},登录时间:{2}.', rowData.loginName, rowData.ip,rowData.loginTime);
				return $.formatString('<span title="{0}" class="easyui-tooltip">{1}</span>', title, value);
			}
		},{
			title : '登录时间',
			field : 'loginTime',
			width : 100,
			formatter:function(value, rowData, rowIndex){
				return value;
			}
		}
		] ],
		onClickRow : function(rowIndex, rowData) {
		},
		onLoadSuccess : function(data) {
			$('#layout_east_onlinePanel').panel('setTitle', '( ' + data.total + ' )人在线');
			$(this).datagrid('getPager').pagination({
				showPageList : false,
				showRefresh : false,
				beforePageText : '',
				afterPageText : '{pages}',
				displayMsg : ''
			});
			//鼠标移动提示列表信息tooltip
			$(this).datagrid('showTooltip',{fields:['loginTime']});
			//用于解析列表tooltip提示样式
			$.parser.parse(); 
		}
	});
	
	$('#layout_east_onlinePanel').panel({
		tools : [ {
			iconCls : 'easyui-icon-reload',
			handler : function() {
				$('#layout_east_onlineDatagrid').datagrid('load');
			}
		} ]
	});

	/* window.setInterval(function() {
		$('#layout_east_onlineDatagrid').datagrid('load', {});
	}, 1000 * 60 * 10); */
});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height:180px;overflow: hidden;">
		<div id="layout_east_calendar"></div>
	</div>
	<div data-options="region:'center',border:false" style="overflow-x: hidden;">
		<div id="layout_east_onlinePanel" data-options="fit:true,border:false" title="用户在线列表">
			<table id="layout_east_onlineDatagrid"></table>
		</div>
	</div>
</div>