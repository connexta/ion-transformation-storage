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
package com.connexta.transformation.commons.inmemory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.MetadataTransformation;
import com.connexta.transformation.commons.api.Transformation;
import com.connexta.transformation.commons.api.TransformationStatus.State;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.github.npathai.hamcrestopt.OptionalMatchers;
import com.google.common.io.CharStreams;
import io.micrometer.core.instrument.Clock;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class InMemoryTransformationManagerTest {

  public static final String TEST_METADATA_TYPE = "myMetadataType";

  private static final Instant TIME = Instant.ofEpochMilli(1L);
  private static final Instant TIME2 = Instant.ofEpochMilli(22L);
  private static final Instant TIME3 = Instant.ofEpochMilli(333L);
  private static final Instant TIME4 = Instant.ofEpochMilli(4444L);
  private static final Instant TIME5 = Instant.ofEpochMilli(55555L);

  private final Clock clock = Mockito.mock(Clock.class);

  private final InMemoryTransformationManager manager = new InMemoryTransformationManager(clock);
  private URL currentUri;
  private URL finalUri;
  private URL metacardUri;

  @Before
  public void setup() throws Exception {
    currentUri = new URL("http://current.com");
    finalUri = new URL("http://final.com");
    metacardUri = new URL("http://metacard.com");
    when(clock.wallTime())
        .thenReturn(
            InMemoryTransformationManagerTest.TIME.toEpochMilli(),
            InMemoryTransformationManagerTest.TIME2.toEpochMilli(),
            InMemoryTransformationManagerTest.TIME3.toEpochMilli(),
            InMemoryTransformationManagerTest.TIME4.toEpochMilli(),
            InMemoryTransformationManagerTest.TIME5.toEpochMilli());
  }

  @Test
  public void createTransformInitializesProperly() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);

    assertEquals(transformation.getRequestInfo().getCurrentLocation(), currentUri);
    assertEquals(transformation.getRequestInfo().getFinalLocation(), finalUri);
    assertEquals(transformation.getRequestInfo().getMetacardLocation(), metacardUri);
    assertNotNull(transformation.getTransformId());
    assertEquals(transformation.getStartTime(), InMemoryTransformationManagerTest.TIME);
    assertThat(transformation.getCompletionTime(), OptionalMatchers.isEmpty());
    assertThat(
        transformation.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME, InMemoryTransformationManagerTest.TIME2)));
    assertEquals(transformation.getState(), State.IN_PROGRESS);
    assertEquals(transformation.metadataTypes().count(), 0);
    assertEquals(transformation.metadatas().count(), 0);
  }

  @Test(expected = TransformationNotFoundException.class)
  public void getTransformNotFound() throws Exception {
    manager.get("gibberish-ID");
  }

  @Test(expected = TransformationNotFoundException.class)
  public void getMetadataWithInvalidTransformThrowsException() throws Exception {
    manager.get("gibberish-ID", TEST_METADATA_TYPE);
  }

  @Test(expected = TransformationNotFoundException.class)
  public void getInvalidMetadataThrowsException() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    manager.get(transformation.getTransformId(), "gibberish-type");
  }

  @Test
  public void addEmptyMetadataTypeInitializesProperly() throws Exception {
    String id = manager.createTransform(currentUri, finalUri, metacardUri).getTransformId();
    Transformation transformation = manager.get(id);
    transformation.add(TEST_METADATA_TYPE);

    assertEquals(transformation.metadataTypes().count(), 1);
    assertEquals(transformation.metadataTypes().findFirst().get(), TEST_METADATA_TYPE);
    assertEquals(transformation.metadatas().count(), 1);

    MetadataTransformation metadata = transformation.metadatas().findFirst().get();
    assertEquals(metadata.getStartTime(), InMemoryTransformationManagerTest.TIME2);
    assertEquals(metadata.getState(), State.IN_PROGRESS);
    assertEquals(metadata.getMetadataType(), TEST_METADATA_TYPE);
    assertEquals(metadata.getTransformId(), transformation.getTransformId());
    assertEquals(metadata.getRequestInfo(), transformation.getRequestInfo());
    assertThat(transformation.getCompletionTime(), OptionalMatchers.isEmpty());
    assertFalse(metadata.getContent().isPresent());
    assertFalse(metadata.getContentType().isPresent());
    assertFalse(metadata.getFailureReason().isPresent());
    assertFalse(metadata.getFailureMessage().isPresent());
    assertThat(
        metadata.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME2, InMemoryTransformationManagerTest.TIME3)));
    assertThat(
        transformation.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME, InMemoryTransformationManagerTest.TIME4)));
  }

  @Test
  public void testAddDuplicateMetadataTypeReturnsOriginalOne() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    Assert.assertThat(transformation.add(TEST_METADATA_TYPE), Matchers.sameInstance(metadata));
  }

  @Test(expected = IllegalStateException.class)
  public void testAddMetadataWhenTransformationIsCompletedFails() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);

    transformation.add(TEST_METADATA_TYPE).fail(ErrorCode.TRANSFORMATION_FAILURE, "this is why");

    transformation.add("new_type");
  }

  @Test(expected = IllegalStateException.class)
  public void testAddMetadataWhenTransformationIsDeletedFails() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);

    transformation.delete();

    transformation.add(TEST_METADATA_TYPE);
  }

  @Test
  public void succeedMetadataStoresContents() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);

    MetadataTransformation metadata =
        manager.get(transformation.getTransformId(), TEST_METADATA_TYPE);
    String metadataContent = "testing";
    String metadataContentType = "text/plain";
    metadata.succeed(metadataContentType, new ByteArrayInputStream(metadataContent.getBytes()));

    assertTrue(metadata.getContent().isPresent());
    String actualMetadataContent = null;
    try (final Reader reader = new InputStreamReader(metadata.getContent().get())) {
      actualMetadataContent = CharStreams.toString(reader);
    }
    assertEquals(actualMetadataContent, metadataContent);
    assertTrue(metadata.wasSuccessful());
    assertTrue(metadata.getContentType().isPresent());
    assertEquals(metadata.getContentType().get(), metadataContentType);
    assertTrue(metadata.getContentLength().isPresent());
    assertEquals(metadata.getContentLength().getAsLong(), 7);
    assertEquals(metadata.getStartTime(), InMemoryTransformationManagerTest.TIME2);
    assertThat(
        metadata.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        transformation.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        metadata.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME2, InMemoryTransformationManagerTest.TIME3)));
    assertThat(
        transformation.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME, InMemoryTransformationManagerTest.TIME3)));
  }

  @Test
  public void succeedMetadataStoresContentsWithReader() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);

    MetadataTransformation metadata =
        manager.get(transformation.getTransformId(), TEST_METADATA_TYPE);
    String metadataContent = "testing";
    String metadataContentType = "text/plain";
    metadata.succeed(
        metadataContentType, Charset.defaultCharset(), new StringReader(metadataContent));

    assertTrue(metadata.getContent().isPresent());
    String actualMetadataContent = null;
    try (final Reader reader = metadata.getContent(Charset.defaultCharset()).get()) {
      actualMetadataContent = CharStreams.toString(reader);
    }
    assertEquals(actualMetadataContent, metadataContent);
    assertTrue(metadata.wasSuccessful());
    assertTrue(metadata.getContentType().isPresent());
    assertEquals(metadata.getContentType().get(), metadataContentType);
    assertTrue(metadata.getContentLength().isPresent());
    assertEquals(metadata.getContentLength().getAsLong(), 7);
    assertEquals(metadata.getStartTime(), InMemoryTransformationManagerTest.TIME2);
    assertThat(
        metadata.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        transformation.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        metadata.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME2, InMemoryTransformationManagerTest.TIME3)));
    assertThat(
        transformation.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME, InMemoryTransformationManagerTest.TIME3)));
  }

  @Test
  public void succeedMetadataDoesNothingIfStreamFailedToClose() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);

    MetadataTransformation metadata =
        manager.get(transformation.getTransformId(), TEST_METADATA_TYPE);
    String metadataContent = "testing";
    String metadataContentType = "text/plain";
    metadata.succeed(
        metadataContentType,
        new ByteArrayInputStream(metadataContent.getBytes()) {
          @Override
          public void close() throws IOException {
            super.close();
            throw new IOException("testing");
          }
        });

    assertTrue(metadata.getContent().isPresent());
    String actualMetadataContent = null;
    try (final Reader reader = new InputStreamReader(metadata.getContent().get())) {
      actualMetadataContent = CharStreams.toString(reader);
    }
    assertEquals(actualMetadataContent, metadataContent);
    assertTrue(metadata.wasSuccessful());
    assertThat(metadata.getContentType(), OptionalMatchers.isPresentAndIs(metadataContentType));
    assertThat(
        metadata.getContentLength().stream().boxed().findFirst(),
        OptionalMatchers.isPresentAndIs(7L));
    assertThat(metadata.getStartTime(), Matchers.equalTo(InMemoryTransformationManagerTest.TIME2));
    assertThat(
        metadata.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        transformation.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(InMemoryTransformationManagerTest.TIME3));
    assertThat(
        metadata.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME2, InMemoryTransformationManagerTest.TIME3)));
    assertThat(
        transformation.getDuration(),
        Matchers.equalTo(
            Duration.between(
                InMemoryTransformationManagerTest.TIME, InMemoryTransformationManagerTest.TIME3)));
  }

  @Test(expected = IllegalStateException.class)
  public void testMetadataContentIsNoLongerAvailableWhenDeleted() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    metadata.succeed("text/plain", Charset.defaultCharset(), new StringReader("done"));
    transformation.delete();

    metadata.getContent();
  }

  @Test
  public void failMetadataCapturesState() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);

    MetadataTransformation metadata = transformation.metadatas().findFirst().get();
    String message = "it failed";
    metadata.fail(ErrorCode.TRANSFORMATION_FAILURE, message);

    assertTrue(metadata.hasFailed());
    assertTrue(metadata.getFailureReason().isPresent());
    assertEquals(metadata.getFailureReason().get(), ErrorCode.TRANSFORMATION_FAILURE);
    assertTrue(metadata.getFailureMessage().isPresent());
    assertEquals(metadata.getFailureMessage().get(), message);
    assertTrue(metadata.getCompletionTime().isPresent());
    assertFalse(metadata.getContent().isPresent());
    assertFalse(metadata.getContentType().isPresent());
    assertFalse(metadata.getContentLength().isPresent());
  }

  @Test(expected = IllegalStateException.class)
  public void failMetadataTwiceThrowsException() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);
    MetadataTransformation metadata = transformation.metadatas().findFirst().get();
    metadata.fail(ErrorCode.TRANSFORMATION_FAILURE, "it failed");
    metadata.fail(ErrorCode.TRANSFORMATION_FAILURE, "it failed");
  }

  @Test(expected = IllegalStateException.class)
  public void succeedMetadataTwiceThrowsException() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);
    MetadataTransformation metadata = transformation.metadatas().findFirst().get();
    String metadataContent = "testing";
    String metadataContentType = "text/plain";
    metadata.succeed(metadataContentType, new ByteArrayInputStream(metadataContent.getBytes()));
    metadata.succeed(metadataContentType, new ByteArrayInputStream(metadataContent.getBytes()));
  }

  @Test
  public void deleteIsSuccessful() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);

    manager.delete(transformation.getTransformId());
    try {
      manager.get(transformation.getTransformId());
      fail();
    } catch (TransformationNotFoundException e) {
      assertTrue(e.getMessage().contains(transformation.getTransformId()));
    }
  }

  @Test(expected = TransformationNotFoundException.class)
  public void deleteInvalidIdThrowsException() throws Exception {
    manager.delete("gibberish-ID");
  }

  @Test
  public void testTransformationDelete() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    transformation.delete();

    Assert.assertThat(transformation.isDeleted(), Matchers.equalTo(true));
    Assert.assertThat(metadata.isDeleted(), Matchers.equalTo(true));
  }

  @Test
  public void testTransformationDeleteFromManager() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    manager.delete(transformation.getTransformId());

    Assert.assertThat(transformation.isDeleted(), Matchers.equalTo(true));
    Assert.assertThat(metadata.isDeleted(), Matchers.equalTo(true));
  }

  @Test(expected = TransformationNotFoundException.class)
  public void testTransformationDeleteIsNoLongerAccessibleFromManager() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);

    transformation.delete();

    manager.get(transformation.getTransformId());
  }

  @Test
  public void testTransformationDeletedTwiceHasNoEffect() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    transformation.delete();

    transformation.delete();

    Assert.assertThat(transformation.isDeleted(), Matchers.equalTo(true));
    Assert.assertThat(metadata.isDeleted(), Matchers.equalTo(true));
  }

  @Test(expected = TransformationNotFoundException.class)
  public void testTransformationDeletedTwiceFromManagerFailsTheSecondTime() throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    manager.delete(transformation.getTransformId());

    manager.delete(transformation.getTransformId());
  }

  @Test
  public void testTransformationDeleteFromManagerThenDeletedFromObjectHasNoEffect()
      throws Exception {
    final Transformation transformation =
        manager.createTransform(currentUri, finalUri, metacardUri);
    final MetadataTransformation metadata = transformation.add(TEST_METADATA_TYPE);

    manager.delete(transformation.getTransformId());

    transformation.delete();

    Assert.assertThat(transformation.isDeleted(), Matchers.equalTo(true));
    Assert.assertThat(metadata.isDeleted(), Matchers.equalTo(true));
  }
}
