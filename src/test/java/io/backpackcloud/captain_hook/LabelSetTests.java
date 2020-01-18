package io.backpackcloud.captain_hook;

import io.backpackcloud.spectaculous.Spec;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class LabelSetTests {

  @Test
  public void testInstance() {
    Map<String, String> values = new HashMap<>();
    values.put("foo", "bar");
    Spec.describe(LabelSet.class)

        .given(LabelSet.empty())
        .expect(true).from(LabelSet::isEmpty)

        .given(LabelSet.of(values))
        .expect(false).from(LabelSet::isEmpty)
        .expect(1).from(LabelSet::size)
        .expect(values).from(LabelSet::values)

        .because("Internal map is a copy of the given map")
        .expect("bar").from(labelSet -> labelSet.get("foo").get())

        .then(() -> values.put("bar", "foo"))

        .expect(NoSuchElementException.class).when(labelSet -> labelSet.get("bar").get());
  }

}
