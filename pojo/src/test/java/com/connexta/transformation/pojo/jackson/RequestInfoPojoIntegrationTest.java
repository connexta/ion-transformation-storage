/**
 * Copyright (c) Connexta
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package com.connexta.transformation.pojo.jackson;

import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.unknown.UnknownRequestInfoPojo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

public class RequestInfoPojoIntegrationTest {
  private static final int VERSION = 1;
  private static final String ID = "1234";
  private static final String METACARD_LOCATION = "http://localhost/metacard";
  private static final String CURRENT_LOCATION = "http://localhost/current";
  private static final String FINAL_LOCATION = "http://localhost/final";

  private final RequestInfoPojo pojo =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojoIntegrationTest.VERSION)
          .setId(RequestInfoPojoIntegrationTest.ID)
          .setMetacardLocation(RequestInfoPojoIntegrationTest.METACARD_LOCATION)
          .setCurrentLocation(RequestInfoPojoIntegrationTest.CURRENT_LOCATION)
          .setFinalLocation(RequestInfoPojoIntegrationTest.FINAL_LOCATION);

  private final JSONObject jsonObject;

  public RequestInfoPojoIntegrationTest() throws Exception {
    jsonObject =
        new JSONObject()
            .put("clazz", "request")
            .put("id", RequestInfoPojoIntegrationTest.ID)
            .put("version", RequestInfoPojoIntegrationTest.VERSION)
            .put("metacard_location", RequestInfoPojoIntegrationTest.METACARD_LOCATION)
            .put("current_location", RequestInfoPojoIntegrationTest.CURRENT_LOCATION)
            .put("final_location", RequestInfoPojoIntegrationTest.FINAL_LOCATION);
  }

  @Test
  public void testPojoJsonPersistenceToString() throws Exception {
    final String json = JsonUtils.write(pojo);
    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceToStream() throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    JsonUtils.write(pojo, baos);
    final RequestInfoPojo pojo2 =
        JsonUtils.read(RequestInfoPojo.class, new ByteArrayInputStream(baos.toByteArray()));

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenVersionIsMissing() throws Exception {
    pojo.setVersion(0);
    jsonObject.remove("version");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsNull() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final String json = JsonUtils.write(pojo);
    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsMissing() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetacardLocationIsNull() throws Exception {
    pojo.setMetacardLocation((String) null);
    jsonObject.remove("metacard_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetacardLocationIsMissing() throws Exception {
    pojo.setMetacardLocation((String) null);
    jsonObject.remove("metacard_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenCurrentLocationIsNull() throws Exception {
    pojo.setCurrentLocation((String) null);
    jsonObject.remove("current_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenCurrentLocationIsMissing() throws Exception {
    pojo.setCurrentLocation((String) null);
    jsonObject.remove("current_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenFinalLocationIsNull() throws Exception {
    pojo.setFinalLocation((String) null);
    jsonObject.remove("final_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenFinalLocationIsMissing() throws Exception {
    pojo.setFinalLocation((String) null);
    jsonObject.remove("final_location");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWithExtraJsonProperties() throws Exception {
    jsonObject.put("extra", "EXTRA");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownRequestInfoPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsMissing() throws Exception {
    final RequestInfoPojo pojo =
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojoIntegrationTest.VERSION)
            .setId(RequestInfoPojoIntegrationTest.ID)
            .setMetacardLocation(RequestInfoPojoIntegrationTest.METACARD_LOCATION)
            .setCurrentLocation(RequestInfoPojoIntegrationTest.CURRENT_LOCATION)
            .setFinalLocation(RequestInfoPojoIntegrationTest.FINAL_LOCATION);

    jsonObject.remove("clazz");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownRequestInfoPojo.class));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsUnknown() throws Exception {
    final RequestInfoPojo pojo =
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojoIntegrationTest.VERSION)
            .setId(RequestInfoPojoIntegrationTest.ID)
            .setMetacardLocation(RequestInfoPojoIntegrationTest.METACARD_LOCATION)
            .setCurrentLocation(RequestInfoPojoIntegrationTest.CURRENT_LOCATION)
            .setFinalLocation(RequestInfoPojoIntegrationTest.FINAL_LOCATION);

    jsonObject.put("clazz", "new_request");

    final RequestInfoPojo pojo2 = JsonUtils.read(RequestInfoPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownRequestInfoPojo.class));
  }
}
