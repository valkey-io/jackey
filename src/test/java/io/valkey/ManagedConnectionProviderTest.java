package io.valkey;

import io.valkey.providers.ManagedConnectionProvider;
import io.valkey.util.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ManagedConnectionProviderTest {

  private Connection connection;

  @Before
  public void setUp() {
    connection = new Connection(HostAndPorts.getRedisServers().get(0),
        DefaultJedisClientConfig.builder().user("acljedis").password("fizzbuzz").build());
  }

  @After
  public void tearDown() {
    IOUtils.closeQuietly(connection);
  }

  @Test
  public void test() {
    ManagedConnectionProvider managed = new ManagedConnectionProvider();
    try (UnifiedJedis jedis = new UnifiedJedis(managed)) {
      try {
        jedis.get("any");
        Assert.fail("Should get NPE.");
      } catch (NullPointerException npe) { }
      managed.setConnection(connection);
      Assert.assertNull(jedis.get("any"));
    }
  }
}
