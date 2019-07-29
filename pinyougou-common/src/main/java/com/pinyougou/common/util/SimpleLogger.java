package com.pinyougou.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SimpleLogger {
    private static ConsoleHandler consoleHandler;

    private SimpleLogger() {}

    static {
        Formatter loggerFormatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String timeStr = sdf.format(new Date());
                String level = record.getLevel().getName();
                String thread = "Thread-" + record.getThreadID();
                String loggerName = record.getLoggerName();
                String sourceMethodName = record.getSourceMethodName();
                String sourceClassName = record.getSourceClassName();
                String message = record.getMessage();
                String result;

                result = String.format("[%s][%s][%10s][%s] %s %s >>>\n[LOG] %s\n",
                    timeStr, level, thread, loggerName, sourceClassName, sourceMethodName,
                    message);

                return result;
            }
        };

        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(loggerFormatter);
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger;

        logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);

        return logger;
    }
}
