package io.valkey.args;

import io.valkey.util.SafeEncoder;

public enum SortedSetOption implements Rawable {

  MIN, MAX;

  private final byte[] raw;

  private SortedSetOption() {
    raw = SafeEncoder.encode(name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
