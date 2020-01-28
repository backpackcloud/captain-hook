/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Marcelo Guimarães <ataxexe@backpackcloud.com>
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

package io.backpackcloud.captain_hook.transmitters.pushover;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.SensitiveValue;
import io.backpackcloud.captain_hook.Transmitter;
import org.jboss.logging.Logger;

import javax.ws.rs.WebApplicationException;

public class PushoverTransmitter implements Transmitter {

  private static final Logger logger = Logger.getLogger(PushoverTransmitter.class);

  private final String token;
  private final PushoverService pushoverService;

  @JsonCreator
  public PushoverTransmitter(@JsonProperty("token") SensitiveValue token,
                             @JacksonInject("pushoverService") PushoverService pushoverService) {
    this.token = token.value();
    this.pushoverService = pushoverService;
  }

  @Override
  public void fire(Notification notification) {
    logger.infov("Sending notification to: {0}", notification.target());
    PushoverNotification pushoverNotification = new PushoverNotification(
        token,
        notification.target(),
        notification.message(),
        notification.title().orElse(null),
        notification.url().orElse(null),
        notification.priority().value()
    );
    try {
      pushoverService.send(pushoverNotification);
    } catch (WebApplicationException e) {
      logger.errorv(e, "Got HTTP Status {0} while sending pushover notification", e.getResponse().getStatus());
    }
  }

}
