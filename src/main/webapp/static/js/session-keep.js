var eu = eu || {};

eu.contextPath = window.document.location.pathname.substring(0, window.document.location.pathname.indexOf('\/', 1));
/**
 * 保持心跳
 */
window.setInterval(sessionInfo, 15 * 60 * 1000);
function sessionInfo() {
    $.ajax({
        type: "GET",
        url: eu.contextPath+"/a/login/sessionInfo",
        cache: false,
        dataType: "json",
        success: function (data) {

        }});
}