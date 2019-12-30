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

package io.backpackcloud.captain_hook.transmitters.router;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Cannon;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.TemplateEngine;
import io.backpackcloud.captain_hook.Transmitter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;

import java.util.Map;

@RegisterForReflection
public class RouterTransmitter implements Transmitter {

  private static final Logger logger = Logger.getLogger(RouterTransmitter.class);

  private final Map<String, Route> routes;
  private final TemplateEngine templateEngine;
  private final Cannon cannon;

  @JsonCreator
  public RouterTransmitter(@JsonProperty("routes") Map<String, Route> routes,
                           @JacksonInject("templateEngine") TemplateEngine templateEngine,
                           @JacksonInject("cannon") Cannon cannon) {
    this.routes = routes;
    this.templateEngine = templateEngine;
    this.cannon = cannon;
  }

  @Override
  public void fire(Notification notification) {
    if (routes.containsKey(notification.target())) {
      Route route = routes.get(notification.target());

      logger.infov("Sending notification to: {0}", notification.target());
      cannon.load(notification)
          .fire(route.payload(), route.headers())
          .at(route.url());
    } else {
      logger.warnv("No route defined for {0}", notification.target());
    }
  }

}
