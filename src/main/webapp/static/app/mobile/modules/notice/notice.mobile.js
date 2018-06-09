var page = 1;
var pageSize = 10;
$(function(){
    init(page);
    page++;
    try {
        WebViewPlugin.showMenu('刷新');//Android环境 调用接口
    } catch (e) {
        console.log(e);
    }

    $("#markReadAll").click(function(){
        $.ajax({
            url: appURL + '/a/notice/markReadedAll',
            type: 'post',
            dataType: 'json',
            cache:false,
            success: function (data) {
                if (data.code == 1) {
                    //window.location.reload();
                    page = 1;
                    init(page);
                }
            }
        });
    });

})
//Adnroid onReatart执行
function refresh(){
    //page = 1;
    //init(page);
}
function viewNotice(id) {
    var url = ctxAdmin + '/notice/view/' + id;
    openURL(url);
}


function getMore(){
    init(page);
    page++;
}

function init(page){
    $.ajax({
        url: ctxMobile + '/notice/noticePage',
        type: "post",
        data: {page:page,rows:pageSize},
        dataType: "json",
        cache:false,
        beforeSend:function(){
            $(".getMore").find(".hidden").show();
            $(".getMore").find(".text").html("加载中...");
        },
        success: function(data) {
            if(page == 1){
                $(".list").html('');
            }
            var addHtml = "";
            $.each(data.rows, function (k, noticeReceiveInfo) {
                addHtml += "<li>";
                if (noticeReceiveInfo['isTop'] == 1){
                    addHtml += "<span>[顶]</span>";
                }
                addHtml += "<a href='#' data-ignore='true'  id='notice_"
                    + noticeReceiveInfo['id'] + "' title='" + noticeReceiveInfo['title'] + "' onclick=\"viewNotice(\'" + noticeReceiveInfo['noticeId'] + "\')\" ";
                if (noticeReceiveInfo['isRead'] == 0){//未读
                    addHtml += "style='color:#ef3a07;'";
                }
                addHtml += ">" + noticeReceiveInfo['title'];
                if (noticeReceiveInfo['typeView'] && noticeReceiveInfo['typeView'].length != 0){
                    addHtml += "[" + noticeReceiveInfo['typeView'] + "]";
                }

                addHtml += "<br/>" + noticeReceiveInfo['publishTime'].substr(5, 11) + "</a></li>";
            });
            $(".list").append(addHtml);


            var operateHtml = "";
            if(data.total != 0){
                var more = false;
                if(data.total>1 && getTotalPages(data['total'],pageSize) > page){
                    more = true;
                }
                if(more){
                    operateHtml+="<div class='getMore' onclick='getMore()'><span class='hidden'/><span class='text'>点击加载更多</span></div>";
                }else{
                    operateHtml+="<div class='getMore'><span class='text'>已显示全部内容</span></div>";
                }
                $(".getMore").remove();
                $(".list").after(operateHtml);
            }

            //返回顶部
            if(page >=2){
                $(".goTotop").remove();
                var goTotopHtml ="<div class='goTotop'></div>";
                $(".list").after(goTotopHtml);
                $(".goTotop").show();
            }

            $(".goTotop").click(function(){
                $(".panel").scrollTop(0);
                $(".goTotop").hide();
            });

        }
    });
}