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

/**
 * Represents a notification that might be delivered by the crew.
 */
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

  public Notification(Event event, Address destination, Priority priority) {
    this.title = event.title().orElse(null);
    this.message = event.message();
    this.url = event.url().orElse(null);
    this.destination = Optional.ofNullable(destination)
        .orElseThrow(UnbelievableException.because("Cannot create a notification without a destination"));
    this.priority = Optional.ofNullable(priority).orElse(Priority.NORMAL);
  }

  /**
   * Returns the message of this notification.
   *
   * @return the message of this notification.
   */
  public String message() {
    return message;
  }

  /**
   * Returns the optional title of this notification.
   *
   * @return the optional title of this notification.
   */
  public Optional<String> title() {
    return Optional.ofNullable(title);
  }

  /**
   * Returns the optional url of this notification.
   *
   * @return the optional url of this notification.
   */
  public Optional<String> url() {
    return Optional.ofNullable(url);
  }

  /**
   * Returns the address which this notification should be delivered.
   *
   * @return the destination of this notification.
   */
  public Address destination() {
    return destination;
  }

  /**
   * Returns the target of this notification. The same as {@code destination().id()}.
   *
   * @return the target of this notification.
   */
  public String target() {
    return destination.id();
  }

  /**
   * Returns the priority of this notification.
   *
   * @return the priority of this notification.
   */
  public Priority priority() {
    return priority;
  }

  /**
   * Creates a new notification by changing the destination to the given value.
   *
   * @param newAddress the new destination
   * @return a new notification based on this one but for the given destination.
   */
  public Notification changeAddress(Address newAddress) {
    return new Notification(title, message, url, newAddress, priority);
  }

  /**
   * Returns a context map of variables for using in templates.
   *
   * @return a context map containing the attributes of this notification.
   */
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
