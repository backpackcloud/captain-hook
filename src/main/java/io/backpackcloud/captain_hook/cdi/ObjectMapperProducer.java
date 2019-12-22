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

package io.backpackcloud.captain_hook.cdi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.backpackcloud.captain_hook.Serializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class ObjectMapperProducer {

  @Produces
  @Singleton
  public ObjectMapper getObjectMapper(Serializer serializer) {
    return serializer.json();
  }

  @Singleton
  @Produces
  public Serializer get() {
    final InjectableValues.Std values = new InjectableValues.Std();

    final ObjectMapper jsonMapper = new ObjectMapper();
    jsonMapper.registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule());
    jsonMapper.setInjectableValues(values);

    final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    yamlMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    yamlMapper.setInjectableValues(values);

    final ObjectMapper xmlMapper = new XmlMapper();
    xmlMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    xmlMapper.setInjectableValues(values);

    Serializer serializer = new Serializer() {

      @Override
      public ObjectMapper json() {
        return jsonMapper;
      }

      @Override
      public ObjectMapper yaml() {
        return yamlMapper;
      }

      @Override
      public ObjectMapper xml() {
        return xmlMapper;
      }

      @Override
      public Serializer addDependency(String name, Object value) {
        values.addValue(name, value);
        return this;
      }

    };
    return serializer.addDependency("serializer", serializer);
  }

}
