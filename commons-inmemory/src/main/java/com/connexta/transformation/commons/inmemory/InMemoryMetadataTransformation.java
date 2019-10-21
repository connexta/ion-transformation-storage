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

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.MetadataTransformation;
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.impl.AbstractMetadataImpl;
import com.connexta.transformation.pojo.MetadataPojo;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link MetadataTransformation} that stores all of the data in memory. The
 * contents of the metadata will be stored as an array of bytes. All of the state that would change
 * during the lifecycle of this object is threadsafe and kept in sync.
 */
public class InMemoryMetadataTransformation extends AbstractMetadataImpl {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(InMemoryMetadataTransformation.class);
  private final InMemoryTransformation transformation;
  private final Object stateLock = new Object();
  private volatile byte[] content;

  /**
   * Sets the state to "In progress" and initializes the start time.
   *
   * @param transformation the associated transformation
   * @param metadataType the type of metadata this structure will hold
   * @param transformId the id of the transform request
   * @param requestInfo the corresponding {@link RequestInfo} object
   */
  public InMemoryMetadataTransformation(
      InMemoryTransformation transformation,
      String metadataType,
      String transformId,
      RequestInfo requestInfo) {
    super(metadataType, transformId, requestInfo, transformation.getClock());
    this.transformation = transformation;
  }

  /**
   * Instantiates an in-memory metadata based on the information provided by the specified pojo.
   *
   * @param pojo the pojo to initializes the metadata with
   * @param transformation the associated transformation
   * @throws com.connexta.transformation.commons.api.exceptions.InvalidFieldException if an error
   *     occurs while trying to deserialize the pojo object
   * @throws PersistenceException if an error occurs while deserializing the pojo object
   */
  public InMemoryMetadataTransformation(MetadataPojo pojo, InMemoryTransformation transformation)
      throws PersistenceException {
    super(pojo, transformation.getClock());
    this.transformation = transformation;
  }

  @Override
  public boolean isDeleted() {
    return transformation.isDeleted();
  }

  @Override
  public Optional<InputStream> getContent() {
    checkForDeletion();
    final byte[] c = content;

    if (c == null) {
      return Optional.empty();
    } else {
      return Optional.of(new ByteArrayInputStream(content));
    }
  }

  @Override
  public void succeed(String contentType, InputStream contentStream) throws IOException {
    checkForDeletion();
    final Instant now = Instant.ofEpochMilli(clock.wallTime());

    synchronized (stateLock) {
      checkForCompletion();
      try {
        this.content = ByteStreams.toByteArray(contentStream);
      } finally {
        try {
          contentStream.close();
        } catch (IOException e) {
          LOGGER.debug(
              "Unable to close contents stream of [{}] metadata for transformation [{}].",
              getMetadataType(),
              getTransformId());
        }
      }
      super.contentLength = content.length;
      super.contentType = contentType;
      super.completionTime = now;
      super.state = State.SUCCESSFUL;
    }
  }

  @Override
  public void fail(ErrorCode reason, String message) {
    checkForDeletion();
    final Instant now = Instant.ofEpochMilli(clock.wallTime());

    synchronized (stateLock) {
      checkForCompletion();
      super.failureReason = reason;
      super.failureMessage = message;
      super.completionTime = now;
      super.state = State.FAILED;
    }
  }

  private void checkForCompletion() {
    if (isCompleted()) {
      throw new IllegalStateException(
          "["
              + getMetadataType()
              + "] metadata for transformation ["
              + getTransformId()
              + "] is already completed.");
    }
  }

  private void checkForDeletion() {
    if (isDeleted()) {
      throw new IllegalStateException(
          "["
              + getMetadataType()
              + "] metadata for transformation ["
              + getTransformId()
              + "] was deleted.");
    }
  }
}
