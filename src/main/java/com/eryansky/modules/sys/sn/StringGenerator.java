package com.eryansky.modules.sys.sn;

import java.util.Map;

public class StringGenerator implements IGenerator {
    private static final String type = GeneratorConstants.STRING;

    @Override
    public String getGeneratorType() {
        return type;
    }

    @Override
    public String generate(String format, Map paraMap) {
        return format;
    }
}