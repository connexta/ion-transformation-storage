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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.connexta.transformation.commons.api.status.Transformation;
import com.connexta.transformation.commons.api.status.TransformationStatus.State;
import com.google.common.io.CharStreams;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;

public class InMemoryTransformationManagerTest {

  public static final String TEST_METADATA_TYPE = "myMetadataType";
  private InMemoryTransformationManager manager = new InMemoryTransformationManager();
  private URI currentUri;
  private URI finalUri;
  private URI metacardUri;

  @Before
  public void setup() throws Exception {
    currentUri = new URI("http://current.com");
    finalUri = new URI("http://final.com");
    metacardUri = new URI("http://metacard.com");
  }

  @Test
  public void createTransformInitializesProperly() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);

    assertEquals(transformation.getRequestInfo().getCurrentLocation(), currentUri);
    assertEquals(transformation.getRequestInfo().getFinalLocation(), finalUri);
    assertEquals(transformation.getRequestInfo().getMetacardLocation(), metacardUri);
    assertNotNull(transformation.getTransformId());
    assertTrue(transformation.getStartTime().isBefore(Instant.now().plus(Duration.ofSeconds(1))));
    assertFalse(transformation.getCompletionTime().isPresent());
    Duration firstDuration = transformation.getDuration();
    assertNotNull(firstDuration);
    await()
        .atMost(1, SECONDS)
        .until(() -> firstDuration.compareTo(transformation.getDuration()) < 0);
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
    assertTrue(metadata.getStartTime().isBefore(Instant.now().plus(Duration.ofSeconds(1))));
    assertEquals(metadata.getState(), State.IN_PROGRESS);
    assertEquals(metadata.getMetadataType(), TEST_METADATA_TYPE);
    assertEquals(metadata.getTransformId(), transformation.getTransformId());
    assertEquals(metadata.getRequestInfo(), transformation.getRequestInfo());
    assertFalse(metadata.getCompletionTime().isPresent());
    assertFalse(metadata.getContent().isPresent());
    assertFalse(metadata.getContentType().isPresent());
    assertFalse(metadata.getFailureReason().isPresent());
    assertFalse(metadata.getFailureMessage().isPresent());
    Duration firstDuration = metadata.getDuration();
    assertNotNull(firstDuration);
    await().atMost(1, SECONDS).until(() -> firstDuration.compareTo(metadata.getDuration()) < 0);
  }

  @Test(expected = IllegalStateException.class)
  public void addDuplicateMetadataTypeThrowsException() throws Exception {
    Transformation transformation = manager.createTransform(currentUri, finalUri, metacardUri);
    transformation.add(TEST_METADATA_TYPE);
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
    assertTrue(metadata.getCompletionTime().isPresent());
    assertTrue(transformation.getCompletionTime().isPresent());
    assertEquals(transformation.getCompletionTime().get(), metadata.getCompletionTime().get());
    assertEquals(
        Duration.between(transformation.getStartTime(), transformation.getCompletionTime().get()),
        transformation.getDuration());
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
}
