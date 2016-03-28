<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var multiSelectUser;
    var multiSelectOrgan;
    var _dialog;
    $(function () {
        initSelectUser();
        loadOrgan();
    });

   function initSelectUser() {
       $.ajax({
           type: "post",
           dataType: 'json',
           contentType: "application/json",
           url: "${ctxAdmin}/sys/user/userList",
           success: function (data) {
               multiSelectUser = $("#personIds").kendoMultiSelect({
                   dataTextField: "name",
                   dataValueField: "id",
                   dataSource: data,
                   dataBound: function (e) {

                   }

               }).data("kendoMultiSelect");
           }
       });

   }
   function selectUser() {
       var personIds = "";
       var dataItems = multiSelectUser.dataItems();
       if (dataItems && dataItems.length > 0) {
           var num = dataItems.length;
           $.each(dataItems, function (n, value) {
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
           href: '${ctxAdmin}/sys/user/select?userIds=' + personIds+"&grade=0",
           width: '700',
           height: '450',
           maximizable: true,
           iconCls: 'easyui-icon-edit',
           modal: true,
           buttons: [
               {
                   text: '确定',
                   iconCls: 'easyui-icon-save',
                   handler: function () {
                       setSelectUser();
                       _dialog.dialog('destroy');
                   }
               },
               {
                   text: '关闭',
                   iconCls: 'icon-cancel',
                   handler: function () {
                       _dialog.dialog('destroy');

                   }
               }
           ],
           onClose: function () {
               _dialog.dialog('destroy');
           }
       }).dialog('open');
   }

   function setSelectUser() {
       var selectPersonIds = new Array();
       $("#selectUser option").each(function () {
           var txt = $(this).val();
           selectPersonIds.push($.trim(txt));
       });
       multiSelectUser.value(selectPersonIds);
   }
   
   
   //分享方式的转变
   function changeType(value){
	  if("PERSON" == value){
		  $("#personTable").show();
		  $("#organDiv").hide();
		  $("#organIds").val("");
	  } else if("ORGAN" == value){
		  $("#personTable").hide();
		  $("#organDiv").show();
		  $("#personIds").val("");
	  } else {
		  $("#personTable").hide();
		  $("#organDiv").hide();
		  $("#personIds").val("");
		  $("#organIds").val("");
	  }
   }
   
   function loadOrgan() {
	   multiSelectOrgan = $("#organIds").combotree({
           url: '${ctxAdmin}/sys/organ/tree?dataScope=1&cascade=true',
           multiple: true,//是否可多选
           editable: false
       });
   }
</script>
<div>
    <form id="share_file_form" class="dialog-form"  method="post" style="padding-top: 20px;">
        <input type="hidden"  name="fileId" value= "${fileId}"/>
        <table style="border: 0px;" >
            <tr>   
                <c:if test="${not empty fileShareType}">
                   <c:forEach items="${fileShareType}" var="shareType" varStatus="status">
                       <td>
                           <label>
                         	<input type="radio" name = "shareType" value="${shareType}" onclick="changeType(this.value);" <c:if test="${status.index == '0' }"> checked = "true"</c:if> > 
                         	<c:if test="${shareType == 'PERSON' }">分享至个人</c:if>
                         	<c:if test="${shareType == 'ORGAN'  }">分享至部门</c:if>
                         	<c:if test="${shareType == 'PUBLIC' }">分享至公共 </c:if>
                           </label>
                        </td>
                    </c:forEach>
                 </c:if>
           </tr>
        </table>
        <table id="personTable" style="border: 0px;width: 100%;">
              <tr>
                <td style="display: inline-block; width:80px; vertical-align: middle;text-align:center;">分享用户:</td>
                <td >
                    <select id="personIds"  name="personIds" multiple="true"
                            style="width:70%; float:left;margin-left:1px;margin-right:2px;"> </select>
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width:60px;height:29px;display:inline-block;vertical-align:middle;" onclick="selectUser();">选择</a>
                </td>
              </tr>
        </table>
          <div id="organDiv" style="display:none;margin-top:10px;">
            <label style="text-align:center;width:82px;vertical-align: middle;display:inline-block;">分享部门:</label>
            <input id="organIds"  name="organIds" style="width: 260px;height: 28px;" data-options="missingMessage:'请选择部门.'"/>
        </div>
    </form>
</div>