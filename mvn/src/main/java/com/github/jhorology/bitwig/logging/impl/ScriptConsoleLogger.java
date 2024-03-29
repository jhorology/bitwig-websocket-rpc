/*
 * This source code is based on https://github.com/qos-ch/slf4j/blob/v_1.7.25/slf4j-simple/src/main/java/org/slf4j/impl/SimpleLogger.java.
 */

/*
 *
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
package com.github.jhorology.bitwig.logging.impl;

// bitwig api
import com.bitwig.extension.callback.StringValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.StringValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
// dependencies
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Script Console Logger for SLF4J.
 */
public class ScriptConsoleLogger
  extends MarkerIgnoringBase
  implements StringValue {

  private static final long serialVersionUID = 1L;
  private static final int TAIL_QUEUE_SIZE = 24;
  private static final long START_TIME = System.currentTimeMillis();

  private static boolean INITIALIZED = false;
  private static com.github.jhorology.bitwig.logging.impl.ScriptConsoleLoggerConfiguration CONFIG_PARAMS =
    null;
  private static ControllerHost host;
  private static Thread controlSurfaceSession;
  private static List<StringValueChangedCallback> subscribers;
  private static Queue<String> tailMessages;
  private static boolean reentrantLock;
  private static int indentColumnSize;
  private static com.github.jhorology.bitwig.logging.impl.LogSeverity globalLogLevel;
  private static boolean outputSystemConsole;
  private int subscribeCount;

  static void lazyInit() {
    if (INITIALIZED) {
      return;
    }
    INITIALIZED = true;
    init();
  }

  // external software might be invoking this method directly. Do not rename
  // or change its semantics.
  static void init() {
    CONFIG_PARAMS = new ScriptConsoleLoggerConfiguration();
    CONFIG_PARAMS.init();
    outputSystemConsole = CONFIG_PARAMS.outputSystemConsole;
    subscribers = new ArrayList<>();
    tailMessages = new ArrayDeque<>(TAIL_QUEUE_SIZE);
    reentrantLock = false;
    indentColumnSize =
      CONFIG_PARAMS.columnSize - CONFIG_PARAMS.indentPrefix.length();
    globalLogLevel = CONFIG_PARAMS.defaultLogLevel;
  }

  /**
   * set a severity level of logger.
   * @param level
   */
  public static void setGlobalLogLevel(LogSeverity level) {
    globalLogLevel = level;
  }

  /**
   * set a severity level of logger.
   * @param namePrefix
   * @param level
   */
  public static void setLogLevel(
    String namePrefix,
    com.github.jhorology.bitwig.logging.impl.LogSeverity level
  ) {
    lazyInit();
    if (CONFIG_PARAMS.logLevels == null) {
      CONFIG_PARAMS.logLevels = new HashMap<>();
    }
    CONFIG_PARAMS.logLevels.put(namePrefix, level);
  }

  /**
   * set a flag to output system console or not.
   * @param on
   */
  public static void setOutputSystemConsole(boolean on) {
    outputSystemConsole = on;
  }

  /**
   * Set a instance of ControllerHost.
   * @param host
   */
  public static void setControllerHost(ControllerHost host) {
    ScriptConsoleLogger.host = host;
    controlSurfaceSession = Thread.currentThread();
  }

  /** The current log level */
  private com.github.jhorology.bitwig.logging.impl.LogSeverity logLevel =
    com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO;
  /** The short name of this simple log instance */
  private transient String shortLogName = null;

  /**
   * Package access allows only {@link ExtensionLoggerFactory} to instantiate
   * ExtensionLogger instances.
   */
  ScriptConsoleLogger(String name) {
    this.name = name;
    this.logLevel = recursivelyComputeLevel();
  }

  // ------------------------------- implementation of Logger

  /**
   * Are {@code trace} messages currently enabled?
   * @return
   */
  @Override
  public boolean isTraceEnabled() {
    return isLevelEnabled(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE
    );
  }

  /**
   * A simple implementation which logs messages of level TRACE according to
   * the format outlined above.
   * @param msg
   */
  @Override
  public void trace(String msg) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE, msg, null);
  }

  /**
   * Perform single parameter substitution before logging the message of level
   * TRACE according to the format outlined above.
   * @param format
   * @param param1
   */
  @Override
  public void trace(String format, Object param1) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE,
      format,
      param1,
      null
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * TRACE according to the format outlined above.
   * @param format
   * @param param1
   * @param param2
   */
  @Override
  public void trace(String format, Object param1, Object param2) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE,
      format,
      param1,
      param2
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * TRACE according to the format outlined above.
   * @param format
   * @param argArray
   */
  @Override
  public void trace(String format, Object... argArray) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE,
      format,
      argArray
    );
  }

  /**
   * Log a message of level TRACE, including an exception.
   * @param msg
   * @param t
   */
  @Override
  public void trace(String msg, Throwable t) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.TRACE, msg, t);
  }

  /**
   * Are {@code debug} messages currently enabled?
   * @return
   */
  @Override
  public boolean isDebugEnabled() {
    return isLevelEnabled(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG
    );
  }

  /**
   * A simple implementation which logs messages of level DEBUG according to
   * the format outlined above.
   * @param msg
   */
  @Override
  public void debug(String msg) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG, msg, null);
  }

  /**
   * Perform single parameter substitution before logging the message of level
   * DEBUG according to the format outlined above.
   * @param format
   * @param param1
   */
  @Override
  public void debug(String format, Object param1) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG,
      format,
      param1,
      null
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * DEBUG according to the format outlined above.
   * @param format
   * @param param1
   * @param param2
   */
  @Override
  public void debug(String format, Object param1, Object param2) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG,
      format,
      param1,
      param2
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * DEBUG according to the format outlined above.
   * @param format
   * @param argArray
   */
  @Override
  public void debug(String format, Object... argArray) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG,
      format,
      argArray
    );
  }

  /**
   * Log a message of level DEBUG, including an exception.
   * @param msg
   * @param t
   */
  @Override
  public void debug(String msg, Throwable t) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.DEBUG, msg, t);
  }

  /**
   * Are {@code info} messages currently enabled?
   * @return
   */
  @Override
  public boolean isInfoEnabled() {
    return isLevelEnabled(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO
    );
  }

  /**
   * A simple implementation which logs messages of level INFO according to
   * the format outlined above.
   * @param msg
   */
  @Override
  public void info(String msg) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO, msg, null);
  }

  /**
   * Perform single parameter substitution before logging the message of level
   * INFO according to the format outlined above.
   * @param format
   * @param arg
   */
  @Override
  public void info(String format, Object arg) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO,
      format,
      arg,
      null
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * INFO according to the format outlined above.
   * @param format
   * @param arg1
   * @param arg2
   */
  @Override
  public void info(String format, Object arg1, Object arg2) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO,
      format,
      arg1,
      arg2
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * INFO according to the format outlined above.
   * @param format
   * @param argArray
   */
  @Override
  public void info(String format, Object... argArray) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO,
      format,
      argArray
    );
  }

  /**
   * Log a message of level INFO, including an exception.
   * @param msg
   * @param t
   */
  @Override
  public void info(String msg, Throwable t) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.INFO, msg, t);
  }

  /**
   * Are {@code warn} messages currently enabled?
   * @return
   */
  @Override
  public boolean isWarnEnabled() {
    return isLevelEnabled(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN
    );
  }

  /**
   * A simple implementation which always logs messages of level WARN
   * according to the format outlined above.
   * @param msg
   */
  @Override
  public void warn(String msg) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN, msg, null);
  }

  /**
   * Perform single parameter substitution before logging the message of level
   * WARN according to the format outlined above.
   * @param format
   * @param arg
   */
  @Override
  public void warn(String format, Object arg) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN,
      format,
      arg,
      null
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * WARN according to the format outlined above.
   * @param format
   * @param arg1
   * @param arg2
   */
  @Override
  public void warn(String format, Object arg1, Object arg2) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN,
      format,
      arg1,
      arg2
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * WARN according to the format outlined above.
   * @param format
   * @param argArray
   */
  @Override
  public void warn(String format, Object... argArray) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN,
      format,
      argArray
    );
  }

  /**
   * Log a message of level WARN, including an exception.
   * @param msg
   * @param t
   */
  @Override
  public void warn(String msg, Throwable t) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN, msg, t);
  }

  /**
   * Are {@code error} messages currently enabled?
   * @return
   */
  @Override
  public boolean isErrorEnabled() {
    return isLevelEnabled(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR
    );
  }

  /**
   * A simple implementation which always logs messages of level ERROR
   * according to the format outlined above.
   * @param msg
   */
  @Override
  public void error(String msg) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR, msg, null);
  }

  /**
   * Perform single parameter substitution before logging the message of level
   * ERROR according to the format outlined above.
   * @param format
   */
  @Override
  public void error(String format, Object arg) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR,
      format,
      arg,
      null
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * ERROR according to the format outlined above.
   * @param format
   * @param arg1
   * @param arg2
   */
  @Override
  public void error(String format, Object arg1, Object arg2) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR,
      format,
      arg1,
      arg2
    );
  }

  /**
   * Perform double parameter substitution before logging the message of level
   * ERROR according to the format outlined above.
   * @param format
   * @param argArray
   */
  @Override
  public void error(String format, Object... argArray) {
    formatAndLog(
      com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR,
      format,
      argArray
    );
  }

  /**
   * Log a message of level ERROR, including an exception.
   * @param msg
   * @param t
   */
  @Override
  public void error(String msg, Throwable t) {
    log(com.github.jhorology.bitwig.logging.impl.LogSeverity.ERROR, msg, t);
  }

  // public void log(LoggingEvent event) {
  //     int levelInt = event.getLevel().toInt();

  //     if (!isLevelEnabled(levelInt)) {
  //         return;
  //     }
  //     FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event.getArgumentArray(), event.getThrowable());
  //     log(levelInt, tp.getMessage(), event.getThrowable());
  // }

  // ------------------------------- implementation of StringValue

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
    return subscribeCount > 0;
  }

  /**
   * An implementation of Subscribable#setIsSubscibe
   * @param subscribed
   */
  @Override
  @Deprecated
  public void setIsSubscribed(boolean subscribed) {}

  /**
   * An implementation of Subscribable#subscibe
   */
  @Override
  public void subscribe() {
    subscribeCount++;
    triggerValueChanged();
  }

  /**
   * An implementation of Subscribable#unsubscibe
   */
  @Override
  public void unsubscribe() {
    if (subscribeCount > 0) {
      subscribeCount--;
    }
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

  // ------------------------------- private routines

  /**
   * This is our internal implementation for logging regular
   * (non-parameterized) log messages.
   *
   * @param level
   *            One of the LOG_LEVEL_XXX constants defining the log level
   * @param message
   *            The message itself
   * @param t
   *            The exception whose stack trace should be logged
   */
  private void log(
    com.github.jhorology.bitwig.logging.impl.LogSeverity level,
    String message,
    Throwable t
  ) {
    if (!isLevelEnabled(level)) {
      return;
    }

    StringBuilder buf = new StringBuilder(32);

    // Append date-time if so configured
    if (CONFIG_PARAMS.showDateTime) {
      if (CONFIG_PARAMS.dateFormatter != null) {
        buf.append(getFormattedDate());
        buf.append(' ');
      } else {
        buf.append(System.currentTimeMillis() - START_TIME);
        buf.append(' ');
      }
    }

    // Append current thread name if so configured
    if (CONFIG_PARAMS.showThreadName) {
      buf.append('[');
      buf.append(Thread.currentThread().getName());
      buf.append("] ");
    }

    if (CONFIG_PARAMS.levelInBrackets) buf.append('[');

    // Append a readable representation of the log level
    buf.append(level.name());
    if (CONFIG_PARAMS.levelInBrackets) buf.append(']');
    buf.append(' ');

    // Append the name of the log instance if so configured
    if (CONFIG_PARAMS.showShortLogName) {
      if (shortLogName == null) shortLogName = computeShortName();
      buf.append(String.valueOf(shortLogName)).append(" - ");
    } else if (CONFIG_PARAMS.showLogName) {
      buf.append(String.valueOf(name)).append(" - ");
    }

    // Append the message
    buf.append(message);
    if (t != null) {
      buf.append(createStackTraceString(t));
    }
    String logMessage = buf.toString();

    if (outputSystemConsole) {
      System.out.println(logMessage);
    }

    // trigger value changed event.
    // only support within control surface session,
    // 'cause need to consider too many things...
    if (Thread.currentThread() == controlSurfaceSession) {
      triggerValueChanged(logMessage);

      if (
        level.compareTo(
          com.github.jhorology.bitwig.logging.impl.LogSeverity.WARN
        ) >=
        0
      ) {
        outputScriptConsole(logMessage, s -> host.errorln(s));
      } else {
        outputScriptConsole(logMessage, s -> host.println(s));
      }
    }
  }

  private com.github.jhorology.bitwig.logging.impl.LogSeverity recursivelyComputeLevel() {
    if (CONFIG_PARAMS.logLevels == null) {
      return null;
    }
    String tempName = name;
    com.github.jhorology.bitwig.logging.impl.LogSeverity level = null;
    int indexOfLastDot = tempName.length();
    while ((level == null) && (indexOfLastDot > -1)) {
      tempName = tempName.substring(0, indexOfLastDot);
      level = CONFIG_PARAMS.logLevels.get(tempName);
      if (level != null) {
        return level;
      }
      indexOfLastDot = String.valueOf(tempName).lastIndexOf(".");
    }
    return null;
  }

  /**
   * For formatted messages, first substitute arguments and then log.
   *
   * @param level
   * @param format
   * @param arg1
   * @param arg2
   */
  private void formatAndLog(
    com.github.jhorology.bitwig.logging.impl.LogSeverity level,
    String format,
    Object arg1,
    Object arg2
  ) {
    if (!isLevelEnabled(level)) {
      return;
    }
    FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
    log(level, tp.getMessage(), tp.getThrowable());
  }

  /**
   * For formatted messages, first substitute arguments and then log.
   *
   * @param level
   * @param format
   * @param arguments
   *            a list of 3 ore more arguments
   */
  private void formatAndLog(
    com.github.jhorology.bitwig.logging.impl.LogSeverity level,
    String format,
    Object... arguments
  ) {
    if (!isLevelEnabled(level)) {
      return;
    }
    FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
    log(level, tp.getMessage(), tp.getThrowable());
  }

  /**
   * Is the given log level currently enabled?
   *
   * @param logLevel
   *            is this level enabled?
   * @return
   */
  private boolean isLevelEnabled(
    com.github.jhorology.bitwig.logging.impl.LogSeverity level
  ) {
    // log level are numerically ordered so can use simple numeric
    // comparison

    return logLevel != null
      ? logLevel.compareTo(level) <= 0
      : globalLogLevel.compareTo(level) <= 0;
  }

  private String createStackTraceString(Throwable ex) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }

  @SuppressWarnings("SynchronizeOnNonFinalField")
  private String getFormattedDate() {
    Date now = new Date();
    String dateText;
    synchronized (CONFIG_PARAMS.dateFormatter) {
      dateText = CONFIG_PARAMS.dateFormatter.format(now);
    }
    return dateText;
  }

  private String computeShortName() {
    return name.substring(name.lastIndexOf(".") + 1);
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
    if (firstLine.length() > CONFIG_PARAMS.columnSize) {
      out.accept(firstLine.substring(0, CONFIG_PARAMS.columnSize));
      firstLine = firstLine.substring(CONFIG_PARAMS.columnSize);
      outputLineFollowing(firstLine, out);
    } else {
      out.accept(firstLine);
    }
    br.lines().forEach(l -> outputLineFollowing(l, out));
  }

  private void outputLineFollowing(String line, Consumer<String> out) {
    while (line.length() > indentColumnSize) {
      out.accept(
        CONFIG_PARAMS.indentPrefix + line.substring(0, indentColumnSize)
      );
      line = line.substring(indentColumnSize);
    }
    out.accept(CONFIG_PARAMS.indentPrefix + line);
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
      while (tailMessages.size() >= TAIL_QUEUE_SIZE) {
        tailMessages.remove();
      }
      if (message != null) {
        tailMessages.add(message);
      }
      if (isSubscribed() && !subscribers.isEmpty() && !tailMessages.isEmpty()) {
        String logMessage = tailMessages.poll();
        while (logMessage != null) {
          final String msg = logMessage;
          subscribers.forEach(s -> s.valueChanged(msg));
          logMessage = tailMessages.poll();
        }
      }
    } finally {
      reentrantLock = false;
    }
  }
}
