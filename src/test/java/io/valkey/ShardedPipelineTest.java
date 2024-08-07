package io.valkey;

import java.util.Arrays;
import java.util.List;

import io.valkey.providers.ShardedConnectionProvider;
import io.valkey.util.Hashing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShardedPipelineTest {

  private static final HostAndPort redis1 = HostAndPorts.getRedisServers().get(0);
  private static final HostAndPort redis2 = HostAndPorts.getRedisServers().get(1);

  private static final ConnectionPoolConfig DEFAULT_POOL_CONFIG = new ConnectionPoolConfig();
  private static final DefaultJedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig
      .builder().password("foobared").build();

  private List<HostAndPort> shards = Arrays.asList(redis1, redis2);

  @Before
  public void setUp() {
    for (HostAndPort shard : shards) {
      try (Jedis j = new Jedis(shard)) {
        j.auth("foobared");
        j.flushAll();
      }
    }
  }

  @Test
  public void shardedPipelineSync() {
    try (ShardedConnectionProvider provider = new ShardedConnectionProvider(shards, DEFAULT_CLIENT_CONFIG)) {
      ShardedPipeline shardedPipeline = new ShardedPipeline(provider);
      
      Response<String> r1 = shardedPipeline.set("key1", "value1");
      Response<String> r2 = shardedPipeline.set("key2", "value2");
      Response<String> r3 = shardedPipeline.set("key3", "value3");
      Response<String> r4 = shardedPipeline.get("key1");
      Response<String> r5 = shardedPipeline.get("key2");
      Response<String> r6 = shardedPipeline.get("key3");
      
      shardedPipeline.sync();
      Assert.assertEquals("OK", r1.get());
      Assert.assertEquals("OK", r2.get());
      Assert.assertEquals("OK", r3.get());
      Assert.assertEquals("value1", r4.get());
      Assert.assertEquals("value2", r5.get());
      Assert.assertEquals("value3", r6.get());
    }
  }

  @Test
  public void constructorClientConfig() {
    try (ShardedPipeline pipe = new ShardedPipeline(shards, DEFAULT_CLIENT_CONFIG)) {
      Response<String> r1 = pipe.set("key1", "value1");
      Response<String> r2 = pipe.set("key2", "value2");
      Response<String> r3 = pipe.set("key3", "value3");
      Response<String> r4 = pipe.get("key1");
      Response<String> r5 = pipe.get("key2");
      Response<String> r6 = pipe.get("key3");
      
      pipe.sync();
      Assert.assertEquals("OK", r1.get());
      Assert.assertEquals("OK", r2.get());
      Assert.assertEquals("OK", r3.get());
      Assert.assertEquals("value1", r4.get());
      Assert.assertEquals("value2", r5.get());
      Assert.assertEquals("value3", r6.get());
    }
  }

  @Test
  public void constructorPoolConfig() {
    try (ShardedPipeline pipe = new ShardedPipeline(shards, DEFAULT_CLIENT_CONFIG, DEFAULT_POOL_CONFIG,
        Hashing.MURMUR_HASH, JedisSharding.DEFAULT_KEY_TAG_PATTERN)) {
      Response<String> r1 = pipe.set("key1", "value1");
      Response<String> r2 = pipe.set("key2", "value2");
      Response<String> r3 = pipe.set("key3", "value3");
      Response<String> r4 = pipe.get("key1");
      Response<String> r5 = pipe.get("key2");
      Response<String> r6 = pipe.get("key3");

      pipe.sync();
      Assert.assertEquals("OK", r1.get());
      Assert.assertEquals("OK", r2.get());
      Assert.assertEquals("OK", r3.get());
      Assert.assertEquals("value1", r4.get());
      Assert.assertEquals("value2", r5.get());
      Assert.assertEquals("value3", r6.get());
    }
  }
}
