<%@ page import="com.eryansky.modules.notice.utils.NoticeUtils" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<script type="text/javascript">
    var modelIsTop = '${model.isTop}';
    var modelType = '${model.type}';
    var dictionaryTypeCode = '<%=NoticeUtils.DIC_NOTICE%>';
    var modelFileIds = ${fns:toJson(model.fileIds)};
    var noticeReceiveUserIds =  ${fns:toJson(model.noticeReceiveUserIds)};
    var noticeReceiveOrganIds = ${fns:toJson(model.noticeReceiveOrganIds)};
    var jsessionid = '<%=session.getId()%>';
    var fileSizeLimit = '<%=AppConstants.getPrettyNoticeMaxUploadSize()%>';//附件上传大小限制
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/notice/notice-input${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="notice_form" class="dialog-form"  method="post" novalidate>
        <input type="hidden" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>

        <div>
            <label>通知标题：</label>
            <input name="title" type="text" class="easyui-validatebox textbox" style="width: 360px;" value="${model.title}"
                   maxLength="128" data-options="required:true,missingMessage:'请输入标题.',validType:['minLength[1]']">
            <e:dictionary id="notice_type" name="type" code="<%=NoticeUtils.DIC_NOTICE%>" type="combobox" value="${model.type}" editable="false" selectType="select" height="28"></e:dictionary>
            <%--<input id="notice_type" name="type" style="height: 28px;" />--%>
        </div>
        <div>
            <label>接收范围：</label>
            <input type="select" id="receiveScope" name="receiveScope" value="${model.receiveScope}"
                   style="width: 200px;height: 28px;"/>
        </div>
        <div id="noticeUserIds_div">
            <label style="display:block;float:left;">接收人员：</label>
            <select id="noticeUserIds" name="_noticeUserIds" multiple="true"
                    style="width:70%; float:left;margin-left:1px;margin-right:2px;"> </select>
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'eu-icon-user'" style="width: 100px;"
               onclick="_selectUser();">选择</a>
        </div>
        <div  id="noticeOrganIds_div" style="clear:both">
            <label>接收部门：</label>
            <input type="select" id="_noticeOrganIds" name="_noticeOrganIds" style="width: 260px;height: 28px;"
                   data-options="missingMessage:'请选择部门.'"/>
        </div>

        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="width: 96px; vertical-align: top;">通知内容：</td>
                <td><textarea id="editor" name="content">${model.content}</textarea></td>
            </tr>
        </table>
        <div>
            <label>有效期：</label>
            <input id="_effectTime" name="effectTime" placeholder="开始时间" type="text"
                   class="easyui-my97" data-options="dateFmt:'yyyy-MM-dd HH:mm',required:true,missingMessage:'请设置开始时间.'" value="<fmt:formatDate value="${model.effectTime}" pattern="yyyy-MM-dd HH:mm"/>" >&nbsp;
            ~&nbsp;<input id="_endTime" name="invalidTime" placeholder="截止时间" type="text" class="easyui-my97"
                          data-options="dateFmt:'yyyy-MM-dd HH:mm'" value="<fmt:formatDate value="${model.invalidTime}" pattern="yyyy-MM-dd HH:mm"/>"/>
            <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                  title="截止时间不设置，则一直有效。" ></span>
        </div>
        <div>
            <label>置顶：</label>
            <label style="text-align: left;width: 80px;">
                <input type="radio" name="isTop" style="width: 20px;" value="0" /> 不置顶
            </label>
            <label style="text-align: left;width: 80px;">
                <input type="radio" name="isTop" style="width: 20px;" value="1" /> 置顶
            </label>
            <span id="endTopDay_span">
                <input id="endTopDay" name="endTopDay" value="${model.endTopDay}" style="width:80px;height: 28px;"
                       class="easyui-numberspinner" data-options="min:0,max:999" >
                &nbsp;天
                <span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"
                      title="设置为0，则永久置顶。" ></span>
            </span>
        </div>
        <table style="border: 0px;width: 100%;">
            <tr>
                <td style="display: inline-block; width: 96px; vertical-align: top;">附件：</td>
                <td><input id="uploadify" name="file" type="file" multiple="true">

                    <div id="queue"></div>
                    <input id="_fileIds" name="_fileIds" type="hidden" />
                    <c:if test="${not empty files}">
                        <div>
                            <c:forEach items="${files}" begin="0" var="file" varStatus="i">
                                <div id='${file.id}' style="font-size: 14px;">${i.index +1}、<a href="#" onclick="loadOrOpen('${file.id}');" style="color: #0000ff;">${file.name}</a>&nbsp;&nbsp;
                                    <a href="#" onclick="delUpload('${file.id}');" style="color: red;">删除</a></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>

    </form>
    <iframe id="annexFrame" style="display:none" src=""></iframe>
</div>
