package io.valkey.graph;

import io.valkey.args.Rawable;
import io.valkey.util.SafeEncoder;
import io.valkey.commands.ProtocolCommand;

@Deprecated
public class GraphProtocol {

  @Deprecated
  public enum GraphCommand implements ProtocolCommand {

    QUERY,
    RO_QUERY,
    DELETE,
    LIST,
    PROFILE,
    EXPLAIN,
    SLOWLOG,
    CONFIG;

    private final byte[] raw;

    private GraphCommand() {
      raw = SafeEncoder.encode("GRAPH." + name());
    }

    @Override
    public byte[] getRaw() {
      return raw;
    }
  }

  @Deprecated
  public enum GraphKeyword implements Rawable {

    CYPHER,
    TIMEOUT,
    SET,
    GET,
    __COMPACT("--COMPACT");

    private final byte[] raw;

    private GraphKeyword() {
      raw = SafeEncoder.encode(name());
    }

    private GraphKeyword(String alt) {
      raw = SafeEncoder.encode(alt);
    }

    @Override
    public byte[] getRaw() {
      return raw;
    }
  }
}
