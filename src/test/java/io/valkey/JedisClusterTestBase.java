package io.valkey;

import io.valkey.args.ClusterResetType;
import org.junit.After;
import org.junit.Before;
import io.valkey.util.JedisClusterTestUtil;

public abstract class JedisClusterTestBase {

  protected static Jedis node1;
  protected static Jedis node2;
  protected static Jedis node3;
  protected static Jedis node4;
  protected static Jedis nodeSlave2;

  protected HostAndPort nodeInfo1 = HostAndPorts.getClusterServers().get(0);
  protected HostAndPort nodeInfo2 = HostAndPorts.getClusterServers().get(1);
  protected HostAndPort nodeInfo3 = HostAndPorts.getClusterServers().get(2);
  protected HostAndPort nodeInfo4 = HostAndPorts.getClusterServers().get(3);
  protected HostAndPort nodeInfoSlave2 = HostAndPorts.getClusterServers().get(4);

  protected static final String LOCAL_IP = "127.0.0.1";

  @Before
  public void setUp() throws InterruptedException {
    node1 = new Jedis(nodeInfo1);
    node1.auth("cluster");
    node1.flushAll();

    node2 = new Jedis(nodeInfo2);
    node2.auth("cluster");
    node2.flushAll();

    node3 = new Jedis(nodeInfo3);
    node3.auth("cluster");
    node3.flushAll();

    node4 = new Jedis(nodeInfo4);
    node4.auth("cluster");
    node4.flushAll();

    nodeSlave2 = new Jedis(nodeInfoSlave2);
    nodeSlave2.auth("cluster");
    nodeSlave2.flushAll();
    // ---- configure cluster

    // add nodes to cluster
    node1.clusterMeet(LOCAL_IP, nodeInfo2.getPort());
    node1.clusterMeet(LOCAL_IP, nodeInfo3.getPort());

    // split available slots across the three nodes
    int slotsPerNode = Protocol.CLUSTER_HASHSLOTS / 3;
    int[] node1Slots = new int[slotsPerNode];
    int[] node2Slots = new int[slotsPerNode + 1];
    int[] node3Slots = new int[slotsPerNode];
    for (int i = 0, slot1 = 0, slot2 = 0, slot3 = 0; i < Protocol.CLUSTER_HASHSLOTS; i++) {
      if (i < slotsPerNode) {
        node1Slots[slot1++] = i;
      } else if (i > slotsPerNode * 2) {
        node3Slots[slot3++] = i;
      } else {
        node2Slots[slot2++] = i;
      }
    }

    node1.clusterAddSlots(node1Slots);
    node2.clusterAddSlots(node2Slots);
    node3.clusterAddSlots(node3Slots);

    JedisClusterTestUtil.waitForClusterReady(node1, node2, node3);
  }

  protected void cleanUp() {
    node1.flushDB();
    node2.flushDB();
    node3.flushDB();
    node4.flushDB();
    node1.clusterReset(ClusterResetType.HARD);
    node2.clusterReset(ClusterResetType.HARD);
    node3.clusterReset(ClusterResetType.HARD);
    node4.clusterReset(ClusterResetType.HARD);
  }

  @After
  public void tearDown() {
    cleanUp();
  }
}
