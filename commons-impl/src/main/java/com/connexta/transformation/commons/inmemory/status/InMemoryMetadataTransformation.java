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
package com.connexta.transformation.commons.inmemory.status;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.TransformationException;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryMetadataTransformation implements MetadataTransformation {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(InMemoryMetadataTransformation.class);
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

  public InMemoryMetadataTransformation(
      String metadataType, String transformId, RequestInfo requestInfo) {
    this.startTime = Instant.now();
    this.state = State.IN_PROGRESS;
    this.metadataType = metadataType;
    this.transformId = transformId;
    this.requestInfo = requestInfo;
  }

  @Override
  public String getMetadataType() {
    return metadataType;
  }

  @Override
  public Optional<InputStream> getContent() {
    if (content == null) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(ByteSource.wrap(content).openStream());
      } catch (IOException e) {
        LOGGER.error(
            "Unable to read contents of [{}] metadata for transformation [{}].",
            metadataType,
            transformId);
        return Optional.empty();
      }
    }
  }

  @Override
  public Optional<String> getContentType() {
    return Optional.ofNullable(contentType);
  }

  @Override
  public OptionalLong getContentLength() {
    if (content == null) {
      return OptionalLong.empty();
    } else {
      return OptionalLong.of(content.length);
    }
  }

  @Override
  public void succeed(String contentType, InputStream contentStream)
      throws TransformationException, IOException, IllegalStateException {
    Instant completionTime = Instant.now();
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
    }
    this.contentType = contentType;
    this.completionTime = completionTime;
  }

  @Override
  public void fail(ErrorCode reason, String message)
      throws TransformationException, IllegalStateException {
    Instant completionTime = Instant.now();
    synchronized (stateLock) {
      checkForCompletion();
      state = State.FAILED;
    }
    failureReason = reason;
    failureMessage = message;
    this.completionTime = completionTime;
  }

  @Override
  public Optional<ErrorCode> getFailureReason() {
    return Optional.ofNullable(failureReason);
  }

  @Override
  public Optional<String> getFailureMessage() {
    return Optional.ofNullable(failureMessage);
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
    return Optional.ofNullable(completionTime);
  }

  @Override
  public Duration getDuration() {
    return Duration.between(startTime, getCompletionTime().orElse(Instant.now()));
  }

  @Override
  public State getState() {
    return state;
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
}
