<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%--共用页面文件--%>
<div>
    <ul style="padding: 10px;" id="organUserTree"></ul>
</div>
<script type="text/javascript">
    var $organUserTree = $("#organUserTree").tree({
        url:'${ctxAdmin}/sys/user/organUserTree?cascade=${cascade}',
        <%--data:${organUserTreeData},--%>
        lines: true,
        onlyLeafCheck:false,
        checkbox: ${not empty checkbox ? checkbox:true},
        onDblClick: function(node) {
            try {
                setOrganUserResult(node);//由父窗体实现
            } catch (e) {
            }

        },
        onBeforeLoad:function(node, param){
            if(node){
                param.parentId = node['id'];
            }
        },
        onLoadSuccess: function (node, data) {
            $(this).tree("collapseAll");
            var rootNode = $(this).tree("getRoot");
            if(rootNode){//展开第一级
                $(this).tree("expand",rootNode.target);
            }
        }
    });
</script>