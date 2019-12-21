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

package io.backpackcloud.captain_hook.transmitters.pushover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.SensitiveValue;
import io.backpackcloud.captain_hook.Transmitter;
import kong.unirest.Empty;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletableFuture;

public class PushoverTransmitter implements Transmitter {

  private static final Logger logger = Logger.getLogger(PushoverTransmitter.class);

  private final String token;

  private boolean error;

  @JsonCreator
  public PushoverTransmitter(@JsonProperty("token") SensitiveValue token) {
    this.token = token.value();
  }

  @Override
  public void fire(Notification notification) {
    logger.infov("Sending pushover notification to: {0}", notification.destination().id());
    CompletableFuture<HttpResponse<Empty>> future = Unirest
        .post("https://api.pushover.net/1/messages.json")
        .field("token", token)
        .field("user", notification.destination().id())
        .field("title", notification.title().orElse(""))
        .field("url", notification.url().orElse(""))
        .field("message", notification.message())
        // normal priority value is 0 for pushover
        .field("priority", String.valueOf(notification.priority().relativeValue(0)))
        .asEmptyAsync();
    future.whenCompleteAsync(
        (response, exception) -> error = 400 <= response.getStatus() && response.getStatus() < 600);
  }

  @Override
  public boolean isUp() {
    return !error;
  }

}
