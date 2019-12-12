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

package io.backpackcloud.captain_hook.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.core.Subscription;
import io.backpackcloud.captain_hook.core.Transmitter;
import io.backpackcloud.captain_hook.core.VirtualAddress;
import io.backpackcloud.captain_hook.core.WebhookMapping;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The mind behind everything that happens aboard. The Captain holds the information about
 * how objects should be converted into other objects until a notification is raised. Also,
 * every information about how to deliver the notifications is on the Captain.
 * <p>
 * Don't expect Captain's orders to change by external factors.
 */
@RegisterForReflection
public class CaptainHook {

  private final List<Subscription> subscriptions;

  private final List<WebhookMapping> webhooks;

  private final Map<String, VirtualAddress> virtualAddresses;

  private final Map<String, Transmitter> transmitters;

  @JsonCreator
  public CaptainHook(@JsonProperty("subscriptions") List<Subscription> subscriptions,
                     @JsonProperty("webhooks") List<WebhookMapping> webhooks,
                     @JsonProperty("virtual_addresses") Map<String, VirtualAddress> virtualAddresses,
                     @JsonProperty("transmitters") Map<String, Transmitter> transmitters) {
    this.subscriptions = subscriptions;
    this.webhooks = webhooks;
    this.virtualAddresses = virtualAddresses;
    this.transmitters = transmitters;
  }

  public List<Subscription> subscriptions() {
    return Collections.unmodifiableList(subscriptions);
  }

  public List<WebhookMapping> webhooks() {
    return Collections.unmodifiableList(webhooks);
  }

  public Map<String, VirtualAddress> virtualAddresses() {
    return Collections.unmodifiableMap(virtualAddresses);
  }

  public Map<String, Transmitter> transmitters() {
    return Collections.unmodifiableMap(transmitters);
  }

}
