package io.valkey.modules.graph;

import static org.junit.Assert.assertEquals;

import io.valkey.RedisProtocol;
import io.valkey.graph.Record;
import io.valkey.graph.ResultSet;
import io.valkey.modules.RedisModuleCommandsTestBase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class GraphValuesTest extends RedisModuleCommandsTestBase {

  @BeforeClass
  public static void prepare() {
    RedisModuleCommandsTestBase.prepare();
  }

  public GraphValuesTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Test
  public void parseInfinity() {
    ResultSet rs = client.graphQuery("db", "RETURN 10^100000");
    assertEquals(1, rs.size());
    Record r = rs.iterator().next();
    assertEquals(Double.POSITIVE_INFINITY, r.getValue(0), 0d);
  }

  @Test
  public void parseInfinity2() {
    ResultSet rs = client.graphQuery("db", "RETURN cot(0)");
    assertEquals(1, rs.size());
    Record r = rs.iterator().next();
    assertEquals(Double.POSITIVE_INFINITY, r.getValue(0), 0d);
  }

  @Test
  public void parseNaN() {
    ResultSet rs = client.graphQuery("db", "RETURN asin(-1.1)");
    assertEquals(1, rs.size());
    Record r = rs.iterator().next();
    assertEquals(Double.NaN, r.getValue(0), 0d);
  }

  @Test
  public void parseMinusNaN() {
    ResultSet rs = client.graphQuery("db", "RETURN sqrt(-1)");
    assertEquals(1, rs.size());
    Record r = rs.iterator().next();
    assertEquals(Double.NaN, r.getValue(0), 0d);
  }
}
