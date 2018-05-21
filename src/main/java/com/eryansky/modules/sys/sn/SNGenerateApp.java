package com.eryansky.modules.sys.sn;

import java.util.HashMap;
import java.util.Map;

public class SNGenerateApp {
    private SNGeneratorEngine snGenEngine = SNGeneratorEngine.getInstance();


    public SNGenerateApp() {
        addBuildInGenerator();
    }

    /**
     * 设置内置的生成器
     */
    private void addBuildInGenerator() {
        snGenEngine.addGenerator(new CharacterSequenceGenerator());
        snGenEngine.addGenerator(new DateTimeGenerator());
        snGenEngine.addGenerator(new NumberSequenceGenerator());
        snGenEngine.addGenerator(new StringGenerator());
    }

    /**
     * 添加 Generator，提供扩展功能
     *
     * @param generator
     */
    public void addGenerator(IGenerator generator) {
        snGenEngine.addGenerator(generator);
    }

    /**
     * 生成序列号
     *
     * @param snFormatStr  流水号格式字符串
     * @param parameterMap 参数列表
     * @return
     */
    public String generateSN(String snFormatStr, Map parameterMap) {
        snGenEngine.setFormatStr(snFormatStr);
        return snGenEngine.generate(parameterMap);
    }

    public static void main(String[] args) {
        SNGenerateApp snGenerateApp = new SNGenerateApp();
        //设定流水号生成规则
        String snFormatStr = "Str@ 国办发〔#DateTime@yyyy#Str@〕#NumSeq@0C0#Str@ 号";
        Map parameterMap1 = new HashMap(); //设定参数
        parameterMap1.put(GeneratorConstants.PARAM_MODULE_CODE, "1"); //使用 sequence id 1 进行流水自增
        for(int i=1;i<=5;i++)//生成 5 个流水号
        {
            System.out.println("流水号"+i+":"+snGenerateApp.generateSN(snFormatStr,parameterMap1));
        }
    }
}