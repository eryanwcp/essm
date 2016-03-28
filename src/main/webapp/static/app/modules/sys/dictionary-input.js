var modelId = modelId;

$(function() {
    loadGroup();
    if(modelId==""){  //新增
        setSortValue();
    }
});
//加载分组
function loadGroup(){
    $('#_parentId').combobox({
        url:ctxAdmin + '/sys/dictionary/groupCombobox?selectType=select',
        multiple:false,//是否可多选
        editable:false,//是否可编辑
        onHidePanel:function(){
            //防止自关联
            if($('#id').val() != undefined && $(this).combobox('getValue') == $('#id').val()){
                $(this).combobox('setValue','');
            }
        }
    });
}

//设置排序默认值
function setSortValue() {
    $.get(ctxAdmin + '/sys/dictionary/maxSort', function(data) {
        if (data.code == 1) {
            $('#dictionary_orderNo').numberspinner('setValue',data.obj+30);
        }
    }, 'json');
}