package io.valkey;

import java.util.Collection;
import java.util.function.Function;

import io.valkey.Protocol.Keyword;
import io.valkey.params.ScanParams;
import io.valkey.providers.ConnectionProvider;
import io.valkey.resps.ScanResult;
import io.valkey.util.JedisCommandIterationBase;

public class ScanIteration extends JedisCommandIterationBase<ScanResult<String>, String> {

  private final int count;
  private final Function<String, CommandArguments> args;

  public ScanIteration(ConnectionProvider connectionProvider, int batchCount, String match) {
    super(connectionProvider, BuilderFactory.SCAN_RESPONSE);
    this.count = batchCount;
    this.args = (cursor) -> new CommandArguments(Protocol.Command.SCAN).add(cursor)
        .add(Keyword.MATCH).add(match).add(Keyword.COUNT).add(count);
  }

  public ScanIteration(ConnectionProvider connectionProvider, int batchCount, String match, String type) {
    super(connectionProvider, BuilderFactory.SCAN_RESPONSE);
    this.count = batchCount;
    this.args = (cursor) -> new CommandArguments(Protocol.Command.SCAN).add(cursor)
        .add(Keyword.MATCH).add(match).add(Keyword.COUNT).add(count).add(Keyword.TYPE).add(type);
  }

  @Override
  protected boolean isNodeCompleted(ScanResult<String> reply) {
    return reply.isCompleteIteration();
  }

  @Override
  protected CommandArguments initCommandArguments() {
    return args.apply(ScanParams.SCAN_POINTER_START);
  }

  @Override
  protected CommandArguments nextCommandArguments(ScanResult<String> lastReply) {
    return args.apply(lastReply.getCursor());
  }

  @Override
  protected Collection<String> convertBatchToData(ScanResult<String> batch) {
    return batch.getResult();
  }
}
