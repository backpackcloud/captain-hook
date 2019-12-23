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

import java.io.File;
import java.util.Map;

/**
 * Interface that abstracts how a data type is serialized and deserialized.
 */
public interface Mapper {

  /**
   * Serializes the given object into a String.
   *
   * @param object the object to serialize
   * @return the serialized object.
   */
  String serialize(Object object);

  /**
   * Deserialize the given input into an object of the given class.
   *
   * @param input the input to deserialize
   * @param type  the type of the result object
   * @return the deserialized object.
   */
  <E> E deserialize(String input, Class<E> type);

  /**
   * Deserialize the given file content into an object of the given class.
   *
   * @param file the file containing the input to deserialize
   * @param type the type of the result object
   * @return the deserialized object.
   */
  <E> E deserialize(File file, Class<E> type);

  /**
   * Deserialize the given input into a generic Map of attributes.
   *
   * @param input the input to deserialize
   * @return the deserialized object.
   */
  default Map<String, ?> deserialize(String input) {
    return deserialize(input, Map.class);
  }

}
