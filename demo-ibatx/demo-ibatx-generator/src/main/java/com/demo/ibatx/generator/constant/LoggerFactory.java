package com.demo.ibatx.generator.constant;

import com.demo.ibatx.ui.data.Logger;

import java.util.Objects;

public class LoggerFactory {

    private static Logger uiLogger;

    public static Logger getLogger() {
        if (Objects.nonNull(uiLogger)) {
            return uiLogger;
        }
        return new Logger() {
            @Override
            public void print(String log) {
                System.out.println(log);
            }
        };
    }


    public static void setLogger(Logger logger) {
        uiLogger = logger;
    }
}
