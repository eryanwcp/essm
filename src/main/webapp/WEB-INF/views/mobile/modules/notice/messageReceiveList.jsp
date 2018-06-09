<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title>消息中心</title>
    <meta name="decorator" content="default_mobile"/>
    <style type="text/css">
        *{box-sizing: border-box;}
        .list{margin: 0;list-style: none;}
        .list>li>a{text-decoration: none;}
        .list>li{padding: 8px 12px;line-height: 30px; border-bottom: 1px solid #d2d2d2; border-top:1px solid #fff;}
        .list>li:nth-child(odd){background-color: #e6e6e6;}
        .list>li:nth-child(even){background-color: #f0f0f0;}
        .list>li.pressed{background-color: #d4d4d4;}
    </style>
    <script type="text/javascript">
        $(function(){
            loadData();
            $("#setReadAll").click(function(){
                $.ajax({
                    url: appURL + '/a/notice/messageReceive/setReadAll',
                    type: 'post',
                    dataType: 'json',
                    cache:false,
                    success: function (data) {
                        if (data.code == 1) {
                            $('#pageNo').val(1);
                            loadData();
                        }
                    }
                });
            });
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            loadData();
            return false;
        }
        function loadData() {
            var queryParam = $.serializeObject($("#searchForm"));
            $.ajax({
                url: ctxAdmin + "/notice/messageReceive?_=" + new Date().getTime(),
                type: "post",
                dataType: "json",
                data: queryParam,
                cache: false,
                beforeSend: function (jqXHR, settings) {
                    $("#list").html("<div style='padding: 10px 30px;text-align:center;font-size: 16px;'>数据加载中...</div>");
                },
                success: function (data) {
                    if (data['totalCount'] > 0) {
                        var html = Mustache.render($("#list_template").html(), data);
                        $("#list").html(html);
                        $(".pagination").append(data['appHtml']);

                    } else {
                        $("#list").html("<div style='color: red;padding: 10px 30px;text-align:center;font-size: 16px;'>暂无数据</div>");
                    }
                }
            });
        }
        /**
         * 设置已读 （异步）
         * @param id
         */
        function setRead(id){
            $.ajax({
                url: '${ctxAdmin}/notice/messageReceive/setRead?id='+id,
                type: 'get',
                dataType: 'json',
                success: function (data) {}
            });
        }
    </script>
    <script type="text/template" id="list_template">
        <ul id="contentTable" class="list">
            {{#result}}
                <li onclick="setRead('{{id}}');">{{message.sendTime}}[{{message.senderName}}]：{{message.content}}</li>
            {{/result}}
        </ul>
        <div class="page pagination"></div>
    </script>
</head>
<body style="border: 0;padding: 0;">
<form:form id="searchForm" modelAttribute="model" action="${ctxAdmin}/notice/messageReceive" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <form:radiobutton path="isRead" value="0" label="未阅" onclick="$('#pageNo').val(1);loadData();"/>
    <form:radiobutton path="isRead" value="1" label="已阅" onclick="$('#pageNo').val(1);loadData();"/>
    <c:if test="${model.isRead eq '0'}">
        <input id="setReadAll" class="btn btn-primary" type="button" value="全部标记为已读"/>
    </c:if>
</form:form>
<div id="list"></div>
</body>
</html>