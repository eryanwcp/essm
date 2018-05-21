package com.eryansky.modules.sys.sn;

import java.util.HashMap;
import java.util.Map;

public class SNGeneratorEngine {
    private static SNGeneratorEngine snGeneratorEngine = new SNGeneratorEngine();
    private static Map<String, IGenerator> generatorMap = new HashMap<String, IGenerator>();
    /**
     * 流水号生成规则
     */
    private String formatStr;
    /**
     * 子序列间分隔符
     */
    private String splitChar = "#";
    /**
     * 子序列内部分隔符
     */
    private String innerChar = "@";
    /**
     * 流水号子序列生成规则
     */
    private String[] subFormatStr;

    /**
     * 按照类型和 Generator 实例存放
     *
     * @param Generator
     */
    public void addGenerator(IGenerator Generator) {
        generatorMap.put(Generator.getGeneratorType(), Generator);
    }

    private SNGeneratorEngine() {
    }

    public static SNGeneratorEngine getInstance() {
        return snGeneratorEngine;
    }

    /**
     * 接收流水号格式字符串，并分割成子序列
     *
     * @param formatStr
     */
    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
        subFormatStr = this.formatStr.split(splitChar);
    }

    /**
     * 生成流水号：分发给各个子序列 Generator 生成
     *
     * @param parameterMap
     * @return
     */
    public String generate(Map parameterMap) {
        StringBuffer seriableNumber = new StringBuffer();
        for (String format : subFormatStr) {
            seriableNumber.append(generateSubSN(format, parameterMap));
        }
        return seriableNumber.toString();
    }

    /**
     * 根据类型调用子序列 Generator 生成
     *
     * @param subFormatStr
     * @param parameterMap
     * @return
     */
    private String generateSubSN(String subFormatStr, Map parameterMap) {
        String[] innerSubStr = subFormatStr.split(innerChar);
        IGenerator Generator = this.getGenerator(innerSubStr[0]);
        return Generator.generate(innerSubStr[1], parameterMap);
    }

    /**
     * 根据 GeneratorType 获取 Generator 实例
     */
    private IGenerator getGenerator(String generatorType) {
        return generatorMap.get(generatorType);
    }
}