/*
 * Copyright (c) 2019 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.ext;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.Track;
import com.github.jhorology.bitwig.Config;
// TODO Action#isEnabled() is dead at 3.1 Beta 4
//#if bitwig.extension.api.version >= 99
import com.github.jhorology.bitwig.ext.api.ApplicationExt;
//#endif
import com.github.jhorology.bitwig.ext.api.ChannelExt;
import com.github.jhorology.bitwig.ext.api.ClipExt;
import com.github.jhorology.bitwig.ext.api.DeviceExt;
import com.github.jhorology.bitwig.ext.api.ExtApi;
import com.github.jhorology.bitwig.ext.api.VuMeterChannelMode;
import com.github.jhorology.bitwig.ext.api.VuMeterPeakMode;
import com.github.jhorology.bitwig.ext.impl.DefaultExtApiFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Concrete factory class for extended API.
 */
public class ExtApiFactory implements ExtApi {

  private static final ExtApiFactory instance = new ExtApiFactory();
  private ExtApi factory = new DefaultExtApiFactory();

  private ExtApiFactory() {}

  /**
   * Gets a default factory.
   * @return
   */
  public static ExtApiFactory getInstance() {
    return instance;
  }

  /**
   * Returns a extended API  interface.
   * @param config Configuration.
   * @param bitwigApi bitwig API interface
   * @return a extended api class
   */
  public static Class<?> getExtApiInterface(Config config, Class<?> bitwigApi) {
    // TODO Action#isEnabled() is dead at 3.1 Beta 4
    //#if bitwig.extension.api.version >= 99
    if (Application.class.isAssignableFrom(bitwigApi)) {
      return ApplicationExt.class;
    }
    //#endif
    if (CursorDevice.class.isAssignableFrom(bitwigApi)) {
      if (config.useCursorDeviceDirectParameter()) {
        return DeviceExt.class;
      }
    }
    if (Channel.class.isAssignableFrom(bitwigApi)) {
      switch (config.getVuMeterUsedFor()) {
        case NONE:
          break;
        case CURSOR_TRACK:
          if (CursorTrack.class.isAssignableFrom(bitwigApi)) {
            return ChannelExt.class;
          }
          break;
        case TRACK:
          if (Track.class.isAssignableFrom(bitwigApi)) {
            return ChannelExt.class;
          }
          break;
        case CHANNEL:
          return ChannelExt.class;
      }
    }
    return null;
  }

  /**
   * Create a new mixin instance.
   * @param config Configuration.
   * @param bitwigApi bitwig API interface
   * @param bitwigApiInstance bitwig API instance
   * @return
   */
  public static Object newMixinInstance(
    Config config,
    Class<?> bitwigApi,
    Object bitwigApiInstance
  ) {
    Class<?> extApi = getExtApiInterface(config, bitwigApi);
    if (extApi != null) {
      final Object extApiInstance = newExtApiInstance(
        config,
        bitwigApi,
        bitwigApiInstance
      );
      return Proxy.newProxyInstance(
        ExtApiFactory.class.getClassLoader(),
        new Class<?>[] { bitwigApi, extApi },
        (Object proxy, Method method, Object[] args) -> {
          if (method.getDeclaringClass().isAssignableFrom(extApi)) {
            return method.invoke(extApiInstance, args);
          } else {
            return method.invoke(bitwigApiInstance, args);
          }
        }
      );
    }
    return bitwigApiInstance;
  }

  /**
   * Sets a custom factory.
   * @param factory
   */
  public void setFactory(ExtApi factory) {
    this.factory = factory;
  }

  // TODO Action#isEnabled() is dead at 3.1 Beta 4
  //#if bitwig.extension.api.version >= 99
  /**
   * {@inheritDoc}
   */
  @Override
  public ApplicationExt createApplicationExt(Application application) {
    return factory.createApplicationExt(application);
  }

  //#endif

  /**
   * {@inheritDoc}
   */
  @Override
  public DeviceExt createDeviceExt(Device device) {
    return factory.createDeviceExt(device);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChannelExt createChannelExt(
    Channel channel,
    int vuMeterRange,
    VuMeterChannelMode vuMeterChannelMode,
    VuMeterPeakMode vuMeterPeakMode
  ) {
    return factory.createChannelExt(
      channel,
      vuMeterRange,
      vuMeterChannelMode,
      vuMeterPeakMode
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ClipExt createClipExt(Clip clip, int gridWidth, int gridHeight) {
    return factory.createClipExt(clip, gridWidth, gridHeight);
  }

  protected static Object newExtApiInstance(
    Config config,
    Class<?> bitwigApiInterface,
    Object bitwigApiInstance
  ) {
    // TODO Acation#isEnabled() is dead at 3.1 Beta 4
    //#if bitwig.extension.api.version > 99
    if (Application.class.isAssignableFrom(bitwigApiInterface)) {
      return getInstance()
        .createApplicationExt((Application) bitwigApiInstance);
    }
    //#endif

    // TODO a little dirty way
    boolean arranger = bitwigApiInstance
      .getClass()
      .getSimpleName()
      .contains("Arranger");
    if (Clip.class.isAssignableFrom(bitwigApiInterface)) {
      return getInstance()
        .createClipExt(
          (Clip) bitwigApiInstance,
          arranger
            ? config.getArrangerCursorClipGridWidth()
            : config.getLauncherCursorClipGridWidth(),
          arranger
            ? config.getArrangerCursorClipGridHeight()
            : config.getLauncherCursorClipGridHeight()
        );
    }
    if (Device.class.isAssignableFrom(bitwigApiInterface)) {
      return getInstance().createDeviceExt((Device) bitwigApiInstance);
    }
    if (Channel.class.isAssignableFrom(bitwigApiInterface)) {
      return getInstance()
        .createChannelExt(
          (Channel) bitwigApiInstance,
          config.getVuMeterRange(),
          config.getVuMeterChannelMode(),
          config.getVuMeterPeakMode()
        );
    }
    return null;
  }
}
