package io.valkey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import io.valkey.args.RawableFactory;
import io.valkey.args.Rawable;
import io.valkey.commands.ProtocolCommand;
import io.valkey.params.IParams;
import io.valkey.search.RediSearchUtil;

public class CommandArguments implements Iterable<Rawable> {

  private final ArrayList<Rawable> args;

  private boolean blocking;

  private CommandArguments() {
    throw new InstantiationError();
  }

  public CommandArguments(ProtocolCommand command) {
    args = new ArrayList<>();
    args.add(command);
  }

  public ProtocolCommand getCommand() {
    return (ProtocolCommand) args.get(0);
  }

  public CommandArguments add(Object arg) {
    if (arg == null) {
      throw new IllegalArgumentException("null is not a valid argument.");
    } else if (arg instanceof Rawable) {
      args.add((Rawable) arg);
    } else if (arg instanceof byte[]) {
      args.add(RawableFactory.from((byte[]) arg));
    } else if (arg instanceof Integer) {
      args.add(RawableFactory.from((Integer) arg));
    } else if (arg instanceof Double) {
      args.add(RawableFactory.from((Double) arg));
    } else if (arg instanceof Boolean) {
      args.add(RawableFactory.from((Boolean) arg ? 1 : 0));
    } else if (arg instanceof float[]) {
      args.add(RawableFactory.from(RediSearchUtil.toByteArray((float[]) arg)));
    } else if (arg instanceof String) {
      args.add(RawableFactory.from((String) arg));
    } else if (arg instanceof GeoCoordinate) {
      GeoCoordinate geo = (GeoCoordinate) arg;
      args.add(RawableFactory.from(geo.getLongitude() + "," + geo.getLatitude()));
    } else {
      args.add(RawableFactory.from(String.valueOf(arg)));
    }
    return this;
  }

  public CommandArguments addObjects(Object... args) {
    for (Object arg : args) {
      add(arg);
    }
    return this;
  }

  public CommandArguments addObjects(Collection args) {
    args.forEach(arg -> add(arg));
    return this;
  }

  public CommandArguments key(Object key) {
    if (key instanceof Rawable) {
      Rawable raw = (Rawable) key;
      processKey(raw.getRaw());
      args.add(raw);
    } else if (key instanceof byte[]) {
      byte[] raw = (byte[]) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else if (key instanceof String) {
      String raw = (String) key;
      processKey(raw);
      args.add(RawableFactory.from(raw));
    } else {
      throw new IllegalArgumentException("\"" + key.toString() + "\" is not a valid argument.");
    }
    return this;
  }

  public final CommandArguments keys(Object... keys) {
    for (Object key : keys) {
      key(key);
    }
    return this;
  }

  public final CommandArguments keys(Collection keys) {
    keys.forEach(key -> key(key));
    return this;
  }

  public final CommandArguments addParams(IParams params) {
    params.addParams(this);
    return this;
  }

  protected CommandArguments processKey(byte[] key) {
    // do nothing
    return this;
  }

  protected final CommandArguments processKeys(byte[]... keys) {
    for (byte[] key : keys) {
      processKey(key);
    }
    return this;
  }

  protected CommandArguments processKey(String key) {
    // do nothing
    return this;
  }

  protected final CommandArguments processKeys(String... keys) {
    for (String key : keys) {
      processKey(key);
    }
    return this;
  }

  public int size() {
    return args.size();
  }

  @Override
  public Iterator<Rawable> iterator() {
    return args.iterator();
  }

  public boolean isBlocking() {
    return blocking;
  }

  public CommandArguments blocking() {
    this.blocking = true;
    return this;
  }
}
