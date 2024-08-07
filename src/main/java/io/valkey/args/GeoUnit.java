package io.valkey.args;

import java.util.Locale;
import io.valkey.util.SafeEncoder;

public enum GeoUnit implements Rawable {

  M, KM, MI, FT;

  private final byte[] raw;

  private GeoUnit() {
    raw = SafeEncoder.encode(name().toLowerCase(Locale.ENGLISH));
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
