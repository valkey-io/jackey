package io.valkey.commands.commandobjects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;

import io.valkey.RedisProtocol;
import io.valkey.gears.TFunctionListParams;
import io.valkey.gears.TFunctionLoadParams;
import io.valkey.gears.resps.GearsLibraryInfo;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to <a href="https://redis.io/commands/?group=triggers_and_functions">Triggers and functions</a> commands.
 */
@Ignore
public class CommandObjectsTriggersAndFunctionsCommandsTest extends CommandObjectsModulesTestBase {

  public CommandObjectsTriggersAndFunctionsCommandsTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Test
  public void testTFunctionLoadAndCall() {
    String libraryCode = "#!js api_version=1.0 name=lib\n" +
        "redis.registerFunction('hello', ()=>{return 42;})";

    TFunctionLoadParams params = new TFunctionLoadParams().replace();

    String load = exec(commandObjects.tFunctionLoad(libraryCode, params));
    assertThat(load, equalTo("OK"));

    Object call = exec(commandObjects.tFunctionCall("lib", "hello", new ArrayList<>(), new ArrayList<>()));
    assertThat(call.toString(), equalTo("42"));

    Object callAsync = exec(commandObjects.tFunctionCallAsync("lib", "hello", new ArrayList<>(), new ArrayList<>()));
    assertThat(callAsync.toString(), equalTo("42"));
  }

  @Test
  public void testTFunctionDeleteAndList() {
    String libraryCode = "#!js api_version=1.0 name=lib\n" +
        "redis.registerFunction('hello', ()=>{return 42;})";

    String load = exec(commandObjects.tFunctionLoad(libraryCode, new TFunctionLoadParams().replace()));
    assertThat(load, equalTo("OK"));

    TFunctionListParams params = new TFunctionListParams().library("lib");

    List<GearsLibraryInfo> list = exec(commandObjects.tFunctionList(params));

    assertThat(list, hasSize(1));
    assertThat(list.get(0).getName(), equalTo("lib"));
    assertThat(list.get(0).getFunctions(), hasSize(1));
    assertThat(list.get(0).getFunctions().get(0).getName(), equalTo("hello"));

    String delete = exec(commandObjects.tFunctionDelete("lib"));
    assertThat(delete, equalTo("OK"));

    list = exec(commandObjects.tFunctionList(params));
    assertThat(list, empty());
  }
}
