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

package io.backpackcloud.captain_hook.cdi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.backpackcloud.captain_hook.Mapper;
import io.backpackcloud.captain_hook.Plank;
import io.backpackcloud.captain_hook.Serializer;
import io.backpackcloud.captain_hook.TemplateEngine;
import io.backpackcloud.captain_hook.UnbelievableException;
import io.backpackcloud.captain_hook.transmitters.pushover.PushoverService;
import io.backpackcloud.captain_hook.transmitters.telegram.TelegramService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@ApplicationScoped
public class SerializerProducer {

  private static final Logger logger = Logger.getLogger(SerializerProducer.class);

  private final ObjectMapper jsonMapper;
  private final ObjectMapper yamlMapper;
  private final ObjectMapper xmlMapper;
  private final InjectableValues.Std values;

  private final Serializer serializer;

  public SerializerProducer() {
    values = new InjectableValues.Std();

    jsonMapper = new ObjectMapper();
    jsonMapper.registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule());
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonMapper.setInjectableValues(values);

    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    yamlMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    yamlMapper.setInjectableValues(values);

    xmlMapper = new XmlMapper();
    xmlMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    xmlMapper.setInjectableValues(values);

    serializer = new SerializerImpl();
  }

  @Produces
  @Singleton
  public ObjectMapper getObjectMapper() {
    return jsonMapper;
  }

  @Singleton
  @Produces
  public Serializer get(TemplateEngine templateEngine,
                        @RestClient TelegramService telegramService,
                        @RestClient PushoverService pushoverService,
                        Plank plank) {
    serializer.addDependency("serializer", serializer);
    serializer.addDependency("templateEngine", templateEngine);
    serializer.addDependency("telegramService", telegramService);
    serializer.addDependency("pushoverService", pushoverService);
    serializer.addDependency("plank", plank);
    return serializer;
  }

  class SerializerImpl implements Serializer {

    @Override
    public Mapper json() {
      return new MapperImpl(jsonMapper);
    }

    @Override
    public Mapper yaml() {
      return new MapperImpl(yamlMapper);
    }

    @Override
    public Mapper xml() {
      return new MapperImpl(xmlMapper);
    }

    @Override
    public Serializer addDependency(String name, Object dependency) {
      logger.infov("Adding dependency: {0}", name);
      values.addValue(name, dependency);
      return this;
    }

  }

  static class MapperImpl implements Mapper {

    private final ObjectMapper objectMapper;

    MapperImpl(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(Object object) {
      Writer writer = new StringWriter();
      try {
        objectMapper.writeValue(writer, object);
      } catch (IOException e) {
        logger.error("Error on serialize", e);
        throw new UnbelievableException(e);
      }
      return writer.toString();
    }

    @Override
    public <E> E deserialize(String input, Class<E> type) {
      try {
        return objectMapper.readValue(input, type);
      } catch (IOException e) {
        logger.error("Error on deserialize", e);
        throw new UnbelievableException(e);
      }
    }

    @Override
    public <E> E deserialize(File file, Class<E> type) {
      try {
        return objectMapper.readValue(file, type);
      } catch (IOException e) {
        logger.error("Error on deserialize", e);
        throw new UnbelievableException(e);
      }
    }

  }

}
