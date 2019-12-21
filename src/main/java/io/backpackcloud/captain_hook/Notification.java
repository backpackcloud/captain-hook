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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RegisterForReflection
public class Notification {

  @JsonProperty
  private final String title;

  @JsonProperty
  private final String message;

  @JsonProperty
  private final String url;

  @JsonProperty
  private final Address destination;

  @JsonProperty
  private final Priority priority;

  public Notification(String title, String message, String url, Address destination, Priority priority) {
    this.title = title;
    this.message = Optional.ofNullable(message)
        .orElseThrow(UnbelievableException.because("Cannot create a notification without a message"));
    this.url = url;
    this.destination = Optional.ofNullable(destination)
        .orElseThrow(UnbelievableException.because("Cannot create a notification without a destination"));
    this.priority = Optional.ofNullable(priority).orElse(Priority.NORMAL);
  }

  public String message() {
    return message;
  }

  public Optional<String> title() {
    return Optional.ofNullable(title);
  }

  public Optional<String> url() {
    return Optional.ofNullable(url);
  }

  public Address destination() {
    return destination;
  }

  public Priority priority() {
    return priority;
  }

  public Notification changeAddress(Address newAddress) {
    return new Notification(title, message, url, newAddress, priority);
  }

  public Map<String, ?> context() {
    Map<String, Object> context = new HashMap<>();
    context.put("title", title);
    context.put("message", message);
    context.put("url", url);
    context.put("destination", destination);
    context.put("priority", priority);
    return context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Notification that = (Notification) o;
    return Objects.equals(title, that.title) &&
        message.equals(that.message) &&
        Objects.equals(url, that.url) &&
        destination.equals(that.destination) &&
        priority == that.priority;
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, message, url, destination, priority);
  }

}
