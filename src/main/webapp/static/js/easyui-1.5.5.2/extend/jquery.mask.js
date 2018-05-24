/**
 *  jquery插件 遮罩
 * 用法:$("#id").mask(); $("#id").mask("hide");
 * 方法: hiden 参数null 隐藏遮罩
 * 属性:
 *     maskMsg	string	提示信息	加载……
 *     zIndex	number	zIndex值	100000
 *     timeout	number	超时(毫秒)	30000
 *     opacity	number	透明程度.(0-1)	0.6
 */

(function ($) {
    if (parent != window && parent.jQuery && parent.jQuery.mask && parent.jQuery.fn.mask) {
        $.mask = parent.jQuery.mask;
        $.fn.mask = parent.jQuery.fn.mask;
        return;
    }
    function init(target, options) {
        var wrap = $(target);
        if ($("div.mask", wrap).length) wrap.mask("hide");
        wrap.attr("position", wrap.css("position"));
        wrap.attr("overflow", wrap.css("overflow"));
        wrap.css("position", "relative");
        wrap.css("overflow", "hidden");
        var maskCss = {
            position: "absolute",
            left: 0,
            top: 0,
            cursor: "wait",
            background: "#ccc",
            opacity: options.opacity,
            filter: "alpha(opacity=" + options.opacity * 100 + ")",
            display: "none"
        };

        var maskMsgCss = {
            position: "absolute",
            width: "auto",
            padding: "10px 20px",
            border: "2px solid #ccc",
            color: "white",
            cursor: "wait",
            display: "none",
            borderRadius: 5,
            background: "black",
            opacity: 0.6,
            filter: "alpha(opacity=60)"
        };
        var width, height, left, top;
        if (target == 'body') {
            width = Math.max(document.documentElement.clientWidth, document.body.clientWidth);
            height = Math.max(document.documentElement.clientHeight, document.body.clientHeight);
        } else {
            width = wrap.outerWidth() || "100%";
            height = wrap.outerHeight() || "100%";
        }
        $('<div class="mask"></div>').css($.extend({}, maskCss, {
            display: 'block',
            width: width,
            height: height,
            zIndex: options.zIndex
        })).appendTo(wrap);

        var maskm = $('<div class="mask-msg"></div>').html(options.maskMsg).appendTo(wrap).css(maskMsgCss);

        if (target == 'body') {
            left = (Math.max(document.documentElement.clientWidth, document.body.clientWidth) - $('div.mask-msg', wrap).outerWidth()) / 2;
            if (document.documentElement.clientHeight > document.body.clientHeight) {
                top = (Math.max(document.documentElement.clientHeight, document.body.clientHeight) - $('div.mask-msg', wrap).outerHeight()) / 2;
            } else {
                top = (Math.min(document.documentElement.clientHeight, document.body.clientHeight) - $('div.mask-msg', wrap).outerHeight()) / 2;
            }

        } else {
            left = (wrap.width() - $('div.mask-msg', wrap).outerWidth()) / 2;
            top = (wrap.height() - $('div.mask-msg', wrap).outerHeight()) / 2;
        }

        maskm.css({
            display: 'block',
            zIndex: options.zIndex + 1,
            left: left,
            top: top
        });

        setTimeout(function () {
            wrap.mask("hide");
        }, options.timeout);

        return wrap;
    }

    $.fn.mask = function (options) {
        if (typeof options == 'string') {
            return $.fn.mask.methods[options](this);
        }
        options = $.extend({}, $.fn.mask.defaults, options);
        return init(this, options);
    };
    $.mask = function (options) {
        if (typeof options == 'string') {
            return $.fn.mask.methods[options]("body");
        }
        options = $.extend({}, $.fn.mask.defaults, options);
        return init("body", options);
    };

    $.mask.hide = function () {
        $("body").mask("hide");
    };

    $.fn.mask.methods = {
        hide: function (jq) {
            return jq.each(function () {
                var wrap = $(this);
                $("div.mask", wrap).fadeOut(function () {
                    $(this).remove();
                });
                $("div.mask-msg", wrap).fadeOut(function () {
                    $(this).remove();
                    wrap.css("position", wrap.attr("position"));
                    wrap.css("overflow", wrap.attr("overflow"));
                });
            });
        }
    };

    $.fn.mask.defaults = {
        //加载中......
        maskMsg: '\u52A0\u8F7D\u4E2D......',
        zIndex: 100000,
        timeout: 30000,
        opacity: 0.6
    };
})(jQuery);