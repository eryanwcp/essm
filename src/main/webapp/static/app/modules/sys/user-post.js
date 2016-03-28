var modelId= modelId;
var organId= organId;

var $post_combobox;
$(function() {
    loadPost();
});

//加载用户
function loadPost(){
    $post_combobox = $("#postIds").combobox({
        url: ctxAdmin + '/sys/post/combobox?userId='+modelId+'&organId='+organId,
        multiple: true,
        editable:false
    });
}