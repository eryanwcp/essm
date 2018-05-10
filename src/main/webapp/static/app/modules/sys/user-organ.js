var defaultOrganComboboxData = defaultOrganComboboxData;
var organIds = organIds;
var defaultOrganId = defaultOrganId;

var $organs_combotree;
var $defaultOrgan_combobox;
$(function() {
    loadOrgan();
    initdDefaultOrgan();
});

//加载默认组织机构
function initdDefaultOrgan(){
    $defaultOrgan_combobox =  $("#defaultOrganId").combobox({
        editable:false,
        data:defaultOrganComboboxData,
        value:defaultOrganId
    });
}


function loadOrgan(){
    var isChange = false; //标识所属组织机构是否变更
    $organs_combotree = $("#organIds").combotree({
        url:ctxAdmin + '/sys/organ/tree?dataScope=2&cascade=true',
        multiple:true,//是否可多选
        editable:false,
        cascadeCheck:false,
        value:organIds,
        onHidePanel:function(){
            if(isChange){
                var selectionsNodes = $(this).combotree("tree").tree("getChecked");
                var defaultOrganData = new Array();
                var defaultOrganValues = $defaultOrgan_combobox.combobox("getValues");
                var defaultOrganTempValues = new Array();
                $.each(selectionsNodes,function(index, node) {
                    defaultOrganData.push({"value":node.id,"text":node.text});
                    $.each(defaultOrganValues,function(index, value) {
                        if(node.id == value){
                            defaultOrganTempValues.push(value);
                        }
                    });
                });
                //默认机构 默认值：第一个机构
                if(defaultOrganTempValues.length ==0 && selectionsNodes !=null && selectionsNodes.length >0){
                    defaultOrganTempValues.push(selectionsNodes[0].id);
                }
                $defaultOrgan_combobox.combobox("setValues",defaultOrganTempValues);
                $defaultOrgan_combobox.combobox("loadData",defaultOrganData);
            }
        },
        onChange:function(newValue, oldValue){
            if((newValue.length != 0 || oldValue.length != 0) && newValue.toString() != oldValue.toString()){
                isChange = true;
            }else{
                isChange = false;
            }
        }
    });
}