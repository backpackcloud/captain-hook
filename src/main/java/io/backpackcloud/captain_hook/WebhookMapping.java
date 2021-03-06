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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Optional;

@RegisterForReflection
public class WebhookMapping {

  private final Selector selector;
  private final Event event;

  @JsonCreator
  public WebhookMapping(@JsonProperty("selector") Selector selector,
                        @JsonProperty("event") Event event) {
    this.selector = Optional.ofNullable(selector).orElseGet(Selector::empty);
    this.event = Optional.ofNullable(event)
        .orElseThrow(UnbelievableException
            .because("Cannot create a webhook mapping without an event definition"));
  }

  public boolean matches(Webhook webhook) {
    return selector.test(webhook.labels());
  }

  public Selector selector() {
    return selector;
  }

  public Event event() {
    return event;
  }

}
