<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>数据字典使用demo</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
$(function() {
    $('#combobox').combobox({
        url:'${ctx}/sys/dictionary/combobox?dictionaryTypeCode=bug&selectType=all',
        multiple:false,//是否可多选
        //editable:false,//是否可编辑
        width:120
    });
    $('#combotree').combotree({
        url:'${ctx}/sys/dictionary/combotree?dictionaryTypeCode=bug',
        multiple:true,//是否可多选
        //editable:false,//是否可编辑
        //cascadeCheck:false,
        width:120,
        valueField:'id'
    });
    $('#combotree').combotree('setValues',['bug001']);

});
</script>
</head>
<body>
    bug类型combobox： <input id="combobox" name="combobox" /> <br>
    bug类型combotree： <input id="combotree" name="combotree" /> <br>

 标签方式：<e:dictionary  code="bug" type="combotree" selectType="select"></e:dictionary>
</body>
</html>
