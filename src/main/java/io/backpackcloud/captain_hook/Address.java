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

/**
 * Represents an address that can receive a notification.
 * <p>
 * Address are structured in two parts: a channel and an id. A channel
 * maps to a {@link Transmitter} so only that transmitter knows how to
 * reach to the given id.
 * <p>
 * An address can also be virtual, which means it points to one or more addresses
 * (which can also be virtual addresses as well) in order to simplify configurations.
 */
@RegisterForReflection
public class Address {

  /**
   * Channel name for representing virtual addresses.
   */
  public static final String VIRTUAL_CHANNEL = "virtual";

  private final String channel;
  private final String id;

  public Address(String channel, String id) {
    this.channel = Optional.ofNullable(channel)
        .orElseThrow(UnbelievableException.because("Cannot create an address without a channel"));
    this.id = Optional.ofNullable(id)
        .orElseThrow(UnbelievableException.because("Cannot create an address without an id"));
  }

  /**
   * Returns the channel of this addresses.
   * <p>
   * A channel references a {@link Transmitter} so this address is reached
   * only if a transmitter for this channel is supplied.
   * <p>
   * Virtual addresses have a channel of {@link #VIRTUAL_CHANNEL}.
   *
   * @return the channel of this address.
   */
  public String channel() {
    return this.channel;
  }

  /**
   * Returns the target of this address. The unique endpoint that can be reached
   * through this address.
   *
   * @return the target of this address.
   */
  public String id() {
    return this.id;
  }

  /**
   * Checks if this address has a virtual channel, which means it must be resolved
   * with a {@link VirtualAddress}.
   *
   * @return {@code true} if this address is a virtual address.
   */
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
    String channel = VIRTUAL_CHANNEL;
    String id = string;

    String[] split = string.split(":", 2);
    if (split.length == 2) {
      channel = split[0];
      id = split[1];
    }

    return new Address(channel, id);
  }

}
