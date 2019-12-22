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

package io.backpackcloud.captain_hook.transmitters.slack;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.SensitiveValue;
import io.backpackcloud.captain_hook.TemplateEngine;
import io.backpackcloud.captain_hook.Transmitter;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;

import java.util.Optional;

public class SlackTransmitter implements Transmitter {

  private final String token;
  private final String username;
  private final String template;
  private final TemplateEngine templateEngine;

  @JsonCreator
  public SlackTransmitter(@JsonProperty("token") SensitiveValue token,
                          @JsonProperty("username") String username,
                          @JsonProperty("template") String template,
                          @JacksonInject("templateEngine") TemplateEngine templateEngine) {
    this.token = token.value();
    this.username = username;
    this.template = Optional.ofNullable(template).orElse("slack/notification.ftl");
    this.templateEngine = templateEngine;
  }

  @Override
  public void fire(Notification notification) {
    MultipartBody post = Unirest.post("https://slack.com/api/chat.postMessage")
        .field("token", this.token)
        .field("channel", notification.destination().id())
        .field("text", templateEngine.evaluate(template, notification.context()));

    if (username != null) {
      post.field("as_user", "false")
          .field("username", username);
    } else {
      post.field("as_user", "true");
    }

    post.asEmptyAsync();
  }

}
