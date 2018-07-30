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
import static com.github.jhorology.bitwig.extension.Logger.Severity.*;

/**
 * A simple logging class for Controller Script Console with supporting Bitwig Value intreface model.
 */
public class Logger implements Value<StringValueChangedCallback>, StringValue {
    public static enum Severity {
        /**
         * severity level of debug
         */
        DEBUG,
    
        /**
         * severity level of trace
         */
        TRACE,
    
        /**
         * severity level of info
         */
        INFO,
    
        /**
         * severity level of warning
         */
        WARN,
        /**
         * severity level of error
         */
        ERROR;
    }
    
    private static final String[] SEVERITIES = {"D", "T", "I", "W", "E"};
    private static final int COLUMN_SIZE = 94;
    private static final String DELIMITER = "|";
    private static final String INDENT_PREFIX = " > ";
    private static final int INDENTED_COLUMN_SIZE = COLUMN_SIZE - INDENT_PREFIX.length();
    private static final int TAIL_QUEUE_SIZE = 24;

    private static ControllerHost host;
    private static Severity level;
    private static Thread controlSurfaceSession;
    private static List<StringValueChangedCallback> subscribers;
    private static boolean subscribed;
    private static Queue<String> tailMessages;
    private static boolean reentrantLock;

    private String category;

    /**
     * Constructor for event subscription.
     */
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
    static void init(ControllerHost host, Severity level) {
        Logger.host = host;
        Logger.level = level;
        Logger.controlSurfaceSession = Thread.currentThread();
        Logger.subscribers = new ArrayList<>();
        Logger.tailMessages = new ArrayDeque<>(TAIL_QUEUE_SIZE);
        Logger.reentrantLock = false;
    }

    /**
     * set a severity level of logger.
     * @param level
     */
    static void setLevel(Severity level) {
        Logger.level = level;
    }
    
    /**
     * this method called at extension's end-of-lifecycle.
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
        return level.compareTo(DEBUG) <= 0;
    }

    /**
     * Is trace logging currently enabled?
     * @return 
     */
    public static boolean isTraceEnabled() {
        return level.compareTo(TRACE) <= 0;
    }
    
    /**
     * Is info logging currently enabled?
     * @return 
     */
    public static boolean isInfoEnabled() {
        return level.compareTo(INFO) <= 0;
    }
    
    /**
     * Is warning logging currently enabled?
     * @return 
     */
    public static boolean isWarnEnabled() {
        return level.compareTo(WARN) <= 0;
    }
    
    /**
     * Logs a debug message.
     * @param msg
     */
    public void debug(String msg) {
        internalLog(DEBUG, msg, null);
    }
    
    /**
     * Logs a debug message.
     * @param ex
     */
    public void debug(Throwable ex) {
        internalLog(DEBUG, null, ex);
    }
    
    /**
     * Logs a debug message.
     * @param msg
     * @param ex
     */
    public void debug(String msg, Throwable ex) {
        internalLog(DEBUG, null, ex);
    }
    
    /**
     * Logs a trace message.
     * @param msg
     */
    public void trace(String msg) {
        internalLog(TRACE, msg, null);
    }
    
    /**
     * Logs a info message.
     * @param msg
     */
    public void info(String msg) {
        internalLog(INFO, msg, null);
    }
    
    /**
     * Logs a warning message.
     * @param msg
     */
    public void warn(String msg) {
        internalLog(WARN, msg, null);
    }
    
    /**
     * Logs a warning message with exception.
     * @param msg
     * @param ex
     */
    public void warn(String msg, Throwable ex) {
        internalLog(WARN, msg, ex);
    }
    
    /**
     * Logs an error message.
     * @param msg
     */
    public void error(String msg) {
        internalLog(ERROR, msg, null);
    }
    
    /**
     * Logs an error message with exception.
     * @param msg
     * @param ex
     */
    public void error(String msg, Throwable ex) {
        internalLog(ERROR, msg, ex);
    }

    /**
     * Logs an exception.
     * @param ex
     */
    public void error(Throwable ex) {
        internalLog(ERROR, null, ex);
    }
    
    /**
     * Logs a message.
     * @param severity
     * @param msg
     */
    public void log(Severity severity, String msg) {
        internalLog(severity, msg, null);
    }
    /**
     * Logs a message with exception.
     * @param severity
     * @param msg
     * @param ex
     */
    public void log(Severity severity, String msg, Throwable ex) {
        internalLog(severity, msg, ex);
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
    private void internalLog(Severity severity, String msg, Throwable ex) {
        if (severity.compareTo(level) < 0) {
            return;
        }
        String logMessage = formatLog(severity, msg, ex);
        // TODO maybe not thread safe.
        if (severity.compareTo(WARN) > 0) {
            outputScriptConsole(logMessage, (s) -> host.errorln(s));
        } else {
            outputScriptConsole(logMessage, (s) -> host.println(s));
        }
        
        // trigger value changed event.
        // only support within control surface session,
        // 'cause need to consider too many things...
        if (Thread.currentThread() == controlSurfaceSession) {
            triggerValueChanged(logMessage);
        }
    }
    
    private void triggerValueChanged() {
        triggerValueChanged(null);
    }

    private void triggerValueChanged(String message) {
        // Logger function will be called within observer's valueChanged method.
        // this is a enough locking way for single thread execution.
        if (reentrantLock) {
            return;
        }
        reentrantLock = true;
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
            reentrantLock = false;
        }
    }
    
    private String formatLog(Severity severity, String msg, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        if (date.length() < 12) {
            date = (date + "            ").substring(0, 12);
        }
        sb.append(date);
        sb.append(DELIMITER);
        sb.append(severity.name().substring(0, 1));
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
