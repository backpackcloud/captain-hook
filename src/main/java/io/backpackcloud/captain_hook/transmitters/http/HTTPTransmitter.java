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

package io.backpackcloud.captain_hook.transmitters.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.Transmitter;
import io.backpackcloud.captain_hook.UnbelievableException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RegisterForReflection
public class HTTPTransmitter implements Transmitter {

  private final String url;
  private final Map<String, String> headers;
  private final boolean route;

  @JsonCreator
  public HTTPTransmitter(@JsonProperty("url") String url,
                         @JsonProperty("headers") Map<String, String> headers) {
    this.url = url;
    this.headers = Optional.ofNullable(headers)
        .orElseGet(Collections::emptyMap);
    this.route = url.contains("{destination}");
  }

  @Override
  public void fire(Notification notification) {
    try {
      Writer writer = new StringWriter();

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(writer, notification);

      HttpRequestWithBody post = Unirest.post(url);

      if (route) post.routeParam("destination", notification.destination().id());

      post.headers(headers)
          .header("Content-Type", "application/json")
          .body(writer.toString())
          .asEmpty();
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }

  }

}
