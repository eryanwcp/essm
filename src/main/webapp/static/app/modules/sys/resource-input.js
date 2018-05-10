var modelId = modelId;
var modelStatus = modelStatus;
var modelType = modelType;

var $type_combobox;
var $_parent_combotree;
var typeUrl = ctxAdmin + '/sys/resource/resourceTypeCombobox';
$(function() {
    loadParent();
    loadIco();
    if(modelId == ""){  //新增
        setSortValue();
        $("input[name=status]:eq(0)").prop("checked",'checked');//状态 初始化值
    }else{
        $('input[name=status][value='+modelStatus+']').prop("checked",'checked');
    }
});

//加载父级资源
function loadParent(){
    $_parent_combotree = $('#_parentId').combotree({
        url:ctxAdmin + '/sys/resource/parent?selectType=select',
        multiple:false,//是否可多选
        editable:false,//是否可编辑
        valueField:'id',
        onBeforeLoad:function(node,param){
            param.id = modelId;
        },
        onSelect:function(node){
            //上级资源类型 菜单：0 功能：1  限制:如果上级是功能则下级只能是功能
            var parentId = node['id'];
            if(parentId != undefined ){
                $type_combobox.combobox('reload',typeUrl+"?parentId="+parentId);
            }
        },
        onLoadSuccess:function(){
            var parentId = $_parent_combotree.combotree("getValue");
            loadType(parentId);
        }

    });
}
//加载资源图标
function loadIco(){
    $('#iconCls').combobox({
        method:'get',
        url:ctxStatic + '/js/json/resource.json',
        multiple:false,//是否可多选
        editable:false,//是否可编辑
        formatter:function(row){
            return $.formatString('<span class="tree-icon tree-file {0}"></span>{1}', row.value, row.text);
        }
    });
}
//加载资源类型
function loadType(parentId){
    $type_combobox = $('#type').combobox({
        url:typeUrl+'?parentId='+parentId,
        multiple:false,
        editable:false,
        value:modelType
    });
}
//设置排序默认值
function setSortValue() {
    $.get(ctxAdmin + '/sys/resource/maxSort', function(data) {
        if (data.code == 1) {
            $('#sort').numberspinner('setValue',data.obj+30);
        }
    }, 'json');
}