package io.valkey.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import io.valkey.GeoCoordinate;
import io.valkey.resps.GeoRadiusResponse;
import org.junit.Test;

public class ResponsesToStringTest {

  @Test
  public void GeoRadiusResponse() {
    byte[] member = {0x01, 0x02, 0x03, 0x04};

    GeoRadiusResponse response = new GeoRadiusResponse(member);
    response.setDistance(5);
    response.setCoordinate(new GeoCoordinate(2, 3));
    response.setRawScore(10);

    GeoRadiusResponse response_copy = new GeoRadiusResponse(member);
    response_copy.setDistance(5);
    response_copy.setCoordinate(new GeoCoordinate(2, 3));
    response_copy.setRawScore(10);

    assertEquals(response, response);
    assertEquals(response, response_copy);
    assertNotEquals(response, new Object());
  }
}
