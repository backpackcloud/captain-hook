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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import io.backpackcloud.captain_hook.TemplateEngine;
import io.backpackcloud.captain_hook.Transmitter;
import io.backpackcloud.captain_hook.UnbelievableException;
import io.backpackcloud.trugger.element.ElementCopy;
import io.backpackcloud.trugger.element.Elements;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class TemplateEngineProducer {

  private static final Logger logger = Logger.getLogger(TemplateEngineProducer.class);

  @Produces
  @Singleton
  public TemplateEngine getEngine() {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_29).build());
    cfg.setClassForTemplateLoading(Transmitter.class, "/io/backpackcloud/captain_hook/transmitters/");

    return new TemplateEngine() {
      @Override
      public String evaluate(String templateString, Map<String, ?> context) {
        if (templateString == null) return null;

        Template template;
        Writer out = new StringWriter();

        try {
          if (templateString.endsWith(".ftl")) template = cfg.getTemplate(templateString);
          else template = new Template("template", new StringReader(templateString), cfg);

          template.process(context, out);
        } catch (Exception e) {
          logger.error("Error while processing template", e);
          throw new UnbelievableException(e);
        }

        return out.toString();
      }

      @Override
      public Map evaluate(Map data, Map<String, ?> context) {
        Map<String, ?> result = new HashMap<>(context.size());

        Elements.copy()
            .from(data)
            .notNull()
            .map(copy -> evalTemplate(copy, context))
            .to(result);

        return result;
      }

      private Object evalTemplate(ElementCopy copy, Map<String, ?> context) {
        if (copy.value() instanceof String) {
          return evaluate((String) copy.value(), context);
        } else if (copy.value() instanceof Map) {
          Map result = new HashMap();
          Elements.copy()
              .from(copy.value())
              .notNull()
              .map(elementCopy -> evalTemplate(elementCopy, context))
              .to(result);
          return result;
        }
        return copy.value();
      }
    };
  }

}
