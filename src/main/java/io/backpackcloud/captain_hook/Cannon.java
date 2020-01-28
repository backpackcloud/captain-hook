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

package io.backpackcloud.captain_hook;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Defines a way of firing payloads that can be based or not on notifications.
 * <p>
 * This component is used to abstract HTTP POST interactions.
 */
public interface Cannon {

  /**
   * Loads the given notification as a context so any String information
   * (payload attributes, header values and the url) will be able to be
   * parsed as a template.
   *
   * @param notification the notification to be used as context.
   * @return a new cannon loaded with the given notification.
   */
  LoadedCannon load(Notification notification);

  interface LoadedCannon {

    /**
     * Adds the given http headers.
     *
     * @param headers the headers to add
     * @return this cannon
     */
    LoadedCannon add(Map<String, String> headers);

    /**
     * Sets the url to aim the cannon.
     *
     * @param url the url to aim
     * @return a cannon ready to fire.
     */
    ReadyCannon aimAt(String url);

  }

  /**
   * A ready to fire cannon.
   */
  interface ReadyCannon {

    /**
     * Fires the given payload.
     *
     * @param payload the payload to fire
     * @return the response
     */
    Response fire(Map<String, ?> payload);

  }

  /**
   * Defines a very simplified response of an http interaction.
   */
  interface Response {

    /**
     * Returns the status code of the response.
     *
     * @return the status code of the response.
     */
    int status();

    /**
     * Returns the message of the response.
     *
     * @return the message of the response.
     */
    String message();

    /**
     * Passes this response to the given consumer.
     *
     * @param consumer the consumer to accept this response.
     */
    default void then(Consumer<Response> consumer) {
      consumer.accept(this);
    }

  }

}
