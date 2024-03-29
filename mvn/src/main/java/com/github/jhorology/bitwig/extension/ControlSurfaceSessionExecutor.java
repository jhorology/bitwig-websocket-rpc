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

import com.github.jhorology.bitwig.logging.LoggerFactory;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

/**
 * Executor class that always runs tasks within 'ControllerHost#flush()' method.
 */
public class ControlSurfaceSessionExecutor implements Executor, Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(
    ControlSurfaceSessionExecutor.class
  );

  private static final int QUEUE_SIZE = 64;

  private final ConcurrentLinkedQueue<Runnable> tasks;
  private ExecutionContext<?> context;
  private Thread controlSurfaceSession;
  private final boolean doNotUseRequestFlush;

  /**
   * Constructor.
   */
  public ControlSurfaceSessionExecutor(boolean doNotUseRequestFlush) {
    this.doNotUseRequestFlush = doNotUseRequestFlush;
    this.tasks = new ConcurrentLinkedQueue<>();
  }

  // TODO Guava 19 or above EventBus is able to register non-public @﻿Subscribe

  @Subscribe
  public final void onInit(InitEvent<?> e) {
    context = new ExecutionContext<>(e.getExtension());
    e.getHost().scheduleTask(this, 0L);
    this.controlSurfaceSession = Thread.currentThread();
  }

  @Subscribe
  public final void onFlush(FlushEvent<?> e) {
    runAllQueuedTasks();
  }

  @Subscribe
  public final void onExit(ExitEvent<?> e) {
    runAllQueuedTasks();
  }

  /**
   * An implementation method of Executor interface
   * @param command
   */
  @Override
  public void execute(Runnable command) {
    tasks.offer(command);
    if (Thread.currentThread() == controlSurfaceSession) {
      runAllQueuedTasks();
    } else if (context != null && !doNotUseRequestFlush) {
      context.getHost().requestFlush();
    }
    if (LOG.isWarnEnabled()) {
      if (tasks.size() > (QUEUE_SIZE * 3 / 4)) {
        LOG.warn(
          "task queues will reach full capacity. currently queued tasks:{}",
          tasks.size()
        );
      }
    }
  }

  private void runAllQueuedTasks() {
    Runnable task = tasks.poll();
    while (task != null) {
      context.init();
      try {
        task.run();
      } catch (Exception ex) {
        LOG.error("task execution error.", ex);
      } finally {
        task = tasks.poll();
        context.destroy();
      }
    }
  }

  @Override
  public void run() {
    try {
      runAllQueuedTasks();
    } finally {
      context.getHost().scheduleTask(this, 0L);
    }
  }
}
