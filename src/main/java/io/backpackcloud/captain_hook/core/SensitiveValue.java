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
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RegisterForReflection
public class SensitiveValue {

  private final String value;

  @JsonCreator
  public SensitiveValue(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  @JsonCreator
  public static SensitiveValue create(@JsonProperty("value") String value,
                                      @JsonProperty("env") String env,
                                      @JsonProperty("property") String property,
                                      @JsonProperty("file") String file) throws IOException {
    if (value != null) return new SensitiveValue(value);
    else if (env != null) return new SensitiveValue(System.getenv(env));
    else if (property != null) return new SensitiveValue(System.getProperty(property));
    else if (file != null) return new SensitiveValue(Files.readString(Path.of(file)));
    else throw new UnbelievableException("Unable to populate value");
  }

}
