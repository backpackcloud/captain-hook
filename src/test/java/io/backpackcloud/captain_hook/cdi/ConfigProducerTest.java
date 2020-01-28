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

import io.backpackcloud.captain_hook.Cannon;
import io.backpackcloud.captain_hook.CaptainHook;
import io.backpackcloud.captain_hook.Mapper;
import io.backpackcloud.captain_hook.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigProducerTest {

  String somePath = "/some/path";
  CaptainHook captainHook = new CaptainHook(
      Collections.emptyList(), Collections.emptyList(), Collections.emptyMap()
  );

  Serializer serializer = mock(Serializer.class, Answers.RETURNS_MOCKS);
  Mapper yamlMapper = mock(Mapper.class, Answers.RETURNS_MOCKS);
  Cannon cannon = mock(Cannon.class);

  @BeforeEach
  void init() {
    when(serializer.yaml()).thenReturn(yamlMapper);
    when(yamlMapper.deserialize(new File(somePath), CaptainHook.class)).thenReturn(captainHook);
  }

  @Test
  public void test() {
    assertSame(captainHook, new ConfigProducer(somePath).getConfig(serializer, cannon));
  }

}
