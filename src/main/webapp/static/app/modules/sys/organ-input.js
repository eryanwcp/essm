var modelId = modelId;
var modelStatus = modelStatus;

var organType_combobox;
var $_parent_combotree;
var organTypeUrl = ctxAdmin + '/sys/organ/organTypeCombobox?1=1';
$(function() {
    loadParent();
    if(modelId == ""){  //新增
        setSortValue();
        $("input[name=status]:eq(0)").prop("checked",'checked');//状态 初始化值
    }else{
        $('input[name=status][value='+modelStatus+']').prop("checked",'checked');
    }
});
//加载父级机构
function loadParent(){
    $_parent_combotree = $('#_parentId').combotree({
        url:ctxAdmin + '/sys/organ/parentOrgan?selectType=select',
        multiple:false,//是否可多选
        editable:false,//是否可编辑
        valueField:'id',
        onChange:function(newValue, oldValue){
            //防止自关联
            if($('#id').val() && newValue == $('#id').val()){
                eu.showMsg('不允许设置上级机构为自己,请重新选择!');
                //$(this).combotree('setValue','');
                window.setTimeout(function(){$_parent_combotree.combotree('reset')},200);
            }
        },
        onBeforeLoad:function(node,param){
            param.id = modelId;
        },
        onSelect:function(node){
            //上级机构类型 机构：0 部门：1  小组：2
            var parentId = node['id'];
            if(parentId != undefined){
                organType_combobox.combobox('reload',organTypeUrl+"&parentId="+parentId);
            }
        },
        onLoadSuccess:function(){
            var parentId = $_parent_combotree.combotree("getValue");
            loadType(parentId);
        }

    });
}

//加载机构类型
function loadType(parentId){
    organType_combobox = $('#type').combobox({
        url:organTypeUrl+"&parentId="+parentId,
        multiple:false,//是否可多选
        editable:false
    });
}

//设置排序默认值
function setSortValue() {
    $.get(ctxAdmin + '/sys/organ/maxSort', function(data) {
        if (data.code == 1) {
            $('#orderNo').numberspinner('setValue',data.obj+30);
        }
    }, 'json');
}