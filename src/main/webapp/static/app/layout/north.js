var login_about_dialog;
var login_password_dialog;
var login_password_form;
function showAbout(){
    //弹出对话窗口
    login_about_dialog = $('<div/>').dialog({
        title:'关于我们',
        iconCls: 'eu-icon-jfit',
        top: 100,
        width: 436,
        height : 400,
        modal : true,
        href : ctxAdmin+'/common/layout/about',
        buttons : [{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                login_about_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            login_about_dialog.dialog('destroy');
        }
    });
//  	$(".panel-title").css('text-align', 'center');
}

function initLoginPasswordForm(){
    login_password_form = $('#login_password_form').form({
        url: ctxAdmin+'/sys/user/updateUserPassword',
        onSubmit: function(param){
            param.upateOperate = '1';
            return $(this).form('validate');
        },
        success: function(data){
            var json = eval('('+ data+')');
            if (json.code == 1){
                login_about_dialog.dialog('close');
                eu.showMsg(json.msg);//操作结果提示
            } else if(json.code == 2){
                $.messager.alert('提示信息！', json.msg, 'warning',function(){
                    var userId = $('#login_password_form_id').val();
                    $(this).form('clear');
                    $('#login_password_form_id').val(userId);
                    if(json.obj){
                        $('#login_password_form input[name="'+json.obj+'"]').focus();
                    }
                });
            }else {
                eu.showAlertMsg(json.msg,'error');
            }
        }
    });
}

function editLoginUserPassword(){
    //弹出对话窗口
    login_about_dialog = $('<div/>').dialog({
        title:'&nbsp;修改用户密码',
        top: 100,
        width : 460,
        height : 240,
        modal : true,
        iconCls:'easyui-icon-edit',
        href : ctxAdmin+'/common/layout/north-password',
        buttons : [{
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                login_password_form.submit();;
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                login_about_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            login_about_dialog.dialog('destroy');
        },
        onLoad:function(){
            initLoginPasswordForm();
        }
    });
}
//注销
function logout(clearCookie) {
    $.messager.confirm('确认提示！', '您确定要退出系统吗？', function(r) {
        if (r) {
            if(clearCookie){
                $.cookie('autoLogin', "", {
                    expires : 7
                });
            }
            window.location.href = ctxAdmin+"/login/logout";
        }
    });
}
//切换到桌面版
function toApp(){
    var themeType_index = "app";
    $.cookie('themeType', themeType_index, {
        expires : 7
    });
    window.location.href = ctxAdmin+'/login/index?theme='+themeType_index;
}

var userinfo_dialog;
function editLoginUserInfo(){
    var inputUrl = ctxAdmin +"/sys/user/userInfoInput";
    //弹出对话窗口
    userinfo_dialog = $('<div/>').dialog({
        title:'个人详细信息',
//            width : document.body.clientWidth,
//            height : document.body.clientHeight,
            height : 360,
        top:100,
        width : 500,
        modal : true,
        maximizable:true,
        href : inputUrl,
        buttons : [ {
            text : '保存',
            iconCls : 'easyui-icon-save',
            handler : function() {
                $("#userinfo_form").submit();
            }
        },{
            text : '关闭',
            iconCls : 'easyui-icon-cancel',
            handler : function() {
                userinfo_dialog.dialog('destroy');
            }
        }],
        onClose : function() {
            userinfo_dialog.dialog('destroy');
        },
        onLoad:function(){
        }
    });

}
