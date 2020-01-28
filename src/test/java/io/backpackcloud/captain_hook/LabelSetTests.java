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

import java.util.HashMap;
import java.util.Map;

public class LabelSetTests {

  @Test
  public void testInstance() {
    Map<String, String> values = new HashMap<>();
    values.put("foo", "bar");
    LabelSet labels = LabelSet.of(values);

    Spec.describe(LabelSet.class)

        .given(LabelSet.empty())
        .expect(true).from(LabelSet::isEmpty)

        .given(labels)
        .expect(false).from(LabelSet::isEmpty)
        .expect(1).from(LabelSet::size)
        .expect(values).from(LabelSet::values)
        .expect("bar").from(labelSet -> labelSet.get("foo").get())

        .because("Internal data should not be exposed")
        .waitFor(() -> values.put("bar", "foo"))
        .then(labelSet -> labelSet.get("bar").get()).willFail();
  }

}
