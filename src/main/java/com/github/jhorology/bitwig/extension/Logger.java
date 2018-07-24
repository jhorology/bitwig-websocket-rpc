/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.extension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.function.Consumer;

import com.bitwig.extension.controller.api.ControllerHost;

/**
 * A simple logging class for Controller Script Console. 
 */
public class Logger {
    
    /**
     * severity level of trace
     */
    public static final int TRACE = 0;
    
    /**
     * severity level of info
     */
    public static final int INFO = 1;
    
    /**
     * severity level of warning
     */
    public static final int WARN = 2;
    /**
     * severity level of error
     */
    public static final int ERROR = 3;
    
    private static final String[] SEVERITIES = {"T","I","W","E"};
    private static final int COLUMN_SIZE = 94;
    private static final String DELIMITER = "|";
    private static final String INDENT_PREFIX = " > ";
    private static final int INDENTED_COLUMN_SIZE = COLUMN_SIZE - INDENT_PREFIX.length();
    private static final String EXTENSION_THREAD_NAME = "Control Surface Session";
    
    private static ControllerHost host;
    private static int level;
    private static Consumer<String> std;
    private static Consumer<String> err;
    private static Thread extensionThread;
    private Class<?> clazz;
    
    private Logger() {
    }

    private Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * get a logger instance.
     * @param clazz
     * @return 
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    /**
     * initialize the logger.
     * @param host
     * @param level 
     */
    static void init(ControllerHost host, int level) {
        Logger.host = host;
        Logger.level = level;
        // is this safe?
        // maybe not callable from other than extension thread.
        std = (s) -> {host.println(s);};
        err = (s) -> {host.errorln(s);};
        String currentThreadName = Thread.currentThread().getName();
        if (EXTENSION_THREAD_NAME.equals(currentThreadName)) {
            extensionThread = Thread.currentThread();
        }
    }
    
    /**
     * Is trace logging currently enabled?
     * @return 
     */
    public static boolean isTraceEnabled() {
        return level <= TRACE;
    }
    
    /**
     * Is info logging currently enabled?
     * @return 
     */
    public static boolean isInfoEnabled() {
        return level <= INFO;
    }
    
    /**
     * Is warning logging currently enabled?
     * @return 
     */
    public static boolean isWarnEnabled() {
        return level <= WARN;
    }
    
    /**
     * Logs a trace message.
     * @param msg
     */
    public void trace(String msg) {
        if (level <= TRACE) {
            log(formatLog(TRACE, msg), std);
        }
    }
    
    /**
     * Logs a info message.
     * @param msg
     */
    public void info(String msg) {
        if (level <= INFO) {
            log(formatLog(INFO, msg), std);
        }
    }
    
    /**
     * Logs a warning message.
     * @param msg
     */
    public void warn(String msg) {
        if (level <= WARN) {
            log(formatLog(WARN, msg), err);
        }
    }

    /**
     * Logs an error message.
     * @param msg
     */
    public void error(String msg) {
        log(formatLog(ERROR, msg), err);
    }
    
    /**
     * Logs an error message with exception.
     * @param msg
     */
    public void error(String msg, Throwable ex) {
        error(msg + "\n" + createStackTraceString(ex));
    }

    /**
     * Logs an exception.
     * @param msg
     */
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
    
    private String formatLog(int severity, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append((LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "   ").substring(0, 12));
        sb.append(DELIMITER);
        sb.append(SEVERITIES[severity]);
        sb.append(DELIMITER);
        sb.append(clazz.getSimpleName());
        sb.append(DELIMITER);
        // only add a thread name when call from other than "Control Surface Session" therad.
        if (extensionThread != Thread.currentThread()) {
            sb.append(Thread.currentThread().getName());
            sb.append(DELIMITER);
        }
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
