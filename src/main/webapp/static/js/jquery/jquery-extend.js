/**
 * jQuery方法扩展
 * 
 * @author 尔演@Eryan eryanwcp@gmail.com
 * 
 * @version 2013-05-26
 */

/**
 *
 * 将form表单元素的值序列化成对象
 * @param form 表单
 * @returns object
 */
$.serializeObject = function(form) {
    return $.serializeObject(form,false);
};
/**
 * 将form表单元素的值序列化成对象
 * @param form 表单
 * @param radio 是否包含radio
 * @returns {{}}
 */
$.serializeObject = function(form,radio) {
    var o = {};
    $.each(form.serializeArray(), function(index) {
        if (o[this['name']]) {
            if(radio){
                var input = form.find("input[name='"+this['name']+"']")[0];
                var inputType = $(input).prop('type');
                if(inputType != undefined && "radio" == inputType){
                    return false;
                }
            }

            o[this['name']] = o[this['name']] + "," + this['value'];
        } else {
            o[this['name']] = this['value'];
        }
    });
    return o;
};
/**
 * 将form表单元素的值序列化成对象
 * @param form 表单
 * @param radio 是否包含radio
 * @returns {{}}
 */
$.serializeFullObject = function(form,radio) {
	var o = {};
	$.each(form.serializeArray(), function(index) {
		if (o[this['name']] != undefined) {
			if(radio){
				var input = form.find("input[name='"+this['name']+"']")[0];
				var inputType = $(input).prop('type');
				if(inputType != undefined && "radio" == inputType){
					return false;
				}
			}

			o[this['name']] = o[this['name']] + "," + this['value'];
		} else {
			o[this['name']] = this['value'];
		}
	});
	return o;
};

function isValidDate(d) {
    if ( Object.prototype.toString.call(d) !== "[object Date]" )
        return false;
    return !isNaN(d.getTime());
}

/**
 * 扩展日期格式化 例：new Date().format("yyyy-MM-dd HH:mm:ss")
 *
 * "M+" :月份
 * "d+" : 天
 * "h+" : 小时
 * "m+" : 分钟
 * "s+" : 秒钟
 * "q+" : 季度
 * "S" : 毫秒数
 * "X": 星期 如星期一
 * "Z": 返回周 如周二
 * "F":英文星期全称，返回如 Saturday
 * "L": 三位英文星期，返回如 Sat
 * @param format 格式化字符串
 * @returns {*}
 */
Date.prototype.format = function(format) {
    if(!isValidDate(this)){
        return '';
    }
    var week = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    var week_cn = [ '日', '一', '二', '三', '四', '五', '六'];
    var o = {
		"M+" : this.getMonth() + 1, //月份
		"d+" : this.getDate(), //天
		"H+" : this.getHours(), //小时
		"m+" : this.getMinutes(), //分钟
		"s+" : this.getSeconds(), //秒钟
		"q+" : Math.floor((this.getMonth() + 3) / 3), //季度
		"S" : this.getMilliseconds(),//毫秒数
        "X": "星期" + week_cn[this.getDay() ], //星期
        "Z": "周" + week_cn[this.getDay() ],  //返回如 周二
        "F": week[this.getDay()],  //英文星期全称，返回如 Saturday
        "L": week[this.getDay()].slice(0, 3)//三位英文星期，返回如 Sat
	}
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
}

/**
 * 日期格式化.
 * @param value 日期
 * @param format 格式化字符串 例如："yyyy-MM-dd"、"yyyy-MM-dd HH:mm:ss"
 * @returns 格式化后的字符串
 */
 $.formatDate = function(value,format) {
	if (value == null || value == '' || value == 'null' ) {
		return '';
	}
	var dt;
	if (value instanceof Date) {
		dt = value;
	} else {
		dt = new Date(value);
		if (isNaN(dt)) {
			//将那个长字符串的日期值转换成正常的JS日期格式
			value = value.replace(/\/Date\((-?\d+)\)\//, '$1'); 
			dt = new Date();
			dt.setTime(value);
		}
	}
	return dt.format(format);
}
/**
 * 
 * 增加formatString功能
 * 
 * 使用方法：$.formatString('字符串{0}字符串{1}字符串','第一个变量','第二个变量');
 * 
 * @returns 格式化后的字符串
 */
$.formatString = function(str) {
	for ( var i = 0; i < arguments.length - 1; i++) {
		str = str.replace("{" + i + "}", arguments[i + 1]);
	}
	return str;
};

/**
 * 根据长度截取先使用字符串，超长部分追加...
 * @param str 对象字符串
 * @param len 目标字节长度
 * @return 处理结果字符串
 */
$.cutString = function(str, len) {
	//length属性读出来的汉字长度为1
	if(str.length*2 <= len) {
		return str;
	}
	var strlen = 0;
	var s = "";
	for(var i = 0;i < str.length; i++) {
		s = s + str.charAt(i);
		if (str.charCodeAt(i) > 128) {
			strlen = strlen + 2;
			if(strlen >= len){
				return s.substring(0,s.length-1) + "...";
			}
		} else {
			strlen = strlen + 1;
			if(strlen >= len){
				return s.substring(0,s.length-2) + "...";
			}
		}
	}
	return s;
}
/**
 * Object to String
 * 不通用,因为拼成的JSON串格式不对.
 */
$.objToStr = function(o) {
	var r = [];
	if (typeof o == "string" || o == null) {
		return "@" + o + "@";
	}
	if (typeof o == "object") {
		if (!o.sort) {
			r[0] = "{";
			for ( var i in o) {
				r[r.length] = "@" + i + "@";
				r[r.length] = ":";
				r[r.length] = $.objToStr(o[i]);
				r[r.length] = ",";
			}
			r[r.length - 1] = "}";
		} else {
			r[0] = "[";
			for ( var i = 0; i < o.length; i++) {
				r[r.length] = $.objToStr(o[i]);
				r[r.length] = ",";
			}
			r[r.length - 1] = "]";
		}
		return r.join("");
	}
	return o.toString();
}

/**
 * json字符串转换为Object对象.
 * @param json json字符串
 * @returns Object
 */
$.jsonToObj = function(json){ 
    return eval("("+json+")"); 
}
/**
 * json对象转换为String字符串对象.
 * @param o Json Object
 * @returns   Object对象
 */
$.jsonToString = function(o) {
	var r = [];
	if (typeof o == "string")
		return "\"" + o.replace(/([\'\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";
	if (typeof o == "object") {
		if (!o.sort) {
			for ( var i in o)
				r.push(i + ":" + $.objToStr(o[i]));
			if (!!document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
				r.push("toString:" + o.toString.toString());
			}
			r = "{" + r.join() + "}";
		} else {
			for ( var i = 0; i < o.length; i++)
				r.push($.objToStr(o[i]));
			r = "[" + r.join() + "]";
		}
		return r;
	}
	return o.toString();
};


/**
 * 获得URL参数
 * 
 * @returns 对应名称的值
 */
$.getUrlParam = function(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
};

/**
 * 接收一个以逗号分割的字符串，返回List，list里每一项都是一个字符串
 * 
 * @returns list
 */
$.getList = function(value) {
	if (value != undefined && value != '') {
		var values = [];
		var t = value.split(',');
		for ( var i = 0; i < t.length; i++) {
			values.push('' + t[i]);/* 避免他将ID当成数字 */
		}
		return values;
	} else {
		return [];
	}
};

/**
 * 获得项目根路径
 * 
 * 使用方法：$.bp();
 * 
 * @returns 项目根路径
 */
$.bp = function() {
	var curWwwPath = window.document.location.href;
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	var localhostPaht = curWwwPath.substring(0, pos);
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return (localhostPaht + projectName);
};

/**
 * 
 * 使用方法:$.pn();
 * 
 * @returns 项目名称(/base)
 */
$.pn = function() {
	return window.document.location.pathname.substring(0, window.document.location.pathname.indexOf('\/', 1));
};


/**
 * 
 * 生成UUID
 * 
 * @returns UUID字符串
 */
$.random4 = function() {
	return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
};
$.UUID = function() {
	return ($.random4() + $.random4() + "-" + $.random4() + "-" + $.random4() + "-" + $.random4() + "-" + $.random4() + $.random4() + $.random4());
};

/**
 * 扩展js 去字符串空格
 */
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, '');
};
String.prototype.ltrim = function() {
	return this.replace(/(^\s*)/g, '');
};
String.prototype.rtrim = function() {
	return this.replace(/(\s*$)/g, '');
};


var DECMAP = { "0": "零", "1": "一", "2": "二", "3": "三", "4": "四", "5": "五", "6": "六", "7": "七", "8": "八", "9": "九"};
var NLEN = ["千", "百", "十", "亿", "千", "百", "十", "万", "千", "百", "十", ""];
/**
 * 数字转换为中文格式
 * @param dec
 * @returns {string}
 */
$.formatDecimals = function (dec) {
    var _td = dec + "";
    var out = "";
    if (_td.indexOf(".") >= 0) {
        var sp = _td.split(".");
        for (var i = 0; i < sp[0].length; i++) {
            out += DECMAP[(sp[0].charAt(i))];
            out += NLEN[NLEN.length - sp[0].length + i];
        }
        out += "点";
        for (var j = 0; j < sp[1].length; j++) {
            out += DECMAP[(sp[1].charAt(j))];
        }
    } else {
        for (var g = 0; g < _td.length; g++) {
            out += DECMAP[(_td.charAt(g))];
            out += NLEN[NLEN.length - _td.length + g];
        }
    }
    return out;
}

/**
 * 货币格式化
 * @param num
 * @param prefix 例如：￥、$
 * @returns {string}
 */
$.formatCurrency = function (num, prefix) {
    num = num.toString().replace(/\$|\,/g, '');
    if (isNaN(num))
        num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num * 100 + 0.50000000001);
    cents = num % 100;
    num = Math.floor(num / 100).toString();
    if (cents < 10)
        cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
        num = num.substring(0, num.length - (4 * i + 3)) + ',' +
            num.substring(num.length - (4 * i + 3));
    num = (((sign) ? '' : '-') + num + '.' + cents);
    if (prefix != undefined) {
        return prefix + num;
    }
    return num;
}
/**
 * 还原货币格式化
 * @param num
 * @returns {XML}
 */
$.restoreFormatCurrency = function (num) {
    var num1 = num.replace(',', '').replace(/,/g, '');
    return num1;
}


function HashMap()
{
	/** Map 大小 **/
	var size = 0;
	/** 对象 **/
	var entry = new Object();

	/** 存 **/
	this.put = function (key , value)
	{
		if(!this.containsKey(key))
		{
			size ++ ;
		}
		entry[key] = value;
	}

	/** 取 **/
	this.get = function (key)
	{
		if( this.containsKey(key) )
		{
			return entry[key];
		}
		else
		{
			return null;
		}
	}

	/** 删除 **/
	this.remove = function ( key )
	{
		if( delete entry[key] )
		{
			size --;
		}
	}

	/** 是否包含 Key **/
	this.containsKey = function ( key )
	{
		return (key in entry);
	}

	/** 是否包含 Value **/
	this.containsValue = function ( value )
	{
		for(var prop in entry)
		{
			if(entry[prop] == value)
			{
				return true;
			}
		}
		return false;
	}

	/** 所有 Value **/
	this.values = function ()
	{
		var values = new Array(size);
		for(var prop in entry)
		{
			values.push(entry[prop]);
		}
		return values;
	}

	/** 所有 Key **/
	this.keys = function ()
	{
		var keys = new Array(size);
		for(var prop in entry)
		{
			keys.push(prop);
		}
		return keys;
	}

	/** Map Size **/
	this.size = function ()
	{
		return size;
	}
}

/**
 ** 加法函数，用来得到精确的加法结果
 ** 说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
 ** 调用：accAdd(arg1,arg2)
 ** 返回值：arg1加上arg2的精确结果
 **/
function accAdd(arg1, arg2) {
	var r1, r2, m, c;
	try {
		r1 = arg1.toString().split(".")[1].length;
	}
	catch (e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	}
	catch (e) {
		r2 = 0;
	}
	c = Math.abs(r1 - r2);
	m = Math.pow(10, Math.max(r1, r2));
	if (c > 0) {
		var cm = Math.pow(10, c);
		if (r1 > r2) {
			arg1 = Number(arg1.toString().replace(".", ""));
			arg2 = Number(arg2.toString().replace(".", "")) * cm;
		} else {
			arg1 = Number(arg1.toString().replace(".", "")) * cm;
			arg2 = Number(arg2.toString().replace(".", ""));
		}
	} else {
		arg1 = Number(arg1.toString().replace(".", ""));
		arg2 = Number(arg2.toString().replace(".", ""));
	}
	return (arg1 + arg2) / m;
}

//给Number类型增加一个add方法，调用起来更加方便。
Number.prototype.add = function (arg) {
	return accAdd(arg, this);
};

/**
 ** 减法函数，用来得到精确的减法结果
 ** 说明：javascript的减法结果会有误差，在两个浮点数相减的时候会比较明显。这个函数返回较为精确的减法结果。
 ** 调用：accSub(arg1,arg2)
 ** 返回值：arg1加上arg2的精确结果
 **/
function accSub(arg1, arg2) {
	var r1, r2, m, n;
	try {
		r1 = arg1.toString().split(".")[1].length;
	}
	catch (e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	}
	catch (e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2)); //last modify by deeka //动态控制精度长度
	n = (r1 >= r2) ? r1 : r2;
	return ((arg1 * m - arg2 * m) / m).toFixed(n);
}

// 给Number类型增加一个mul方法，调用起来更加方便。
Number.prototype.sub = function (arg) {
	return accSub(this, arg);
};

/**
 ** 乘法函数，用来得到精确的乘法结果
 ** 说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
 ** 调用：accMul(arg1,arg2)
 ** 返回值：arg1乘以 arg2的精确结果
 **/
function accMul(arg1, arg2) {
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	}
	catch (e) {
	}
	try {
		m += s2.split(".")[1].length;
	}
	catch (e) {
	}
	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

// 给Number类型增加一个mul方法，调用起来更加方便。
Number.prototype.mul = function (arg) {
	return accMul(arg, this);
};

/**
 ** 除法函数，用来得到精确的除法结果
 ** 说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
 ** 调用：accDiv(arg1,arg2)
 ** 返回值：arg1除以arg2的精确结果
 **/
function accDiv(arg1, arg2) {
	var t1 = 0, t2 = 0, r1, r2;
	try {
		t1 = arg1.toString().split(".")[1].length;
	}
	catch (e) {
	}
	try {
		t2 = arg2.toString().split(".")[1].length;
	}
	catch (e) {
	}
	with (Math) {
		r1 = Number(arg1.toString().replace(".", ""));
		r2 = Number(arg2.toString().replace(".", ""));
		return (r1 / r2) * pow(10, t2 - t1);
	}
}

//给Number类型增加一个div方法，调用起来更加方便。
Number.prototype.div = function (arg) {
	return accDiv(this, arg);
};
/**
 * 深度克隆
 * @param obj
 * @returns {*}
 */
$.deepClone = function (obj) {
    var objClone;
    if (obj.constructor == Object) {
        objClone = new obj.constructor();
    } else {
        objClone = new obj.constructor(obj.valueOf());
    }
    for (var key in obj) {
        if (objClone[key] != obj[key]) {
            if (typeof(obj[key]) == 'object') {
                objClone[key] = $.deepClone(obj[key]);
            } else {
                objClone[key] = obj[key];
            }
        }
    }
    objClone.toString = obj.toString;
    objClone.valueOf = obj.valueOf;
    return objClone;
};