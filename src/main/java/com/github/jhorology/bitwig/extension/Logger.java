package com.github.jhorology.bitwig.extension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.bitwig.extension.controller.api.ControllerHost;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.function.Consumer;

public class Logger {
    public static final int TRACE = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    private static final String[] SEVERITIES = {"T","I","W","E"};
    private static final int COLUMN_SIZE = 94;
    private static final String INDENT_PREFIX = " > ";
    private static final int INDENTED_COLUMN_SIZE = COLUMN_SIZE - INDENT_PREFIX.length();
    
    private static ControllerHost host;
    private static int level;
    private static Consumer<String> std;
    private static Consumer<String> err;
    private Class<?> clazz;
    
    private Logger() {
    }

    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    static void init(ControllerHost host, int level) {
        Logger.host = host;
        Logger.level = level;
        std = (s) -> {host.println(s);};
        err = (s) -> {host.errorln(s);};
    }
    
    public boolean isTraceEnabled() {
        return level <= TRACE;
    }
    public boolean isInfoEnabled() {
        return level <= INFO;
    }
    public boolean isWarnEnabled() {
        return level <= WARN;
    }
    
    public void trace(String msg) {
        if (level <= TRACE) {
            log(createLogString(TRACE, msg), std);
        }
    }
    
    public void info(String msg) {
        if (level <= INFO) {
            log(createLogString(INFO, msg), std);
        }
    }
    
    public void warn(String msg) {
        if (level <= WARN) {
            log(createLogString(WARN, msg), std);
        }
    }

    public void error(String msg) {
        log(createLogString(ERROR, msg), err);
    }
    
    public void error(String msg, Throwable ex) {
        error(msg + "\n" + createStackTraceString(ex));
    }

    public void error(Throwable ex) {
        error(createStackTraceString(ex));
    }

    private String createStackTraceString(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
    
    private String createLogString(int severity, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        sb.append("|");
        sb.append(SEVERITIES[severity]);
        sb.append("|");
        sb.append(clazz.getName());
        sb.append("|");
        sb.append(Thread.currentThread().getName());
        sb.append("|");
        sb.append(msg);
        return sb.toString();
    }

    private void log(String msg, Consumer<String> out) {
        BufferedReader br = new BufferedReader(new StringReader(msg));
        boolean isFirstLine = true;
        try {
            String str = br.readLine();
            while(str != null) {
                while (str != null) {
                    if (isFirstLine) {
                        if (str.length() > COLUMN_SIZE) {
                            out.accept(str.substring(0, COLUMN_SIZE));
                            isFirstLine = false;
                            str = str.substring(COLUMN_SIZE);
                        } else {
                            out.accept(str);
                            isFirstLine = false;
                            break;
                        }
                    } else {
                        if (str.length() > INDENTED_COLUMN_SIZE) {
                            out.accept(INDENT_PREFIX + str.substring(0, INDENTED_COLUMN_SIZE));
                            str = str.substring(INDENTED_COLUMN_SIZE);
                        } else {
                            out.accept(INDENT_PREFIX + str);
                            break;
                        }
                    }
                }
                str = br.readLine();
            }
        } catch (IOException ex) {
        }
    }
}
