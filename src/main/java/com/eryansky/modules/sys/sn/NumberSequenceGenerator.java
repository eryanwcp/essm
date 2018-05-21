package com.eryansky.modules.sys.sn;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;

import java.util.Map;

public class NumberSequenceGenerator implements IGenerator {
    private static final String type = GeneratorConstants.NUMBER_SEQUENCE;
    private String splitC = "C";

    @Override
    public String getGeneratorType() {
        return type;
    }


    @Override
    public String generate(String formatStr, Map paraMap) {
        String seqId = (String) paraMap.get(GeneratorConstants.PARAM_MODULE_CODE);
        //从数据库中获取当前数值，并自动加 1
        String maxSerial = SystemSerialNumberUtils.getMaxSerialByModuleCode(seqId);
        int seqNum = StringUtils.isBlank(maxSerial) ? 0 : Integer.parseInt(maxSerial);
        seqNum++;
        String[] charArray = formatStr.split(splitC);
        int seqNumLength = Integer.parseInt(charArray[0]);
        char prefixChar = charArray[1].charAt(0);
        if (seqNumLength == 0)
            return String.valueOf(seqNum);
        else
            return appendPrefixChar(seqNum, seqNumLength, prefixChar);
    }

    /**
     * 补足前缀以保证序列定长，如 0001, 保持 4 位，不足 4 位用'0'补齐
     *
     * @param seqNum       当前数值
     * @param seqNumLength 需要返回的字符串长度
     * @param prefixChar   用于补齐的前置字符串
     * @return
     */
    private String appendPrefixChar(int seqNum, int seqNumLength, char prefixChar) {
        String seqNumStr = String.valueOf(seqNum);
        for (int i = seqNumStr.length(); i < seqNumLength; i++) {
            seqNumStr = prefixChar + seqNumStr;
        }
        return seqNumStr;
    }
}