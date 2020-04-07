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

package io.backpackcloud.captain_hook.transmitters.telegram;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.SensitiveValue;
import io.backpackcloud.captain_hook.TemplateEngine;
import io.backpackcloud.captain_hook.Transmitter;
import io.backpackcloud.captain_hook.UnbelievableException;
import org.jboss.logging.Logger;

import javax.ws.rs.WebApplicationException;
import java.util.Optional;

public class TelegramTransmitter implements Transmitter {

  private static final Logger logger = Logger.getLogger(TelegramTransmitter.class);

  private final String token;
  private final String template;
  private final TemplateEngine templateEngine;
  private final TelegramService telegramService;

  @JsonCreator
  public TelegramTransmitter(@JsonProperty("token") SensitiveValue token,
                             @JsonProperty("template") String template,
                             @JacksonInject("templateEngine") TemplateEngine templateEngine,
                             @JacksonInject("telegramService") TelegramService telegramService) {
    this.token = token.value();
    this.template = Optional.ofNullable(template).orElse("telegram/notification.ftl");
    this.templateEngine = templateEngine;
    this.telegramService = telegramService;
  }

  @Override
  public void fire(Notification notification) {
    logger.infov("Sending message to {0}", notification.target());
    try {
      String content = templateEngine.evaluate(template, notification.context());
      TelegramMessage message = new TelegramMessage(notification.target(), content);
      telegramService.send(token, message);
    } catch (WebApplicationException e) {
      logger.errorv(e, "Got HTTP Status {0} while sending pushover notification", e.getResponse().getStatus());
    } catch (Exception e) {
      logger.error("Error while processing template", e);
      throw new UnbelievableException(e);
    }
  }

  @Override
  public boolean isUp() {
    try {
      TelegramResponse<User> result = telegramService.getMe(token);
      return result.ok();
    } catch (WebApplicationException e) {
      logger.error("Error while checking transmitter", e);
      return false;
    }
  }

}
