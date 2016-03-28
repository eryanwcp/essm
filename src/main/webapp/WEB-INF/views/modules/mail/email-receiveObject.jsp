<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<div>
  <c:choose>
    <%--用户--%>
    <c:when test="${receiveObjectType eq 0}">
      <a href="#" class="easyui-linkbutton" onclick="parent.edit('QuickWriteMail',null,'${receiveObjectType}','${receiveObjectId}')"
         data-options="iconCls:'eu-icon-mail_email_edit'">写邮件</a>
      <br>
      用户：${user.name}<br>
      部门：${user.organNames}<br>
    </c:when>
    <%--用户组--%>
    <c:when test="${receiveObjectType eq 1}">
      <a href="#" class="easyui-linkbutton" onclick="parent.edit('QuickWriteMail',null,'${receiveObjectType}','${receiveObjectId}')"
         data-options="iconCls:'eu-icon-mail_email_edit'">写邮件</a>
      <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:''">导入用户组</a>--%>
      <br>
      <c:forEach items="${fnc:findContactGroupUsers(receiveObjectId)}" var="groupUser">
        ${groupUser.name}：${groupUser.organNames}<br>
      </c:forEach>

    </c:when>
    <%--部门--%>
    <c:when test="${receiveObjectType eq 2}">
      <a href="#" class="easyui-linkbutton" onclick="parent.edit('QuickWriteMail',null,'${receiveObjectType}','${receiveObjectId}')"
         data-options="iconCls:'eu-icon-mail_email_edit'">写邮件</a>
      <br>
      <c:forEach items="${fnc:getOrgan(receiveObjectId)}" var="organ">
        ${organ.name}<br>
      </c:forEach>

    </c:when>
    <%--联系人--%>
    <c:when test="${receiveObjectType eq 3}">
      <a href="#" class="easyui-linkbutton" onclick="parent.edit('QuickWriteMail',null,'${receiveObjectType}','${receiveObjectId}')"
         data-options="iconCls:'eu-icon-mail_email_edit'">写邮件</a>
      <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:''">添加到地址簿</a>--%>
      <br>
      ${mailContact.name}：${mailContact.email}<br>
    </c:when>
    <%--联系人组--%>
    <c:when test="${receiveObjectType eq 4}">
      <a href="#" class="easyui-linkbutton" onclick="parent.edit('QuickWriteMail',null,'${receiveObjectType}','${receiveObjectId}')"
         data-options="iconCls:'eu-icon-mail_email_edit'">写邮件</a>
      <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:''">导入联系人组</a>--%>
      <br>
      <c:forEach items="${fnc:findContactGroupMailContacts(receiveObjectId)}" var="mailContact">
        ${mailContact.name}：${mailContact.email}<br>
      </c:forEach>

    </c:when>
  </c:choose>
</div>