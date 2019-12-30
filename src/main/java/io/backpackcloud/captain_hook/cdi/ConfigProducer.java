/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Marcelo Guimaraes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.backpackcloud.captain_hook.cdi;

import io.backpackcloud.captain_hook.CaptainHook;
import io.backpackcloud.captain_hook.Cannon;
import io.backpackcloud.captain_hook.Serializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;

@ApplicationScoped
public class ConfigProducer {

  private final String configFile;

  public ConfigProducer(@ConfigProperty(name = "config.file", defaultValue = "captain-hook.yml") String configFile) {
    this.configFile = configFile;
  }

  @Produces
  @Singleton
  // WORKAROUND: declare cannon to force cdi to load it before jackson can inject it on the transmitters as needed
  public CaptainHook getConfig(Serializer serializer, Cannon cannon) {
    serializer.addDependency("cannon", cannon);
    return serializer.yaml().deserialize(new File(configFile), CaptainHook.class);
  }

}
