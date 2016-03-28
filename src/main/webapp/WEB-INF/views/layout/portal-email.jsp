<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    function viewEmail(id){
    	$("#email_icon_"+id).removeClass('tree-icon tree-file eu-icon-star_yellow').addClass("tree-icon tree-file eu-icon-star_gray")
        .attr("title","已读");
        $("#email_label_"+id).removeClass("tip_unread").html("[已读]");
    	eu.addTab(window.parent.layout_center_tabs, "我的邮箱",'${ctxAdmin}/mail/email?emailId=' + id, true,"eu-icon-email","",true);
    }
</script>
<div class="portal-div">
    <table class="table table-striped">
        <%--<thead>--%>
        <%--<tr>--%>
            <%--<th>名称</th>--%>
        <%--</tr>--%>
        <%--</thead>--%>
        <tbody>

        <c:forEach items="${inboxs}" begin="0" end="4" var="inbox">
            <c:set var="title" value="${inbox.title}"></c:set>
            <tr>
                <td>
                    <%--<span class="tree-icon tree-file icon_star_yellow"></span>--%><c:choose>
                    <c:when test="${inbox.isRead == 2}">
                            <span id="email_icon_${inbox.id}" title="已读"
                                  class="tree-icon tree-file eu-icon-star_gray"></span>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <c:set var="tip_unread" value="tip_unread"></c:set>
                            <span id="email_icon_${inbox.id}" title="未读"
                                  class="tree-icon tree-file eu-icon-star_yellow"></span>&nbsp;
                    </c:otherwise>
                    </c:choose>


                    <a href="#" id="email_${inbox.id}" class="easyui-tooltip portal-span" title="[${inbox.priorityView}]${title}" onclick="viewEmail('${inbox.emailId}')" data-options="position: 'right'">
                        <c:if test="${inbox.priority eq '3'}"><span style="color:#D94600;">[${inbox.priorityView}]</span></c:if>
                        <c:if test="${inbox.priority eq '1'}"><span style="color: #FE6600;">[${inbox.priorityView}]</span></c:if>
                        ${fns:abbr(title,45)} <c:choose>
                            <c:when test="${inbox.isRead == 2}">
                                <span id="email_label_${inbox.id}" >[已读]</span>
                            </c:when>
                            <c:otherwise>
                            <span id="email_label_${inbox.id}" class="tip_unread" >[未读]</span>
                            </c:otherwise>
                        </c:choose>
                    </a>
                </td>
                <td align="right" class="portal-time">
                	<fmt:formatDate value="${inbox.sendTime}" pattern="MM-dd HH:mm"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>