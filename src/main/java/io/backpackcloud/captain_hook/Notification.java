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

package io.backpackcloud.captain_hook;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;
import java.util.Optional;

@RegisterForReflection
public class Notification {

  @JsonProperty
  private final Event event;

  @JsonProperty
  private final Address destination;

  public Notification(Event event, Address destination) {
    this.event = event;
    this.destination = destination;
  }

  public LabelSet labels() {
    return event.labels();
  }

  public String message() {
    return event.message();
  }

  public Optional<String> title() {
    return Optional.ofNullable(event.title());
  }

  public Optional<String> url() {
    return Optional.ofNullable(event.url());
  }

  public Event event() {
    return event;
  }

  public Address destination() {
    return destination;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Notification that = (Notification) o;
    return event.equals(that.event) &&
        destination.equals(that.destination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(event, destination);
  }

}
