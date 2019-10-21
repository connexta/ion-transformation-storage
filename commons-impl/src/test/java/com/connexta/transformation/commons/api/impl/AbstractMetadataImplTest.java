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
package com.connexta.transformation.commons.api.impl;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.TransformationStatus.State;
import com.connexta.transformation.commons.api.exceptions.InvalidFieldException;
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.MetadataPojo;
import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.unknown.UnknownRequestInfoPojo;
import com.github.npathai.hamcrestopt.OptionalMatchers;
import io.micrometer.core.instrument.Clock;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class AbstractMetadataImplTest {
  private static final String ID = "1234";
  private static final String TYPE = "metacard";
  private static final String TRANSFORM_ID = "2233xy";
  private static final String REQUEST_ID = "24567";
  private static final String REQUEST_ID2 = "11124567";
  private static final URL REQUEST_CURRENT_LOCATION;
  private static final URL REQUEST_FINAL_LOCATION;
  private static final URL REQUEST_METACARD_LOCATION;
  private static final URL REQUEST_METACARD_LOCATION2;
  private static final Instant START_TIME = Instant.ofEpochMilli(111L);
  private static final Instant COMPLETION_TIME = Instant.ofEpochMilli(2222L);
  private static final State STATE = State.SUCCESSFUL;
  private static final ErrorCode FAILURE_REASON = ErrorCode.TRANSFORMATION_FAILURE;
  private static final String FAILURE_MSG = "some message";
  private static final String CONTENT_TYPE = "application/xml";
  private static final long CONTENT_LENGTH = 981234L;

  static {
    try {
      REQUEST_CURRENT_LOCATION = new URL("https://localhost/current");
      REQUEST_FINAL_LOCATION = new URL("https://localhost/final");
      REQUEST_METACARD_LOCATION = new URL("https://localhost/metacard");
      REQUEST_METACARD_LOCATION2 = new URL("https://localhost/metacard2");
    } catch (MalformedURLException e) {
      throw new AssertionError(e);
    }
  }

  private static final RequestInfoImpl REQUEST =
      new RequestInfoImpl(
          AbstractMetadataImplTest.REQUEST_CURRENT_LOCATION,
          AbstractMetadataImplTest.REQUEST_FINAL_LOCATION,
          AbstractMetadataImplTest.REQUEST_METACARD_LOCATION);
  private static final RequestInfoImpl REQUEST2 =
      new RequestInfoImpl(
          AbstractMetadataImplTest.REQUEST_CURRENT_LOCATION,
          AbstractMetadataImplTest.REQUEST_FINAL_LOCATION,
          AbstractMetadataImplTest.REQUEST_METACARD_LOCATION2);
  private static final RequestInfoPojo REQUEST_POJO =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojo.CURRENT_VERSION)
          .setId(AbstractMetadataImplTest.REQUEST_ID)
          .setMetacardLocation(AbstractMetadataImplTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(AbstractMetadataImplTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(AbstractMetadataImplTest.REQUEST_FINAL_LOCATION);

  static {
    AbstractMetadataImplTest.REQUEST.setId(AbstractMetadataImplTest.REQUEST_ID);
    AbstractMetadataImplTest.REQUEST2.setId(AbstractMetadataImplTest.REQUEST_ID2);
  }

  @Rule public ExpectedException exception = ExpectedException.none();

  private final Clock clock = Mockito.mock(Clock.class);
  private final Clock clock2 = Mockito.mock(Clock.class);

  private AbstractMetadataImpl persistable;
  private AbstractMetadataImpl persistable2;

  private final MetadataPojo pojo =
      new MetadataPojo()
          .setId(AbstractMetadataImplTest.ID)
          .setVersion(MetadataPojo.CURRENT_VERSION)
          .setTransformId(AbstractMetadataImplTest.TRANSFORM_ID)
          .setType(AbstractMetadataImplTest.TYPE)
          .setRequestInfo(AbstractMetadataImplTest.REQUEST_POJO)
          .setState(AbstractMetadataImplTest.STATE)
          .setFailureReason(AbstractMetadataImplTest.FAILURE_REASON)
          .setFailureMessage(AbstractMetadataImplTest.FAILURE_MSG)
          .setStartTime(AbstractMetadataImplTest.START_TIME)
          .setCompletionTime(AbstractMetadataImplTest.COMPLETION_TIME)
          .setContentType(AbstractMetadataImplTest.CONTENT_TYPE)
          .setContentLength(AbstractMetadataImplTest.CONTENT_LENGTH);

  @Before
  public void setup() throws Exception {
    Mockito.when(clock.wallTime())
        .thenReturn(
            AbstractMetadataImplTest.START_TIME.toEpochMilli(),
            AbstractMetadataImplTest.COMPLETION_TIME.toEpochMilli());
    Mockito.when(clock2.wallTime())
        .thenReturn(
            AbstractMetadataImplTest.START_TIME.toEpochMilli(),
            AbstractMetadataImplTest.COMPLETION_TIME.toEpochMilli());
    this.persistable =
        AbstractMetadataImplTest.newMetadata(
            AbstractMetadataImplTest.TYPE,
            AbstractMetadataImplTest.TRANSFORM_ID,
            AbstractMetadataImplTest.REQUEST,
            clock);
    persistable.setId(AbstractMetadataImplTest.ID);
    persistable.setState(AbstractMetadataImplTest.STATE);
    persistable.setFailureReason(AbstractMetadataImplTest.FAILURE_REASON);
    persistable.setFailureMessage(AbstractMetadataImplTest.FAILURE_MSG);
    persistable.setStartTime(AbstractMetadataImplTest.START_TIME);
    persistable.setCompletionTime(AbstractMetadataImplTest.COMPLETION_TIME);
    persistable.setContentType(AbstractMetadataImplTest.CONTENT_TYPE);
    persistable.setContentLength(AbstractMetadataImplTest.CONTENT_LENGTH);
    this.persistable2 =
        AbstractMetadataImplTest.newMetadata(
            AbstractMetadataImplTest.TYPE,
            AbstractMetadataImplTest.TRANSFORM_ID,
            AbstractMetadataImplTest.REQUEST,
            clock2);
    persistable2.setId(AbstractMetadataImplTest.ID);
    persistable2.setState(AbstractMetadataImplTest.STATE);
    persistable2.setFailureReason(AbstractMetadataImplTest.FAILURE_REASON);
    persistable2.setFailureMessage(AbstractMetadataImplTest.FAILURE_MSG);
    persistable2.setStartTime(AbstractMetadataImplTest.START_TIME);
    persistable2.setCompletionTime(AbstractMetadataImplTest.COMPLETION_TIME);
    persistable2.setContentType(AbstractMetadataImplTest.CONTENT_TYPE);
    persistable2.setContentLength(AbstractMetadataImplTest.CONTENT_LENGTH);
  }

  @Test
  public void testConstructor() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);

    Mockito.when(clock.wallTime()).thenReturn(AbstractMetadataImplTest.START_TIME.toEpochMilli());

    final AbstractMetadataImpl persistable =
        AbstractMetadataImplTest.newMetadata(
            AbstractMetadataImplTest.TYPE,
            AbstractMetadataImplTest.TRANSFORM_ID,
            AbstractMetadataImplTest.REQUEST,
            clock);

    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(State.IN_PROGRESS));
    Assert.assertThat(persistable.getFailureReason(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getFailureMessage(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getCompletionTime(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getContentType(), OptionalMatchers.isEmpty());
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));

    Mockito.verify(clock).wallTime();
  }

  @Test
  public void testGetFailureReasonWhenNull() throws Exception {
    persistable2.setFailureReason(null);

    Assert.assertThat(persistable2.getFailureReason(), OptionalMatchers.isEmpty());
  }

  @Test
  public void testGetFailureMessageWhenNull() throws Exception {
    persistable2.setFailureMessage(null);

    Assert.assertThat(persistable2.getFailureMessage(), OptionalMatchers.isEmpty());
  }

  @Test
  public void testGetCompletionTimeWhenNull() throws Exception {
    persistable2.setCompletionTime(null);

    Assert.assertThat(persistable2.getCompletionTime(), OptionalMatchers.isEmpty());
  }

  @Test
  public void testGetDurationWhenCompletionTimeIsNotNull() throws Exception {
    Assert.assertThat(
        persistable.getDuration(),
        Matchers.equalTo(
            Duration.between(
                AbstractMetadataImplTest.START_TIME, AbstractMetadataImplTest.COMPLETION_TIME)));

    Mockito.verify(clock).wallTime();
  }

  @Test
  public void testGetDurationWhenCompletionIsNull() throws Exception {
    persistable2.setCompletionTime(null);

    Assert.assertThat(
        persistable2.getDuration(),
        Matchers.equalTo(
            Duration.between(
                AbstractMetadataImplTest.START_TIME, AbstractMetadataImplTest.COMPLETION_TIME)));

    Mockito.verify(clock2, Mockito.times(2)).wallTime();
  }

  @Test
  public void testGetContentTypeWhenNull() throws Exception {
    persistable2.setContentType(null);

    Assert.assertThat(persistable2.getContentType(), OptionalMatchers.isEmpty());
  }

  @Test
  public void testGetContentLengthWhenNegative() throws Exception {
    persistable2.setContentLength(-5L);

    Assert.assertThat(
        persistable2.getContentLength().stream().boxed().findFirst(), OptionalMatchers.isEmpty());
  }

  @Test
  public void testHasUnknownsWhenStateIsUnknown() throws Exception {
    persistable2.setState(State.UNKNOWN);

    Assert.assertThat(persistable2.hasUnknowns(), Matchers.equalTo(true));
  }

  @Test
  public void testHasUnknownsWhenFailureReasonIsUnknown() throws Exception {
    persistable2.setFailureReason(ErrorCode.UNKNOWN);

    Assert.assertThat(persistable2.hasUnknowns(), Matchers.equalTo(true));
  }

  @Test
  public void testWriteTo() throws Exception {
    final MetadataPojo pojo = new MetadataPojo();

    persistable.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  @Test
  public void testWriteToWhenTransformIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*transformId.*"));

    persistable2.setTransformId(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenTransformIdIsEmpty() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*transformId.*"));

    persistable2.setTransformId("");

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenTypeIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*type.*"));

    persistable2.setType(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenTypeIsEmpty() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*type.*"));

    persistable2.setType("");

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenRequestInfoIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*requestInfo.*"));

    persistable2.setRequestInfo(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenStateIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*state.*"));

    persistable2.setState(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenFailureReasonIsNull() throws Exception {
    persistable2.setFailureReason(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(pojo.getFailureReason(), Matchers.nullValue());
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  @Test
  public void testWriteToWhenFailureMessageIsNull() throws Exception {
    persistable2.setFailureMessage(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(pojo.getFailureMessage(), Matchers.nullValue());
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  @Test
  public void testWriteToWhenStartTimeIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*startTime.*"));

    persistable2.setStartTime(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenCompletionTimeIsNull() throws Exception {
    persistable2.setCompletionTime(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(pojo.getCompletionTime(), Matchers.nullValue());
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  @Test
  public void testWriteToWhenContentTypeIsNull() throws Exception {
    persistable2.setContentType(null);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(pojo.getContentType(), Matchers.nullValue());
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  @Test
  public void testWriteToWhenContentLengthIsMinusOne() throws Exception {
    persistable2.setContentLength(-1L);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(pojo.getContentLength(), Matchers.equalTo(-1L));
  }

  @Test
  public void testWriteToWhenContentLengthIsNegative() throws Exception {
    persistable2.setContentLength(-5L);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(pojo.getContentLength(), Matchers.equalTo(-1L));
  }

  @Test
  public void testWriteToWhenHasUnknowns() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.equalTo("unknown metadata"));

    persistable2.setState(State.UNKNOWN);

    final MetadataPojo pojo = new MetadataPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testReadFromCurrentVersion() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromFutureVersion() throws Exception {
    pojo.setVersion(9999999);
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromUnsupportedVersion() throws Exception {
    exception.expect(UnsupportedVersionException.class);
    exception.expectMessage(Matchers.matchesPattern(".*unsupported.*version.*"));

    pojo.setVersion(-1);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullTransformId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*transformId.*"));

    pojo.setTransformId(null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyTransformId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*transformId.*"));

    pojo.setTransformId("");
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullType() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*type.*"));

    pojo.setType(null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyType() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*type.*"));

    pojo.setType("");
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullRequestInfo() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*requestInfo.*"));

    pojo.setRequestInfo(null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithUnknownRequestInfo() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    pojo.setRequestInfo(
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojo.CURRENT_VERSION)
            .setId(AbstractMetadataImplTest.REQUEST_ID)
            .setMetacardLocation(AbstractMetadataImplTest.REQUEST_METACARD_LOCATION)
            .setCurrentLocation(AbstractMetadataImplTest.REQUEST_CURRENT_LOCATION)
            .setFinalLocation(AbstractMetadataImplTest.REQUEST_FINAL_LOCATION));

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithNullState() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*state.*"));

    pojo.setState((String) null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyState() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*state.*"));

    pojo.setState("");
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNewState() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    pojo.setState("NEW_STATE");

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(State.UNKNOWN));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithNullFailureReason() throws Exception {
    pojo.setFailureReason((String) null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(persistable.getFailureReason(), OptionalMatchers.isEmpty());
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testReadFromCurrentVersionWithNewFailureReason() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    pojo.setFailureReason("NEW_REASON");

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(), OptionalMatchers.isPresentAndIs(ErrorCode.UNKNOWN));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithNullFailureMessage() throws Exception {
    pojo.setFailureMessage(null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(persistable.getFailureMessage(), OptionalMatchers.isEmpty());
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testReadFromCurrentVersionWithNullContentType() throws Exception {
    pojo.setContentType(null);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(persistable.getContentType(), OptionalMatchers.isEmpty());
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_LENGTH));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testReadFromCurrentVersionWithMinusOneContentType() throws Exception {
    pojo.setContentLength(-1L);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testReadFromCurrentVersionWithNegativeContentType() throws Exception {
    pojo.setContentLength(-5L);
    final AbstractMetadataImpl persistable = AbstractMetadataImplTest.newMetadata(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getMetadataType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(persistable.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE));
    Assert.assertThat(
        persistable.getFailureReason(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_REASON));
    Assert.assertThat(
        persistable.getFailureMessage(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(
        persistable.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        persistable.getContentType(),
        OptionalMatchers.isPresentAndIs(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        persistable.getContentLength().stream().boxed().findFirst(), OptionalMatchers.isEmpty());
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(persistable.hashCode0(), Matchers.equalTo(persistable2.hashCode0()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    persistable2.setType(AbstractMetadataImplTest.TYPE + "2");

    Assert.assertThat(
        persistable.hashCode0(), Matchers.not(Matchers.equalTo(persistable2.hashCode0())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    Assert.assertThat(persistable.equals0(persistable2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(persistable.equals0(persistable), Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(persistable.equals0(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotTheSameClass() throws Exception {
    Assert.assertThat(persistable.equals0("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    persistable2.setId(AbstractMetadataImplTest.ID + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenTransformIdIsDifferent() throws Exception {
    persistable2.setTransformId(AbstractMetadataImplTest.TRANSFORM_ID + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenTypeIsDifferent() throws Exception {
    persistable2.setType(AbstractMetadataImplTest.TYPE + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenRequestInfoIsDifferent() throws Exception {
    persistable2.setRequestInfo(AbstractMetadataImplTest.REQUEST2);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStateIsDifferent() throws Exception {
    persistable2.setState(State.IN_PROGRESS);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFailureReasonIsDifferent() throws Exception {
    persistable2.setFailureReason(ErrorCode.UNKNOWN);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFailureMessageIsDifferent() throws Exception {
    persistable2.setFailureMessage(AbstractMetadataImplTest.FAILURE_MSG + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStartTimeIsDifferent() throws Exception {
    persistable2.setStartTime(AbstractMetadataImplTest.START_TIME.minusMillis(2L));

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenCompletionTimeIsDifferent() throws Exception {
    persistable2.setCompletionTime(null);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenContentTypeIsDifferent() throws Exception {
    persistable2.setContentType(AbstractMetadataImplTest.CONTENT_TYPE + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenContentLengthIsDifferent() throws Exception {
    persistable2.setContentLength(AbstractMetadataImplTest.CONTENT_LENGTH + 2L);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testToPojo() throws Exception {
    final MetadataPojo pojo = AbstractMetadataImpl.toPojo(persistable);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractMetadataImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getTransformId(), Matchers.equalTo(AbstractMetadataImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getType(), Matchers.equalTo(AbstractMetadataImplTest.TYPE));
    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractMetadataImplTest.REQUEST_POJO));
    Assert.assertThat(pojo.getState(), Matchers.equalTo(AbstractMetadataImplTest.STATE.name()));
    Assert.assertThat(
        pojo.getFailureReason(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_REASON.name()));
    Assert.assertThat(
        pojo.getFailureMessage(), Matchers.equalTo(AbstractMetadataImplTest.FAILURE_MSG));
    Assert.assertThat(pojo.getStartTime(), Matchers.equalTo(AbstractMetadataImplTest.START_TIME));
    Assert.assertThat(
        pojo.getCompletionTime(), Matchers.equalTo(AbstractMetadataImplTest.COMPLETION_TIME));
    Assert.assertThat(
        pojo.getContentType(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_TYPE));
    Assert.assertThat(
        pojo.getContentLength(), Matchers.equalTo(AbstractMetadataImplTest.CONTENT_LENGTH));
  }

  private static AbstractMetadataImpl newMetadata(
      String type, String transformId, RequestInfo requestInfo, Clock clock) {
    return Mockito.mock(
        AbstractMetadataImpl.class,
        Mockito.withSettings()
            .useConstructor(type, transformId, requestInfo, clock)
            .defaultAnswer(Mockito.CALLS_REAL_METHODS));
  }

  private static AbstractMetadataImpl newMetadata(Clock clock) {
    return Mockito.mock(
        AbstractMetadataImpl.class,
        Mockito.withSettings().useConstructor(clock).defaultAnswer(Mockito.CALLS_REAL_METHODS));
  }
}
