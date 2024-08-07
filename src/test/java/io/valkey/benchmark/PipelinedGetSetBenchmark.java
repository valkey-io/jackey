package io.valkey.benchmark;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;

import io.valkey.HostAndPort;
import io.valkey.Jedis;
import io.valkey.Pipeline;
import io.valkey.HostAndPorts;

public class PipelinedGetSetBenchmark {

  private static HostAndPort hnp = HostAndPorts.getRedisServers().get(0);
  private static final int TOTAL_OPERATIONS = 200000;

  public static void main(String[] args) throws UnknownHostException, IOException {
    Jedis jedis = new Jedis(hnp);
    jedis.connect();
    jedis.auth("foobared");
    jedis.flushAll();

    long begin = Calendar.getInstance().getTimeInMillis();

    Pipeline p = jedis.pipelined();
    for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
      String key = "foo" + n;
      p.set(key, "bar" + n);
      p.get(key);
    }
    p.sync();

    long elapsed = Calendar.getInstance().getTimeInMillis() - begin;

    jedis.disconnect();

    System.out.println(((1000 * 2 * TOTAL_OPERATIONS) / elapsed) + " ops");
  }
}
