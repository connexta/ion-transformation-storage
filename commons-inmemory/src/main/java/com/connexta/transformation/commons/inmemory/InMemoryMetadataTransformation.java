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
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link MetadataTransformation} that stores all of the data in memory. The
 * contents of the metadata will be stored as an array of bytes. All of the state that would change
 * during the lifecycle of this object is threadsafe and kept in sync.
 */
public class InMemoryMetadataTransformation implements MetadataTransformation {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(InMemoryMetadataTransformation.class);
  private final InMemoryTransformation transformation;
  private final Object stateLock = new Object();
  private final String metadataType;
  private final String transformId;
  private final RequestInfo requestInfo;
  private final Instant startTime;
  private Instant completionTime;
  private byte[] content;
  private String contentType;
  private State state;
  private ErrorCode failureReason;
  private String failureMessage;

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
    this.transformation = transformation;
    this.startTime = Instant.now();
    this.state = State.IN_PROGRESS;
    this.metadataType = metadataType;
    this.transformId = transformId;
    this.requestInfo = requestInfo;
  }

  @Override
  public boolean isDeleted() {
    return transformation.isDeleted();
  }

  @Override
  public String getMetadataType() {
    return metadataType;
  }

  @Override
  public Optional<InputStream> getContent() {
    checkForDeletion();
    synchronized (stateLock) {
      if (content == null) {
        return Optional.empty();
      } else {
        return Optional.of(new ByteArrayInputStream(content));
      }
    }
  }

  @Override
  public Optional<String> getContentType() {
    synchronized (stateLock) {
      return Optional.ofNullable(contentType);
    }
  }

  @Override
  public OptionalLong getContentLength() {
    synchronized (stateLock) {
      if (content == null) {
        return OptionalLong.empty();
      } else {
        return OptionalLong.of(content.length);
      }
    }
  }

  @Override
  public void succeed(String contentType, InputStream contentStream) throws IOException {
    checkForDeletion();
    Instant now = Instant.now();

    synchronized (stateLock) {
      checkForCompletion();
      try {
        content = ByteStreams.toByteArray(contentStream);
      } finally {
        try {
          contentStream.close();
        } catch (IOException e) {
          LOGGER.debug(
              "Unable to close contents stream of [{}] metadata for transformation [{}].",
              metadataType,
              transformId);
        }
      }
      this.state = State.SUCCESSFUL;
      this.contentType = contentType;
      this.completionTime = now;
    }
  }

  @Override
  public void fail(ErrorCode reason, String message) {
    checkForDeletion();
    Instant now = Instant.now();

    synchronized (stateLock) {
      checkForCompletion();
      state = State.FAILED;
      failureReason = reason;
      failureMessage = message;
      this.completionTime = now;
    }
  }

  @Override
  public Optional<ErrorCode> getFailureReason() {
    synchronized (stateLock) {
      return Optional.ofNullable(failureReason);
    }
  }

  @Override
  public Optional<String> getFailureMessage() {
    synchronized (stateLock) {
      return Optional.ofNullable(failureMessage);
    }
  }

  @Override
  public String getTransformId() {
    return transformId;
  }

  @Override
  public RequestInfo getRequestInfo() {
    return requestInfo;
  }

  @Override
  public Instant getStartTime() {
    return startTime;
  }

  @Override
  public Optional<Instant> getCompletionTime() {
    synchronized (stateLock) {
      return Optional.ofNullable(completionTime);
    }
  }

  @Override
  public State getState() {
    synchronized (stateLock) {
      return state;
    }
  }

  private void checkForCompletion() {
    if (isCompleted()) {
      throw new IllegalStateException(
          "["
              + metadataType
              + "] metadata for transformation ["
              + transformId
              + "] is already completed.");
    }
  }

  private void checkForDeletion() {
    if (isDeleted()) {
      throw new IllegalStateException(
          "[" + metadataType + "] metadata for transformation [" + transformId + "] was deleted.");
    }
  }
}
