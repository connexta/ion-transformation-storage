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
package com.connexta.transformation.pojo;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class TransformationPojoTest {
  private static final int VERSION = 1;
  private static final String ID = "1234";
  private static final Instant START_TIME = Instant.now();
  private static final Instant COMPLETION_TIME = Instant.now().plusSeconds(30L);
  private static final int REQUEST_VERSION = 1;
  private static final String REQUEST_ID = "12345";
  private static final String REQUEST_METACARD_LOCATION = "http://localhost/metacard";
  private static final String REQUEST_CURRENT_LOCATION = "http://localhost/current";
  private static final String REQUEST_FINAL_LOCATION = "http://localhost/final";
  private static final int REQUEST_VERSION2 = 22;
  private static final String REQUEST_ID2 = "22345";
  private static final String REQUEST_METACARD_LOCATION2 = "http://localhost/metacard2";
  private static final String REQUEST_CURRENT_LOCATION2 = "http://localhost/current2";
  private static final String REQUEST_FINAL_LOCATION2 = "http://localhost/final2";
  private static final int METADATA_VERSION = 2;
  private static final String METADATA_ID = "2234";
  private static final String METADATA_TYPE = "irm";
  private static final String METADATA_CONTENT_TYPE = "application/xml";
  private static final long METADATA_CONTENT_LENGTH = 1024L;
  private static final int METADATA_VERSION2 = 22;
  private static final String METADATA_ID2 = "22234";
  private static final String METADATA_TYPE2 = "ddms";
  private static final String METADATA_CONTENT_TYPE2 = "application/xml2";
  private static final long METADATA_CONTENT_LENGTH2 = 2024L;

  private static final RequestInfoPojo REQUEST =
      new RequestInfoPojo()
          .setVersion(TransformationPojoTest.REQUEST_VERSION)
          .setId(TransformationPojoTest.REQUEST_ID)
          .setMetacardLocation(TransformationPojoTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(TransformationPojoTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(TransformationPojoTest.REQUEST_FINAL_LOCATION);
  private static final RequestInfoPojo REQUEST2 =
      new RequestInfoPojo()
          .setVersion(TransformationPojoTest.REQUEST_VERSION2)
          .setId(TransformationPojoTest.REQUEST_ID2)
          .setMetacardLocation(TransformationPojoTest.REQUEST_METACARD_LOCATION2)
          .setCurrentLocation(TransformationPojoTest.REQUEST_CURRENT_LOCATION2)
          .setFinalLocation(TransformationPojoTest.REQUEST_FINAL_LOCATION2);
  private static final MetadataPojo METADATA =
      new MetadataPojo()
          .setVersion(TransformationPojoTest.METADATA_VERSION)
          .setId(TransformationPojoTest.METADATA_ID)
          .setType(TransformationPojoTest.METADATA_TYPE)
          .setContentType(TransformationPojoTest.METADATA_CONTENT_TYPE)
          .setContentLength(TransformationPojoTest.METADATA_CONTENT_LENGTH);
  private static final MetadataPojo METADATA2 =
      new MetadataPojo()
          .setVersion(TransformationPojoTest.METADATA_VERSION2)
          .setId(TransformationPojoTest.METADATA_ID2)
          .setType(TransformationPojoTest.METADATA_TYPE2)
          .setContentType(TransformationPojoTest.METADATA_CONTENT_TYPE2)
          .setContentLength(TransformationPojoTest.METADATA_CONTENT_LENGTH2);

  private static final TransformationPojo POJO =
      new TransformationPojo()
          .setVersion(TransformationPojoTest.VERSION)
          .setId(TransformationPojoTest.ID)
          .setRequestInfo(TransformationPojoTest.REQUEST)
          .setStartTime(TransformationPojoTest.START_TIME)
          .addMetadata(TransformationPojoTest.METADATA)
          .addMetadata(TransformationPojoTest.METADATA2);

  private final TransformationPojo pojo2 =
      new TransformationPojo()
          .setVersion(TransformationPojoTest.VERSION)
          .setId(TransformationPojoTest.ID)
          .setRequestInfo(TransformationPojoTest.REQUEST)
          .setStartTime(TransformationPojoTest.START_TIME)
          .addMetadata(TransformationPojoTest.METADATA)
          .addMetadata(TransformationPojoTest.METADATA2);

  @Test
  public void testSetAndGetId() throws Exception {
    final TransformationPojo pojo = new TransformationPojo().setId(TransformationPojoTest.ID);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(TransformationPojoTest.ID));
  }

  @Test
  public void testSetAndGetVersion() throws Exception {
    final TransformationPojo pojo =
        new TransformationPojo().setVersion(TransformationPojoTest.VERSION);

    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(TransformationPojoTest.VERSION));
  }

  @Test
  public void testSetAndGetRequestInfo() throws Exception {
    final TransformationPojo pojo =
        new TransformationPojo().setRequestInfo(TransformationPojoTest.REQUEST);

    Assert.assertThat(pojo.getRequestInfo(), Matchers.equalTo(TransformationPojoTest.REQUEST));
  }

  @Test
  public void testSetAndGetStartTime() throws Exception {
    final TransformationPojo pojo =
        new TransformationPojo().setStartTime(TransformationPojoTest.START_TIME);

    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(TransformationPojoTest.START_TIME));
  }

  @Test
  public void testSetAndGetMetadatas() throws Exception {
    final TransformationPojo pojo =
        new TransformationPojo()
            .setMetadatas(
                List.of(TransformationPojoTest.METADATA, TransformationPojoTest.METADATA2));

    Assert.assertThat(
        pojo.getMetadatas(),
        Matchers.equalTo(
            List.of(TransformationPojoTest.METADATA, TransformationPojoTest.METADATA2)));
  }

  @Test
  public void testSetAndGetMetadatasViaStreams() throws Exception {
    final TransformationPojo pojo =
        new TransformationPojo()
            .setMetadatas(
                Stream.of(TransformationPojoTest.METADATA2, TransformationPojoTest.METADATA));

    Assert.assertThat(
        pojo.metadatas().collect(Collectors.toList()),
        Matchers.equalTo(
            List.of(TransformationPojoTest.METADATA2, TransformationPojoTest.METADATA)));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(TransformationPojoTest.POJO.hashCode(), Matchers.equalTo(pojo2.hashCode()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    pojo2.setStartTime(TransformationPojoTest.START_TIME.plusMillis(2L));

    Assert.assertThat(
        TransformationPojoTest.POJO.hashCode(), Matchers.not(Matchers.equalTo(pojo2.hashCode())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    Assert.assertThat(TransformationPojoTest.POJO.equals(pojo2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(
        TransformationPojoTest.POJO.equals(TransformationPojoTest.POJO), Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(TransformationPojoTest.POJO.equals(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotATransformationPojo() throws Exception {
    Assert.assertThat(TransformationPojoTest.POJO.equals("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    pojo2.setId(TransformationPojoTest.ID + "2");

    Assert.assertThat(
        TransformationPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenVersionIsDifferent() throws Exception {
    pojo2.setVersion(TransformationPojoTest.VERSION + 2);

    Assert.assertThat(
        TransformationPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenRequestInfoIsDifferent() throws Exception {
    pojo2.setRequestInfo(TransformationPojoTest.REQUEST2);

    Assert.assertThat(
        TransformationPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStartTimeIsDifferent() throws Exception {
    pojo2.setStartTime(TransformationPojoTest.START_TIME.plusMillis(2L));

    Assert.assertThat(
        TransformationPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenMetadatasAreDifferent() throws Exception {
    pojo2.addMetadata(TransformationPojoTest.METADATA2);

    Assert.assertThat(
        TransformationPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }
}
