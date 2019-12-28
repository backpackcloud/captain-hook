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

package io.backpackcloud.captain_hook.transmitters.pushover;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushoverNotification {

  private final String token;
  private final String user;
  private final String message;
  private final String title;
  private final String url;
  private final int priority;

  public PushoverNotification(String token, String user, String message, String title, String url, int priority) {
    this.token = token;
    this.user = user;
    this.message = message;
    this.title = title;
    this.url = url;
    this.priority = priority;
  }

  @JsonProperty
  public String token() {
    return token;
  }

  @JsonProperty
  public String user() {
    return user;
  }

  @JsonProperty
  public String message() {
    return message;
  }

  @JsonProperty
  public String title() {
    return title;
  }

  @JsonProperty
  public String url() {
    return url;
  }

  @JsonProperty
  public int priority() {
    return priority;
  }

}