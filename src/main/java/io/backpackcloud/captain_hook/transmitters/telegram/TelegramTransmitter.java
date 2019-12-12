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

package io.backpackcloud.captain_hook.transmitters.telegram;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.core.Notification;
import io.backpackcloud.captain_hook.core.SensitiveValue;
import io.backpackcloud.captain_hook.core.Transmitter;
import kong.unirest.Unirest;

public class TelegramTransmitter implements Transmitter {

  private final String token;

  @JsonCreator
  public TelegramTransmitter(@JsonProperty("token") SensitiveValue token) {
    this.token = token.value();
  }

  @Override
  public void deliver(Notification notification) {
    Unirest.post("https://api.telegram.org/bot{token}/sendMessage")
        .routeParam("token", token)
        .field("chat_id", notification.destination().id())
        .field("text", notification.message()
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;"))
        .asEmptyAsync();
  }

  public boolean isUp() {
    return Unirest.post("https://api.telegram.org/bot{token}/getMe")
        .routeParam("token", this.token)
        .asEmpty()
        .isSuccess();
  }

}
