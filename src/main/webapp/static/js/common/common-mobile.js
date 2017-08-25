/*! 通用公共方法
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
$(document).ready(function() {
	try{
		// 链接去掉虚框
		$("a").bind("focus",function() {
			if(this.blur) {this.blur()};
		});
	}catch(e){
		// blank
	}
});

// 引入js和css文件
function include(id, path, file){
	if (document.getElementById(id)==null){
		var files = typeof file == "string" ? [file] : file;
		for (var i = 0; i < files.length; i++){
			var name = files[i].replace(/^\s|\s$/g, "");
			var att = name.split('.');
			var ext = att[att.length - 1].toLowerCase();
			var isCSS = ext == "css";
			var tag = isCSS ? "link" : "script";
			var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
			var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
			document.write("<" + tag + (i==0?" id="+id:"") + attr + link + "></" + tag + ">");
		}
	}
}

// 获取URL地址参数
function getQueryString(name, url) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	if (!url || url == ""){
		url = window.location.search;
	}else{
		url = url.substring(url.indexOf("?"));
	}
	r = url.substr(1).match(reg)
	if (r != null) return unescape(r[2]); return null;
}

//获取字典标签
function getDictLabel(data, value, defaultValue){
	for (var i=0; i<data.length; i++){
		var row = data[i];
		if (row.value == value){
			return row.label;
		}
	}
	return defaultValue;
}

// 打开一个窗体
function windowOpen(url, name, width, height){
	var top=parseInt((window.screen.height-height)/2,10),left=parseInt((window.screen.width-width)/2,10),
		options="location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,"+
			"resizable=yes,scrollbars=yes,"+"width="+width+",height="+height+",top="+top+",left="+left;
	window.open(url ,name , options);
}

// 恢复提示框显示
function resetTip(){
	top.$.jBox.tip.mess = null;
}

// 关闭提示框
function closeTip(){
	top.$.jBox.closeTip();
}

//显示提示框
function showTip(mess, type, timeout, lazytime){
	resetTip();
	setTimeout(function(){
		top.$.jBox.tip(mess, (type == undefined || type == '' ? 'info' : type), {opacity:0,
			timeout:  timeout == undefined ? 2000 : timeout});
	}, lazytime == undefined ? 500 : lazytime);
}

// 显示加载框
function loading(mess){
	if (mess == undefined || mess == ""){
		mess = "正在提交，请稍等...";
	}
	resetTip();
	top.$.jBox.tip(mess,'loading',{opacity:0});
}

// 警告对话框
function alertx(mess, closed){
	top.$.jBox.info(mess, '提示', {closed:function(){
		if (typeof closed == 'function') {
			closed();
		}
	}});
	top.$('.jbox-body .jbox-icon').css('top','55px');
}

// 确认对话框
function confirmx(mess, href, closed){
	top.$.jBox.confirm(mess,'系统提示',function(v,h,f){
		if(v=='ok'){
			if (typeof href == 'function') {
				href();
			}else{
				resetTip(); //loading();
				location = href;
			}
		}
	},{buttonsFocus:1, closed:function(){
		if (typeof closed == 'function') {
			closed();
		}
	}});
	top.$('.jbox-body .jbox-icon').css('top','55px');
	return false;
}

// 提示输入对话框
function promptx(title, lable, href, closed){
	top.$.jBox("<div class='form-search' style='padding:20px;text-align:center;'>" + lable + "：<input type='text' id='txt' name='txt'/></div>", {
		title: title, submit: function (v, h, f){
			if (f.txt == '') {
				top.$.jBox.tip("请输入" + lable + "。", 'error');
				return false;
			}
			if (typeof href == 'function') {
				href();
			}else{
				resetTip(); //loading();
				location = href + encodeURIComponent(f.txt);
			}
		},closed:function(){
			if (typeof closed == 'function') {
				closed();
			}
		}});
	return false;
}

// 添加TAB页面
function addTabPage(title, url, closeable, $this, refresh){
	top.$.fn.jerichoTab.addTab({
		tabFirer: $this,
		title: title,
		closeable: closeable == undefined,
		data: {
			dataType: 'iframe',
			dataLink: url
		}
	}).loadData(refresh != undefined);
}

// cookie操作
function cookie(name, value, options) {
	if (typeof value != 'undefined') { // name and value given, set cookie
		options = options || {};
		if (value === null) {
			value = '';
			options.expires = -1;
		}
		var expires = '';
		if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
			var date;
			if (typeof options.expires == 'number') {
				date = new Date();
				date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
			} else {
				date = options.expires;
			}
			expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
		}
		var path = options.path ? '; path=' + options.path : '';
		var domain = options.domain ? '; domain=' + options.domain : '';
		var secure = options.secure ? '; secure' : '';
		document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
	} else { // only name given, get cookie
		var cookieValue = null;
		if (document.cookie && document.cookie != '') {
			var cookies = document.cookie.split(';');
			for (var i = 0; i < cookies.length; i++) {
				var cookie = jQuery.trim(cookies[i]);
				// Does this cookie string begin with the name we want?
				if (cookie.substring(0, name.length + 1) == (name + '=')) {
					cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
					break;
				}
			}
		}
		return cookieValue;
	}
}

// 数值前补零
function pad(num, n) {
	var len = num.toString().length;
	while(len < n) {
		num = "0" + num;
		len++;
	}
	return num;
}

// 转换为日期
function strToDate(date){
	return new Date(date.replace(/-/g,"/"));
}

// 日期加减
function addDate(date, dadd){
	date = date.valueOf();
	date = date + dadd * 24 * 60 * 60 * 1000;
	return new Date(date);
}

//截取字符串，区别汉字和英文
function abbr(name, maxLength){
	if(!maxLength){
		maxLength = 20;
	}
	if(name==null||name.length<1){
		return "";
	}
	var w = 0;//字符串长度，一个汉字长度为2
	var s = 0;//汉字个数
	var p = false;//判断字符串当前循环的前一个字符是否为汉字
	var b = false;//判断字符串当前循环的字符是否为汉字
	var nameSub;
	for (var i=0; i<name.length; i++) {
		if(i>1 && b==false){
			p = false;
		}
		if(i>1 && b==true){
			p = true;
		}
		var c = name.charCodeAt(i);
		//单字节加1
		if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {
			w++;
			b = false;
		}else {
			w+=2;
			s++;
			b = true;
		}
		if(w>maxLength && i<=name.length-1){
			if(b==true && p==true){
				nameSub = name.substring(0,i-2)+"...";
			}
			if(b==false && p==false){
				nameSub = name.substring(0,i-3)+"...";
			}
			if(b==true && p==false){
				nameSub = name.substring(0,i-2)+"...";
			}
			if(p==true){
				nameSub = name.substring(0,i-2)+"...";
			}
			break;
		}
	}
	if(w<=maxLength){
		return name;
	}
	return nameSub;
}

// 添加收藏
function addFavorite(sURL, sTitle){
	if(!sTitle){sTitle = document.title;}
	try{
		window.external.addFavorite(sURL, sTitle);
	}catch (e){
		try{
			window.sidebar.addPanel(sTitle, sURL, "");
		}catch (e){
			alert("加入收藏失败，请使用Ctrl+D进行添加");
		}
	}
}

// 自动滚动：setInterval("autoScroll('.jcarousellite')",3000);
function autoScroll(obj){
	var height = $(obj).find("ul:first li:first").height()+3;
	$(obj).find("ul:first").animate({marginTop:"-" + height + "px"},'slow',function(){
		$(this).css({marginTop:"0px"}).find("li:first").appendTo(this);
	});
}

//是否支持canvas
function isCanvasSupported(){
	var elem = document.createElement('canvas');
	return !!(elem.getContext && elem.getContext('2d'));
}

var U = {
	/**
	 *
	 * @param conf 配置文件
	 *     file 文件对象 quality：压缩比例 默认1.0 callback 成功回调函数 error 失败回调函数
	 */
	uploadImageFile:function(conf){
		if (typeof FileReader == "undefined" || ($.browser.safari && navigator.userAgent.match(/AppleWebKit\/(\d+)/).pop() < 534)) {
			U.uploadImageFile2(conf);
			return false;
		}

		var quality = conf['quality'];
		var maxWidth = conf['maxWidth'];
		var maxHeight = conf['maxHeight'];
		var callback = conf['callback'];
		var error = conf['error'];

		var fs = conf['file'].target.files;
		$.each(fs,function(i,f){
			var reader = new FileReader();
			reader.onload = function(e){
				var data = e.target.result;
				if (data.lastIndexOf('data:base64') != -1) {
					data = data.replace('data:base64', 'data:image/jpeg;base64');
				} else if (data.lastIndexOf('data:,') != -1) {
					data = data.replace('data:,', 'data:image/jpeg;base64,');
				}
				if(isCanvasSupported()){
					U.compressImg(data,quality,function(data){//压缩完成后执行的callback
						U.ajaxUploadBase64ImageFile(data,callback);
					},maxWidth,maxHeight);
				}else{
					var eMsg = "您的客户端不支持文件上传";
					if(error){
						error(eMsg);
					}
				}

			};
			reader.onerror = function(){
				var eMsg = "上传失败";
				if(error){
					error(eMsg);
				}
			};
			reader.readAsDataURL(f);
		});

	},
	/**
	 *
	 * @param conf 配置文件
	 *     file 文件对象 callback 成功回调函数 error 失败回调函数
	 */
	uploadImageFile2:function(conf) {
		var fs = conf['file'].target.files;
		$.each(fs,function(i,f){
			var f = conf['file'].target.files[0];
			var quality = conf['quality'];
			var callback = conf['callback'];
			var error = conf['error'];
			U.ajaxUploadImageFile(f,callback,error);
		});

	},
	compressImg:function(imgData,quality,onCompress,maxWidth,maxHeight){
		if(!imgData)return;
		onCompress = onCompress || function(){};
		var canvas = document.createElement('canvas');
		var img = new Image();
		img.onload = function(){
			var ctx = canvas.getContext("2d");
			ctx.clearRect(0, 0, canvas.width, canvas.height); // canvas清屏
			//重置canvans宽高
			var _maxWidth = maxWidth || img.width;
			var _maxHeight = maxHeight || img.height;
			if (img.width > _maxWidth || img.height > _maxHeight) {
				var bili = Math.max(img.width / _maxWidth, img.height / _maxHeight);
				canvas.width = img.width / bili;
				canvas.height = img.height / bili;
			} else {
				canvas.width = img.width;
				canvas.height = img.height;
			}
			ctx.drawImage(img, 0, 0, img.width, img.height,0,0,canvas.width,canvas.height); // 将图像绘制到canvas上
			onCompress(canvas.toDataURL("image/jpeg",quality || 1.0));
		};
		img.src = imgData;
	},
	ajaxUploadBase64ImageFile: function (base64Data,callback,error) {
		var url = appURL + "/m/base64ImageUpLoad";
		$.ajax({
			url:url,
			type:"post",
			data:{base64Data:base64Data},
			dataType:"json",
			success:function(data){
				if(data['code'] ==1){
					if(callback){
						callback(base64Data,data['obj']);
					}
				}else{
					if(error){
						error(data['msg']);
					}
				}
			}
		});
	},
	ajaxUploadImageFile: function (f,callback,error) {
		var url = appURL + "/m/imageUpLoad";
		var formData  = new FormData();   //这里连带form里的其他参数也一起提交了,如果不需要提交其他参数可以直接FormData无参数的构造函数
		formData .append("uploadFile",f);  //append函数的第一个参数是后台获取数据的参数名,和html标签的input的name属性功能相同
		$.ajax({
			url : url,
			type : "POST",
			data : formData ,
			dataType:"json",
			processData : false,         // 告诉jQuery不要去处理发送的数据
			contentType : false,        // 告诉jQuery不要去设置Content-Type请求头
			success:function(data){
				if(data['code'] ==1){
					if(callback){
						callback(data['obj']['data'],data['obj']['file']);
					}
				}else{
					if(error){
						error(data['msg']);
					}
				}
			}

		});
	}
}
