/**
 * 浏览器事件屏蔽.
 * 依赖jQuery.
 */
$(document).ready(function() {
	// 禁止右键
	$(document).bind("contextmenu", function() {
		return false;
	});
	// 禁止选择
	$(document).bind("selectstart", function() {
		return false;
	});
	// 禁止Ctrl+C 和Ctrl+A
	$(document).keydown(function(event) {
		
		if ((event.ctrlKey && event.which == 67) || (event.ctrlKey && event.which == 86)) {
			return false;
		}
		if ((event.ctrlKey) && (event.keyCode == 82)) {
			return false;
		}
		// 屏蔽F5刷新键
		if (event.keyCode == 116) {
			return false; 
		}
		//禁止退格删除键(Backspace)密码或单行、多行文本框除外.
	    if(banBackSpace(event) == false){
	    	return false;
	    }
		// 完全屏蔽退格删除键(Backspace)
		/*if (event.keyCode == 8) {
			return false; 
		}*/
		if ((event.altKey) && ((event.keyCode == 37) || // 屏蔽 Alt+ 方向键 ←
		(event.keyCode == 39))) // 屏蔽 Alt+ 方向键 →
		{
			event.returnValue = false;
			return false;
		}

	});
});
/**
 * 屏蔽回车键.
 * 禁止退格删除键(Backspace)密码或单行、多行文本框除外.
 * @param e 事件源
 * @returns {Boolean}
 */
function banBackSpace(e){     
    var ev = e || window.event;//获取event对象     
    var obj = ev.target || ev.srcElement;//获取事件源     
      
    var t = obj.type || obj.getAttribute('type');//获取事件源类型    
      
    //获取作为判断条件的事件类型  
    var vReadOnly = obj.getAttribute('readonly');  
    var vEnabled = obj.getAttribute('enabled');  
    //处理null值情况  
    vReadOnly = (vReadOnly == null) ? false : vReadOnly;  
    vEnabled = (vEnabled == null) ? true : vEnabled;  
    
    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，  
    //并且readonly属性为true或enabled属性为false的，则退格键失效  
    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")   
                && (vReadOnly==true || vEnabled!=true))?true:false; 
    
    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效  
    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")  
                ?true:false; 
    if(flag2){  
        return false;  
    }  
    if(flag1){     
        return false;     
    } 
}  