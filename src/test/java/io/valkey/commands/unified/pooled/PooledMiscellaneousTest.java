package io.valkey.commands.unified.pooled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.valkey.AbstractPipeline;
import io.valkey.AbstractTransaction;
import io.valkey.RedisProtocol;
import io.valkey.Response;
import io.valkey.commands.unified.UnifiedJedisCommandsTestBase;
import io.valkey.exceptions.JedisDataException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PooledMiscellaneousTest extends UnifiedJedisCommandsTestBase {

  public PooledMiscellaneousTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Before
  public void setUp() {
    jedis = PooledCommandsTestHelper.getPooled(protocol);
    PooledCommandsTestHelper.clearData();
  }

  @After
  public void cleanUp() {
    jedis.close();
  }

  @Test
  public void pipeline() {
    final int count = 10;
    int totalCount = 0;
    for (int i = 0; i < count; i++) {
      jedis.set("foo" + i, "bar" + i);
    }
    totalCount += count;
    for (int i = 0; i < count; i++) {
      jedis.rpush("foobar" + i, "foo" + i, "bar" + i);
    }
    totalCount += count;

    List<Response<?>> responses = new ArrayList<>(totalCount);
    List<Object> expected = new ArrayList<>(totalCount);

    try (AbstractPipeline pipeline = jedis.pipelined()) {
      for (int i = 0; i < count; i++) {
        responses.add(pipeline.get("foo" + i));
        expected.add("bar" + i);
      }
      for (int i = 0; i < count; i++) {
        responses.add(pipeline.lrange("foobar" + i, 0, -1));
        expected.add(Arrays.asList("foo" + i, "bar" + i));
      }
      pipeline.sync();
    }

    for (int i = 0; i < totalCount; i++) {
      assertEquals(expected.get(i), responses.get(i).get());
    }
  }

  @Test
  public void transaction() {
    final int count = 10;
    int totalCount = 0;
    for (int i = 0; i < count; i++) {
      jedis.set("foo" + i, "bar" + i);
    }
    totalCount += count;
    for (int i = 0; i < count; i++) {
      jedis.rpush("foobar" + i, "foo" + i, "bar" + i);
    }
    totalCount += count;

    List<Object> responses;
    List<Object> expected = new ArrayList<>(totalCount);

    try (AbstractTransaction transaction = jedis.multi()) {
      for (int i = 0; i < count; i++) {
        transaction.get("foo" + i);
        expected.add("bar" + i);
      }
      for (int i = 0; i < count; i++) {
        transaction.lrange("foobar" + i, 0, -1);
        expected.add(Arrays.asList("foo" + i, "bar" + i));
      }
      responses = transaction.exec();
    }

    for (int i = 0; i < totalCount; i++) {
      assertEquals(expected.get(i), responses.get(i));
    }
  }

  @Test
  public void watch() {
    List<Object> resp;
    try (AbstractTransaction tx = jedis.transaction(false)) {
      assertEquals("OK", tx.watch("mykey", "somekey"));
      tx.multi();

      jedis.set("mykey", "bar");

      tx.set("mykey", "foo");
      resp = tx.exec();
    }
    assertNull(resp);
    Assert.assertEquals("bar", jedis.get("mykey"));
  }

  @Test
  public void broadcast() {

    String script_1 = "return 'jedis'";
    String sha1_1 = jedis.scriptLoad(script_1);

    String script_2 = "return 79";
    String sha1_2 = jedis.scriptLoad(script_2);

    Assert.assertEquals(Arrays.asList(true, true), jedis.scriptExists(Arrays.asList(sha1_1, sha1_2)));

    jedis.scriptFlush();

    Assert.assertEquals(Arrays.asList(false, false), jedis.scriptExists(Arrays.asList(sha1_1, sha1_2)));
  }

  @Test
  public void broadcastWithError() {
    JedisDataException error = assertThrows(JedisDataException.class,
        () -> jedis.functionDelete("xyz"));
    assertEquals("ERR Library not found", error.getMessage());
  }
}
