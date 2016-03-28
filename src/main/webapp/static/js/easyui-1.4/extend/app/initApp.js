/**
 * 初始化桌面版应用程序
 */
var app;
$(function () {
    app = $('body').app({
//        taskBlankPos:'north',
//        wallpaper:ctx+'/img/top_bg.png',
        loadUrl : { //远程数据加载路径
            app : ctx+'/login/apps', //app数据
            startMenu : ctx+'/login/startMenu'//, //开始菜单数据
//            widget : ctxStatic+'/js/jquery/easyui-1.4/extent/app/widget.json'
        },
        otherStartMenus:[{
                id:'startMenu_index',
                text:'切换到传统版',
                onclick:function(){
                    var themeType_index = "index";
                    $.cookie('themeType', themeType_index, {
                        expires : 7
                    });
                    window.location.href = ctx+"/login/index?theme="+themeType_index;
                }
            },'-',{
            id:'startMenu_logout',
            text:'注销',
            iconCls:'eu-icon-lock',
            onclick:function(){
                $.messager.confirm('确认提示！', '您确定要退出系统吗？', function(r) {
                    if (r) {
                        $.cookie('autoLogin', "", {
                            expires : 7
                        });
                        window.location.href = ctx+"/login/logout";
                    }
                });
            }
        }] ,
        onTaskBlankContextMenu:onTaskBlankContextMenu,
        onAppContextMenu:onAppContextMenu,
        onWallContextMenu:onWallContextMenu,
		onStartMenuClick:onStartMenuClick
    });
	
	function onStartMenuClick(item){
        var data = $(item.target).data("data");
        if(data !=undefined && data.type != undefined && data.type == "refresh"){ //刷新桌面
            app.app("refresh",app.app("options").loadUrl.app);
        } else{
            $('body').app("createwindow",data);
        }
	}

    var appListMenuData = [{
            "id":"appListMenuData_open",
            "text":"打开"
        },{
            "text":"刷新"
        },{
            "text":"关闭"
        },/*{
            "text":"关闭其他"
        },*/{
            "text":"关闭所有"
        },{
            "text":"任务管理器"
        },{
            "text":"属性"
        }
    ];
   function taskOnclick(item,appid){
       if(item.text){
           var type = item.text;
           if (type === '打开') {
               app.app('openapp',appid);
           }else if (type === '刷新') {
               app.app('refreshapp',appid);
           }else if (type === '关闭') {
               app.app('closeapp',appid);
           }else if (type === '关闭所有') {
               app.app('closeall');
           }
       }
   }
    var appListMenu = $('body').app('createMenu', {data:appListMenuData});

    function onTaskBlankContextMenu(e, appid) {
        if (appid) {
            appListMenu.menu('findItem', '任务管理器').target.style.display = 'none';
            appListMenu.menu('findItem', '属性').target.style.display = 'none';
            appListMenu.menu('findItem', '打开').target.style.display = 'block';
            appListMenu.menu('findItem', '刷新').target.style.display = 'block';
            appListMenu.menu('findItem', '关闭').target.style.display = 'block';
//            appListMenu.menu('findItem', '关闭其他').target.style.display = 'block';
            appListMenu.menu('findItem', '关闭所有').target.style.display = 'block';
            appListMenu.menu({
                onClick:function(item){
                    taskOnclick(item,appid);
                }
            });
        } else {
            appListMenu.menu('findItem', '任务管理器').target.style.display = 'block';
            appListMenu.menu('findItem', '属性').target.style.display = 'block';
            appListMenu.menu('findItem', '打开').target.style.display = 'none';
            appListMenu.menu('findItem', '刷新').target.style.display = 'none';
            appListMenu.menu('findItem', '关闭').target.style.display = 'none';
//            appListMenu.menu('findItem', '关闭其他').target.style.display = 'none';
            appListMenu.menu('findItem', '关闭所有').target.style.display = 'none';
        }

        appListMenu.menu('show', {
            left:e.pageX,
            top:e.pageY
        });
        e.preventDefault();
    }


    var wallMenuData = [{
            "text":"刷新",
            "type":"refresh"
        },{
            "text":"关于",
            "href":ctx+"/common/layout/about"
        }
    ];
    var appMenuData = [{
            "text":"打开"
        },'-',{
            "text":"属性"
        }
    ];

    var wallMenu = $('body').app('createMenu', {data:wallMenuData,opt:{onClick:onStartMenuClick}});
    var appMenu = $('body').app('createMenu', {data:appMenuData,opt:{onClick:onAppContextMenuClick}});

	var APPID;
    function onAppContextMenu(e,appid) {
        appMenu.menu('show', {
            left:e.pageX,
            top:e.pageY
        });
		APPID = appid;
    }
	
	function onAppContextMenuClick(item){
		if(item.text == '打开'){
			$("li[app_id='"+APPID+"']").dblclick();
		}
	}

    function onWallContextMenu(e) {
        wallMenu.menu('show', {
            left:e.pageX,
            top:e.pageY
        });
    }
});
