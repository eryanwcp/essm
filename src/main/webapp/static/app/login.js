var ctx = ctx;
var ctxAdmin = ctxAdmin;
var ctxStatic = ctxStatic;
var sysInitTime = sysInitTime;
var isValidateCodeLogin = isValidateCodeLogin;
var rememberMeCookieValue = rememberMeCookieValue;



var $loginForm;
var $password, $rememberMe;
$(function () {
    $.backstretch([
        ctxStatic + '/js/images/bg1.jpg?_='+sysInitTime,
        ctxStatic + '/js/images/bg2.jpg?_='+sysInitTime,
        ctxStatic + '/js/images/bg3.jpg?_='+sysInitTime
    ], {duration: 5000, fade: 2000});
    $("#qr").qrcode({
        "render": "div",
        "size": 80,
        "color": "#3a3",
        "text": window.location.href
    });

    if(isValidateCodeLogin != "" && isValidateCodeLogin == "true"){
        $(".validateCode").show();
    }

    $loginForm = $("#loginForm").validate({
        rules: {
            validateCode: {remote: ctx + '/servlet/ValidateCodeServlet'}
        },
        messages: {
            loginName: {required: "请填写用户名."},password: {required: "请填写密码."},
            validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
        },
        errorLabelContainer: "#messageBox",
        errorPlacement: function(error, element) {
            error.appendTo($("#loginError").parent());
        },
        submitHandler:function(form){
            login();
        }
    });

    $password = $("#password");
    $rememberMe = $("#rememberMe");

    $rememberMe.prop("checked", rememberMeCookieValue == "" ? false : true);


    $rememberMe.click(function () {
        var checked = $(this).prop('checked');
        if (checked) {
            $.cookie('password', $password.val(), {
                expires: 7
            });
            $.cookie('rememberMe', checked, {
                expires: 7
            });
        } else {
            $.cookie('password', "", {
                expires: 7
            });
            $.cookie('rememberMe', "", {
                expires: 7
            });
        }
    });

    $loginName = $("#loginName").autocomplete(ctxAdmin + '/sys/user/autoComplete', {
        remoteDataType:'json',
        minChars: 0,
        maxItemsToShow: 10
    });
    var ac = $loginName.data('autocompleter');
    //添加查询属性
    ac.setExtraParam("rows",ac.options.maxItemsToShow);
});
// 登录
function login() {
    $("#messageBox2").addClass("hide");
    $.cookie('loginName', $("#loginName").val(), {
        expires: 7
    });
    if ($rememberMe.prop("checked")) {
        $.cookie('password', $password.val(), {
            expires: 7
        });
    }
    $.ajax({
        url: ctxAdmin + '/login/login',
        type: 'post',
        data: $.extend({theme: ''},$.serializeObject($("#loginForm"))),
        traditional: true,
        async:false,
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                window.location = data.obj;
                //setTimeout(function(){//延时1秒 集群环境等待缓存同步
                //    window.location = data.obj;
                //},1000);
            } else {
                $('#validateCode').val('');
                refreshCheckCode();
                $("#loginError2").html(data.msg);
                $("#messageBox2").removeClass("hide");
                if (data.obj != undefined && data.obj == true) {
                    $(".validateCode").show();
                }
            }
        }
    });
}

//刷新验证码
function refreshCheckCode() {
    //加上随机时间 防止IE浏览器不请求数据
    var url = ctx + '/servlet/ValidateCodeServlet?' + new Date().getTime();
    $('#validateCodeImage').attr('src', url);
}
var user_dialog;
// 选择用户
function chooseUser() {
    //弹出对话窗口
    user_dialog = $('<div/>').dialog({
        title: '用户选择',
        width: 400,
        height: 500,
        modal: true,
        maximizable: true,
        href: ctxAdmin + '/sys/user/organUserTreePage?checkbox=false&cascade=false',
        buttons: [
            {
                text: '确定',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    setOrganUserResult();
                    user_dialog.dialog('destroy');
                }
            },{
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    user_dialog.dialog('close');
                }
            }
        ],
        onClose: function () {
            user_dialog.dialog('destroy');
        },
        onLoad: function () {
        }
    });
}


function setOrganUserResult(node){
    if(node == undefined){
        node = $("#organUserTree").tree("getSelected");
    }
    if(node){
        if("u" ==node['attributes']['nType']){
            $("#loginName").val(node['attributes']['loginName']);
            user_dialog.dialog('close');
        }

    }
}