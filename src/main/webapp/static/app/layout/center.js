var layout_center_tabs;
var layout_center_tabsMenu;
$(function() {
    layout_center_tabs = $('#layout_center_tabs').tabs();
    layout_center_tabsMenu = $('#layout_center_tabsMenu').menu({
        onClick : function(item) {
            var curTabTitle = $(this).data('tabTitle');
            var type = $(item.target).attr('type');
            //刷新
            if (type === 'refresh') {
                refresh(layout_center_tabs.tabs('getTab',curTabTitle));
                return;
            }
            //关闭
            if (type === 'close') {
                cancel();
                return;
            }

            var allTabs = layout_center_tabs.tabs('tabs');
            var closeTabsTitle = [];

            $.each(allTabs, function() {
                var opt = $(this).panel('options');
                if (opt.closable && opt.title != curTabTitle && type === 'closeOther') {
                    closeTabsTitle.push(opt.title);
                } else if (opt.closable && type === 'closeAll') {
                    closeTabsTitle.push(opt.title);
                }
            });

            for ( var i = 0; i < closeTabsTitle.length; i++) {
                layout_center_tabs.tabs('close', closeTabsTitle[i]);
            }
        }
    });

    //活动中的tip消息
    var activeTip;
    layout_center_tabs.tabs({
        fit : true,
        border : false,
        tabHeight:36,
        tools:'#layout_center_tabs_full-tools',
//        tools:[{
//            text:'',
//            iconCls:'easyui-icon-reload',
//            handler:function(){refresh()}
//        },{
//            text:'',
//            iconCls:'easyui-icon-cancel',
//            handler:function(){cancel()}
//        }],
        onContextMenu : function(e, title,index) {
            e.preventDefault();
            layout_center_tabsMenu.menu('show', {
                left : e.pageX,
                top : e.pageY
            }).data('tabTitle', title);
        },
        onAdd:function(title,index){
            //tip标题提示
            var tab = $(this).tabs('getTab',index).panel('options').tab;
            tab.unbind('mouseenter').bind('mouseenter',function(e){
                activeTip = $(this).tooltip({
                    position: 'top',
                    content: title
                }).tooltip('show',e);
            });
        },
        onBeforeClose:function(title,index){
            if(activeTip){
                activeTip.tooltip('destroy');
            }
        }
    });

    var indexTitle = "首页";
    layout_center_tabs.tabs('add',{
        title:indexTitle,
        iconCls:'eu-icon-application',
        closable:false,
        content:'<iframe id="iframe_index" name="iframe_index" scrolling="no" frameborder="0" src="'+ctxAdmin+'/portal" style="width:100%;height:100%;"></iframe>'
    });

    //首页tip标题提示
    layout_center_tabs.tabs('getTab',indexTitle).panel('options').tab.unbind('mouseenter').bind('mouseenter',function(e){
        $(this).tooltip({
            position: 'top',
            content: indexTitle
        }).tooltip('show',e);
    });
});
//刷新
function refresh(selectedTab){
    var tab;
    if(selectedTab){
        tab = selectedTab;
    }else{
        tab = layout_center_tabs.tabs('getSelected');
    }
    if(tab == undefined){//未添加任何tab
        return;
    }
    var href = tab.panel('options').href;
    if (href) {/*说明tab是以href方式引入的目标页面*/
        var index = layout_center_tabs.tabs('getTabIndex', tab);
        layout_center_tabs.tabs('getTab', index).panel('refresh');
    } else {/*说明tab是以content方式引入的目标页面*/
        var panel = tab.panel('panel');
        var iframe = panel.find('iframe');
        layout_center_tabs.tabs('updateIframeTab',{
            which:tab.panel('options').title,
            iframe:{src:iframe[0].src}
        });
    }
}
//关闭
function cancel(){
    var index = layout_center_tabs.tabs('getTabIndex', layout_center_tabs.tabs('getSelected'));
    var tab = layout_center_tabs.tabs('getTab', index);
    if(tab == undefined){//未添加任何tab
        return;
    }
    if (tab.panel('options').closable) {
        layout_center_tabs.tabs('close', index);
    } else {
        eu.showAlertMsg('[' + tab.panel('options').title + ']不可以被关闭.', 'error');
    }
}
/**
 *  全屏切换
 * @param flag
 */
function screenToggle(flag){
    var tools ;
    $(".easyui-tooltip").tooltip("hide","click");//  点击图标 隐藏tooltip提示
    //选中的tab页 防止 全屏导致的自动选择第一个tab
    var selectedTab = layout_center_tabs.tabs("getSelected");
    var selectedTab_Title = undefined;
    if(selectedTab){
        selectedTab_Title = selectedTab.panel("options").title;
    }

    if(flag){  //全屏
        tools = "#layout_center_tabs_unfull-tools";
        parent.indexLayout.layout("fullCenter");

    }else{ //退出全屏
        tools = "#layout_center_tabs_full-tools";
        parent.indexLayout.layout("unFullCenter");
    }
    layout_center_tabs.tabs({tools:tools});
    if(selectedTab_Title){
        layout_center_tabs.tabs("select",selectedTab_Title);
    }
}

var login_about_dialog;
var login_password_dialog;
var login_password_form;
function showAbout(){
    //弹出对话窗口
    login_about_dialog = $('<div/>').dialog({
        title:'关于我们',
        iconCls: 'eu-icon-jfit_logo',
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
//            userinfo_dialog.dialog('destroy');
            $(this).dialog('destroy');
        },
        onLoad:function(){
        }
    });

}

/**
 * 更多 右键菜单显示
 * @param el
 */
function showLayoutTabmenu(el){
    var pos = $(el).offset();
    $('#layout_center_tabs_menu').menu('show',{
        left:pos.left+$(el).outerWidth()-$('#layout_center_tabs_menu').outerWidth(),
        top:pos.top+$(el).outerHeight()
    });
}

/**
 * 刷新首页
 */
function refreshIndex(){
    var indexTab = layout_center_tabs.tabs("getTab",indexTitle);
    refresh(indexTab);
}

/**
 * 刷新 portal消息 不提示声音
 */
function refreshPortal(){
    try {
        $("#iframe_index")[0].contentWindow.mymessages(true, false);
    } catch (e) {
    }
}