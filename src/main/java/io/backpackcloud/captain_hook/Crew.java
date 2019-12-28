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

import io.backpackcloud.captain_hook.api.JollyRoger;
import io.vertx.axle.core.eventbus.EventBus;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Crew is responsible for dealing with everything that boards the ship.
 * <p>
 * Webhooks will be converted into events, events will be converted into notifications
 * and notifications will be delivered. Anything else will be walking the plank.
 *
 * @see JollyRoger
 */
@ApplicationScoped
public class Crew {

  private static final Logger logger = Logger.getLogger(Crew.class);

  private final CaptainHook captainHook;

  private final TemplateEngine templateEngine;

  private final EventBus eventBus;

  /**
   * The crew needs the orders from the Captain in order
   *
   * @param captainHook    the orders from the captain so the crew can obey
   * @param templateEngine the template engine for parsing webhooks
   * @param eventBus       the event bus to use
   */
  @Inject
  public Crew(CaptainHook captainHook, TemplateEngine templateEngine, EventBus eventBus) {
    this.captainHook = captainHook;
    this.templateEngine = templateEngine;
    this.eventBus = eventBus;
  }

  /**
   * Handles a notification that comes aboard, resolving virtual addresses
   * as needed.
   *
   * @param notification the notification for delivery
   */
  public void handle(Notification notification) {
    if (notification.destination().isVirtual()) {
      VirtualAddress virtualAddress = captainHook.virtualAddresses()
          .getOrDefault(notification.destination().id(), VirtualAddress.NULL);
      for (Address newAddress : virtualAddress.addresses()) {
        if (notification.destination().equals(newAddress)) {
          throw new UnbelievableException("Cannot redirect to the same address");
        }
        handle(notification.changeAddress(newAddress));
      }
    } else {
      if (captainHook.transmitters().containsKey(notification.destination().channel())) {
        deliver(notification);
      }
    }
  }

  /**
   * Delivers the notification. It doesn't resolve virtual addresses nor addresses without
   * transmitters available.
   * <p>
   * Captain Hook ordered the crew to count the actual delivers so that's why this
   * method is here.
   *
   * @param notification the notification to fire
   */
  @Counted(name = "notifications", description = "How many notifications were fired")
  public void deliver(Notification notification) {
    logger.infov("Delivering notification for {0}", notification.destination());
    eventBus.sender("fire").write(notification);
  }

  /**
   * Analyses the given event and fire notifications to its subscribers.
   *
   * @param event the event to handle
   * @return a list containing the notifications fired
   */
  @Counted(name = "events", description = "How many events were fired")
  public List<Notification> handle(Event event) {
    logger.infov("Handling event {0}", event.name());
    return captainHook.subscriptions().stream()
        .map(subscription -> subscription.yield(event))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .peek(this::handle)
        .collect(Collectors.toList());
  }

  /**
   * Analyses the given webhook and produces events according to the orders defined by the captain.
   * @param webhook the webhook to handle
   * @return a list of the events raised by this webhook
   */
  @Counted(name = "webhooks", description = "How many webhooks were received")
  public List<Event> handle(Webhook webhook) {
    logger.infov("Handling webhook");
    return captainHook.webhooks().stream()
        .filter(mapping -> mapping.matches(webhook))
        .map(WebhookMapping::event)
        .map(mapping -> {
          Map<String, String> eventLabels = new HashMap<>();

          mapping.labels().values()
              .forEach((key, value) -> eventLabels.put(key, templateEngine.evaluate(value, webhook.payload())));

          return new Event(LabelSet.of(eventLabels),
              templateEngine.evaluate(mapping.name(), webhook.payload()),
              templateEngine.evaluate(mapping.message(), webhook.payload()),
              templateEngine.evaluate(mapping.title().orElse(""), webhook.payload()),
              templateEngine.evaluate(mapping.url().orElse(""), webhook.payload()));
        })
        .peek(this::handle)
        .collect(Collectors.toList());
  }

}
