/**
 * Easyui KindEditor的简单扩展.
 * 插件需要在Easyui.min和KindEditor之后导入.
 **/
(function ($, K) {
    if (!K)
        throw "KindEditor未定义!";

    function create(target) {
        var opts = $.data(target, 'kindeditor').options;
        var editor = K.create(target, opts);
        $.data(target, 'kindeditor').options.editor = editor;
    }

    $.fn.kindeditor = function (options, param) {
        if (typeof options == 'string') {
            var method = $.fn.kindeditor.methods[options];
            if (method) {
                return method(this, param);
            }
        }
        options = options || {};
        return this.each(function () {
            var state = $.data(this, 'kindeditor');
            if (state) {
                $.extend(state.options, options);
            } else {
                state = $.data(this, 'kindeditor', {
                    options : $.extend({}, $.fn.kindeditor.defaults, $.fn.kindeditor.parseOptions(this), options)
                });
            }
            create(this);
        });
    }

    $.fn.kindeditor.parseOptions = function (target) {
        return $.extend({}, $.parser.parseOptions(target, []));
    };

    $.fn.kindeditor.methods = {
        editor : function (jq) {
            return $.data(jq[0], 'kindeditor').options.editor;
        }
    };

    $.fn.kindeditor.defaults = {
        resizeType : 1,
        allowPreviewEmoticons : false,
        allowImageUpload : false,
        items : [
            'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
            'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
            'insertunorderedlist', '|', 'emoticons', 'image', 'link'],
        afterChange:function(){
            //加载完成后改变皮肤
            var color = $('.panel-header').css('background-color');
            $('.ke-toolbar').css('background-color',color);
            this.sync();//这个是必须的,如果你要覆盖afterChange事件的话,请记得最好把这句加上.
        }
    };
    $.parser.plugins.push("kindeditor");
})(jQuery, KindEditor);