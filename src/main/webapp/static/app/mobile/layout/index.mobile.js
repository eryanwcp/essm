$(function(){
    //$(".af-badge").hide();
    var screenwidth = $(window).width();
    $("#div1").find("a").width(screenwidth - 30);
    message();
    var interval = window.setInterval("message()",5 * 60 * 1000);
});
function notice(){
    $.ajax({
        url: appURL + '/a/notice/myUnreadNotice',
        type: "post",
        data: {},
        dataType: "json",
        async:false,
        success: function(data) {
            var html = "";
            data['obj'] ={noticeId:1,title:'title'};
            if(data['code'] == 1 && data['obj'] != undefined){
                $.each(data['obj'], function (i, noticeReceiveInfo) {
                    html +="<a onclick='openURL(\""+appURL + "/a/notice/view/"+noticeReceiveInfo['noticeId']+"\")' data-ignore='true'>"+noticeReceiveInfo['title']+"</a>"
                });
            }
            if(html != ""){
                $("#div1").append(html);
            }else{
                $(".shell").hide();
                $("#shell_hr").hide();
            }
        }
    });
}

var message = function(){
    $(".af-badge").hide();
    $.ajax({
        url: appURL + '/a/portal/mymessages',
        type: 'get',
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                var obj = data.obj;
                if(obj["noticeReceiveInfos"] >0){
                    $("#notice_badge").show().html(obj["noticeReceiveInfos"]);
                }

                if(obj["inboxs"] >0){
                    $("#email_badge").show().html(obj["inboxs"]);
                }
            }
        }
    });

}
