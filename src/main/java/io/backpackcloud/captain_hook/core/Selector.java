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

package io.backpackcloud.captain_hook.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@RegisterForReflection
public class Selector implements Predicate<LabelSet> {

  private final List<Predicate<LabelSet>> predicates;

  public Selector(List<Predicate<LabelSet>> predicates) {
    this.predicates = predicates;
  }

  @Override
  public boolean test(LabelSet labelSet) {
    return predicates.stream().allMatch(p -> p.test(labelSet));
  }

  @JsonCreator
  public static Selector from(Map<String, String> values) {
    List<Predicate<LabelSet>> predicates = new ArrayList<>(values.size());

    values.forEach((key, value) -> {
      Predicate<LabelSet> predicate = null;

      for (String v : value.split("\\s*\\|\\s*")) {
        if (predicate == null) {
          predicate = createPredicate(key, v);
        } else {
          predicate = predicate.or(createPredicate(key, v));
        }
      }

      predicates.add(predicate);
    });

    return new Selector(predicates);
  }

  private static Predicate<LabelSet> createPredicate(String key, String value) {
    switch (value) {
      case "*":
        return labelSet -> labelSet.get(key).isPresent();
      case "!":
        return labelSet -> labelSet.get(key).isEmpty();
      default:
        if (value.startsWith("!")) return createPredicate(key, value.substring(1)).negate();
        return labelSet -> labelSet.get(key).filter(value::equals).isPresent();
    }
  }

  public static Selector empty() {
    return new Selector(Collections.emptyList());
  }

}
