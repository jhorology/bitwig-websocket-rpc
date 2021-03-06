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
package com.github.jhorology.bitwig;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.github.jhorology.bitwig.extension.AbstractExtensionDefinition;
import com.github.jhorology.bitwig.extension.ExtensionUtils;
import com.google.gson.annotations.Expose;

/**
 * A Definition class of this extension.
 */
public class WebSocketRpcServerExtensionDefinition
  extends AbstractExtensionDefinition<Config> {

  // populate from json -->
  @Expose
  private Config defaultConfig;

  // <--

  /**
   * Creates an instance of this extension.<br>
   * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#createInstance(com.bitwig.extension.controller.api.ControllerHost)}
   * @param host
   * @return
   */
  @Override
  public ControllerExtension createInstance(ControllerHost host) {
    return new WebSocketRpcServerExtension(this, host);
  }

  /**
   * Returns configuration defaults
   * @return
   */
  @Override
  public Config newDefaultConfig() {
    Config config = new Config();
    ExtensionUtils.deepCopy(this.defaultConfig, config);
    return config;
  }
}
