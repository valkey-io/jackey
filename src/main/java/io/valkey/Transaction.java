package io.valkey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.valkey.Protocol.Command;
import io.valkey.exceptions.JedisConnectionException;
import io.valkey.exceptions.JedisDataException;
import io.valkey.graph.GraphCommandObjects;

/**
 * A pipeline based transaction.
 */
public class Transaction extends TransactionBase {

  private final Queue<Response<?>> pipelinedResponses = new LinkedList<>();

  private Jedis jedis = null;

  protected final Connection connection;
  private final boolean closeConnection;

  private boolean broken = false;
  private boolean inWatch = false;
  private boolean inMulti = false;

  // Legacy - to support Jedis.multi()
  // TODO: Should be package private ??
  public Transaction(Jedis jedis) {
    this(jedis.getConnection());
    this.jedis = jedis;
  }

  /**
   * Creates a new transaction.
   * 
   * A MULTI command will be added to be sent to server. WATCH/UNWATCH/MULTI commands must not be
   * called with this object.
   * @param connection connection
   */
  public Transaction(Connection connection) {
    this(connection, true);
  }

  /**
   * Creates a new transaction.
   *
   * A user wanting to WATCH/UNWATCH keys followed by a call to MULTI ({@link #multi()}) it should
   * be {@code doMulti=false}.
   *
   * @param connection connection
   * @param doMulti {@code false} should be set to enable manual WATCH, UNWATCH and MULTI
   */
  public Transaction(Connection connection, boolean doMulti) {
    this(connection, doMulti, false);
  }

  /**
   * Creates a new transaction.
   *
   * A user wanting to WATCH/UNWATCH keys followed by a call to MULTI ({@link #multi()}) it should
   * be {@code doMulti=false}.
   *
   * @param connection connection
   * @param doMulti {@code false} should be set to enable manual WATCH, UNWATCH and MULTI
   * @param closeConnection should the 'connection' be closed when 'close()' is called?
   */
  public Transaction(Connection connection, boolean doMulti, boolean closeConnection) {
    this.connection = connection;
    this.closeConnection = closeConnection;
    setGraphCommands(new GraphCommandObjects(this.connection));
    if (doMulti) multi();
  }

  @Override
  public final void multi() {
    connection.sendCommand(Command.MULTI);
    // processMultiResponse(); // do nothing
    inMulti = true;
  }

  @Override
  public String watch(final String... keys) {
    connection.sendCommand(Command.WATCH, keys);
    String status = connection.getStatusCodeReply();
    inWatch = true;
    return status;
  }

  @Override
  public String watch(final byte[]... keys) {
    connection.sendCommand(Command.WATCH, keys);
    String status = connection.getStatusCodeReply();
    inWatch = true;
    return status;
  }

  @Override
  public String unwatch() {
    connection.sendCommand(Command.UNWATCH);
    String status = connection.getStatusCodeReply();
    inWatch = false;
    return status;
  }

  @Override
  protected final <T> Response<T> appendCommand(CommandObject<T> commandObject) {
    connection.sendCommand(commandObject.getArguments());
    // processAppendStatus(); // do nothing
    Response<T> response = new Response<>(commandObject.getBuilder());
    pipelinedResponses.add(response);
    return response;
  }

  @Override
  public final void close() {
    try {
      clear();
    } finally {
      if (closeConnection) {
        connection.close();
      }
    }
  }

  @Deprecated // TODO: private
  public final void clear() {
    if (broken) {
      return;
    }
    if (inMulti) {
      discard();
    } else if (inWatch) {
      unwatch();
    }
  }

  @Override
  public List<Object> exec() {
    if (!inMulti) {
      throw new IllegalStateException("EXEC without MULTI");
    }

    try {
      // ignore QUEUED (or ERROR)
      // processPipelinedResponses(pipelinedResponses.size());
      connection.getMany(1 + pipelinedResponses.size());

      connection.sendCommand(Command.EXEC);

      List<Object> unformatted = connection.getObjectMultiBulkReply();
      if (unformatted == null) {
        pipelinedResponses.clear();
        return null;
      }

      List<Object> formatted = new ArrayList<>(unformatted.size());
      for (Object o : unformatted) {
        try {
          Response<?> response = pipelinedResponses.poll();
          response.set(o);
          formatted.add(response.get());
        } catch (JedisDataException e) {
          formatted.add(e);
        }
      }
      return formatted;
    } catch (JedisConnectionException jce) {
      broken = true;
      throw jce;
    } finally {
      inMulti = false;
      inWatch = false;
      pipelinedResponses.clear();
      if (jedis != null) {
        jedis.resetState();
      }
    }
  }

  @Override
  public String discard() {
    if (!inMulti) {
      throw new IllegalStateException("DISCARD without MULTI");
    }

    try {
      // ignore QUEUED (or ERROR)
      // processPipelinedResponses(pipelinedResponses.size());
      connection.getMany(1 + pipelinedResponses.size());

      connection.sendCommand(Command.DISCARD);

      return connection.getStatusCodeReply();
    } catch (JedisConnectionException jce) {
      broken = true;
      throw jce;
    } finally {
      inMulti = false;
      inWatch = false;
      pipelinedResponses.clear();
      if (jedis != null) {
        jedis.resetState();
      }
    }
  }
}
