package io.jackey;

import java.time.Duration;

import io.jackey.args.ClientPauseMode;
import io.jackey.providers.RedirectConnectionProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class RedirectPoolTest {
    private static final HostAndPort node1 = HostAndPorts.getRedisServers().get(11);
    private static final HostAndPort node2 = HostAndPorts.getRedisServers().get(12);

    private HostAndPort masterAddress;
    private HostAndPort replicaAddress;

    @Before
    public void prepare() {
        String role1, role2;
        try (Jedis jedis1 = new Jedis(node1)) {
            role1 = (String)jedis1.role().get(0);
        }
        try (Jedis jedis2 = new Jedis(node2)) {
            role2 = (String)jedis2.role().get(0);
        }

        if ("master".equals(role1) && "slave".equals(role2)) {
            masterAddress = node1;
            replicaAddress = node2;
        } else if ("master".equals(role2) && "slave".equals(role1)) {
            masterAddress = node2;
            replicaAddress = node1;
        } else {
            fail();
        }
    }

    private void changeMaster() throws Exception {
        prepare();
        Jedis masterJedis = new Jedis(masterAddress);
        Jedis replicaJedis = new Jedis(replicaAddress);
        replicaJedis.readonly();
        // pause master
        masterJedis.clientPause(60000, ClientPauseMode.WRITE);
        // check replication
        do {
            Thread.sleep(1000);
        } while (masterJedis.dbSize() != replicaJedis.dbSize());
        // change master and replica
        replicaJedis.slaveofNoOne();
        masterJedis.slaveof(replicaAddress.getHost(), replicaAddress.getPort());
        // unpause
        masterJedis.clientUnpause();
    }

    @Test
    public void basicRedirect() throws Exception {
        UnifiedJedis unifiedJedis = new UnifiedJedis(new RedirectConnectionProvider(masterAddress,
            DefaultJedisClientConfig.builder().socketTimeoutMillis(60000).build()), 60,
            Duration.ofSeconds(60));
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 600; i++) {
                            Thread.sleep(100);
                            Assert.assertEquals("OK", unifiedJedis.set(i + "", i + ""));
                            Assert.assertEquals(i + "", unifiedJedis.get(i + ""));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Redirect should not throw exception");
                    }
                }
            });
        }

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(5000);
                        changeMaster();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Redirect should not throw exception");
                }
            }
        });
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        thread2.start();
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        thread2.join();
        unifiedJedis.close();
    }
}
