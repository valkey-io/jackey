package io.jackey.util;

import java.util.concurrent.atomic.AtomicReference;

import io.jackey.HostAndPort;
import io.jackey.Jedis;
import io.jackey.JedisPubSub;
import io.jackey.exceptions.FailoverAbortedException;

public class JedisSentinelTestUtil {

  public static HostAndPort waitForNewPromotedMaster(final String masterName,
      final Jedis sentinelJedis, final Jedis commandJedis) throws InterruptedException {

    final AtomicReference<String> newmaster = new AtomicReference<String>("");

    sentinelJedis.psubscribe(new JedisPubSub() {

      @Override
      public void onPMessage(String pattern, String channel, String message) {
        if (channel.equals("+switch-master")) {
          newmaster.set(message);
          punsubscribe();
        } else if (channel.startsWith("-failover-abort")) {
          punsubscribe();
          throw new FailoverAbortedException("Unfortunately sentinel cannot failover..."
              + " reason(channel) : " + channel + " / message : " + message);
        }
      }

      @Override
      public void onPSubscribe(String pattern, int subscribedChannels) {
        commandJedis.sentinelFailover(masterName);
      }
    }, "*");

    String[] chunks = newmaster.get().split(" ");
    HostAndPort newMaster = new HostAndPort(chunks[3], Integer.parseInt(chunks[4]));

    return newMaster;
  }

}
