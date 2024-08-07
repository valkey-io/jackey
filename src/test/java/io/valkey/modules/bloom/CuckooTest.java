package io.valkey.modules.bloom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import io.valkey.RedisProtocol;
import io.valkey.bloom.CFInsertParams;
import io.valkey.bloom.CFReserveParams;
import io.valkey.exceptions.JedisDataException;
import io.valkey.modules.RedisModuleCommandsTestBase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for the Cuckoo Filter Implementation
 */
@RunWith(Parameterized.class)
public class CuckooTest extends RedisModuleCommandsTestBase {

  @BeforeClass
  public static void prepare() {
    RedisModuleCommandsTestBase.prepare();
  }
//
//  @AfterClass
//  public static void tearDown() {
////    RedisModuleCommandsTestBase.tearDown();
//  }

  public CuckooTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Test
  public void testReservationCapacityOnly() {
    client.cfReserve("cuckoo1", 10);

    Map<String, Object> info = client.cfInfo("cuckoo1");
    assertEquals(8L, info.get("Number of buckets"));
    assertEquals(0L, info.get("Number of items inserted"));
    assertEquals(72L, info.get("Size"));
    assertEquals(1L, info.get("Expansion rate"));
    assertEquals(1L, info.get("Number of filters"));
    assertEquals(2L, info.get("Bucket size"));
    assertEquals(20L, info.get("Max iterations"));
    assertEquals(0L, info.get("Number of items deleted"));
  }

  @Test
  public void testReservationCapacityAndBucketSize() {
    client.cfReserve("cuckoo2", 200, CFReserveParams.reserveParams().bucketSize(10));

    Map<String, Object> info = client.cfInfo("cuckoo2");

    assertEquals(32L, info.get("Number of buckets"));
    assertEquals(0L, info.get("Number of items inserted"));
    assertEquals(376L, info.get("Size"));
    assertEquals(1L, info.get("Expansion rate"));
    assertEquals(1L, info.get("Number of filters"));
    assertEquals(10L, info.get("Bucket size"));
    assertEquals(20L, info.get("Max iterations"));
    assertEquals(0L, info.get("Number of items deleted"));
  }

  @Test
  public void testReservationCapacityAndBucketSizeAndMaxIterations() {
    client.cfReserve("cuckoo3", 200, CFReserveParams.reserveParams()
        .bucketSize(10).maxIterations(20));

    Map<String, Object> info = client.cfInfo("cuckoo3");

    assertEquals(32L, info.get("Number of buckets"));
    assertEquals(0L, info.get("Number of items inserted"));
    assertEquals(376L, info.get("Size"));
    assertEquals(1L, info.get("Expansion rate"));
    assertEquals(1L, info.get("Number of filters"));
    assertEquals(10L, info.get("Bucket size"));
    assertEquals(20L, info.get("Max iterations"));
    assertEquals(0L, info.get("Number of items deleted"));
  }

  @Test
  public void testReservationAllParams() {
    client.cfReserve("cuckoo4", 200, CFReserveParams.reserveParams()
        .bucketSize(10).expansion(4).maxIterations(20));

    Map<String, Object> info = client.cfInfo("cuckoo4");

    assertEquals(32L, info.get("Number of buckets"));
    assertEquals(0L, info.get("Number of items inserted"));
    assertEquals(376L, info.get("Size"));
    assertEquals(4L, info.get("Expansion rate"));
    assertEquals(1L, info.get("Number of filters"));
    assertEquals(10L, info.get("Bucket size"));
    assertEquals(20L, info.get("Max iterations"));
    assertEquals(0L, info.get("Number of items deleted"));
  }

  @Test
  public void testAdd() {
    client.cfReserve("cuckoo5", 64000);
    assertTrue(client.cfAdd("cuckoo5", "test"));

    Map<String, Object> info = client.cfInfo("cuckoo5");
    assertEquals(1L, info.get("Number of items inserted"));
  }

  @Test
  public void testAddNxItemDoesExist() {
    client.cfReserve("cuckoo6", 64000);
    assertTrue(client.cfAddNx("cuckoo6", "filter"));
  }

  @Test
  public void testAddNxItemExists() {
    client.cfReserve("cuckoo7", 64000);
    client.cfAdd("cuckoo7", "filter");
    assertFalse(client.cfAddNx("cuckoo7", "filter"));
  }

  @Test
  public void testInsert() {
    Assert.assertEquals(Arrays.asList(true), client.cfInsert("cuckoo8", "foo"));
  }

  @Test
  public void testInsertWithCapacity() {
    Assert.assertEquals(Arrays.asList(true), client.cfInsert("cuckoo9",
        CFInsertParams.insertParams().capacity(1000), "foo"));
  }

  @Test
  public void testInsertNoCreateFilterDoesNotExist() {
    try {
      client.cfInsert("cuckoo10", CFInsertParams.insertParams().noCreate(), "foo", "bar");
      fail();
    } catch (JedisDataException e) {
      assertEquals("ERR not found", e.getMessage());
    }
  }

  @Test
  public void testInsertNoCreateFilterExists() {
    client.cfInsert("cuckoo11", "bar");
    Assert.assertEquals(Arrays.asList(true, true), client.cfInsert("cuckoo11",
        CFInsertParams.insertParams().noCreate(), "foo", "bar"));
  }

  @Test
  public void testInsertNx() {
    Assert.assertEquals(Arrays.asList(true), client.cfInsertNx("cuckoo12", "bar"));
  }

  @Test
  public void testInsertNxWithCapacity() {
    client.cfInsertNx("cuckoo13", "bar");
    Assert.assertEquals(Arrays.asList(false), client.cfInsertNx("cuckoo13",
        CFInsertParams.insertParams().capacity(1000), "bar"));
  }

  @Test
  public void testInsertNxMultiple() {
    client.cfInsertNx("cuckoo14", "foo");
    client.cfInsertNx("cuckoo14", "bar");
    Assert.assertEquals(Arrays.asList(false, false, true),
        client.cfInsertNx("cuckoo14", "foo", "bar", "baz"));
  }

  @Test
  public void testInsertNxNoCreate() {
    try {
      client.cfInsertNx("cuckoo15", CFInsertParams.insertParams().noCreate(), "foo", "bar");
      fail();
    } catch (JedisDataException e) {
      assertEquals("ERR not found", e.getMessage());
    }
  }

  @Test
  public void testExistsItemDoesntExist() {
    assertFalse(client.cfExists("cuckoo16", "foo"));
    Assert.assertEquals(Collections.singletonList(false), client.cfMExists("cuckoo16", "foo"));
  }

  @Test
  public void testExistsItemExists() {
    client.cfInsert("cuckoo17", "foo");
    assertTrue(client.cfExists("cuckoo17", "foo"));
    Assert.assertEquals(Collections.singletonList(true), client.cfMExists("cuckoo17", "foo"));
  }

  @Test
  public void testMExistsMixedItems() {
    client.cfInsert("cuckoo27", "foo");
    Assert.assertEquals(Arrays.asList(true, false), client.cfMExists("cuckoo27", "foo", "bar"));
    Assert.assertEquals(Arrays.asList(false, true), client.cfMExists("cuckoo27", "bar", "foo"));
  }

  @Test
  public void testDeleteItemDoesntExist() {
    client.cfInsert("cuckoo8", "bar");
    assertFalse(client.cfDel("cuckoo8", "foo"));
  }

  @Test
  public void testDeleteItemExists() {
    client.cfInsert("cuckoo18", "foo");
    assertTrue(client.cfDel("cuckoo18", "foo"));
  }

  @Test
  public void testDeleteFilterDoesNotExist() {
    Exception ex = assertThrows(JedisDataException.class, () -> {
      client.cfDel("cuckoo19", "foo");
    });
    assertTrue(ex.getMessage().contains("Not found"));
  }

  @Test
  public void testCountFilterDoesNotExist() {
    Assert.assertEquals(0L, client.cfCount("cuckoo20", "filter"));
  }

  @Test
  public void testCountFilterExist() {
    client.cfInsert("cuckoo21", "foo");
    Assert.assertEquals(0L, client.cfCount("cuckoo21", "filter"));
  }

  @Test
  public void testCountItemExists() {
    client.cfInsert("cuckoo22", "foo");
    Assert.assertEquals(1L, client.cfCount("cuckoo22", "foo"));
  }

  @Test
  public void testInfoFilterDoesNotExists() {
    Exception ex = assertThrows(JedisDataException.class, () -> {
      client.cfInfo("cuckoo23");
    });
    assertTrue(ex.getMessage().contains("ERR not found"));
  }

  @Test(timeout = 2000L)
  public void testScanDumpAndLoadChunk() {
    client.cfReserve("cuckoo24", 100L /*capacity*/, CFReserveParams.reserveParams().bucketSize(50));
    client.cfAdd("cuckoo24-dump", "a");

    long iterator = 0;
    while (true) {
      Map.Entry<Long, byte[]> chunkData = client.cfScanDump("cuckoo24-dump", iterator);
      iterator = chunkData.getKey();
      if (iterator == 0L) break;
      Assert.assertEquals("OK", client.cfLoadChunk("cuckoo24-load", iterator, chunkData.getValue()));
    }

    // check for properties
    Assert.assertEquals(client.cfInfo("cuckoo24-dump"), client.cfInfo("cuckoo24-load"));
    // check for existing items
    assertTrue(client.cfExists("cuckoo24-load", "a"));
  }
}
