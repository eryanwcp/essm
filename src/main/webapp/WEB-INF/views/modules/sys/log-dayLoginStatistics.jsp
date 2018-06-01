<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>每日登陆次数分析</title>
    <meta name="decorator" content="default_sys"/>
    <%@ include file="/common/highstock.jsp"%>
    <script type="text/javascript">
        $(function () {
            GetseriesValue();  //获取数据源信息
        });

        function GetseriesValue(){

            $.ajax({
                url: ctxAdmin + '/sys/log/report/dayLoginStatisticsData',
                type: 'post',
                dataType:'json',
                success: function (data) {
                    var a = data["obj"];
                    var jsonArray=new Array();
                    for(var o in a){
                        jsonArray.push([a[o].loginDate,a[o].count]);
                    }
                    GetData(jsonArray);
                }
            });
        }

        function GetData(data) {
            Highcharts.setOptions({
                lang: {
                    rangeSelectorFrom: '从',
                    rangeSelectorTo: '到',
                    rangeSelectorZoom:'区域'
                }
            });
            $('#container').highcharts('StockChart',{
                rangeSelector: {
                    selected: 5,
                    enabled:true,
                    inputEnabled:true,
                    inputDateFormat : '%Y-%m-%d',
                    buttons: [{
                        type: 'day',
                        count: 7,
                        text: '周'
                    }, {
                        type: 'month',
                        count: 1,
                        text: '月'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '季度'
                    }, {
                        type: 'all',
                        text: '所有'
                    }]
                },
                title: {
                    text: '每日登陆次数分析'
                },
                series: [{
                    name: '人次',
                    data: data,
                    tooltip: {
                        valueDecimals: 2
                    }
                }],
                xAxis:{
                    type: 'datetime',
                    dateTimeLabelFormats: {
                        day: '%Y-%m-%d'
                    },
                    title : {
                        text:'日期'
                    }
                },
                yAxis:{
                    title:{
                        text:'人次'
                    }
                },
                tooltip:{
                    formatter: function() {
                        return "时间：" + Highcharts.dateFormat('%Y-%m-%d', this.x)  + "<br>"
                                + "人次：" +this.y;
                    }
                }
            });
        }
    </script>
</head>
<body>

    <div id="container" style="min-width:400px;height:400px"></div>
</body>
</html>


