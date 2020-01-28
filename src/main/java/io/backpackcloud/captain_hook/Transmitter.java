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

package io.backpackcloud.captain_hook;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.backpackcloud.captain_hook.transmitters.http.HTTPTransmitter;
import io.backpackcloud.captain_hook.transmitters.pushover.PushoverTransmitter;
import io.backpackcloud.captain_hook.transmitters.router.RouterTransmitter;
import io.backpackcloud.captain_hook.transmitters.telegram.TelegramTransmitter;
import io.backpackcloud.captain_hook.transmitters.virtual.VirtualAddressTransmitter;

/**
 * Interface that represents how a notification is delivered to its desintation.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HTTPTransmitter.class, name = "http"),
    @JsonSubTypes.Type(value = PushoverTransmitter.class, name = "pushover"),
    @JsonSubTypes.Type(value = TelegramTransmitter.class, name = "telegram"),
    @JsonSubTypes.Type(value = VirtualAddressTransmitter.class, name = "virtual"),
    @JsonSubTypes.Type(value = RouterTransmitter.class, name = "router")
})
public interface Transmitter {

  /**
   * Fires the notification to its destination.
   *
   * @param notification the notification to send
   */
  void fire(Notification notification);

  /**
   * Checks if this transmitter is up and running. The liveness health check will call this method.
   *
   * @return {@code true} if this transmitter is up and running
   */
  default boolean isUp() {
    return true;
  }

}
