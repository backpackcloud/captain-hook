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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Serializer;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.Transmitter;
import io.backpackcloud.captain_hook.UnbelievableException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import kong.unirest.Empty;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RegisterForReflection
public class HTTPTransmitter implements Transmitter {

  private final String url;
  private final Map<String, String> headers;
  private final boolean route;
  private final Serializer serializer;

  private boolean error;

  @JsonCreator
  public HTTPTransmitter(@JsonProperty("url") String url,
                         @JsonProperty("headers") Map<String, String> headers,
                         @JacksonInject("serializer") Serializer serializer) {
    this.url = url;
    this.headers = Optional.ofNullable(headers)
        .orElseGet(Collections::emptyMap);
    this.route = url.contains("{destination}");
    this.serializer = serializer;
  }

  @Override
  public void fire(Notification notification) {
    try {
      Writer writer = new StringWriter();

      serializer.json().writeValue(writer, notification);

      HttpRequestWithBody post = Unirest.post(url);

      if (route) post.routeParam("destination", notification.destination().id());

      CompletableFuture<HttpResponse<Empty>> future = post.headers(headers)
          .header("Content-Type", "application/json")
          .body(writer.toString())
          .asEmptyAsync();

      future.whenCompleteAsync(
          (response, exception) -> error = response.getStatus() % 500 < 100);
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }

  }

  @Override
  public boolean isUp() {
    return !error;
  }

}
