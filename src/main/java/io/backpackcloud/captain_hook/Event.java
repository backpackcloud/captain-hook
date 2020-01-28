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

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an event that can be subscribed for receiving notifications.
 *
 * @see Subscription
 */
@RegisterForReflection
public class Event {

  @JsonProperty
  private final LabelSet labels;

  @JsonProperty
  private final String name;

  @JsonProperty
  private final String title;

  @JsonProperty
  private final String message;

  @JsonProperty
  private final String url;

  @JsonCreator
  public Event(@JsonProperty("labels") LabelSet labelSet,
               @JsonProperty("name") String name,
               @JsonProperty("message") String message,
               @JsonProperty("title") String title,
               @JsonProperty("url") String url) {
    this.name = Optional.ofNullable(name)
        .orElseThrow(UnbelievableException.because("Cannot create an event without a name"));
    this.labels = Optional.ofNullable(labelSet).orElseGet(LabelSet::empty);
    this.message = Optional.ofNullable(message)
        .orElseThrow(UnbelievableException.because("Cannot create an event without a message"));
    this.title = title;
    this.url = url;
  }

  /**
   * Returns the labels attached to this event.
   *
   * @return the labels attached to this event.
   */
  public LabelSet labels() {
    return this.labels;
  }

  /**
   * Returns the name of this event.
   *
   * @return the name of this event.
   */
  public String name() {
    return this.name;
  }

  /**
   * Returns the message of this event.
   *
   * @return the message of this event.
   */
  public String message() {
    return message;
  }

  /**
   * Return the title of this event. May be {@code null}.
   *
   * @return the title of this event.
   */
  public Optional<String> title() {
    return Optional.ofNullable(title);
  }

  /**
   * Return the url of this event. May be {@code null}.
   *
   * @return the url of this event.
   */
  public Optional<String> url() {
    return Optional.ofNullable(url);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Event event = (Event) o;
    return labels.equals(event.labels) &&
        name.equals(event.name) &&
        Objects.equals(title, event.title) &&
        message.equals(event.message) &&
        Objects.equals(url, event.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labels, name, title, message, url);
  }

}
