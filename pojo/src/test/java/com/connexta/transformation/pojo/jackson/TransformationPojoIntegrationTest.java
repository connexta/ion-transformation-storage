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
import com.connexta.transformation.pojo.TransformationPojo;
import com.connexta.transformation.pojo.unknown.UnknownTransformationPojo;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

public class TransformationPojoIntegrationTest {
  private static final int VERSION = 1;
  private static final String TRANSFORM_ID = "9991234";
  private static final Instant START_TIME = Instant.now();
  private static final int REQUEST_VERSION = 1;
  private static final String REQUEST_ID = "1234";
  private static final String REQUEST_METACARD_LOCATION = "http://localhost/metcard";
  private static final String REQUEST_CURRENT_LOCATION = "http://localhost/current";
  private static final String REQUEST_FINAL_LOCATION = "http://localhost/final";
  private static final int METADATA_VERSION = 2;
  private static final String METADATA_ID = "2234";
  private static final Instant METADATA_START_TIME = Instant.now();
  private static final Instant METADATA_COMPLETION_TIME = Instant.now().plusSeconds(30L);
  private static final String METADATA_STATE = TransformationStatus.State.IN_PROGRESS.name();
  private static final String METADATA_TYPE = "irm";
  private static final String METADATA_CONTENT_TYPE = "application/xml";
  private static final long METADATA_CONTENT_LENGTH = 1024L;
  private static final int METADATA_VERSION2 = 22;
  private static final String METADATA_ID2 = "22234";
  private static final Instant METADATA_START_TIME2 = Instant.now();
  private static final Instant METADATA_COMPLETION_TIME2 = Instant.now().plusSeconds(10L);
  private static final String METADATA_STATE2 = TransformationStatus.State.SUCCESSFUL.name();
  private static final String METADATA_TYPE2 = "ddms";
  private static final String METADATA_CONTENT_TYPE2 = "application/xml2";
  private static final long METADATA_CONTENT_LENGTH2 = 2024L;

  private static final JSONObject REQUEST_JSON;
  private static final JSONObject METADATA_JSON;
  private static final JSONObject METADATA_JSON2;

  static {
    try {
      REQUEST_JSON =
          new JSONObject()
              .put("clazz", "request")
              .put("id", TransformationPojoIntegrationTest.REQUEST_ID)
              .put("version", TransformationPojoIntegrationTest.REQUEST_VERSION)
              .put("metacard_location", TransformationPojoIntegrationTest.REQUEST_METACARD_LOCATION)
              .put("current_location", TransformationPojoIntegrationTest.REQUEST_CURRENT_LOCATION)
              .put("final_location", TransformationPojoIntegrationTest.REQUEST_FINAL_LOCATION);
      METADATA_JSON =
          new JSONObject()
              .put("clazz", "metadata")
              .put("id", TransformationPojoIntegrationTest.METADATA_ID)
              .put("version", TransformationPojoIntegrationTest.METADATA_VERSION)
              .put("transform_id", TransformationPojoIntegrationTest.TRANSFORM_ID)
              .put("request_info", TransformationPojoIntegrationTest.REQUEST_JSON)
              .put(
                  "start_time",
                  DecimalUtils.toBigDecimal(
                      TransformationPojoIntegrationTest.METADATA_START_TIME.getEpochSecond(),
                      TransformationPojoIntegrationTest.METADATA_START_TIME.getNano()))
              .put(
                  "completion_time",
                  DecimalUtils.toBigDecimal(
                      TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME.getEpochSecond(),
                      TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME.getNano()))
              .put("state", TransformationPojoIntegrationTest.METADATA_STATE)
              .put("type", TransformationPojoIntegrationTest.METADATA_TYPE)
              .put("content_type", TransformationPojoIntegrationTest.METADATA_CONTENT_TYPE)
              .put("content_length", TransformationPojoIntegrationTest.METADATA_CONTENT_LENGTH);
      METADATA_JSON2 =
          new JSONObject()
              .put("clazz", "metadata")
              .put("id", TransformationPojoIntegrationTest.METADATA_ID2)
              .put("version", TransformationPojoIntegrationTest.METADATA_VERSION2)
              .put("transform_id", TransformationPojoIntegrationTest.TRANSFORM_ID)
              .put("request_info", TransformationPojoIntegrationTest.REQUEST_JSON)
              .put(
                  "start_time",
                  DecimalUtils.toBigDecimal(
                      TransformationPojoIntegrationTest.METADATA_START_TIME2.getEpochSecond(),
                      TransformationPojoIntegrationTest.METADATA_START_TIME2.getNano()))
              .put(
                  "completion_time",
                  DecimalUtils.toBigDecimal(
                      TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME2.getEpochSecond(),
                      TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME2.getNano()))
              .put("state", TransformationPojoIntegrationTest.METADATA_STATE2)
              .put("type", TransformationPojoIntegrationTest.METADATA_TYPE2)
              .put("content_type", TransformationPojoIntegrationTest.METADATA_CONTENT_TYPE2)
              .put("content_length", TransformationPojoIntegrationTest.METADATA_CONTENT_LENGTH2);
    } catch (JSONException e) {
      throw new AssertionError(e);
    }
  }

  private static final RequestInfoPojo REQUEST =
      new RequestInfoPojo()
          .setVersion(TransformationPojoIntegrationTest.REQUEST_VERSION)
          .setId(TransformationPojoIntegrationTest.REQUEST_ID)
          .setMetacardLocation(TransformationPojoIntegrationTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(TransformationPojoIntegrationTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(TransformationPojoIntegrationTest.REQUEST_FINAL_LOCATION);
  private static final MetadataPojo METADATA =
      new MetadataPojo()
          .setVersion(TransformationPojoIntegrationTest.METADATA_VERSION)
          .setId(TransformationPojoIntegrationTest.METADATA_ID)
          .setTransformId(TransformationPojoIntegrationTest.TRANSFORM_ID)
          .setRequestInfo(TransformationPojoIntegrationTest.REQUEST)
          .setStartTime(TransformationPojoIntegrationTest.METADATA_START_TIME)
          .setCompletionTime(TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME)
          .setState(TransformationPojoIntegrationTest.METADATA_STATE)
          .setType(TransformationPojoIntegrationTest.METADATA_TYPE)
          .setContentType(TransformationPojoIntegrationTest.METADATA_CONTENT_TYPE)
          .setContentLength(TransformationPojoIntegrationTest.METADATA_CONTENT_LENGTH);
  private static final MetadataPojo METADATA2 =
      new MetadataPojo()
          .setVersion(TransformationPojoIntegrationTest.METADATA_VERSION2)
          .setId(TransformationPojoIntegrationTest.METADATA_ID2)
          .setTransformId(TransformationPojoIntegrationTest.TRANSFORM_ID)
          .setRequestInfo(TransformationPojoIntegrationTest.REQUEST)
          .setStartTime(TransformationPojoIntegrationTest.METADATA_START_TIME2)
          .setCompletionTime(TransformationPojoIntegrationTest.METADATA_COMPLETION_TIME2)
          .setState(TransformationPojoIntegrationTest.METADATA_STATE2)
          .setType(TransformationPojoIntegrationTest.METADATA_TYPE2)
          .setContentType(TransformationPojoIntegrationTest.METADATA_CONTENT_TYPE2)
          .setContentLength(TransformationPojoIntegrationTest.METADATA_CONTENT_LENGTH2);

  private final TransformationPojo pojo =
      new TransformationPojo()
          .setVersion(TransformationPojoIntegrationTest.VERSION)
          .setId(TransformationPojoIntegrationTest.TRANSFORM_ID)
          .setRequestInfo(TransformationPojoIntegrationTest.REQUEST)
          .setStartTime(TransformationPojoIntegrationTest.START_TIME)
          .addMetadata(TransformationPojoIntegrationTest.METADATA)
          .addMetadata(TransformationPojoIntegrationTest.METADATA2);

  private final JSONObject jsonObject;

  public TransformationPojoIntegrationTest() throws Exception {
    // there should be no "transform_id" in this Json since we are purposely ignoring it in the
    // pojo class in favor of storing it in the "id" of the base Pojo class
    jsonObject =
        new JSONObject()
            .put("clazz", "transformation")
            .put("id", TransformationPojoIntegrationTest.TRANSFORM_ID)
            .put("version", TransformationPojoIntegrationTest.VERSION)
            .put("request_info", TransformationPojoIntegrationTest.REQUEST_JSON)
            .put(
                "start_time",
                DecimalUtils.toBigDecimal(
                    TransformationPojoIntegrationTest.START_TIME.getEpochSecond(),
                    TransformationPojoIntegrationTest.START_TIME.getNano()))
            .put(
                "metadatas",
                new JSONArray()
                    .put(0, TransformationPojoIntegrationTest.METADATA_JSON)
                    .put(1, TransformationPojoIntegrationTest.METADATA_JSON2));
  }

  @Test
  public void testPojoJsonPersistenceToString() throws Exception {
    final String json = JsonUtils.write(pojo);
    final TransformationPojo pojo2 = JsonUtils.read(TransformationPojo.class, json);

    // the following assertion will also ensure that no "transform_id" is part of the Json since
    // it is supposed to be ignored
    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceToStream() throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    JsonUtils.write(pojo, baos);
    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, new ByteArrayInputStream(baos.toByteArray()));

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenVersionIsMissing() throws Exception {
    pojo.setVersion(0);
    jsonObject.remove("version");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsNull() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final String json = JsonUtils.write(pojo);
    final TransformationPojo pojo2 = JsonUtils.read(TransformationPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenIdIsMissing() throws Exception {
    pojo.setId(null);
    jsonObject.remove("id");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenRequestInfoIsNull() throws Exception {
    pojo.setRequestInfo(null);
    jsonObject.remove("request_info");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenRequestInfoIsMissing() throws Exception {
    pojo.setRequestInfo(null);
    jsonObject.remove("request_info");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStartTimeIsNull() throws Exception {
    pojo.setStartTime(null);
    jsonObject.remove("start_time");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenStartTimeIsMissing() throws Exception {
    pojo.setStartTime(null);
    jsonObject.remove("start_time");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetatadasIsNull() throws Exception {
    pojo.setMetadatas((List<MetadataPojo>) null);
    jsonObject.remove("metadatas");

    final String json = JsonUtils.write(pojo);
    final TransformationPojo pojo2 = JsonUtils.read(TransformationPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetadatasIsMissing() throws Exception {
    pojo.setMetadatas((List<MetadataPojo>) null);
    jsonObject.remove("metadatas");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetatadasIsEmptyInPojo() throws Exception {
    pojo.setMetadatas(Collections.emptyList());
    jsonObject.remove("metadatas");

    final String json = JsonUtils.write(pojo);
    final TransformationPojo pojo2 = JsonUtils.read(TransformationPojo.class, json);

    Assert.assertThat(json, SameJSONAs.sameJSONAs(jsonObject.toString()));
    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenMetadatasIsEmptyInJson() throws Exception {
    pojo.setMetadatas((List<MetadataPojo>) null);
    jsonObject.put("metadatas", new JSONArray());

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWithExtraJsonProperties() throws Exception {
    jsonObject.put("extra", "EXTRA");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.not(Matchers.isA(UnknownTransformationPojo.class)));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsMissing() throws Exception {
    final TransformationPojo pojo =
        new UnknownTransformationPojo()
            .setVersion(TransformationPojoIntegrationTest.VERSION)
            .setId(TransformationPojoIntegrationTest.TRANSFORM_ID)
            .setRequestInfo(TransformationPojoIntegrationTest.REQUEST)
            .setStartTime(TransformationPojoIntegrationTest.START_TIME)
            .addMetadata(TransformationPojoIntegrationTest.METADATA)
            .addMetadata(TransformationPojoIntegrationTest.METADATA2);

    jsonObject.remove("clazz");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownTransformationPojo.class));
  }

  @Test
  public void testPojoJsonPersistenceWhenClassIsUnknown() throws Exception {
    final TransformationPojo pojo =
        new UnknownTransformationPojo()
            .setVersion(TransformationPojoIntegrationTest.VERSION)
            .setId(TransformationPojoIntegrationTest.TRANSFORM_ID)
            .setRequestInfo(TransformationPojoIntegrationTest.REQUEST)
            .setStartTime(TransformationPojoIntegrationTest.START_TIME)
            .addMetadata(TransformationPojoIntegrationTest.METADATA)
            .addMetadata(TransformationPojoIntegrationTest.METADATA2);

    jsonObject.put("clazz", "new_request");

    final TransformationPojo pojo2 =
        JsonUtils.read(TransformationPojo.class, jsonObject.toString());

    Assert.assertThat(pojo2, Matchers.equalTo(pojo));
    Assert.assertThat(pojo2, Matchers.isA(UnknownTransformationPojo.class));
  }
}
