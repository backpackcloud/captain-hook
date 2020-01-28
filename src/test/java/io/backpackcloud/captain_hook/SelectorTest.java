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

import io.backpackcloud.spectaculous.Operation;
import io.backpackcloud.spectaculous.Spec;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectorTest {

  Operation<Selector, Boolean> test(LabelSet labelSet) {
    return selector -> selector.test(labelSet);
  }

  @Test
  public void testSelection() {
    LabelSet labels = LabelSet.empty();
    Predicate<LabelSet> ok = mock(Predicate.class);
    Predicate<LabelSet> notOk = mock(Predicate.class);

    when(ok.test(labels)).thenReturn(true);
    when(notOk.test(labels)).thenReturn(false);

    Spec.describe(Selector.class)

        .given(new Selector(Arrays.asList(ok, ok, ok, ok)))
        .because("All predicates should accept the label set")
        .expect(true).from(test(labels))

        .given(new Selector(Arrays.asList(ok, ok, notOk, ok)))
        .expect(false).from(test(labels))

        .given(Selector.empty())
        .because("Empty selector should match any label set")
        .expect(true).from(test(labels))
        .expect(true).from(test(LabelSet.empty()));
  }

  @Test
  public void testPredicateCreation() {
    Map<String, String> values = new HashMap<>();
    values.put("foo", "bar");
    values.put("bar", "foo");
    LabelSet labels = LabelSet.of(values);

    Spec.describe(Selector.class)

        .given(Selector.from(values))

        .because("All values should be tested")
        .expect(true).from(test(labels))

        .given(selector("foo:*", "bar:*"))

        .because("Wildcard should accept any values")
        .expect(true).from(test(labels))

        .given(selector("foo:!", "bar:!"))
        .because("Exclamation mark rejects any value")
        .expect(false).from(test(labels))

        .given(selector("baz:!", "test:!"))
        .expect(true).from(test(labels))

        .given(selector("foo:baz|bar"))
        .because("Pipe should defines a set of allowed values")
        .expect(true).from(test(labels))

        .given(selector("bar:baz|bar"))
        .expect(false).from(test(labels));
  }

  private Selector selector(String... values) {
    Map map = Arrays.stream(values)
                    .map(v -> {
                      String[] split = v.split(":");
                      return new SimpleEntry(split[0], split[1]);
                    })
                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    return Selector.from(map);
  }

}
