package io.valkey.commands;

import java.util.List;
import java.util.Map;

import io.valkey.params.XAddParams;
import io.valkey.params.XAutoClaimParams;
import io.valkey.params.XClaimParams;
import io.valkey.params.XPendingParams;
import io.valkey.params.XReadGroupParams;
import io.valkey.params.XReadParams;
import io.valkey.params.XTrimParams;

public interface StreamBinaryCommands {

  default byte[] xadd(byte[] key, Map<byte[], byte[]> hash, XAddParams params) {
    return xadd(key, params, hash);
  }

  byte[] xadd(byte[] key, XAddParams params, Map<byte[], byte[]> hash);

  long xlen(byte[] key);

  List<Object> xrange(byte[] key, byte[] start, byte[] end);

  List<Object> xrange(byte[] key, byte[] start, byte[] end, int count);

  List<Object> xrevrange(byte[] key, byte[] end, byte[] start);

  List<Object> xrevrange(byte[] key, byte[] end, byte[] start, int count);

  long xack(byte[] key, byte[] group, byte[]... ids);

  String xgroupCreate(byte[] key, byte[] groupName, byte[] id, boolean makeStream);

  String xgroupSetID(byte[] key, byte[] groupName, byte[] id);

  long xgroupDestroy(byte[] key, byte[] groupName);

  boolean xgroupCreateConsumer(byte[] key, byte[] groupName, byte[] consumerName);

  long xgroupDelConsumer(byte[] key, byte[] groupName, byte[] consumerName);

  long xdel(byte[] key, byte[]... ids);

  long xtrim(byte[] key, long maxLen, boolean approximateLength);

  long xtrim(byte[] key, XTrimParams params);

  Object xpending(byte[] key, byte[] groupName);

  List<Object> xpending(byte[] key, byte[] groupName, XPendingParams params);

  List<byte[]> xclaim(byte[] key, byte[] group, byte[] consumerName, long minIdleTime, XClaimParams params, byte[]... ids);

  List<byte[]> xclaimJustId(byte[] key, byte[] group, byte[] consumerName, long minIdleTime, XClaimParams params, byte[]... ids);

  List<Object> xautoclaim(byte[] key, byte[] groupName, byte[] consumerName,
      long minIdleTime, byte[] start, XAutoClaimParams params);

  List<Object> xautoclaimJustId(byte[] key, byte[] groupName, byte[] consumerName,
      long minIdleTime, byte[] start, XAutoClaimParams params);

  Object xinfoStream(byte[] key);

  /**
   * Introspection command used in order to retrieve all information about the stream
   * @param key Stream name
   */
  Object xinfoStreamFull(byte[] key);

  /**
   * Introspection command used in order to retrieve all information about the stream
   * @param key Stream name
   * @param count stream info count
   */
  Object xinfoStreamFull(byte[] key, int count);

  List<Object> xinfoGroups(byte[] key);

  List<Object> xinfoConsumers(byte[] key, byte[] group);

  List<Object> xread(XReadParams xReadParams, Map.Entry<byte[], byte[]>... streams);

  List<Object> xreadGroup(byte[] groupName, byte[] consumer, XReadGroupParams xReadGroupParams,
      Map.Entry<byte[], byte[]>... streams);

}
