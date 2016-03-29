var organIds = organIds;
var isSystem = isSystem;

var $organ_combotree;
var $organIds_combotree;
$(function () {
    loadData();
    loadDataScope();
    //级联点击事件
    $('#changeMode').click(function(){
        var tempData =  $organIds_combotree.combotree('getValues');
        $organIds_combotree.combotree({
            cascadeCheck:$(this).is(':checked'),
            onShowPanel:function(){
                var tree =  $(this).combotree("tree")
                var checkeNodes = tree.tree("getChecked");
                var tempValues = new Array();
                $.each(checkeNodes,function(index,nodeData){
                    tempValues.push(nodeData.id);
                });
                $organIds_combotree.combotree("setValues",tempValues);
            }
        });
        $organIds_combotree.combotree('setValues',tempData);
        $organIds_combotree.combotree("showPanel");
    });
    $('input[name=isSystem][value='+isSystem+']').prop("checked",'checked');
    $("input[name='isSystem']").change(function(){
        var value = $(this).val();
        if("1" == value){
            $("#div_organId").hide();
            $organ_combotree.combotree('clear');
        }else{
            $("#div_organId").show();
        }
    });
});

function loadData() {
    $.ajax({
        url: ctxAdmin + '/sys/organ/tree?dataScope=2&cascade=true',
        type: 'post',
        data: {},
        traditional: true,
        dataType: 'json',
        success: function (data) {
            loadOrgan(data);
            loadOrganIds(data);
        }
    });
}

function loadOrgan(data) {
    $organ_combotree = $("#organId").combotree({
        data: data,
        missingMessage: '请选择所属机构.',
        multiple: false,
        editable: false
    });
}
function loadOrganIds(data) {
    $organIds_combotree = $("#organIds").combotree({
        data: data,
        cascadeCheck: false,
        multiple: true,
        editable: false,
        value: organIds
    });
}

function loadDataScope(data) {
    $("#dataScope").combobox({
        url: ctxAdmin + '/sys/role/dataScope',
        editable: false,
        onSelect: function (record) {
            var dataScope = record['value'];
            if("9" == dataScope){
                $("#div_organIds").show();
            }else{
                $("#div_organIds").hide();
                $organIds_combotree.combotree("clear");
            }
        }
    });
}