package io.valkey;

public abstract class Builder<T> {

  public abstract T build(Object data);
}
