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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents data that can be attached to some particles.
 */
@RegisterForReflection
public class LabelSet {

  private final Map<String, String> values;

  private LabelSet() {
    this(Collections.emptyMap());
  }

  @JsonCreator
  public LabelSet(Map<String, String> values) {
    this.values = values;
  }

  /**
   * Gets the value mapped to the given key name in this data instance.
   *
   * @param label the label name
   * @return the value mapped to the given label.
   */
  @JsonIgnore
  public Optional<String> get(String label) {
    return Optional.ofNullable(values.get(label));
  }

  /**
   * Checks if this label set is empty.
   *
   * @return {@code true} if there are no labels in this set, {@code false} otherwise.
   */
  public boolean isEmpty() {
    return this.values.isEmpty();
  }

  /**
   * Creates a new map containing this data.
   *
   * @return a new map containing this data.
   */
  @JsonValue
  public Map<String, String> values() {
    return new HashMap<>(values);
  }

  /**
   * Creates a new data object based on the given map. Further modifications
   * to the given map won't affect the created data.
   *
   * @param values the map to use as a reference
   * @return a new data object
   */
  @JsonCreator
  public static LabelSet of(Map<String, String> values) {
    return values == null ? empty() : new LabelSet(new HashMap<>(values));
  }

  /**
   * Returns the shared empty data instance.
   *
   * @return an empty data instance.
   */
  public static LabelSet empty() {
    return new LabelSet();
  }

}
