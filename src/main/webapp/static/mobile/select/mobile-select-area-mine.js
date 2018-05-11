/*！
 * 选择框 适合直接用于锦峰软件
 * 部分内容已修改：
 * 1、数据结构变化
 * 2、回调函数支持添加选中节点参数
 * 3、添加部分自定义方法
 * @data 2017-03-10
 * 参考 https://github.com/tianxiangbing/mobile-select-area
 */
;
(function(root, factory) {
	//amd
	if (typeof define === 'function' && define.amd) {
		define([ "jquery",'dialog' ], factory );
	} else if (typeof define === 'function' && define.cmd) {
		define(function(require, exports, module) {
			var $ = require("jquery");
			var Dialog = require("dialog");
			return factory($, Dialog);
		});
	} else if (typeof exports === 'object') { //umd
		module.exports = factory();
	} else {
		root.MobileSelectArea = factory(jQuery);
	}
})(this, function($, Dialog) {
	var MobileSelectArea = function() {
		var rnd = Math.random().toString().replace('.', '');
		this.id = 'scroller_' + rnd;
		this.scroller;
		this.data;
		this.tempData;
		this.index = 0;
		this.value = [0, 0, 0];
		this.oldvalue;
		this.oldtext = [];
		this.text = ['', '', ''];
		this.level = 3;
		this.mtop = 30;
		this.separator = ' ';
	};
	MobileSelectArea.prototype = {
		init: function(settings) {
			this.settings = $.extend({
				eventName: 'click'
			}, settings);
			this.trigger = $(this.settings.trigger);
			this.settings.default == undefined ? this.default = 1 : this.default = 0; //0为空,1时默认选中第一项
			level = parseInt(this.settings.level);
			this.level = level > 0 ? level : 3;
			this.trigger.attr("readonly", "readonly");
			this.value = (this.settings.value && this.settings.value.split(",")) || [0, 0, 0];
			this.text = this.settings.text || this.trigger.val().split(' ') || ['', '', ''];
			this.oldvalue = this.value.concat([]);
			this.oldtext = this.text.concat([]);
			this.clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
			this.clientWidth = document.documentElement.clientWidth || document.body.clientWidth;
			// this.promise = this.getData();
			this.tempData = new Array();
			this.bindEvent();
		},
		getData: function() {
			var _this = this;
			var dtd = $.Deferred();
			if (typeof this.settings.data == "object") {
				this.data = this.settings.data;
				dtd.resolve();
			} else {
				$.ajax({
					dataType: 'json',
					cache: true,
					url: this.settings.data,
					type: 'GET',
					success: function(result) {
						_this.data = result;
						dtd.resolve();
					},
					accepts: {
						json: "application/json, text/javascript, */*; q=0.01"
					}
				});
			}
			return dtd;
		},
		bindEvent: function() {
			var _this = this;
			this.trigger[_this.settings.eventName](function(e) {
				var dlgContent = '';
				for (var i = 0; i < _this.level; i++) {
					dlgContent += '<div></div>';
				};
				var settings, buttons;
				if (_this.settings.position == "bottom") {
					settings = {
						position: "bottom",
						width: "100%",
						className: "ui-dialog-bottom",
						animate: false
					}
					var buttons = [{
						'no': '取消'
					}, {
						'yes': '确定'
					}];
				}
				$.confirm('<div class="ui-scroller-mask"><div id="' + _this.id + '" class="ui-scroller">' + dlgContent + '<p></p></div></div>', buttons, function(t, c) {
					if (t == "yes") {
						_this.submit()
					}
					if (t == 'no') {
						_this.cancel();
					}
					this.dispose();
				}, $.extend({
					width: (document.documentElement.clientWidth || document.body.clientWidth) - 10 + "px",
					height: 215
				}, settings));
				_this.scroller = $('#' + _this.id);
				_this.getData().done(function() {
					_this.format();
				});
				$(".ui-scroller>div").css({width:100/_this.level+"%"});
				var start = 0,
					end = 0;
				_this.scroller.children().bind('touchstart', function(e) {
					start = (e.changedTouches || e.originalEvent.changedTouches)[0].pageY;
				});
				_this.scroller.children().bind('touchmove', function(e) {
					end = (e.changedTouches || e.originalEvent.changedTouches)[0].pageY;
					var diff = end - start;
					var dl = $(e.target).parent();
					if (dl[0].nodeName != "DL") {
						return;
					}
					var top = parseInt(dl.css('top') || 0) + diff;
					dl.css('top', top);
					start = end;
					return false;
				});
				_this.scroller.children().bind('touchend', function(e) {
					end = (e.changedTouches || e.originalEvent.changedTouches)[0].pageY;
					var diff = end - start;
					var dl = $(e.target).parent();
					if (dl[0].nodeName != "DL") {
						return;
					}
					var i = $(dl.parent()).index();
					var top = parseInt(dl.css('top') || 0) + diff;
					if (top > _this.mtop) {
						top = _this.mtop;
					}
					if (top < -$(dl).height() + 60) {
						top = -$(dl).height() + 60;
					}
					var mod = top / _this.mtop;
					var mode = Math.round(mod);
					var index = Math.abs(mode) + 1;
					if (mode == 1) {
						index = 0;
					}
					_this.value[i] = $(dl.children().get(index)).attr('ref');
					_this.value[i] == 0 ? _this.text[i] = "" : _this.text[i] = $(dl.children().get(index)).html();
					if (!$(dl.children().get(index)).hasClass('focus')) {
						for (var j = _this.level - 1; j > i; j--) {
							_this.value[j] = 0;
							_this.text[j] = "";
						}
						_this.format();
					}
					$(dl.children().get(index)).addClass('focus').siblings().removeClass('focus');
					dl.css('top', mode * _this.mtop);
					return false;
				});
				return false;
			});
		},
		format: function() {
			var _this = this;
			var child = _this.scroller.children();
			this.f(this.data);
			//console.log(_this.text)
		},
		f: function(data) {
			var _this = this;
			var item = data;
			if (!item) {
				item = [];
			}
			var str = '<dl><dd ref="0">——</dd>';
			var focus = 0,
				childData, top = _this.mtop;
			if (_this.index !== 0 && _this.value[_this.index - 1] == "0" && this.default == 0) {
				str = '<dl><dd ref="0" class="focus">——</dd>';
				_this.value[_this.index] = 0;
				_this.text[_this.index] = "";
				focus = 0;
			} else {
				if (_this.value[_this.index] == "0") {
					str = '<dl><dd ref="0" class="focus">——</dd>';
					focus = 0;
				}
				if (item.length > 0 && this.default == 1) {
					str = '<dl>';
					var pid = item[0].pid || 0;
					var id = item[0].id || 0;
					focus = item[0].id;
					childData = item[0].children;
					if (!_this.value[this.index]) {
						_this.value[this.index] = id;
						_this.text[this.index] = item[0].text;
					}
					this.tempData[id] = item[0];
					str += '<dd pid="' + pid + '" class="' + cls + '" ref="' + id + '">' + item[0].text + '</dd>';
				}
				for (var j = _this.default, len = item.length; j < len; j++) {
					var pid = item[j].pid || 0;
					var id = item[j].id || 0;
					var cls = '';
					if (_this.value[_this.index] == id) {
						cls = "focus";
						focus = id;
						childData = item[j].children;
						top = _this.mtop * (-(j - _this.default));
					}
					this.tempData[id] = item[j];
					str += '<dd pid="' + pid + '" class="' + cls + '" ref="' + id + '">' + item[j].text + '</dd>';
				}
			}
			str += "</dl>";
			var newdom = $(str);
			newdom.css('top', top);
			var child = _this.scroller.children();
			$(child[_this.index]).html(newdom);
			_this.index++;
			if (_this.index > _this.level - 1) {
				_this.index = 0;
				return;
			}
			_this.f(childData);
		},
		submit: function() {
			this.oldvalue = this.value.concat([]);
			this.oldtext = this.text.concat([]);
			if (this.trigger[0].nodeType == 1) {
				//input
				this.trigger.val(this.text.join(this.separator));
				this.trigger.attr('data-value', this.value.join(','));
			}
			this.trigger.next(':hidden').val(this.value.join(','));
			var item = this.tempData[this.getValue()];//最后一个选中的节点数据
			this.settings.callback && this.settings.callback.call(this, this.scroller, this.text, this.value,item);
		},
		cancel: function() {
			this.value = this.oldvalue.concat([]);
			this.text = this.oldtext.concat([]);
		},
		getValue: function() {
			var _value = '';
			if(this.value){
				for(var i=this.value.length-1; i>=0; --i){
					if(this.value[i] != 0){
						_value = this.value[i];
						break;
					}
				}
			}
			return _value;
		},
		getValues: function() {
			return this.value;
		},
		getText: function() {
			var _text = '';
			if(this.text){
				for(var i=this.text.length-1; i>=0; --i){
					if(this.text[i] != 0){
						_text = this.text[i];
						break;
					}
				}
			}
			return _text;
		},
		getTexts: function() {
			return this.text;
		},
		getTempData: function() {
			return this.tempData;
		}
	};
	return MobileSelectArea;
});