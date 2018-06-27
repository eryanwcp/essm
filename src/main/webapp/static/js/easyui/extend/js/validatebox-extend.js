/*!
 * 扩展easyui validatebox校验
 */
$.extend($.fn.validatebox.defaults.rules, {
    alpha:{
        validator:function(value,param){
            if (value){
                return /^[a-zA-Z\u00A1-\uFFFF]*$/.test(value);
            } else {
                return true;
            }
        },
        message:'只能输入字母.'
    },
    number: {
        validator: function (value, param) {
            return /^\d+$/.test(value);
        },
        message: '请输入数字.'
    },
    alphanum:{
        validator:function(value,param){
            if (value){
                return /^[a-zA-Z0-9]*$/.test(value);
            } else {
                return true;
            }
        },
        message:'只能输入字母和数字.'
    },
    positive_int:{
        validator:function(value,param){
            if (value){
                return /^[0-9]*[1-9][0-9]*$/.test(value);
            } else {
                return true;
            }
        },
        message:'只能输入正整数.'
    },
    numeric:{
        validator:function(value,param){
            if (value){
                return /^[0-9]*(\.[0-9]+)?$/.test(value);
            } else {
                return true;
            }
        },
        message:'只能输入数字.'
    },
    noNumeric:{
        validator:function(value,param){
            if (value){
                return !/^[0-9]*(\.[0-9]+)?$/.test(value);
            } else {
                return true;
            }
        },
        message:'不能全为数字.'
    },
    chinese:{
        validator:function(value,param){
            if (value){
                return /[^\u4E00-\u9FA5]/g.test(value);
            } else {
                return true;
            }
        },
        message:'只能输入中文.'
    },
    minLength: {
        validator: function(value, param){
            value = $.trim(value);	//去空格
            return value.length >= param[0];
        },
        message: '请输入至少 {0}个非空字符 .'
    },
    CHS: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5]+$/.test(value);
        },
        message: '请输入汉字.'
    },
    ZIP: {
        validator: function (value, param) {
            return /^[1-9]\d{5}$/.test(value);
        },
        message: '邮政编码不存在.'
    },
    IP: {
        validator : function(value) {
            return /\d+\.\d+\.\d+\.\d+/.test(value);
        },
        message : 'IP地址格式不正确'
    },
    QQ: {
        validator: function (value, param) {
            return /^[1-9]\d{4,10}$/.test(value);
        },
        message: 'QQ号码不正确.'
    },
    mobile: {
        validator: function (value, param) {
            return /^(13|15|18)\d{9}$/i.test(value);
        },
        message: '以13、15、18开头的11位手机号码.'
    },
    phone: {//座机号码/固定电话验证
        validator: function(value, param){
            return /^(\([0-9]{3,4}\)|[0-9]{3,4}\-)[0-9]{7,8}$/.test(value);
        },
        message: '电话号码输入有误，匹配格式：区号3位或4位，号码7位或8位，区号与电话号码之间用小括号或"-"隔开.例如:"0791-88888888"'
    },
    mobileOrPhone: {//手机号码或固定电话验证
        validator: function(value, param){
            return (/^(13|15|18)\d{9}$/i.test(value)) || (/^(\([0-9]{3,4}\)|[0-9]{3,4}\-)[0-9]{7,8}$/.test(value));
        },
        message: '手机格式:以13、15、18开头的11位号码; 电话格式:区号3位或4位,号码7位或8位,区号与号码之间用小括号或"-"隔开.'
    },
    empty: {
        validator: function (value, param) {
            return /^\S+$/gi.test(value);
        },
        message: '不允许有空格.'

    },
    legalInput: {//合法输入
        validator: function (value, param) {
            return /^[\u0391-\uFFE5\w]+$/.test(value);
        },
        message: '只允许输入汉字、英文字母、数字及下划线.'
    },
    safepass: {
        validator: function (value, param) {
            return safePassword(value);
        },
        message: '密码由字母和数字组成，至少6位.'
    },
    safepass_yc: {
        validator: function (value, param) {
            return safePassword_yc(value);
        },
        message: '密码由数字、字母、符号混排，至少8位.'
    },
    equalTo: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '两次输入的字符不一至.'
    },
    idcard: {
        validator: function (value, param) {
            return idCard(value);
        },
        message:'请输入正确的身份证号码.'
    },
    comboboxRequired: {
        validator: function (value, param) {
            var currVar = $(param[0]).combobox('getValue');
            if( "" == currVar){
                return false;
            }
            var data = $(param[0]).combobox('getData');
            if(data == null || data.length == 0){
                return false;
            }
            var i =0;
            while(i<data.length){
                if(currVar == data[i].value)
                    return true;
                i++;
            }
            if(i == data.length){
                return false;
            }
            return true;
        },
        message:"请选择..."
    },
    combotreeRequired: {
        validator: function (value, param) {
            var currVar = $(param[0]).combotree('getValue');
            if( "" == currVar){
                return false;
            }
            var tree = $(param[0]).combotree('tree');
            var target = undefined;
            if(tree.tree('getRoot') != undefined){
                target = tree.tree('getRoot').target;
            }
            var data = tree.tree('getData',target);
            if(data == null || data.length == 0){
                return false;
            }
            var i =0;
            while(i<data.length){
                if(currVar == data[i].value)
                    return true;
                i++;
            }
            if(i == data.length){
                return false;
            }
            return true;
        },
        message:"请选择..."
    },
    combogridRequired: {
        validator: function (value, param) {
            var currVar = $(param[0]).combogrid('getValue');
            if( "" == currVar){
                return false;
            }
            var datagrid = $(param[0]).combogrid('grid');
            var target = undefined;
            var data = datagrid.datagrid('getData').rows;
            if(data == null || data.length == 0){
                return false;
            }
            var i =0;
            while(i<data.length){
                if(currVar == data[i].id)
                    return true;
                i++;
            }
            if(i == data.length){
                return false;
            }
            return true;
        },
        message:"请选择..."
    }
});

/* 密码由字母和数字组成，至少6位 */
var safePassword = function (value) {
    return !(/^(([A-Z]*|[a-z]*|\d*|[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,5})$|\s/.test(value));
};


/* 密码由数字、字母、符号混排，至少8位 */
var safePassword_yc = function (value) {
    return !(/^(([A-Z]*|[a-z]*|\d*|[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,7})$|\s/.test(value));
};

var idCard = function (value) {
    if (value.length == 18 && 18 != value.length) return false;
    var number = value.toLowerCase();
    var d, sum = 0, v = '10x98765432', w = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2], a = '11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91';
    var re = number.match(/^(\d{2})\d{4}(((\d{2})(\d{2})(\d{2})(\d{3}))|((\d{4})(\d{2})(\d{2})(\d{3}[x\d])))$/);
    if (re == null || a.indexOf(re[1]) < 0) return false;
    if (re[2].length == 9) {
        number = number.substr(0, 6) + '19' + number.substr(6);
        d = ['19' + re[4], re[5], re[6]].join('-');
    } else d = [re[9], re[10], re[11]].join('-');
    if (!isDateTime.call(d, 'yyyy-MM-dd')) return false;
    for (var i = 0; i < 17; i++) sum += number.charAt(i) * w[i];
    return (re[2].length == 9 || number.charAt(17) == v.charAt(sum % 11));
};

var isDateTime = function (format, reObj) {
    format = format || 'yyyy-MM-dd';
    var input = this, o = {}, d = new Date();
    var f1 = format.split(/[^a-z]+/gi), f2 = input.split(/\D+/g), f3 = format.split(/[a-z]+/gi), f4 = input.split(/\d+/g);
    var len = f1.length, len1 = f3.length;
    if (len != f2.length || len1 != f4.length) return false;
    for (var i = 0; i < len1; i++) if (f3[i] != f4[i]) return false;
    for (var i = 0; i < len; i++) o[f1[i]] = f2[i];
    o.yyyy = s(o.yyyy, o.yy, d.getFullYear(), 9999, 4);
    o.MM = s(o.MM, o.M, d.getMonth() + 1, 12);
    o.dd = s(o.dd, o.d, d.getDate(), 31);
    o.hh = s(o.hh, o.h, d.getHours(), 24);
    o.mm = s(o.mm, o.m, d.getMinutes());
    o.ss = s(o.ss, o.s, d.getSeconds());
    o.ms = s(o.ms, o.ms, d.getMilliseconds(), 999, 3);
    if (o.yyyy + o.MM + o.dd + o.hh + o.mm + o.ss + o.ms < 0) return false;
    if (o.yyyy < 100) o.yyyy += (o.yyyy > 30 ? 1900 : 2000);
    d = new Date(o.yyyy, o.MM - 1, o.dd, o.hh, o.mm, o.ss, o.ms);
    var reVal = d.getFullYear() == o.yyyy && d.getMonth() + 1 == o.MM && d.getDate() == o.dd && d.getHours() == o.hh && d.getMinutes() == o.mm && d.getSeconds() == o.ss && d.getMilliseconds() == o.ms;
    return reVal && reObj ? d : reVal;
    function s(s1, s2, s3, s4, s5) {
        s4 = s4 || 60, s5 = s5 || 2;
        var reVal = s3;
        if (s1 != undefined && s1 != '' || !isNaN(s1)) reVal = s1 * 1;
        if (s2 != undefined && s2 != '' && !isNaN(s2)) reVal = s2 * 1;
        return (reVal == s1 && s1.length != s5 || reVal > s4) ? -10000 : reVal;
    }
};
