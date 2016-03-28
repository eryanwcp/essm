var _ctx = _ctx;//参考west.jsp
var $menu_tree;
$(function() {
    //初始化导航菜单
//		initMenu();
    initMenuTree();
});

/*  初始化导航菜单 */
function initMenu(){
    $.post(ctxAdmin+"/login/navTree", function(data) {
        $.each(data, function(i, n) {
            var menulist = "<div class='easyui-panel' data-options='fit:true,border:false' style='overflow-y:auto;overflow-X: hidden;'><ul>";
            $.each(n.children, function(j, o) {//依赖于center界面选项卡layout_center_tabs对象
                menulist += "<li><div><strong><a onClick='javascript:eu.addTab(layout_center_tabs,\""
                    + o.text+"\",\"" + ctxAdmin + o.attributes.url+ "\",true,\""+o.iconCls+"\")' style='font-size:14px;' > <span class='tree-icon tree-file "+o.iconCls+"'></span>" + o.text + "</a></strong></div></li> ";
            });
            menulist += '</ul></div>';

            $(".easyui-accordion").accordion('add', {
                title : n.text,
                content : menulist,
                iconCls : n.iconCls
            });

        });
        $('.easyui-accordion div li div strong a').click(function(){
            $('.easyui-accordion li div').removeClass("selected");
            $(this).parent().parent().addClass("selected");
        }).hover(function(){
            $(this).parent().parent().addClass("hover");
        },function(){
            $(this).parent().parent().removeClass("hover");
        });

    },"json");
}

function initMenuTree(){
    //组织机构树
    $menu_tree = $("#menu_tree").tree({
        url : ctxAdmin+"/login/navTree",
        method:'get',
        animate:true,
        lines:true,
        onClick:function(node){
            var url = node.attributes.url;
            if(url){

                if(url.substring(0,4) == "http"){

                }else if(url.substring(0,1) == "/" ){
                    url = ctx + url;
                }else{
                    url = ctxAdmin+'/' + url;
                }
                eu.addTab(layout_center_tabs,node.text,url,true,node.iconCls);
            }
        }
    });
}