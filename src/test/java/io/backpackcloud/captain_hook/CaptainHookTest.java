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

import io.backpackcloud.spectaculous.Spec;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptainHookTest {

  @Test
  public void test() {
    List<Subscription> subscriptions = new ArrayList<>();
    List<WebhookMapping> webhooks = new ArrayList<>();
    Map<String, Transmitter> transmitters = new HashMap<>();

    subscriptions.add(new Subscription(Selector.empty(), "test", Address.fromString("void"), Priority.NORMAL));
    webhooks.add(new WebhookMapping(Selector.empty(), new Event(LabelSet.empty(), "name", "message", "title", "url")));
    transmitters.put("default", notification -> {});

    Spec.describe(CaptainHook.class)

        .given(new CaptainHook(subscriptions, webhooks, transmitters))

        .expect(subscriptions).from(CaptainHook::subscriptions)
        .expect(webhooks).from(CaptainHook::webhooks)
        .expect(transmitters).from(CaptainHook::transmitters)

        .because("No external modification is allowed")
        .expect(Exception.class).when(captainHook -> captainHook.transmitters().clear())
        .expect(Exception.class).when(captainHook -> captainHook.webhooks().clear())
        .expect(Exception.class).when(captainHook -> captainHook.subscriptions().clear());
  }

}
