package io.valkey.commands;

import java.util.List;

import io.valkey.Response;
import io.valkey.args.FlushMode;
import io.valkey.util.KeyValue;

public interface SampleBinaryKeyedPipelineCommands {

  Response<Long> waitReplicas(byte[] sampleKey, int replicas, long timeout);

  Response<KeyValue<Long, Long>> waitAOF(byte[] sampleKey, long numLocal, long numReplicas, long timeout);

  Response<Object> eval(byte[] script, byte[] sampleKey);

  Response<Object> evalsha(byte[] sha1, byte[] sampleKey);
//
//  Response<Boolean> scriptExists(byte[] sha1, byte[] sampleKey);

  Response<List<Boolean>> scriptExists(byte[] sampleKey, byte[]... sha1s);

  Response<byte[]> scriptLoad(byte[] script, byte[] sampleKey);

  Response<String> scriptFlush(byte[] sampleKey);

  Response<String> scriptFlush(byte[] sampleKey, FlushMode flushMode);

  Response<String> scriptKill(byte[] sampleKey);
}
