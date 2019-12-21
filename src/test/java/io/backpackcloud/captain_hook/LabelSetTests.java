package io.backpackcloud.captain_hook;

import io.backpackcloud.kodo.Spec;
import org.junit.jupiter.api.Test;

public class LabelSetTests {

  @Test
  public void testEmptyLabelSet() {
    Spec.given(LabelSet.empty())
        .expect(LabelSet::isEmpty);
  }

}
