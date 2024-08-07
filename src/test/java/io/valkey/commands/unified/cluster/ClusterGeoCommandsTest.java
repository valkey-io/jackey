package io.valkey.commands.unified.cluster;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.valkey.GeoCoordinate;
import io.valkey.RedisProtocol;
import io.valkey.args.GeoUnit;
import io.valkey.commands.unified.GeoCommandsTestBase;
import io.valkey.params.GeoRadiusParam;
import io.valkey.params.GeoRadiusStoreParam;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ClusterGeoCommandsTest extends GeoCommandsTestBase {

  public ClusterGeoCommandsTest(RedisProtocol protocol) {
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
  public void georadiusStore() {
    // prepare datas
    Map<String, GeoCoordinate> coordinateMap = new HashMap<>();
    coordinateMap.put("Palermo", new GeoCoordinate(13.361389, 38.115556));
    coordinateMap.put("Catania", new GeoCoordinate(15.087269, 37.502669));
    jedis.geoadd("Sicily {ITA}", coordinateMap);

    long size = jedis.georadiusStore("Sicily {ITA}", 15, 37, 200, GeoUnit.KM,
        GeoRadiusParam.geoRadiusParam(),
        GeoRadiusStoreParam.geoRadiusStoreParam().store("{ITA} SicilyStore"));
    assertEquals(2, size);
    List<String> expected = new ArrayList<>();
    expected.add("Palermo");
    expected.add("Catania");
    Assert.assertEquals(expected, jedis.zrange("{ITA} SicilyStore", 0, -1));
  }

  @Ignore
  @Override
  public void georadiusStoreBinary() {
  }

  @Test
  @Override
  public void georadiusByMemberStore() {
    jedis.geoadd("Sicily {ITA}", 13.583333, 37.316667, "Agrigento");
    jedis.geoadd("Sicily {ITA}", 13.361389, 38.115556, "Palermo");
    jedis.geoadd("Sicily {ITA}", 15.087269, 37.502669, "Catania");

    long size = jedis.georadiusByMemberStore("Sicily {ITA}", "Agrigento", 100, GeoUnit.KM,
        GeoRadiusParam.geoRadiusParam(),
        GeoRadiusStoreParam.geoRadiusStoreParam().store("{ITA} SicilyStore"));
    assertEquals(2, size);
    List<String> expected = new ArrayList<>();
    expected.add("Agrigento");
    expected.add("Palermo");
    Assert.assertEquals(expected, jedis.zrange("{ITA} SicilyStore", 0, -1));
  }

  @Ignore
  @Override
  public void georadiusByMemberStoreBinary() {
  }

  @Test
  @Ignore
  public void geosearchstore() {
  }

  @Test
  @Ignore
  public void geosearchstoreWithdist() {
  }
}
