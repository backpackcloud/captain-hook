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

public class AddressTest {

  @Test
  public void test() {
    Spec.describe(Address.class)
        .because("Addresses from string should have at least an ID")
        .then(() -> Address.fromString("")).willFail()
        .then(() -> Address.fromString(null)).willFail()

        .given(Address.fromString("bar"))
        .because("The default channel should be assigned if not provided")
        .expect("default").from(Address::channel)
        .expect("bar").from(Address::id)

        .given(Address.fromString("foo:bar"))
        .because("The pattern channel:id should be used to parse the address")
        .expect("foo").from(Address::channel)
        .expect("bar").from(Address::id);
  }

}
