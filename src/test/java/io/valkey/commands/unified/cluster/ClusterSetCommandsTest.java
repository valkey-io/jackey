package io.valkey.commands.unified.cluster;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import io.valkey.RedisProtocol;
import io.valkey.commands.unified.SetCommandsTestBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ClusterSetCommandsTest extends SetCommandsTestBase {

  final byte[] bfoo = { 0x01, 0x02, 0x03, 0x04 };
  final byte[] bfoo_same_hashslot = { 0x01, 0x02, 0x03, 0x04, 0x03, 0x00, 0x03, 0x1b };
  final byte[] ba = { 0x0A };
  final byte[] bb = { 0x0B };
  final byte[] bc = { 0x0C };

  public ClusterSetCommandsTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Before
  public void setUp() {
    jedis = ClusterCommandsTestHelper.getCleanCluster(protocol);
  }

  @After
  public void tearDown() {
    jedis.close();
    ClusterCommandsTestHelper.clearClusterData();
  }

  @Test
  @Override
  public void smove() {
    jedis.sadd("{.}foo", "a");
    jedis.sadd("{.}foo", "b");

    jedis.sadd("{.}bar", "c");

    long status = jedis.smove("{.}foo", "{.}bar", "a");
    assertEquals(status, 1);

    Set<String> expectedSrc = new HashSet<>();
    expectedSrc.add("b");

    Set<String> expectedDst = new HashSet<>();
    expectedDst.add("c");
    expectedDst.add("a");

    Assert.assertEquals(expectedSrc, jedis.smembers("{.}foo"));
    Assert.assertEquals(expectedDst, jedis.smembers("{.}bar"));

    status = jedis.smove("{.}foo", "{.}bar", "a");
    assertEquals(status, 0);
  }

  @Test
  @Override
  public void sinter() {
    jedis.sadd("foo{.}", "a");
    jedis.sadd("foo{.}", "b");

    jedis.sadd("bar{.}", "b");
    jedis.sadd("bar{.}", "c");

    Set<String> expected = new HashSet<>();
    expected.add("b");

    Set<String> intersection = jedis.sinter("foo{.}", "bar{.}");
    assertEquals(expected, intersection);
  }

  @Test
  @Override
  public void sinterstore() {
    jedis.sadd("foo{.}", "a");
    jedis.sadd("foo{.}", "b");

    jedis.sadd("bar{.}", "b");
    jedis.sadd("bar{.}", "c");

    Set<String> expected = new HashSet<>();
    expected.add("b");

    long status = jedis.sinterstore("car{.}", "foo{.}", "bar{.}");
    assertEquals(1, status);

    Assert.assertEquals(expected, jedis.smembers("car{.}"));
  }

  @Test
  @Override
  public void sunion() {
    jedis.sadd("{.}foo", "a");
    jedis.sadd("{.}foo", "b");

    jedis.sadd("{.}bar", "b");
    jedis.sadd("{.}bar", "c");

    Set<String> expected = new HashSet<>();
    expected.add("a");
    expected.add("b");
    expected.add("c");

    Set<String> union = jedis.sunion("{.}foo", "{.}bar");
    assertEquals(expected, union);
  }

  @Test
  @Override
  public void sunionstore() {
    jedis.sadd("{.}foo", "a");
    jedis.sadd("{.}foo", "b");

    jedis.sadd("{.}bar", "b");
    jedis.sadd("{.}bar", "c");

    Set<String> expected = new HashSet<>();
    expected.add("a");
    expected.add("b");
    expected.add("c");

    long status = jedis.sunionstore("{.}car", "{.}foo", "{.}bar");
    assertEquals(3, status);

    Assert.assertEquals(expected, jedis.smembers("{.}car"));
  }

  @Test
  @Override
  public void sdiff() {
    jedis.sadd("foo{.}", "x");
    jedis.sadd("foo{.}", "a");
    jedis.sadd("foo{.}", "b");
    jedis.sadd("foo{.}", "c");

    jedis.sadd("bar{.}", "c");

    jedis.sadd("car{.}", "a");
    jedis.sadd("car{.}", "d");

    Set<String> expected = new HashSet<>();
    expected.add("x");
    expected.add("b");

    Set<String> diff = jedis.sdiff("foo{.}", "bar{.}", "car{.}");
    assertEquals(expected, diff);
  }

  @Test
  @Override
  public void sdiffstore() {
    jedis.sadd("foo{.}", "x");
    jedis.sadd("foo{.}", "a");
    jedis.sadd("foo{.}", "b");
    jedis.sadd("foo{.}", "c");

    jedis.sadd("bar{.}", "c");

    jedis.sadd("car{.}", "a");
    jedis.sadd("car{.}", "d");

    Set<String> expected = new HashSet<>();
    expected.add("x");
    expected.add("b");

    long status = jedis.sdiffstore("tar{.}", "foo{.}", "bar{.}", "car{.}");
    assertEquals(2, status);
    Assert.assertEquals(expected, jedis.smembers("tar{.}"));
  }

  @Test
  public void sintercard() {
    jedis.sadd("foo{.}", "a");
    jedis.sadd("foo{.}", "b");

    jedis.sadd("bar{.}", "a");
    jedis.sadd("bar{.}", "b");
    jedis.sadd("bar{.}", "c");

    long card = jedis.sintercard("foo{.}", "bar{.}");
    assertEquals(2, card);
    long limitedCard = jedis.sintercard(1, "foo{.}", "bar{.}");
    assertEquals(1, limitedCard);

    // Binary
    jedis.sadd(bfoo, ba);
    jedis.sadd(bfoo, bb);

    jedis.sadd(bfoo_same_hashslot, ba);
    jedis.sadd(bfoo_same_hashslot, bb);
    jedis.sadd(bfoo_same_hashslot, bc);

    long bcard = jedis.sintercard(bfoo, bfoo_same_hashslot);
    assertEquals(2, bcard);
    long blimitedCard = jedis.sintercard(1, bfoo, bfoo_same_hashslot);
    assertEquals(1, blimitedCard);
  }

}
