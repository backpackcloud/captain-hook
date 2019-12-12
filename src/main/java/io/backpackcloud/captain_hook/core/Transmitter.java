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

package io.backpackcloud.captain_hook.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.backpackcloud.captain_hook.transmitters.http.HTTPTransmitter;
import io.backpackcloud.captain_hook.transmitters.pushover.PushoverTransmitter;
import io.backpackcloud.captain_hook.transmitters.slack.SlackTransmitter;
import io.backpackcloud.captain_hook.transmitters.telegram.TelegramTransmitter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HTTPTransmitter.class, name = "http"),
    @JsonSubTypes.Type(value = PushoverTransmitter.class, name = "pushover"),
    @JsonSubTypes.Type(value = TelegramTransmitter.class, name = "telegram"),
    @JsonSubTypes.Type(value = SlackTransmitter.class, name = "slack")
})
public interface Transmitter {

  void deliver(Notification notification);

  default boolean isUp() {
    return true;
  }

  Transmitter NULL = notification -> {
  };

}
