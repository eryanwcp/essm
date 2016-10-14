package com.eryansky.modules.sys.web.demo;

import com.eryansky.core.excelTools.*;
import com.eryansky.modules.sys.web.demo.module.DataCount;
import com.eryansky.modules.sys.web.demo.module.People;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-31 20:07
 */
@Controller
@RequestMapping("${adminPath}/sys/demo/export")
@SuppressWarnings("unchecked")
public class ExportController {

    /**
     * 数组数据样例
     * @return
     */
    private List<Object[]> getData(){
        List<Object[]> list = new ArrayList<Object[]>();
        for(int i=1;i<100;i++){
            list.add(new Object[]{i,"童鞋"+i,"你猜","K型","阴阳路"+i+"号"});
        }
        return list;
    }

    /**
     * Bean数据样例
     * @return
     */
    private List<People> getBeanData(){
        List<People> list = new ArrayList<People>();
        for(int i=1;i<100;i++){
            People p = new People();
            p.setCode(i);
            p.setName("童鞋"+i);
            p.setAddr("阴阳路"+i+"号");
            p.setBlood("K型");
            p.setSex("你猜");
            p.setBirthday(new Date());
            list.add(p);
        }
        return list;
    }

    /**
     * Map数据样例
     * @return
     */
    private List<Map> getMapData(){
        List<Map> list = new ArrayList<Map>();
        for(int i=1;i<100;i++){
            Map map = new HashMap();
            map.put("code", i);
            map.put("name", "童鞋"+i);
            map.put("sex", "你猜");
            map.put("blood", "K型");
            map.put("addr", "阴阳路"+i+"号");
            list.add(map);
        }
        return list;
    }

    /**
     * 表格数据查询
     * @return
     */
    @RequestMapping("loadData")
    @ResponseBody
    public List<People> loadData(){
        return getBeanData();
    }

    /**
     * 普通Excel导出，获取的数据格式是List<JavaBean>
     * @return
     * @throws Exception
     */
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=GBK");

        List<People> list = getBeanData();//获取数据

        String title = "普通Excel表";
        String[] hearders = new String[] {"编号", "姓名", "性别", "血型", "地址", "生日"};//表头数组
        String[] fields = new String[] {"code", "name", "sex", "blood", "addr", "birthday"};//People对象属性数组
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(hearders),fields);
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, "admin", td);
    }

    /**
     * 合并列表头Excel导出，获取的数据格式是List<Map>
     * @return
     * @throws Exception
     */
    @RequestMapping("spanExport")
    public void spanExport(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=GBK");
        List<Map> list = getMapData();//获取数据

        String title = "合并表头Excel表";
        String[] parents = new String[] {"", "基本信息", ""};//父表头数组
        String[][] children = new String[][] {
                new String[]{"编号"},
                new String[]{"姓名", "性别", "血型"},
                new String[]{"地址"}};//子表头数组
        String[] fields = new String[] {"code", "name", "sex", "blood", "addr"};//People对象属性数组
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(parents,children),fields);
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, "admin", td);
    }

    /**
     * 多Sheet Excel导出，获取的数据格式是List<Object[]>
     * @return
     * @throws Exception
     */
    @RequestMapping("sheetsExport")
    public void exportSheets(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=GBK");
        List<Object[]> list = getData();

        List<TableData> tds = new ArrayList<TableData>();

        //Sheet1
        String[] parents = new String[] {"", "基本信息", ""};//父表头数组
        String[][] children = new String[][] {
                new String[]{"编号"},
                new String[]{"姓名", "性别", "血型"},
                new String[]{"地址"}};//子表头数组
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(parents,children),null);
        td.setSheetTitle("合并表头示例");
        tds.add(td);

        //Sheet2
        String[] hearders = new String[] {"编号", "姓名", "性别", "血型", "地址"};//表头数组
        td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(hearders),null);
        td.setSheetTitle("普通表头示例");
        tds.add(td);

        String title = "多Sheet表";
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, "admin", tds);

    }

    /**
     * 表格数据查询
     * @return
     */
    @RequestMapping("loadRowSpanData")
    @ResponseBody
    public List<DataCount> loadRowSpanData(){
        List<DataCount> list = new ArrayList<DataCount>();//获取数据
        list.add(new DataCount("广东","广州","天河",131,121,14));
        list.add(new DataCount("广东","广州","海珠",53,44,9));
        list.add(new DataCount("广东","清远","小市",20,53,13));
        list.add(new DataCount("湖南","湘潭","北区",64,22,22));
        list.add(new DataCount("浙江","杭州","南区",123,78,12));
        list.add(new DataCount("浙江","杭州","西区",142,54,12));
        return list;
    }

    /**
     * 行合并Excel导出，获取的数据格式是List<Object[]>
     * @return
     * @throws Exception
     */
    @RequestMapping("rowSpanExport")
    public void exportRowSpan(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=GBK");

        List<DataCount> list = loadRowSpanData();

        String title = "行合并Excel表";
        String[] hearders = new String[] {"省份", "地市", "区县", "流浪狗数量", "流浪猫数量", "流浪汉数量"};//表头数组
        String[] fields = new String[] {"province", "city", "area", "dog", "cat", "people"};//People对象属性数组
        int spanCount = 3;//需要合并的列数。从第1列开始到指定列。
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(hearders,spanCount),fields);
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, "admin", td);
    }

    /**
     * 带小计Excel导出，获取的数据格式是List<Object[]>
     * 		目前支持：max(最大值)、min(最小值)、avg(平均)、sum(求和)、count(求行数)四种统计方式
     * @return
     * @throws Exception
     */
    @RequestMapping("exportTotal")
    public void exportTotal(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=GBK");

        List<DataCount> list = loadRowSpanData();

        String title = "带小计Excel表";
        String[] hearders = new String[] {"省份", "地市", "区县", "流浪狗数量", "流浪猫数量", "流浪汉数量"};//表头数组
        String[] fields = new String[] {"province", "city", "area", "dog", "cat", "people"};//People对象属性数组

        TableData td = ExcelUtils.createTableData(list, getTableHeader(hearders),fields);
        td.compute();//执行小计计算
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, "admin", td);
    }

    private TableHeaderMetaData getTableHeader(String[] hearders){
        int spanCount = 3;//需要合并的列数。从第1列开始到指定列。如不需要合并，则将该行和237、238行去掉即可

        //创建表头对象
        TableHeaderMetaData headMeta = new TableHeaderMetaData();
        for(int i=0;i<hearders.length;i++){
            TableColumn tc = new TableColumn();
            tc.setDisplay(hearders[i]);

            //设置按指定列统计
            if(i==0)//按第1列省份进行小计
                tc.setDisplaySummary(true);

            //设置统计目标列的统计方式，目前支持：max、min、avg、sum
            if(i==3)//流浪狗数量进行求最大
                tc.setAggregateRule("max");
            if(i==4)//流浪猫数量进行求平均
                tc.setAggregateRule("avg");
            if(i==5)//流浪汉数量进行求和
                tc.setAggregateRule("sum");

            if(i<spanCount)//前3列行合并
                tc.setGrouped(true);
            headMeta.addColumn(tc);
        }
        return headMeta;
    }

    /**
     * 导出Zip文件，zip文件中包含多个excel文件，对待大数据量文件导出（测试50W数据，6个字段）
     * 		默认一个Excel文件10W数据，一个sheet2W数据
     * 		该设置可以修改JsGridReportBase的静态变量
     * @return
     * @throws Exception
     */
    @RequestMapping("exportZip")
    public void exportZip(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<DataCount> list = new ArrayList<DataCount>();//获取数据
        // 这里的数据集查询的话就看个人具体情况了，比如可以分页多次查询
        for(int i=0;i<500000;i++){
            list.add(new DataCount("广东"+i,"广州"+i,"天河"+i,i,i,i));
        }

        String title = "ZIP大数据量Excel导出";//ZIP包名称
        ZipOutputStream zout = ExcelUtils.createZipStream(response,title); //创建压缩输出流

        String[] hearders = new String[] {"省份", "地市", "区县", "流浪狗数量", "流浪猫数量", "流浪汉数量"};//表头数组
        String[] fields = new String[] {"province", "city", "area", "dog", "cat", "people"};//People对象属性数组

        ExcelBean bean = new ExcelBean();
        bean.setTitle(title);
        bean.setCreator("admin");
        bean.setList(list);
        bean.setHearders(hearders);
        bean.setFields(fields);
        try {
            JsGridReportBase report = new JsGridReportBase(request, response);
            report.exportToExcel(zout,bean);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zout.close();// 关闭压缩输出流
        }
    }
}
