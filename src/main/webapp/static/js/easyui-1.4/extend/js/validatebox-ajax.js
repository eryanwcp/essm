/*!
 * 服务端异步校验
 */
$.extend($.fn.validatebox.defaults.rules, {
	ajax_exist:{//通用异步校验
		validator:function(value,param){     
            return ajax_exist(value,param);   
        },     
        message:'该数据项已存在.' 
	},
	user_loginName:{ //用户登录名 
        validator:function(value,param){     
            return user_loginName(value,param);   
        },     
        message:'该用户已存在.'   
    },
	menu_name:{ //[菜单管理]名称    
        validator:function(value,param){     
            return menu_name(value,param);   
        },     
        message:'该菜单名称已被使用.'   
    }
});
/**
 * param fieldId,entityName,fieldName
 */
var ajax_exist = function(value,param){
	var returnflag = ajax_check(param[1],param[2],value,param);
	return returnflag;
}
//用户登录名 
var user_loginName = function(value,param){
	var returnflag = ajax_check('User','loginName',value,param);
	return returnflag;
}
//[菜单管理]名称
var menu_name = function(value,param){
	var returnflag = ajax_check('Resource','name',value,param);
	return returnflag;
}

//共通方法
var ajax_check = function(entityName,fieldName,fieldVlaue,param){
	//获取编辑页面的数据主键
	var rowId = null;
	if(param!=null){
		 var obid_id = param[0];
		 rowId = $(obid_id).val();
	}else{
		rowId = $("input[name='id']").val();
	}
	var check_flag;
	$.ajax({
		url : '${ctx}/common/fieldCheck',
		async:false,//同步ajax
		data : {
			entityName : entityName,
			fieldName : fieldName,
			fieldValue: fieldVlaue,
			rowId   : rowId
		},
		dataType : 'json',
		success : function(data) {
			if(data.code==1){
				check_flag = data.obj;
			}
		}
	});
	return check_flag;
}