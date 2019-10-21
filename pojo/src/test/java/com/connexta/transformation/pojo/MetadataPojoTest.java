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

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.TransformationStatus;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class MetadataPojoTest {
  private static final int VERSION = 1;
  private static final String ID = "1234";
  private static final String TRANSFORM_ID = "9991234";
  private static final Instant START_TIME = Instant.now();
  private static final Instant COMPLETION_TIME = Instant.now().plusSeconds(30L);
  private static final String STATE = TransformationStatus.State.IN_PROGRESS.name();
  private static final String FAILURE_REASON = ErrorCode.TRANSFORMATION_FAILURE.name();
  private static final String FAILURE_MSG = "failure message";
  private static final String TYPE = "irm";
  private static final String CONTENT_TYPE = "application/xml";
  private static final long CONTENT_LENGTH = 1024L;
  private static final int REQUEST_VERSION = 12;
  private static final String REQUEST_ID = "12345";
  private static final String REQUEST_METACARD_LOCATION = "http://localhost/metacard";
  private static final String REQUEST_CURRENT_LOCATION = "http://localhost/current";
  private static final String REQUEST_FINAL_LOCATION = "http://localhost/final";
  private static final int REQUEST_VERSION2 = 22;
  private static final String REQUEST_ID2 = "22345";
  private static final String REQUEST_METACARD_LOCATION2 = "http://localhost/metacard2";
  private static final String REQUEST_CURRENT_LOCATION2 = "http://localhost/current2";
  private static final String REQUEST_FINAL_LOCATION2 = "http://localhost/final2";

  private static final RequestInfoPojo REQUEST =
      new RequestInfoPojo()
          .setVersion(MetadataPojoTest.REQUEST_VERSION)
          .setId(MetadataPojoTest.REQUEST_ID)
          .setMetacardLocation(MetadataPojoTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(MetadataPojoTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(MetadataPojoTest.REQUEST_FINAL_LOCATION);
  private static final RequestInfoPojo REQUEST2 =
      new RequestInfoPojo()
          .setVersion(MetadataPojoTest.REQUEST_VERSION2)
          .setId(MetadataPojoTest.REQUEST_ID2)
          .setMetacardLocation(MetadataPojoTest.REQUEST_METACARD_LOCATION2)
          .setCurrentLocation(MetadataPojoTest.REQUEST_CURRENT_LOCATION2)
          .setFinalLocation(MetadataPojoTest.REQUEST_FINAL_LOCATION2);

  private static final MetadataPojo POJO =
      new MetadataPojo()
          .setVersion(MetadataPojoTest.VERSION)
          .setId(MetadataPojoTest.ID)
          .setTransformId(MetadataPojoTest.TRANSFORM_ID)
          .setRequestInfo(MetadataPojoTest.REQUEST)
          .setStartTime(MetadataPojoTest.START_TIME)
          .setCompletionTime(MetadataPojoTest.COMPLETION_TIME)
          .setState(MetadataPojoTest.STATE)
          .setFailureMessage(MetadataPojoTest.FAILURE_MSG)
          .setFailureReason(MetadataPojoTest.FAILURE_REASON)
          .setType(MetadataPojoTest.TYPE)
          .setContentType(MetadataPojoTest.CONTENT_TYPE)
          .setContentLength(MetadataPojoTest.CONTENT_LENGTH);

  private final MetadataPojo pojo2 =
      new MetadataPojo()
          .setVersion(MetadataPojoTest.VERSION)
          .setId(MetadataPojoTest.ID)
          .setTransformId(MetadataPojoTest.TRANSFORM_ID)
          .setRequestInfo(MetadataPojoTest.REQUEST)
          .setStartTime(MetadataPojoTest.START_TIME)
          .setCompletionTime(MetadataPojoTest.COMPLETION_TIME)
          .setState(MetadataPojoTest.STATE)
          .setFailureMessage(MetadataPojoTest.FAILURE_MSG)
          .setFailureReason(MetadataPojoTest.FAILURE_REASON)
          .setType(MetadataPojoTest.TYPE)
          .setContentType(MetadataPojoTest.CONTENT_TYPE)
          .setContentLength(MetadataPojoTest.CONTENT_LENGTH);

  @Test
  public void testSetAndGetId() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setId(MetadataPojoTest.ID);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(MetadataPojoTest.ID));
  }

  @Test
  public void testSetAndGetVersion() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setVersion(MetadataPojoTest.VERSION);

    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojoTest.VERSION));
  }

  @Test
  public void testSetAndGetTransformId() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setTransformId(MetadataPojoTest.TRANSFORM_ID);

    Assert.assertThat(pojo.getTransformId(), Matchers.equalTo(MetadataPojoTest.TRANSFORM_ID));
  }

  @Test
  public void testSetAndGetRequestInfo() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setRequestInfo(MetadataPojoTest.REQUEST);

    Assert.assertThat(pojo.getRequestInfo(), Matchers.equalTo(MetadataPojoTest.REQUEST));
  }

  @Test
  public void testSetAndGetStartTime() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setStartTime(MetadataPojoTest.START_TIME);

    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(MetadataPojoTest.START_TIME));
  }

  @Test
  public void testSetAndGetCompletionTime() throws Exception {
    final MetadataPojo pojo =
        new MetadataPojo().setCompletionTime(MetadataPojoTest.COMPLETION_TIME);

    Assert.assertThat(pojo.getCompletionTime(), Matchers.equalTo(MetadataPojoTest.COMPLETION_TIME));
  }

  @Test
  public void testSetAndGetState() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setState(MetadataPojoTest.STATE);

    Assert.assertThat(pojo.getState(), Matchers.equalTo(MetadataPojoTest.STATE));
  }

  @Test
  public void testSetAndGetStateWithEnum() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setState(TransformationStatus.State.SUCCESSFUL);

    Assert.assertThat(
        pojo.getState(), Matchers.equalTo(TransformationStatus.State.SUCCESSFUL.name()));
  }

  @Test
  public void testSetAndGetFailureReason() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setFailureReason(MetadataPojoTest.FAILURE_REASON);

    Assert.assertThat(pojo.getFailureReason(), Matchers.equalTo(MetadataPojoTest.FAILURE_REASON));
  }

  @Test
  public void testSetAndGetFailureReasonWithEnum() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setFailureReason(ErrorCode.TRANSFORMATION_FAILURE);

    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(ErrorCode.TRANSFORMATION_FAILURE.name()));
  }

  @Test
  public void testSetAndGetFailureMessage() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setFailureMessage(MetadataPojoTest.FAILURE_REASON);

    Assert.assertThat(pojo.getFailureMessage(), Matchers.equalTo(MetadataPojoTest.FAILURE_REASON));
  }

  @Test
  public void testSetAndGetType() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setType(MetadataPojoTest.TYPE);

    Assert.assertThat(pojo.getType(), Matchers.equalTo(MetadataPojoTest.TYPE));
  }

  @Test
  public void testSetAndGetContentType() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setContentType(MetadataPojoTest.CONTENT_TYPE);

    Assert.assertThat(pojo.getContentType(), Matchers.equalTo(MetadataPojoTest.CONTENT_TYPE));
  }

  @Test
  public void testSetAndGetContentLength() throws Exception {
    final MetadataPojo pojo = new MetadataPojo().setContentLength(MetadataPojoTest.CONTENT_LENGTH);

    Assert.assertThat(pojo.getContentLength(), Matchers.equalTo(MetadataPojoTest.CONTENT_LENGTH));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(MetadataPojoTest.POJO.hashCode(), Matchers.equalTo(pojo2.hashCode()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    pojo2.setType(MetadataPojoTest.TYPE + "2");

    Assert.assertThat(
        MetadataPojoTest.POJO.hashCode(), Matchers.not(Matchers.equalTo(pojo2.hashCode())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(MetadataPojoTest.POJO.equals(MetadataPojoTest.POJO), Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(MetadataPojoTest.POJO.equals(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotAMetadataPojo() throws Exception {
    Assert.assertThat(MetadataPojoTest.POJO.equals("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    pojo2.setId(MetadataPojoTest.ID + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenVersionIsDifferent() throws Exception {
    pojo2.setVersion(MetadataPojoTest.VERSION + 2);

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenTransformIdIsDifferent() throws Exception {
    pojo2.setTransformId(MetadataPojoTest.TRANSFORM_ID + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenRequestInfoIsDifferent() throws Exception {
    pojo2.setRequestInfo(MetadataPojoTest.REQUEST2);

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStartTimeIsDifferent() throws Exception {
    pojo2.setStartTime(MetadataPojoTest.START_TIME.plusMillis(2L));

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenCompletionTimeIsDifferent() throws Exception {
    pojo2.setCompletionTime(MetadataPojoTest.COMPLETION_TIME.plusMillis(2L));

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStateIsDifferent() throws Exception {
    pojo2.setState(MetadataPojoTest.STATE + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFailureReasonIsDifferent() throws Exception {
    pojo2.setFailureReason(MetadataPojoTest.FAILURE_REASON + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFailureMessageIsDifferent() throws Exception {
    pojo2.setFailureMessage(MetadataPojoTest.FAILURE_MSG + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenTypeIsDifferent() throws Exception {
    pojo2.setType(MetadataPojoTest.TYPE + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenCurrentContentTypeIsDifferent() throws Exception {
    pojo2.setContentType(MetadataPojoTest.CONTENT_TYPE + "2");

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFinaContentLengthIsDifferent() throws Exception {
    pojo2.setContentLength(MetadataPojoTest.CONTENT_LENGTH + 2L);

    Assert.assertThat(MetadataPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }
}
