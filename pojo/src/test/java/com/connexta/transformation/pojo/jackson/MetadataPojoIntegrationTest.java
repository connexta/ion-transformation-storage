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

import com.connexta.transformation.commons.api.TransformationStatus;
import com.connexta.transformation.pojo.MetadataPojo;
import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.unknown.UnknownMetadataPojo;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

public class MetadataPojoIntegrationTest {
  private static final int VERSION = 1;
  private static final String ID = "1234";
  private static final String TRANSFORM_ID = "9991234";
  private static final Instant START_TIME = Instant.now();
  private static final Instant COMPLETION_TIME = Instant.now().plusSeconds(30L);
  private static final String STATE = TransformationStatus.State.IN_PROGRESS.name();
  private static final String TYPE = "irm";
  private static final String CONTENT_TYPE = "application/xml";
  private static final long CONTENT_LENGTH = 1024L;
  private static final int REQUEST_VERSION = 1;
  private static final String REQUEST_ID = "1234";
  private static final String REQUEST_METACARD_LOCATION = "http://localhost/metcard";
  private static final String REQUEST_CURRENT_LOCATION = "http://localhost/current";
  private static final String REQUEST_FINAL_LOCATION = "http://localhost/final";

  private static final JSONObject REQUEST_JSON;

  static {
    try {
      REQUEST_JSON =
          new JSONObject()
              .put("clazz", "request")
              .put("id", MetadataPojoIntegrationTest.REQUEST_ID)
              .put("version", MetadataPojoIntegrationTest.REQUEST_VERSION)
              .put("metacard_location", MetadataPojoIntegrationTest.REQUEST_METACARD_LOCATION)
              .put("current_location", MetadataPojoIntegrationTest.REQUEST_CURRENT_LOCATION)
              .put("final_location", MetadataPojoIntegrationTest.REQUEST_FINAL_LOCATION);
    } catch (JSONException e) {
      throw new AssertionError(e);
    }
  }

  private static final RequestInfoPojo REQUEST =
      new RequestInfoPojo()
          .setVersion(MetadataPojoIntegrationTest.REQUEST_VERSION)
          .setId(MetadataPojoIntegrationTest.REQUEST_ID)
          .setMetacardLocation(MetadataPojoIntegrationTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(MetadataPojoIntegrationTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(MetadataPojoIntegrationTest.REQUEST_FINAL_LOCATION);

  private final MetadataPojo pojo =
      new MetadataPojo()
          .setVersion(MetadataPojoIntegrationTest.VERSION)
          .setId(MetadataPojoIntegrationTest.ID)
          .setTransformId(MetadataPojoIntegrationTest.TRANSFORM_ID)
          .setRequestInfo(MetadataPojoIntegrationTest.REQUEST)
          .setStartTime(MetadataPojoIntegrationTest.START_TIME)
          .setCompletionTime(MetadataPojoIntegrationTest.COMPLETION_TIME)
          .setState(MetadataPojoIntegrationTest.STATE)
          .setType(MetadataPojoIntegrationTest.TYPE)
          .setContentType(MetadataPojoIntegrationTest.CONTENT_TYPE)
          .setContentLength(MetadataPojoIntegrationTest.CONTENT_LENGTH);

  private final JSONObject jsonObject;

  public MetadataPojoIntegrationTest() throws Exception {
    this.jsonObject =
        new JSONObject()
            .put("clazz", "metadata")
            .put("id", MetadataPojoIntegrationTest.ID)
            .put("version", MetadataPojoIntegrationTest.VERSION)
            .put("transform_id", MetadataPojoIntegrationTest.TRANSFORM_ID)
            .put("request_info", MetadataPojoIntegrationTest.REQUEST_JSON)
            .put(
                "start_time",
                DecimalUtils.toBigDecimal(
                    MetadataPojoIntegrationTest.START_TIME.getEpochSecond(),
                    MetadataPojoIntegrationTest.START_TIME.getNano()))
            .put(
                "completion_time",
                DecimalUtils.toBigDecimal(
                    MetadataPojoIntegrationTest.COMPLETION_TIME.getEpochSecond(),
                    MetadataPojoIntegrationTest.COMPLETION_TIME.getNano()))
            .put("state", MetadataPojoIntegrationTest.STATE)
            .put("type", MetadataPojoIntegrationTest.TYPE)
            .put("content_type", MetadataPojoIntegrationTest.CONTENT_TYPE)
            .put("content_length", MetadataPojoIntegrationTest.CONTENT_LENGTH);
  }

  @Test
  public void testPojoJsonPersistenceToString() throws Exception {
    final String json = JsonUtils.write(pojo);
    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceToStream() throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    JsonUtils.write(pojo, baos);
    final MetadataPojo pojo2 =
        JsonUtils.read(MetadataPojo.class, new ByteArrayInputStream(baos.toByteArray()));

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenVersionIsMissing() throws Exception {
    pojo.setVersion(0);
    jsonObject.remove("version");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsNull() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final String json = JsonUtils.write(pojo);
    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsMissing() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenTypeNull() throws Exception {
    pojo.setType(null);
    jsonObject.remove("type");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenTypeIsMissing() throws Exception {
    pojo.setType(null);
    jsonObject.remove("type");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenTransformIdIsNull() throws Exception {
    pojo.setTransformId(null);
    jsonObject.remove("transform_id");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenTransformIdIsMissing() throws Exception {
    pojo.setTransformId(null);
    jsonObject.remove("transform_id");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenRequestInfoIsNull() throws Exception {
    pojo.setRequestInfo(null);
    jsonObject.remove("request_info");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenRequestInfoIsMissing() throws Exception {
    pojo.setRequestInfo(null);
    jsonObject.remove("request_info");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStartTimeIsNull() throws Exception {
    pojo.setStartTime(null);
    jsonObject.remove("start_time");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStartTimeIsMissing() throws Exception {
    pojo.setStartTime(null);
    jsonObject.remove("start_time");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenCompletionTimeIsNull() throws Exception {
    pojo.setCompletionTime(null);
    jsonObject.remove("completion_time");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenCompletionTimeIsMissing() throws Exception {
    pojo.setCompletionTime(null);
    jsonObject.remove("completion_time");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStateIsNull() throws Exception {
    pojo.setState((String) null);
    jsonObject.remove("state");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStateIsMissing() throws Exception {
    pojo.setState((String) null);
    jsonObject.remove("state");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenContentTypeIsNull() throws Exception {
    pojo.setContentType(null);
    jsonObject.remove("content_type");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenContentTypeIsMissing() throws Exception {
    pojo.setContentType(null);
    jsonObject.remove("content_type");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenContentLengthIsMissing() throws Exception {
    pojo.setContentLength(-1L);
    jsonObject.remove("content_length");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWithExtraJsonProperties() throws Exception {
    jsonObject.put("extra", "EXTRA");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownMetadataPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsMissing() throws Exception {
    final MetadataPojo pojo =
        new UnknownMetadataPojo()
            .setVersion(MetadataPojoIntegrationTest.VERSION)
            .setId(MetadataPojoIntegrationTest.ID)
            .setTransformId(MetadataPojoIntegrationTest.TRANSFORM_ID)
            .setRequestInfo(MetadataPojoIntegrationTest.REQUEST)
            .setStartTime(MetadataPojoIntegrationTest.START_TIME)
            .setCompletionTime(MetadataPojoIntegrationTest.COMPLETION_TIME)
            .setState(MetadataPojoIntegrationTest.STATE)
            .setType(MetadataPojoIntegrationTest.TYPE)
            .setContentType(MetadataPojoIntegrationTest.CONTENT_TYPE)
            .setContentLength(MetadataPojoIntegrationTest.CONTENT_LENGTH);

    jsonObject.remove("clazz");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownMetadataPojo.class));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsUnknown() throws Exception {
    final MetadataPojo pojo =
        new UnknownMetadataPojo()
            .setVersion(MetadataPojoIntegrationTest.VERSION)
            .setId(MetadataPojoIntegrationTest.ID)
            .setTransformId(MetadataPojoIntegrationTest.TRANSFORM_ID)
            .setRequestInfo(MetadataPojoIntegrationTest.REQUEST)
            .setStartTime(MetadataPojoIntegrationTest.START_TIME)
            .setCompletionTime(MetadataPojoIntegrationTest.COMPLETION_TIME)
            .setState(MetadataPojoIntegrationTest.STATE)
            .setType(MetadataPojoIntegrationTest.TYPE)
            .setContentType(MetadataPojoIntegrationTest.CONTENT_TYPE)
            .setContentLength(MetadataPojoIntegrationTest.CONTENT_LENGTH);
    jsonObject.put("clazz", "new_metadata");

    final MetadataPojo pojo2 = JsonUtils.read(MetadataPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownMetadataPojo.class));
  }
}
