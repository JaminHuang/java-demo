package com.demo.ibatx.ui.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logger {

    private ExecutorService executorService;

    private List<LoggerOut> loggerOuts;

    public Logger() {

        this.loggerOuts = new ArrayList<>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void print(String log) {
        System.out.println(log);
        this.printOut(log);
    }

    private void printOut(String log) {
        this.executorService.submit(() -> {
            this.loggerOuts.forEach(out -> out.out(log + "\n"));
        });
    }

    public void print(String log, Object... params) {
        System.out.println(String.format(log, params));
        this.printOut(String.format(log, params));
    }

    public void addLoggerOut(LoggerOut loggerOut) {
        this.loggerOuts.add(loggerOut);
    }

}
