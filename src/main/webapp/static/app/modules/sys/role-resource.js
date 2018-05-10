var resourceComboboxData = resourceComboboxData;
var resourceIds = resourceIds;

var $resource_combotree;
$(function() {
    //级联点击事件
    $('#changeMode').click(function(){
        var tempData =  $resource_combotree.combotree('getValues');
        $resource_combotree.combotree({
            cascadeCheck:$(this).is(':checked'),
            onShowPanel:function(){
                var tree =  $(this).combotree("tree")
                var checkeNodes = tree.tree("getChecked");
                var tempValues = new Array();
                $.each(checkeNodes,function(index,nodeData){
                    tempValues.push(nodeData.id);
                });
                $resource_combotree.combotree("setValues",tempValues);
            }
        });
        $resource_combotree.combotree('setValues',tempData);
        $resource_combotree.combotree("showPanel");
    });
    loadResource();
});
//加载资源
function loadResource(){
    $resource_combotree = $('#resourceIds').combotree({
        data : resourceComboboxData,
        cascadeCheck : false,
        multiple : true
    });
    if(resourceIds){
        $resource_combotree.combotree('setValues',resourceIds);
    }
}