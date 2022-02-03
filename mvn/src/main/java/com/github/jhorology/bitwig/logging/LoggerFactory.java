/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.jhorology.bitwig.logging;

import com.github.jhorology.bitwig.logging.impl.StaticLoggerBinder;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public final class LoggerFactory {

  // private constructor prevents instantiation
  private LoggerFactory() {}

  /**
   * Return a logger named according to the name parameter using the
   * statically bound {@link ILoggerFactory} instance.
   *
   * @param name
   *            The name of the logger.
   * @return logger
   */
  public static Logger getLogger(String name) {
    ILoggerFactory iLoggerFactory = getILoggerFactory();
    return iLoggerFactory.getLogger(name);
  }

  /**
   * Return a logger named corresponding to the class passed as parameter,
   * using the statically bound {@link ILoggerFactory} instance.
   *
   * <p>
   * In case the the <code>clazz</code> parameter differs from the name of the
   * caller as computed internally by SLF4J, a logger name mismatch warning
   * will be printed but only if the
   * <code>slf4j.detectLoggerNameMismatch</code> system property is set to
   * true. By default, this property is not set and no warnings will be
   * printed even in case of a logger name mismatch.
   *
   * @param clazz
   *            the returned logger will be named after clazz
   * @return logger
   *
   *
   * @see <a
   *      href="http://www.slf4j.org/codes.html#loggerNameMismatch">Detected
   *      logger name mismatch</a>
   */
  public static Logger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * Return the {@link ILoggerFactory} instance in use.
   * <p/>
   * <p/>
   * ILoggerFactory instance is bound with this class at compile time.
   *
   * @return the ILoggerFactory instance in use
   */
  public static ILoggerFactory getILoggerFactory() {
    return StaticLoggerBinder.getSingleton().getLoggerFactory();
  }
}
