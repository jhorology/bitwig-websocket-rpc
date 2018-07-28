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

// jvm
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

// bitwig api
import com.bitwig.extension.callback.StringValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.Value;

// provided dependencies
import com.google.common.eventbus.Subscribe;

/**
 * A simple logging class for Controller Script Console. 
 */
public class Logger implements Value<StringValueChangedCallback>, StringValue {
    
    /**
     * severity level of debug
     */
    public static final int DEBUG = 0;
    
    /**
     * severity level of trace
     */
    public static final int TRACE = 1;
    
    /**
     * severity level of info
     */
    public static final int INFO = 2;
    
    /**
     * severity level of warning
     */
    public static final int WARN = 3;
    /**
     * severity level of error
     */
    public static final int ERROR = 4;
    
    private static final String[] SEVERITIES = {"D", "T", "I", "W", "E"};
    private static final int COLUMN_SIZE = 94;
    private static final String DELIMITER = "|";
    private static final String INDENT_PREFIX = " > ";
    private static final int INDENTED_COLUMN_SIZE = COLUMN_SIZE - INDENT_PREFIX.length();
    private static final int TAIL_QUEUE_SIZE = 4;

    private static ControllerHost host;
    private static int level;
    private static Thread controlSurfaceSession;
    private static List<StringValueChangedCallback> subscribers;
    private static boolean subscribed;
    private static Queue<String> tailMessages;
    private String category;
    
    Logger() {
    }

    private Logger(Class<?> clazz) {
        this(clazz.getSimpleName());
    }
    
    private Logger(String category) {
        this.category = category;
    }

    /**
     * get a logger instance.
     * @param clazz
     * @return Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
    
    /**
     * get a logger instance.
     * @param category
     * @return Logger
     */
    public static Logger getLogger(String category) {
        return new Logger(category);
    }

    /**
     * initialize logger.
     * @param host
     * @param level
     */
    static void init(ControllerHost host, int level) {
        Logger.host = host;
        Logger.level = level;
        Logger.controlSurfaceSession = Thread.currentThread();
        Logger.subscribers = new ArrayList<>();
        Logger.tailMessages = new ArrayDeque<>(TAIL_QUEUE_SIZE);
        Logger.reentrant = false;
    }
    
    /**
     * called on extension's end-of-lifecycle.
     * @param e
     */
    @Subscribe
    static void onExit(ExitEvent e) {
        subscribers.clear();
        tailMessages.clear();
        host = null;
        controlSurfaceSession = null;
    }
    
    /**
     * Is debug logging currently enabled?
     * @return 
     */
    public static boolean isDebugEnabled() {
        return level <= DEBUG;
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
     * Logs a debug message.
     * @param msg
     */
    public void debug(String msg) {
        if (level <= DEBUG) {
            internalLog(DEBUG, msg, null);
        }
    }
    
    /**
     * Logs a debug message.
     * @param ex
     */
    public void debug(Throwable ex) {
        if (level <= DEBUG) {
            internalLog(DEBUG, null, ex);
        }
    }
    
    /**
     * Logs a debug message.
     * @param msg
     * @param ex
     */
    public void debug(String msg, Throwable ex) {
        if (level <= DEBUG) {
            internalLog(DEBUG, null, ex);
        }
    }
    
    /**
     * Logs a trace message.
     * @param msg
     */
    public void trace(String msg) {
        if (level <= TRACE) {
            internalLog(TRACE, msg, null);
        }
    }
    
    /**
     * Logs a info message.
     * @param msg
     */
    public void info(String msg) {
        if (level <= INFO) {
            internalLog(INFO, msg, null);
        }
    }
    
    /**
     * Logs a warning message.
     * @param msg
     */
    public void warn(String msg) {
        if (level <= WARN) {
            internalLog(WARN, msg, null);
        }
    }
    
    /**
     * Logs a warning message with exception.
     * @param msg
     * @param ex
     */
    public void warn(String msg, Throwable ex) {
        if (level <= WARN) {
            internalLog(WARN, msg, ex);
        }
    }

    /**
     * Logs an error message.
     * @param msg
     */
    public void error(String msg) {
        if (level <= ERROR) {
            internalLog(ERROR, msg, null);
        }
    }
    
    /**
     * Logs an error message with exception.
     * @param msg
     * @param ex
     */
    public void error(String msg, Throwable ex) {
        if (level <= ERROR) {
            internalLog(ERROR, msg, ex);
        }
    }

    /**
     * Logs an exception.
     * @param ex
     */
    public void error(Throwable ex) {
        if (level <= ERROR) {
            internalLog(ERROR, null, ex);
        }
    }
    
    /**
     * Logs a message.
     * @param severity
     * @param msg
     */
    public void log(int severity, String msg) {
        if (level <= severity) {
            internalLog(severity, msg, null);
        }
    }
    /**
     * Logs a message with exception.
     * @param severity
     * @param msg
     * @param ex
     */
    public void log(int severity, String msg, Throwable ex) {
        if (level <= severity) {
            internalLog(severity, msg, ex);
        }
    }
    

    /**
     * An implementation of Value#markInterested
     */
    @Override
    public void markInterested() {
        subscribe();
    }

    /**
     * An implementation of Value#addValueObserver
     * @param callback
     */
    @Override
    public void addValueObserver(StringValueChangedCallback callback) {
        subscribers.add(callback);
    }

    /**
     * An implementation of Subscribable#isSubscibed
     * @return
     */
    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    /**
     * An implementation of Subscribable#setIsSubscibe
     * @param subscribed
     */
    @Override
    @SuppressWarnings("static-access")
    public void setIsSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
        // to knows tail messages
        if (subscribed) {
            // to let subscribers to know tail messages.
            triggerValueChanged();
        }
    }

    /**
     * An implementation of Subscribable#subscibe
     */
    @Override
    public void subscribe() {
        setIsSubscribed(true);
    }

    /**
     * An implementation of Subscribable#unsubscibe
     */
    @Override
    public void unsubscribe() {
        setIsSubscribed(false);
    }

    /**
     * An implementation of StringValue#get
     * @return last log message
     */
    @Override
    public String get() {
        return tailMessages.peek();
    }

    /**
     * An implementation of StringValue#getLimited
     * @param length
     * @return trimed last log message
     */
    @Override
    public String getLimited(int length) {
        String message = get();
        if (message != null && message.length() > length) {
            return message.substring(0, length);
        }
        return message;
    }

    /**
     * Logs a message with exception.
     * @param severity
     * @param msg
     * @param ex
     */
    private void internalLog(int severity, String msg, Throwable ex) {
        String logMessage = formatLog(severity, msg, ex);
        // TODO maybe not thread safe.
        if (severity < WARN) {
            outputScriptConsole(logMessage, (s) -> host.println(s));
        } else {
            outputScriptConsole(logMessage, (s) -> host.errorln(s));
        }
        
        // trigger RPC event.
        // only support within control surface session,
        // 'cause need to consider too many things...
        if (Thread.currentThread() == controlSurfaceSession) {
            triggerValueChanged(logMessage);
        }
    }
    
    private void triggerValueChanged() {
        triggerValueChanged(null);
    }

    private static boolean reentrant;
    private void triggerValueChanged(String message) {
        // for debug
        if (reentrant) {
            return;
        }
        reentrant = true;
        try {
            while(tailMessages.size() >= TAIL_QUEUE_SIZE) {
                tailMessages.remove();
            }
            if (message != null) {
                tailMessages.add(message);
            }
            if (subscribed && !subscribers.isEmpty() && !tailMessages.isEmpty()) {
                String logMessage = tailMessages.poll();
                while(logMessage != null) {
                    final String msg = logMessage;
                    subscribers.stream()
                        .forEach((s)-> s.valueChanged(msg));
                    logMessage = tailMessages.poll();
                }
            }
        } finally {
            reentrant = false;
        }
    }
    
    private String formatLog(int severity, String msg, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        if (date.length() < 12) {
            date = (date + "            ").substring(0, 12);
        }
        sb.append(date);
        sb.append(DELIMITER);
        sb.append(SEVERITIES[severity]);
        sb.append(DELIMITER);
        // only add a thread name when call from other than "Control Surface Session" therad.
        if (controlSurfaceSession != Thread.currentThread()) {
            sb.append(Thread.currentThread().getName());
            sb.append(DELIMITER);
        }
        sb.append(category);
        sb.append(DELIMITER);
        if (msg != null) {
            sb.append(msg);
            if (ex != null) {
                sb.append("\n");
            }
        }
        if (ex != null) {
            sb.append(createStackTraceString(ex));
        }
        return sb.toString();
    }
    
    private String createStackTraceString(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private void outputScriptConsole(String msg, Consumer<String> out) {
        BufferedReader br = new BufferedReader(new StringReader(msg));
        String firstLine;
        try {
            firstLine = br.readLine();
        } catch (IOException ex) {
            // never happend.
            firstLine = ex.getMessage();
        }
        if (firstLine.length() > COLUMN_SIZE) {
            out.accept(firstLine.substring(0, COLUMN_SIZE));
            firstLine = firstLine.substring(COLUMN_SIZE);
            outputLineFollowing(firstLine, out);
        } else {
            out.accept(firstLine);
        }
        br.lines().forEach(l -> outputLineFollowing(l, out));
    }
    
    private void outputLineFollowing(String line, Consumer<String> out) {
        while (line.length() > INDENTED_COLUMN_SIZE) {
            out.accept(INDENT_PREFIX + line.substring(0, INDENTED_COLUMN_SIZE));
            line = line.substring(INDENTED_COLUMN_SIZE);
        }
        out.accept(INDENT_PREFIX + line);
    }
}
