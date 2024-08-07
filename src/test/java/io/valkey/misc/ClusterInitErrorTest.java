package io.valkey.misc;

import java.util.Collections;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.HostAndPorts;
import io.valkey.JedisCluster;
import io.valkey.exceptions.JedisClusterOperationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ClusterInitErrorTest {

  private static final String INIT_NO_ERROR_PROPERTY = "jedis.cluster.initNoError";

  @After
  public void cleanUp() {
    System.getProperties().remove(INIT_NO_ERROR_PROPERTY);
  }

  @Test(expected = JedisClusterOperationException.class)
  public void initError() {
    Assert.assertNull(System.getProperty(INIT_NO_ERROR_PROPERTY));
    try (JedisCluster cluster = new JedisCluster(
        Collections.singleton(HostAndPorts.getRedisServers().get(0)),
        DefaultJedisClientConfig.builder().password("foobared").build())) {
      throw new IllegalStateException("should not reach here");
    }
  }

  @Test
  public void initNoError() {
    System.setProperty(INIT_NO_ERROR_PROPERTY, "");
    try (JedisCluster cluster = new JedisCluster(
        Collections.singleton(HostAndPorts.getRedisServers().get(0)),
        DefaultJedisClientConfig.builder().password("foobared").build())) {
      Assert.assertThrows(JedisClusterOperationException.class, () -> cluster.get("foo"));
    }
  }
}
