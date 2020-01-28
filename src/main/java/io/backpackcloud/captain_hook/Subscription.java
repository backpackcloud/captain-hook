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

/**
 * Represents a subscription for an event.
 * <p>
 * Subscriptions bind events to addresses allowing to create notifications.
 */
@RegisterForReflection
public class Subscription {

  private final Selector selector;
  private final String name;
  private final Address destination;
  private final Priority priority;

  @JsonCreator
  public Subscription(@JsonProperty("selector") Selector selector,
                      @JsonProperty("name") String name,
                      @JsonProperty("destination") Address destination,
                      @JsonProperty("priority") Priority priority) {
    this.name = name;
    this.destination = Optional.ofNullable(destination)
        .orElseThrow(UnbelievableException.because("No destination defined"));
    this.selector = Optional.ofNullable(selector).orElseGet(Selector::empty);
    this.priority = priority;
  }

  /**
   * Checks if this subscription matches the given event.
   *
   * @param event the event to check.
   * @return {@code true} if this subscription matches the given event.
   */
  public boolean matches(Event event) {
    if (name != null && !name.equals(event.name())) {
      return false;
    }
    return selector.test(event.labels());
  }

  /**
   * Analyzes the given event and produces a notification if it matches this event.
   *
   * @param event the event to analyze
   * @return an optional notification if this subscription matches the given event.
   */
  public Optional<Notification> yield(Event event) {
    if (matches(event))
      return Optional.of(new Notification(event, destination, priority));
    else return Optional.empty();
  }

}
