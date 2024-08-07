package io.valkey.modules.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.valkey.search.aggr.Reducer;
import io.valkey.search.aggr.Reducers;
import org.junit.Test;

public class AggregationBuilderTest {

  @Test
  public void reducerObject() {
    Reducer reducer = Reducers.sum("@count").as("total");
    assertEquals("SUM", reducer.getName());
    assertEquals("@count", reducer.getField());
    assertEquals("total", reducer.getAlias());
  }

  @Test
  public void countObject() {
    Reducer count = Reducers.count();
    assertEquals("COUNT", count.getName());
    assertNull(count.getField());
    assertNull(count.getAlias());
  }
}
