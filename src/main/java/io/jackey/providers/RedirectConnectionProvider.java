package io.jackey.providers;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.jackey.CommandArguments;
import io.jackey.Connection;
import io.jackey.ConnectionPool;
import io.jackey.DefaultJedisClientConfig;
import io.jackey.HostAndPort;
import io.jackey.JedisClientConfig;
import io.jackey.exceptions.JedisException;
import io.jackey.util.Pool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectConnectionProvider implements ConnectionProvider {
  private static final Logger logger = LoggerFactory.getLogger(RedirectConnectionProvider.class);

  private Pool<Connection> pool;
  private HostAndPort hostAndPort;
  private JedisClientConfig clientConfig;
  private GenericObjectPoolConfig<Connection> poolConfig;

  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock r = rwl.readLock();
  private final Lock w = rwl.writeLock();
  private final Lock rediscoverLock = new ReentrantLock();

  public RedirectConnectionProvider(HostAndPort hostAndPort) {
    this(hostAndPort, DefaultJedisClientConfig.builder().build());
  }

  public RedirectConnectionProvider(HostAndPort hostAndPort, JedisClientConfig clientConfig) {
    this(hostAndPort, clientConfig, new GenericObjectPoolConfig<>());
  }

  public RedirectConnectionProvider(HostAndPort hostAndPort, JedisClientConfig clientConfig,
      GenericObjectPoolConfig<Connection> poolConfig) {
    this.hostAndPort = hostAndPort;
    this.clientConfig = clientConfig;
    this.poolConfig = poolConfig;
    this.pool = new ConnectionPool(hostAndPort, clientConfig, poolConfig);
  }

  public void renewPool(Connection connection, HostAndPort targetNode) {
    if (rediscoverLock.tryLock()) {
      try {
        // if memberOf is closed, means old flight connection return redirect exception, just return.
        if (connection != null && connection.getMemberOf().isClosed()) {
          return;
        }
        // if targetNode is not null, update the new address
        HostAndPort oldNode = hostAndPort;
        if (targetNode != null) {
          this.hostAndPort = targetNode;
        }
        // close old pool and create new pool
        w.lock();
        try {
          if (!pool.isClosed()) {
            try {
              pool.close();
            } catch (JedisException e) {
              logger.warn("close pool get exception, hostAndPort:{}", oldNode, e);
            }
          }
          this.pool = new ConnectionPool(hostAndPort, clientConfig, poolConfig);
        } finally {
          w.unlock();
        }
      } finally {
        rediscoverLock.unlock();
      }
    }
  }

  @Override
  public void close() {
    w.lock();
    try {
      pool.close();
    } finally {
      w.unlock();
    }
  }

  @Override
  public Connection getConnection(CommandArguments args) {
    return getConnection();
  }

  @Override
  public Connection getConnection() {
    r.lock();
    try {
      return pool.getResource();
    } finally {
      r.unlock();
    }
  }

  @Override
  public Map<?, Pool<Connection>> getConnectionMap() {
    r.lock();
    try {
      return Collections.singletonMap(hostAndPort, pool);
    } finally {
      r.unlock();
    }
  }
}
