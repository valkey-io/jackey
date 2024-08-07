package io.valkey.mocked.pipeline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.valkey.Response;
import io.valkey.graph.ResultSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PipeliningBaseGraphCommandsTest extends PipeliningBaseMockedTestBase {

  @Test
  public void testGraphQuery() {
    String query = "MATCH (n) RETURN n";

    when(graphCommandObjects.graphQuery("myGraph", query)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphQuery("myGraph", query);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphReadonlyQuery() {
    String query = "MATCH (n) RETURN n";

    when(graphCommandObjects.graphReadonlyQuery("myGraph", query)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphReadonlyQuery("myGraph", query);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphQueryWithTimeout() {
    String query = "MATCH (n) RETURN n";

    when(graphCommandObjects.graphQuery("myGraph", query, 1000L)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphQuery("myGraph", query, 1000L);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphReadonlyQueryWithTimeout() {
    String query = "MATCH (n) RETURN n";

    when(graphCommandObjects.graphReadonlyQuery("myGraph", query, 1000L)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphReadonlyQuery("myGraph", query, 1000L);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphQueryWithParams() {
    String query = "MATCH (n) WHERE n.name = $name RETURN n";
    Map<String, Object> params = Collections.singletonMap("name", "Alice");

    when(graphCommandObjects.graphQuery("myGraph", query, params)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphQuery("myGraph", query, params);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphReadonlyQueryWithParams() {
    String query = "MATCH (n) WHERE n.name = $name RETURN n";
    Map<String, Object> params = Collections.singletonMap("name", "Alice");

    when(graphCommandObjects.graphReadonlyQuery("myGraph", query, params)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphReadonlyQuery("myGraph", query, params);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphQueryWithParamsAndTimeout() {
    String query = "MATCH (n) WHERE n.name = $name RETURN n";
    Map<String, Object> params = Collections.singletonMap("name", "Alice");

    when(graphCommandObjects.graphQuery("myGraph", query, params, 1000L)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphQuery("myGraph", query, params, 1000L);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphReadonlyQueryWithParamsAndTimeout() {
    String query = "MATCH (n) WHERE n.name = $name RETURN n";
    Map<String, Object> params = Collections.singletonMap("name", "Alice");

    when(graphCommandObjects.graphReadonlyQuery("myGraph", query, params, 1000L)).thenReturn(resultSetCommandObject);

    Response<ResultSet> response = pipeliningBase.graphReadonlyQuery("myGraph", query, params, 1000L);

    MatcherAssert.assertThat(commands, Matchers.contains(resultSetCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphDelete() {
    when(graphCommandObjects.graphDelete("myGraph")).thenReturn(stringCommandObject);

    Response<String> response = pipeliningBase.graphDelete("myGraph");

    MatcherAssert.assertThat(commands, Matchers.contains(stringCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

  @Test
  public void testGraphProfile() {
    String query = "PROFILE MATCH (n) RETURN n";

    when(commandObjects.graphProfile("myGraph", query)).thenReturn(listStringCommandObject);

    Response<List<String>> response = pipeliningBase.graphProfile("myGraph", query);

    MatcherAssert.assertThat(commands, Matchers.contains(listStringCommandObject));
    assertThat(response, Matchers.is(predefinedResponse));
  }

}
