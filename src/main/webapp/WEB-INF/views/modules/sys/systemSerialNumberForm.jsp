<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>序列号信息</title>
    <meta name="decorator" content="default_sys"/>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs" style="margin-bottom: 0;">
    <li><a href="${ctxAdmin}/sys/systemSerialNumber/">序列号列表</a></li>
    <li class="active"><a href="${ctxAdmin}/sys/systemSerialNumber/form?id=${model.id}">序列号<e:hasPermission name="sys:formTemplate:edit">${not empty model.id?'修改':'添加'}</e:hasPermission><e:lacksPermission name="form:formTemplate:edit">查看</e:lacksPermission></a></li>
</ul><br>
<form:form id="inputForm" modelAttribute="model" action="${ctxAdmin}/sys/systemSerialNumber/save" method="post" class="form-horizontal">
    <form:hidden id="id" path="id"/>
    <tags:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">模块名称:</label>
        <div class="controls">
            <form:input path="moduleName" htmlEscape="false" maxlength="200" class="input-xxlarge required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">模块编码:</label>
        <div class="controls">
            <form:input path="moduleCode" htmlEscape="false" maxlength="128" class="input-xxlarge required"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">模板:</label>
        <div class="controls">
            <form:textarea id="configTemplate" htmlEscape="true" path="configTemplate" rows="4" maxlength="128" class="input-xxlarge required"/>
            <span class="help-inline">“#” 为子序列间的分隔符。在每个子序列规则定义的内部，用 “@” 分为两部分，前面一部分（Str/DateTime/NumSeq）表明该子序列的类型，后面一部分是用于生成子序列串所需的信息。
                <br>例如 “Str@ 国办发〔# DateTime@yyyy # Str@〕# NumSeq@0C0 # Str@”，“Str” 表示为固定字符串类型，固定字符串为 “国办发〔”；<br>
                类似的 “DateTime@yyyy”，“DateTime” 表示时间日期类型，格式为四位的年代号，根据当前时间生成，格式：“yyyyMMddHHmmss”；<br>
                “NumSeq@0C0”，“0C0”，“C”前部分为数值长度（默认为“0”，“0”表示长度为[当前最大值]的长度），“后半部分”表示补充字符（默认为：“0”）。
            </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">当前最大值:</label>
        <div class="controls">
            <form:input path="maxSerial" htmlEscape="false" maxlength="20" class="input-large digits"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">缓存大小:</label>
        <div class="controls">
            <form:input path="preMaxNum" htmlEscape="false" maxlength="11" class="input-large digits"/>
            <span class="help-inline">预生成流水号数量</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">重置类型:</label>
        <div class="controls">
            <form:select path="resetType">
                <form:option value="">请选择...</form:option>
                <form:options items="${resetTypes}" itemValue="value" itemLabel="description"></form:options>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备注:</label>
        <div class="controls">
            <form:textarea id="remark" htmlEscape="true" path="remark" rows="4" maxlength="200" class="input-xxlarge"/>
        </div>
    </div>

    <div class="form-actions" style="margin: 0;">
        <e:hasPermission name="sys:systemSerialNumber:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
        </e:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>