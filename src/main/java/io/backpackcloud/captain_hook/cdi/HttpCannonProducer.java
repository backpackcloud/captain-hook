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

package io.backpackcloud.captain_hook.cdi;

import io.backpackcloud.captain_hook.HttpCannon;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.Serializer;
import io.backpackcloud.captain_hook.TemplateEngine;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class HttpCannonProducer {

  @Produces
  public HttpCannon get(Serializer serializer, TemplateEngine templateEngine) {
    HttpCannon httpCannon = new HttpCannonImpl(Collections.emptyMap(), serializer, templateEngine);
    serializer.addDependency("httpCannon", httpCannon);
    return httpCannon;
  }

  private static class HttpCannonImpl implements HttpCannon {

    private final Map<String, ?> context;
    private final Serializer serializer;
    private final TemplateEngine templateEngine;

    private HttpCannonImpl(Map<String, ?> context, Serializer serializer, TemplateEngine templateEngine) {
      this.context = context;
      this.serializer = serializer;
      this.templateEngine = templateEngine;
    }

    @Override
    public HttpCannon load(Notification notification) {
      return new HttpCannonImpl(notification.context(), serializer, templateEngine);
    }

    @Override
    public Options fire(Map<String, ?> payload, Map<String, String> headers) {
      return url -> {
        HttpResponse httpResponse = Unirest.post(templateEngine.evaluate(url, context))
            .headers(templateEngine.evaluate(headers, context))
            .header("Content-Type", "application/json")
            .body(serializer.json().serialize(templateEngine.evaluate(payload, context)))
            .asEmpty();

        return new HttpCannon.Response() {
          @Override
          public int status() {
            return httpResponse.getStatus();
          }

          @Override
          public String message() {
            return httpResponse.getStatusText();
          }
        };
      };
    }

  }

}
