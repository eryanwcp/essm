$.afui.useOSThemes=false;
var $loginName;
var $password;
var $rememberMe;
$(function(){
    $loginName = $("#loginName");
    $password = $("#password");
    $rememberMe = $("#rememberMe");

    $rememberMe.prop("checked", window.localStorage.getItem("rememberMe") == "" ? false : true);
    $loginName.val(window.localStorage.getItem("loginName"));

    if(window.localStorage.getItem("rememberMe")){
        $password.val(window.localStorage.getItem("password"));
    }else{
        $loginName.focus();
    }


    $rememberMe.click(function(){
        var checked = $(this).prop('checked');
        if(checked){
            window.localStorage.setItem("loginName", $loginName.val());
            window.localStorage.setItem("password", $password.val());
            window.localStorage.setItem("rememberMe", checked);
        }else{
            window.localStorage.setItem("password", "");
            window.localStorage.setItem("rememberMe", "");
        }
    });

});

function  login(){
    if(!$loginName.val()){
        $.afui.toast({
            message:"账号不能为空!",
            position:"tc",
            autoClose:true, //have to click the message to close
            type:"success"
        });
        return;
    }
    if(!$password.val()){
        $.afui.toast({
            message:"密码不能为空!",
            position:"tc",
            autoClose:true, //have to click the message to close
            type:"success"
        });
        return;
    }

    $.ajax({
        url: ctxAdmin + '/login/login',
        type: 'post',
        data: {loginName:$loginName.val(),password:$password.val(),checkDevice:false},
        traditional: true,
        async:false,
        dataType: 'json',
        success: function (data) {
            if (data.code == 1) {
                if(data['obj']){
                    setTimeout(function(){//延时1秒 集群环境等待缓存同步
                        window.location = data['obj'];
                    },1000);
                }else{
                    setTimeout(function(){//延时1秒 集群环境等待缓存同步
                        window.location = ctxMobile;
                    },1000);
                }
            }else {
                $.afui.toast({
                    message:data.msg,
                    position:"tc",
                    autoClose:true, //have to click the message to close
                    type:"warning"
                });
            }
        }
    });
}