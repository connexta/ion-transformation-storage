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
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.MetadataPojo;
import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.TransformationPojo;
import com.connexta.transformation.pojo.unknown.UnknownMetadataPojo;
import com.connexta.transformation.pojo.unknown.UnknownRequestInfoPojo;
import com.github.npathai.hamcrestopt.OptionalMatchers;
import io.micrometer.core.instrument.Clock;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class AbstractTransformationImplTest {
  private static final String TRANSFORM_ID = "2233xy";
  private static final String REQUEST_ID = "24567";
  private static final String REQUEST_ID2 = "11124567";
  private static final URL REQUEST_CURRENT_LOCATION;
  private static final URL REQUEST_FINAL_LOCATION;
  private static final URL REQUEST_METACARD_LOCATION;
  private static final URL REQUEST_METACARD_LOCATION2;
  private static final Instant START_TIME = Instant.ofEpochMilli(111L);
  private static final Instant COMPLETION_TIME = Instant.ofEpochMilli(2222L);
  private static final String METADATA_ID = "1234";
  private static final String METADATA_TYPE = "metacard";
  private static final Instant METADATA_START_TIME =
      AbstractTransformationImplTest.START_TIME.plusMillis(1L);
  private static final Instant METADATA_COMPLETION_TIME =
      AbstractTransformationImplTest.COMPLETION_TIME.minusMillis(1L);
  private static final State METADATA_STATE = State.SUCCESSFUL;
  private static final ErrorCode METADATA_FAILURE_REASON = null;
  private static final String METADATA_FAILURE_MSG = null;
  private static final String METADATA_CONTENT_TYPE = "application/xml";
  private static final long METADATA_CONTENT_LENGTH = 981234L;
  private static final String METADATA_ID2 = "2234";
  private static final String METADATA_TYPE2 = "irm";
  private static final Instant METADATA_START_TIME2 =
      AbstractTransformationImplTest.START_TIME.plusMillis(2L);
  private static final Instant METADATA_COMPLETION_TIME2 =
      AbstractTransformationImplTest.COMPLETION_TIME;
  private static final State METADATA_STATE2 = State.FAILED;
  private static final String METADATA_ID3 = "2234567";
  private static final String METADATA_TYPE3 = "ddms";
  private static final State METADATA_STATE3 = State.IN_PROGRESS;
  private static final Instant METADATA_START_TIME3 =
      AbstractTransformationImplTest.START_TIME.plusMillis(3L);
  private static final ErrorCode METADATA_FAILURE_REASON2 = ErrorCode.TRANSFORMATION_FAILURE;
  private static final String METADATA_FAILURE_MSG2 = "some message";
  private static final String METADATA_CONTENT_TYPE2 = null;
  private static final long METADATA_CONTENT_LENGTH2 = -1L;

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
          AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION,
          AbstractTransformationImplTest.REQUEST_FINAL_LOCATION,
          AbstractTransformationImplTest.REQUEST_METACARD_LOCATION);
  private static final RequestInfoImpl REQUEST2 =
      new RequestInfoImpl(
          AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION,
          AbstractTransformationImplTest.REQUEST_FINAL_LOCATION,
          AbstractTransformationImplTest.REQUEST_METACARD_LOCATION2);
  private static final AbstractMetadataImpl METADATA =
      AbstractTransformationImplTest.newMetadata(
          AbstractTransformationImplTest.METADATA_TYPE,
          AbstractTransformationImplTest.TRANSFORM_ID,
          AbstractTransformationImplTest.REQUEST,
          Mockito.mock(Clock.class));
  private static final AbstractMetadataImpl METADATA2 =
      AbstractTransformationImplTest.newMetadata(
          AbstractTransformationImplTest.METADATA_TYPE2,
          AbstractTransformationImplTest.TRANSFORM_ID,
          AbstractTransformationImplTest.REQUEST,
          Mockito.mock(Clock.class));
  private static final AbstractMetadataImpl METADATA3 =
      AbstractTransformationImplTest.newMetadata(
          AbstractTransformationImplTest.METADATA_TYPE3,
          AbstractTransformationImplTest.TRANSFORM_ID,
          AbstractTransformationImplTest.REQUEST,
          Mockito.mock(Clock.class));

  static {
    AbstractTransformationImplTest.REQUEST.setId(AbstractTransformationImplTest.REQUEST_ID);
    AbstractTransformationImplTest.REQUEST2.setId(AbstractTransformationImplTest.REQUEST_ID2);

    AbstractTransformationImplTest.METADATA.setId(AbstractTransformationImplTest.METADATA_ID);
    AbstractTransformationImplTest.METADATA.setState(AbstractTransformationImplTest.METADATA_STATE);
    AbstractTransformationImplTest.METADATA.setFailureReason(
        AbstractTransformationImplTest.METADATA_FAILURE_REASON);
    AbstractTransformationImplTest.METADATA.setFailureMessage(
        AbstractTransformationImplTest.METADATA_FAILURE_MSG);
    AbstractTransformationImplTest.METADATA.setStartTime(
        AbstractTransformationImplTest.METADATA_START_TIME);
    AbstractTransformationImplTest.METADATA.setCompletionTime(
        AbstractTransformationImplTest.METADATA_COMPLETION_TIME);
    AbstractTransformationImplTest.METADATA.setContentType(
        AbstractTransformationImplTest.METADATA_CONTENT_TYPE);
    AbstractTransformationImplTest.METADATA.setContentLength(
        AbstractTransformationImplTest.METADATA_CONTENT_LENGTH);

    AbstractTransformationImplTest.METADATA2.setId(AbstractTransformationImplTest.METADATA_ID2);
    AbstractTransformationImplTest.METADATA2.setState(
        AbstractTransformationImplTest.METADATA_STATE2);
    AbstractTransformationImplTest.METADATA2.setFailureReason(
        AbstractTransformationImplTest.METADATA_FAILURE_REASON2);
    AbstractTransformationImplTest.METADATA2.setFailureMessage(
        AbstractTransformationImplTest.METADATA_FAILURE_MSG2);
    AbstractTransformationImplTest.METADATA2.setStartTime(
        AbstractTransformationImplTest.METADATA_START_TIME2);
    AbstractTransformationImplTest.METADATA2.setCompletionTime(
        AbstractTransformationImplTest.METADATA_COMPLETION_TIME2);
    AbstractTransformationImplTest.METADATA2.setContentType(
        AbstractTransformationImplTest.METADATA_CONTENT_TYPE2);
    AbstractTransformationImplTest.METADATA2.setContentLength(
        AbstractTransformationImplTest.METADATA_CONTENT_LENGTH2);

    AbstractTransformationImplTest.METADATA3.setId(AbstractTransformationImplTest.METADATA_ID3);
    AbstractTransformationImplTest.METADATA3.setState(
        AbstractTransformationImplTest.METADATA_STATE3);
    AbstractTransformationImplTest.METADATA3.setFailureReason(null);
    AbstractTransformationImplTest.METADATA3.setFailureMessage(null);
    AbstractTransformationImplTest.METADATA3.setStartTime(
        AbstractTransformationImplTest.METADATA_START_TIME3);
    AbstractTransformationImplTest.METADATA3.setCompletionTime(null);
    AbstractTransformationImplTest.METADATA3.setContentType(null);
    AbstractTransformationImplTest.METADATA3.setContentLength(-1L);
  }

  private static final RequestInfoPojo REQUEST_POJO =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojo.CURRENT_VERSION)
          .setId(AbstractTransformationImplTest.REQUEST_ID)
          .setMetacardLocation(AbstractTransformationImplTest.REQUEST_METACARD_LOCATION)
          .setCurrentLocation(AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION)
          .setFinalLocation(AbstractTransformationImplTest.REQUEST_FINAL_LOCATION);
  private static final MetadataPojo METADATA_POJO =
      new MetadataPojo()
          .setId(AbstractTransformationImplTest.METADATA_ID)
          .setVersion(MetadataPojo.CURRENT_VERSION)
          .setTransformId(AbstractTransformationImplTest.TRANSFORM_ID)
          .setType(AbstractTransformationImplTest.METADATA_TYPE)
          .setRequestInfo(AbstractTransformationImplTest.REQUEST_POJO)
          .setState(AbstractTransformationImplTest.METADATA_STATE)
          .setFailureReason(AbstractTransformationImplTest.METADATA_FAILURE_REASON)
          .setFailureMessage(AbstractTransformationImplTest.METADATA_FAILURE_MSG)
          .setStartTime(AbstractTransformationImplTest.METADATA_START_TIME)
          .setCompletionTime(AbstractTransformationImplTest.METADATA_COMPLETION_TIME)
          .setContentType(AbstractTransformationImplTest.METADATA_CONTENT_TYPE)
          .setContentLength(AbstractTransformationImplTest.METADATA_CONTENT_LENGTH);
  private static final MetadataPojo METADATA_POJO2 =
      new MetadataPojo()
          .setId(AbstractTransformationImplTest.METADATA_ID2)
          .setVersion(MetadataPojo.CURRENT_VERSION)
          .setTransformId(AbstractTransformationImplTest.TRANSFORM_ID)
          .setType(AbstractTransformationImplTest.METADATA_TYPE2)
          .setRequestInfo(AbstractTransformationImplTest.REQUEST_POJO)
          .setState(AbstractTransformationImplTest.METADATA_STATE2)
          .setFailureReason(AbstractTransformationImplTest.METADATA_FAILURE_REASON2)
          .setFailureMessage(AbstractTransformationImplTest.METADATA_FAILURE_MSG2)
          .setStartTime(AbstractTransformationImplTest.METADATA_START_TIME2)
          .setCompletionTime(AbstractTransformationImplTest.METADATA_COMPLETION_TIME2)
          .setContentType(AbstractTransformationImplTest.METADATA_CONTENT_TYPE2)
          .setContentLength(AbstractTransformationImplTest.METADATA_CONTENT_LENGTH2);

  @Rule public ExpectedException exception = ExpectedException.none();

  private final Clock clock = Mockito.mock(Clock.class);
  private final Clock clock2 = Mockito.mock(Clock.class);

  private AbstractTransformationImpl persistable;
  private AbstractTransformationImpl persistable2;

  private final TransformationPojo pojo =
      new TransformationPojo()
          .setId(AbstractTransformationImplTest.TRANSFORM_ID)
          .setVersion(TransformationPojo.CURRENT_VERSION)
          .setRequestInfo(AbstractTransformationImplTest.REQUEST_POJO)
          .setStartTime(AbstractTransformationImplTest.START_TIME)
          .addMetadata(AbstractTransformationImplTest.METADATA_POJO)
          .addMetadata(AbstractTransformationImplTest.METADATA_POJO2);

  @Before
  public void setup() throws Exception {
    Mockito.when(clock.wallTime())
        .thenReturn(
            AbstractTransformationImplTest.START_TIME.toEpochMilli(),
            AbstractTransformationImplTest.COMPLETION_TIME.toEpochMilli());
    Mockito.when(clock2.wallTime())
        .thenReturn(
            AbstractTransformationImplTest.START_TIME.toEpochMilli(),
            AbstractTransformationImplTest.COMPLETION_TIME.toEpochMilli());
    this.persistable =
        AbstractTransformationImplTest.newTransformation(
            AbstractTransformationImplTest.REQUEST,
            AbstractTransformationImplTest.START_TIME,
            clock);
    persistable.setId(AbstractTransformationImplTest.TRANSFORM_ID);
    persistable.add(
        AbstractTransformationImplTest.METADATA, AbstractTransformationImplTest.METADATA2);
    this.persistable2 =
        AbstractTransformationImplTest.newTransformation(
            AbstractTransformationImplTest.REQUEST,
            AbstractTransformationImplTest.START_TIME,
            clock2);
    persistable2.setId(AbstractTransformationImplTest.TRANSFORM_ID);
    persistable2.add(
        AbstractTransformationImplTest.METADATA, AbstractTransformationImplTest.METADATA2);
  }

  @Test
  public void testConstructor() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);

    Mockito.when(clock.wallTime())
        .thenReturn(AbstractTransformationImplTest.START_TIME.toEpochMilli());

    final AbstractTransformationImpl persistable =
        AbstractTransformationImplTest.newTransformation(
            AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION,
            AbstractTransformationImplTest.REQUEST_FINAL_LOCATION,
            AbstractTransformationImplTest.REQUEST_METACARD_LOCATION,
            clock);

    final RequestInfo requestInfo = persistable.getRequestInfo();

    Assert.assertThat(
        requestInfo.getMetacardLocation(),
        Matchers.equalTo(AbstractTransformationImplTest.REQUEST_METACARD_LOCATION));
    Assert.assertThat(
        requestInfo.getCurrentLocation(),
        Matchers.equalTo(AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION));
    Assert.assertThat(
        requestInfo.getFinalLocation(),
        Matchers.equalTo(AbstractTransformationImplTest.REQUEST_FINAL_LOCATION));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    Assert.assertThat(persistable.getTransformId(), Matchers.equalTo(persistable.getId()));
    Assert.assertThat(persistable.metadatas().count(), Matchers.equalTo(0L));
    Assert.assertThat(persistable.getClock(), Matchers.sameInstance(clock));

    Mockito.verify(clock).wallTime();
  }

  @Test
  public void testGetCompletionTime() throws Exception {
    Assert.assertThat(
        persistable2.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(AbstractTransformationImplTest.COMPLETION_TIME));
  }

  @Test
  public void testGetDurationWhenCompletionTimeIsAvailable() throws Exception {
    Assert.assertThat(
        persistable.getDuration(),
        Matchers.equalTo(
            Duration.between(
                AbstractTransformationImplTest.START_TIME,
                AbstractTransformationImplTest.COMPLETION_TIME)));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testGetDurationTimeWhenStillPending() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);

    Mockito.when(clock.wallTime())
        .thenReturn(
            AbstractTransformationImplTest.START_TIME.toEpochMilli(),
            AbstractTransformationImplTest.COMPLETION_TIME.toEpochMilli());

    final AbstractTransformationImpl persistable =
        AbstractTransformationImplTest.newTransformation(
            AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION,
            AbstractTransformationImplTest.REQUEST_FINAL_LOCATION,
            AbstractTransformationImplTest.REQUEST_METACARD_LOCATION,
            clock);

    Assert.assertThat(persistable.getState(), Matchers.equalTo(State.IN_PROGRESS));
    Assert.assertThat(
        persistable.getDuration(),
        Matchers.equalTo(
            Duration.between(
                AbstractTransformationImplTest.START_TIME,
                AbstractTransformationImplTest.COMPLETION_TIME)));

    Mockito.verify(clock, Mockito.times(2)).wallTime();
  }

  @Test
  public void testMetadatas() throws Exception {
    Assert.assertThat(
        persistable.metadatas().collect(Collectors.toList()),
        Matchers.hasItems(
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA),
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA2)));
  }

  @Test
  public void testGet() throws Exception {
    Assert.assertThat(
        persistable.get(AbstractTransformationImplTest.METADATA_TYPE),
        Matchers.sameInstance(AbstractTransformationImplTest.METADATA));
  }

  @Test(expected = TransformationNotFoundException.class)
  public void testGetWhenNotFound() throws Exception {
    persistable.get("dummy");
  }

  @Test
  public void testAddWithSet() throws Exception {
    final AbstractTransformationImpl persistable =
        newTransformation(
            AbstractTransformationImplTest.REQUEST,
            AbstractTransformationImplTest.START_TIME,
            clock);

    persistable.add(
        Set.of(AbstractTransformationImplTest.METADATA, AbstractTransformationImplTest.METADATA2));

    Assert.assertThat(
        persistable.metadatas().collect(Collectors.toList()),
        Matchers.hasItems(
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA),
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA2)));
  }

  @Test
  public void testAddWithStream() throws Exception {
    final AbstractTransformationImpl persistable =
        newTransformation(
            AbstractTransformationImplTest.REQUEST,
            AbstractTransformationImplTest.START_TIME,
            clock);

    persistable.add(
        Stream.of(
            AbstractTransformationImplTest.METADATA, AbstractTransformationImplTest.METADATA2));

    Assert.assertThat(
        persistable.metadatas().collect(Collectors.toList()),
        Matchers.hasItems(
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA),
            Matchers.sameInstance(AbstractTransformationImplTest.METADATA2)));
  }

  @Test
  public void testWriteTo() throws Exception {
    final TransformationPojo pojo = new TransformationPojo();

    persistable.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(MetadataPojo.CURRENT_VERSION));

    Assert.assertThat(
        pojo.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST_POJO));
    Assert.assertThat(
        pojo.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    Assert.assertThat(
        pojo.getMetadatas(),
        Matchers.hasItems(
            AbstractTransformationImplTest.METADATA_POJO,
            AbstractTransformationImplTest.METADATA_POJO2));
  }

  @Test
  public void testWriteToWhenTransformIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*id.*"));

    persistable2.setId(null);

    final TransformationPojo pojo = new TransformationPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenTransformIdIsEmpty() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*id.*"));

    persistable2.setId("");

    final TransformationPojo pojo = new TransformationPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenRequestInfoIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*requestInfo.*"));

    persistable2.setRequestInfo(null);

    final TransformationPojo pojo = new TransformationPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenMetadatasIsEmpty() throws Exception {
    final AbstractTransformationImpl persistable =
        AbstractTransformationImplTest.newTransformation(
            AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION,
            AbstractTransformationImplTest.REQUEST_FINAL_LOCATION,
            AbstractTransformationImplTest.REQUEST_METACARD_LOCATION,
            clock);

    final TransformationPojo pojo = new TransformationPojo();

    persistable.writeTo(pojo);

    Assert.assertThat(pojo.getMetadatas(), Matchers.emptyIterable());
  }

  @Test
  public void testWriteToWhenStartTimeIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*startTime.*"));

    persistable2.setStartTime(null);

    final TransformationPojo pojo = new TransformationPojo();

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenHasUnknowns() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.equalTo("unknown transformation"));

    final RequestInfoPojo unknownPojo =
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojo.CURRENT_VERSION)
            .setId(AbstractTransformationImplTest.REQUEST_ID)
            .setMetacardLocation(AbstractTransformationImplTest.REQUEST_METACARD_LOCATION)
            .setCurrentLocation((AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION))
            .setFinalLocation(AbstractTransformationImplTest.REQUEST_FINAL_LOCATION);

    pojo.setRequestInfo(unknownPojo);

    final AbstractTransformationImpl persistable3 = newTransformation(Mockito.mock(Clock.class));

    persistable3.readFrom(pojo);

    final TransformationPojo pojo = new TransformationPojo();

    persistable3.writeTo(pojo);
  }

  @Test
  public void testReadFromCurrentVersion() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    AbstractTransformationImplTest.assertMetadatas(
        persistable,
        AbstractTransformationImplTest.METADATA,
        AbstractTransformationImplTest.METADATA2);
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromFutureVersion() throws Exception {
    pojo.setVersion(9999999);
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    AbstractTransformationImplTest.assertMetadatas(
        persistable,
        AbstractTransformationImplTest.METADATA,
        AbstractTransformationImplTest.METADATA2);
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromUnsupportedVersion() throws Exception {
    exception.expect(UnsupportedVersionException.class);
    exception.expectMessage(Matchers.matchesPattern(".*unsupported.*version.*"));

    pojo.setVersion(-1);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*id.*"));

    pojo.setId(null);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*id.*"));

    pojo.setId("");
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullRequestInfo() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*requestInfo.*"));

    pojo.setRequestInfo(null);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithUnknownRequestInfo() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    pojo.setRequestInfo(
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojo.CURRENT_VERSION)
            .setId(AbstractTransformationImplTest.REQUEST_ID)
            .setMetacardLocation(AbstractTransformationImplTest.REQUEST_METACARD_LOCATION)
            .setCurrentLocation(AbstractTransformationImplTest.REQUEST_CURRENT_LOCATION)
            .setFinalLocation(AbstractTransformationImplTest.REQUEST_FINAL_LOCATION));

    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    AbstractTransformationImplTest.assertMetadatas(
        persistable,
        AbstractTransformationImplTest.METADATA,
        AbstractTransformationImplTest.METADATA2);
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithNullMetadatas() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    pojo.setMetadatas((List<MetadataPojo>) null);

    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    Assert.assertThat(persistable.metadatas().count(), Matchers.equalTo(0L));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithNoMetadatas() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    pojo.setMetadatas(Collections.emptyList());

    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    Assert.assertThat(persistable.metadatas().count(), Matchers.equalTo(0L));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testReadFromCurrentVersionWithUnknownMetadatas() throws Exception {
    final Clock clock = Mockito.mock(Clock.class);
    final AbstractTransformationImpl persistable = newTransformation(clock);

    pojo.setMetadatas(new ArrayList<>());
    pojo.addMetadata(AbstractTransformationImplTest.METADATA_POJO);
    pojo.addMetadata(
        new UnknownMetadataPojo()
            .setId(AbstractTransformationImplTest.METADATA_ID2)
            .setVersion(MetadataPojo.CURRENT_VERSION)
            .setTransformId(AbstractTransformationImplTest.TRANSFORM_ID)
            .setType(AbstractTransformationImplTest.METADATA_TYPE2)
            .setRequestInfo(AbstractTransformationImplTest.REQUEST_POJO)
            .setState(AbstractTransformationImplTest.METADATA_STATE2)
            .setFailureReason(AbstractTransformationImplTest.METADATA_FAILURE_REASON2)
            .setFailureMessage(AbstractTransformationImplTest.METADATA_FAILURE_MSG2)
            .setStartTime(AbstractTransformationImplTest.METADATA_START_TIME2)
            .setCompletionTime(AbstractTransformationImplTest.METADATA_COMPLETION_TIME2)
            .setContentType(AbstractTransformationImplTest.METADATA_CONTENT_TYPE2)
            .setContentLength(AbstractTransformationImplTest.METADATA_CONTENT_LENGTH2));
    persistable.readFrom(pojo);

    Assert.assertThat(
        persistable.getId(), Matchers.equalTo(AbstractTransformationImplTest.TRANSFORM_ID));
    Assert.assertThat(
        persistable.getRequestInfo(), Matchers.equalTo(AbstractTransformationImplTest.REQUEST));
    Assert.assertThat(
        persistable.getStartTime(), Matchers.equalTo(AbstractTransformationImplTest.START_TIME));
    AbstractTransformationImplTest.assertMetadatas(
        persistable,
        AbstractTransformationImplTest.METADATA,
        AbstractTransformationImplTest.METADATA2);
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));

    Mockito.verify(clock, Mockito.never()).wallTime();
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(persistable.hashCode0(), Matchers.equalTo(persistable2.hashCode0()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    persistable2.setStartTime(AbstractTransformationImplTest.START_TIME.plusMillis(2L));

    Assert.assertThat(
        persistable.hashCode0(), Matchers.not(Matchers.equalTo(persistable2.hashCode0())));
  }

  private static void assertMetadatas(
      AbstractTransformationImpl transformation, AbstractMetadataImpl... metadatas) {
    final Map<String, AbstractMetadataImpl> expecteds =
        Stream.of(metadatas)
            .collect(Collectors.toMap(AbstractMetadataImpl::getId, Function.identity()));
    final List<AbstractMetadataImpl> actuals =
        transformation
            .metadatas()
            .map(AbstractMetadataImpl.class::cast)
            .collect(Collectors.toList());

    Assert.assertThat(
        "mismatched number of metadatas", actuals.size(), Matchers.equalTo(expecteds.size()));
    for (final AbstractMetadataImpl actual : actuals) {
      final AbstractMetadataImpl expected = expecteds.remove(actual.getId());

      Assert.assertThat("unknown metadata: " + actual, expected, Matchers.notNullValue());
      Assert.assertThat(expected.equals0(actual), Matchers.equalTo(true));
    }
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
    persistable2.setId(AbstractTransformationImplTest.TRANSFORM_ID + "2");

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenStartTimeIsDifferent() throws Exception {
    persistable2.setStartTime(AbstractTransformationImplTest.START_TIME.plusMillis(2L));

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenRequestInfoIsDifferent() throws Exception {
    persistable2.setRequestInfo(AbstractTransformationImplTest.REQUEST2);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenMetatadasIsDifferent() throws Exception {
    persistable2.add(AbstractTransformationImplTest.METADATA3);

    Assert.assertThat(persistable.equals0(persistable2), Matchers.not(Matchers.equalTo(true)));
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

  private static AbstractTransformationImpl newTransformation(
      URL currentLocation, URL finalLocation, URL metacardLocation, Clock clock) {
    return Mockito.mock(
        AbstractTransformationImpl.class,
        Mockito.withSettings()
            .useConstructor(currentLocation, finalLocation, metacardLocation, clock)
            .defaultAnswer(Mockito.CALLS_REAL_METHODS));
  }

  private static AbstractTransformationImpl newTransformation(
      RequestInfo requestInfo, Instant startTime, Clock clock) {
    return Mockito.mock(
        AbstractTransformationImpl.class,
        Mockito.withSettings()
            .useConstructor(requestInfo, startTime, clock)
            .defaultAnswer(Mockito.CALLS_REAL_METHODS));
  }

  private static AbstractTransformationImpl newTransformation(Clock clock) throws Exception {
    final AbstractTransformationImpl transformation =
        Mockito.mock(
            AbstractTransformationImpl.class,
            Mockito.withSettings().useConstructor(clock).defaultAnswer(Mockito.CALLS_REAL_METHODS));

    Mockito.when(transformation.fromPojo(Mockito.any(MetadataPojo.class)))
        .thenAnswer(
            (Answer<AbstractMetadataImpl>)
                i -> {
                  final MetadataPojo pojo = i.getArgument(0);
                  final AbstractMetadataImpl metadata =
                      AbstractTransformationImplTest.newMetadata(clock);

                  metadata.readFrom(pojo);
                  return metadata;
                });
    return transformation;
  }
}
