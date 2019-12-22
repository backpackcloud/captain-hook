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

package io.backpackcloud.captain_hook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.backpackcloud.captain_hook.Address;
import io.backpackcloud.captain_hook.Crew;
import io.backpackcloud.captain_hook.Event;
import io.backpackcloud.captain_hook.LabelSet;
import io.backpackcloud.captain_hook.Serializer;
import io.backpackcloud.captain_hook.Notification;
import io.backpackcloud.captain_hook.UnbelievableException;
import io.backpackcloud.captain_hook.Webhook;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Captain's ship. If you wanna board something, be sure to do it right :)
 */
@Path("/")
@ApplicationScoped
public class JollyRoger {

  private static final Logger logger = Logger.getLogger(JollyRoger.class);

  private final Crew crew;

  private final Serializer serializer;

  @Inject
  public JollyRoger(Crew crew, Serializer serializer) {
    this.crew = crew;
    this.serializer = serializer;
  }

  /**
   * Boards the given notification. If the notification is ok, thw crew will handle it.
   *
   * @param notification the notification to handle
   * @return the operation's response
   */
  @POST
  @Path("/notifications")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timed(name = "notificationTimer",
      unit = MetricUnits.MILLISECONDS,
      description = "Measure of how long it takes for the crew to be aware of a notification.")
  public Response process(Notification notification) {
    logger.infov("Received notification for {0}", notification.destination());
    crew.handle(notification);
    return Response.ok().build();
  }

  /**
   * Boards the given event. If the event is ok, the crew will handle it.
   *
   * @param event the event to handle
   * @return a response containing the list of addresses notified about the given event.
   */
  @POST
  @Path("/events")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Timed(name = "eventTimer",
      unit = MetricUnits.MILLISECONDS,
      description = "Measure of how long it takes for the crew to be aware of an event.")
  public Response process(Event event) {
    logger.infov("Received event {0}", event.name());

    Set<Address> addresses = crew.handle(event).stream()
        .map(Notification::destination)
        .collect(Collectors.toSet());

    if (addresses.isEmpty()) return Response.noContent().build();

    return Response.ok(addresses).build();
  }

  /**
   * Boards the given webhook. If the webhook is ok, the crew will handle it.
   * <p>
   * The webhook should have a JSON payload, which the Captain prefer, or an XML.
   * <p>
   * Be careful if you pass an XML, the Captain might order you to walk the plank.
   *
   * @param uriInfo the information about the url
   * @param headers the http headers
   * @param payload the webhook's payload
   * @return a response containing the list of events generated by the given webhook
   */
  @POST
  @Path("/webhooks")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Timed(name = "webhookTimer",
      unit = MetricUnits.MILLISECONDS,
      description = "Measure of how long it takes for the crew to be aware of a webhook.")
  public Response process(@Context UriInfo uriInfo,
                          @Context HttpHeaders headers,
                          String payload) {
    logger.infov("Received webhook");
    Map<String, ?> payloadData;
    ObjectMapper objectMapper;

    if (MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType())) {
      objectMapper = serializer.xml();
    } else {
      objectMapper = serializer.json();
    }

    try {
      JsonNode node = objectMapper.readTree(payload);
      payloadData = objectMapper.treeToValue(node, Map.class);

      Map<String, String> labelMap = new HashMap<>();

      headers.getRequestHeaders().entrySet().stream()
          .filter(entry -> !entry.getValue().isEmpty())
          .forEach(entry -> labelMap.put(entry.getKey(), entry.getValue().get(0)));

      uriInfo.getQueryParameters().entrySet().stream()
          .filter(entry -> !entry.getValue().isEmpty())
          .forEach(entry -> labelMap.put(entry.getKey(), entry.getValue().get(0)));

      Webhook webhook = new Webhook(LabelSet.of(labelMap), payloadData);

      List<Event> events = crew.handle(webhook);

      if (events.isEmpty()) return Response.noContent().build();

      return Response.ok(new HashSet<>(events)).build();
    } catch (Exception e) {
      throw new UnbelievableException(e);
    }
  }

}
