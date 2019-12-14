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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an address of a message.
 */
@RegisterForReflection
public class Address {

  private static final Pattern PATTERN = Pattern.compile("(?<channel>\\w+):(?<id>.+)");

  public static final String VIRTUAL_CHANNEL = "virtual";

  private final String channel;
  private final String id;

  public Address(String channel, String id) {
    this.channel = Optional.ofNullable(channel)
        .orElseThrow(UnbelievableException.because("Cannot create an address without a channel"));
    this.id = Optional.ofNullable(id)
        .orElseThrow(UnbelievableException.because("Cannot create an address without an id"));
  }

  public String channel() {
    return this.channel;
  }

  public String id() {
    return this.id;
  }

  public boolean isVirtual() {
    return VIRTUAL_CHANNEL.equals(this.channel);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return channel.equals(address.channel) &&
        id.equals(address.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, id);
  }

  @JsonValue
  @Override
  public String toString() {
    return channel + ":" + id;
  }

  @JsonCreator
  public static Address fromString(String string) {
    if (string == null || string.isEmpty()) {
      throw new UnbelievableException("Cannot create an address with an empty string");
    }
    Matcher matcher = PATTERN.matcher(string);
    if (matcher.matches()) {
      try {
        String transmitter = matcher.group("channel");
        String id = matcher.group("id");
        return new Address(transmitter, id);
      } catch (IllegalStateException e) {
        throw new UnbelievableException("Cannot parse address: " + string, e);
      }
    }
    return new Address(VIRTUAL_CHANNEL, string);
  }

}
