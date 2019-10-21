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
import com.connexta.transformation.commons.api.MetadataTransformation;
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.InvalidFieldException;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.MetadataPojo;
import com.connexta.transformation.pojo.TransformationPojo;
import com.connexta.transformation.pojo.unknown.UnknownPojo;
import com.google.common.annotations.VisibleForTesting;
import io.micrometer.core.instrument.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import javax.annotation.Nullable;

/**
 * Provides an abstraction implementation for the {@link MetadataTransformation} interface which
 * adds persistence support to be used by all concrete implementations.
 */
public abstract class AbstractMetadataImpl extends Persistable<MetadataPojo>
    implements MetadataTransformation {
  private static final String PERSISTABLE_TYPE = "metadata";

  protected final Clock clock;

  private String type;
  private String transformId;
  private RequestInfo requestInfo;
  private Instant startTime;

  protected volatile State state = State.IN_PROGRESS;
  protected volatile ErrorCode failureReason;
  protected volatile String failureMessage;
  @Nullable protected volatile Instant completionTime = null;
  @Nullable protected volatile String contentType = null;
  protected volatile long contentLength = -1;

  private boolean hasUnknowns = false;

  /**
   * Instantiates a new metadata with the specified information.
   *
   * @param type the type of metadata this structure will hold
   * @param transformId the id of the transform request
   * @param requestInfo the corresponding {@link RequestInfo} object
   * @param clock the clock to use for retrieving wall and monotonic times
   */
  public AbstractMetadataImpl(
      String type, String transformId, RequestInfo requestInfo, Clock clock) {
    super(AbstractMetadataImpl.PERSISTABLE_TYPE);
    this.clock = clock;
    this.type = type;
    this.transformId = transformId;
    this.requestInfo = requestInfo;
    this.startTime = Instant.ofEpochMilli(clock.wallTime());
  }

  /**
   * Instantiates a metadata based on the information provided by the specified pojo.
   *
   * @param pojo the pojo to initializes the metadata with
   * @param clock the clock to use for retrieving wall and monotonic times
   * @throws InvalidFieldException if an error occurs while trying to deserialize the pojo object
   * @throws PersistenceException if an error occurs while deserializing the pojo object
   */
  public AbstractMetadataImpl(MetadataPojo pojo, Clock clock) throws PersistenceException {
    this(clock);
    readFrom(pojo);
  }

  @VisibleForTesting
  AbstractMetadataImpl(Clock clock) {
    super(AbstractMetadataImpl.PERSISTABLE_TYPE, null);
    this.clock = clock;
  }

  @Override
  public String getMetadataType() {
    return type;
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
  public State getState() {
    return state;
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
  public Instant getStartTime() {
    return startTime;
  }

  @Override
  public Optional<Instant> getCompletionTime() {
    return Optional.ofNullable(completionTime);
  }

  @Override
  public Duration getDuration() {
    final Instant time = completionTime;

    return Duration.between(
        startTime, (time != null) ? time : Instant.ofEpochMilli(clock.wallTime()));
  }

  @Override
  public Optional<String> getContentType() {
    return Optional.ofNullable(contentType);
  }

  @Override
  public OptionalLong getContentLength() {
    return (contentLength > -1L) ? OptionalLong.of(contentLength) : OptionalLong.empty();
  }

  @Override
  public boolean hasUnknowns() {
    return hasUnknowns || (state == State.UNKNOWN) || (failureReason == ErrorCode.UNKNOWN);
  }

  /**
   * Gets the clock to use for retrieving wall and monotonic times.
   *
   * @return the clock to use for retrieving wall and monotonic times
   */
  public Clock getClock() {
    return clock;
  }

  @Override
  public int hashCode() {
    return hashCode0();
  }

  @Override
  public boolean equals(Object obj) {
    return equals0(obj);
  }

  @Override
  public String toString() {
    return String.format(
        "AbstractMetadataImpl[id=%s, transformId=%s, type=%s, requestInfo=%s, state=%s, startTime=%s, completionTime=%s, contentType=%s, contentLength=%d, failureReason=%s, failureMessage=%s]",
        getId(),
        transformId,
        type,
        requestInfo,
        state,
        startTime,
        completionTime,
        contentType,
        contentLength,
        failureReason,
        failureMessage);
  }

  @Override
  protected MetadataPojo writeTo(MetadataPojo pojo) throws PersistenceException {
    if (hasUnknowns()) { // cannot serialize if it contains unknowns
      throw new InvalidFieldException("unknown metadata");
    }
    super.writeTo(pojo);
    setOrFailIfNullOrEmpty("transformId", this::getTransformId, pojo::setTransformId);
    setOrFailIfNullOrEmpty("type", this::getMetadataType, pojo::setType);
    convertAndSetOrFailIfNull(
        "requestInfo", this::getRequestInfo, RequestInfoImpl::toPojo, pojo::setRequestInfo);
    setOrFailIfNull("startTime", this::getStartTime, pojo::setStartTime);
    convertAndSetOrFailIfNull("state", this::getState, State::name, pojo::setState);
    convertAndSet(
        "failureReason", this::getFailureReason0, ErrorCode::name, pojo::setFailureReason);
    return pojo.setVersion(TransformationPojo.CURRENT_VERSION)
        .setCompletionTime(completionTime)
        .setFailureMessage(failureMessage)
        .setContentType(contentType)
        .setContentLength(Math.max(-1L, contentLength));
  }

  @Override
  protected final void readFrom(MetadataPojo pojo) throws PersistenceException {
    super.readFrom(pojo);
    if (pojo.getVersion() < MetadataPojo.MINIMUM_VERSION) {
      throw new UnsupportedVersionException(
          "unsupported "
              + AbstractMetadataImpl.PERSISTABLE_TYPE
              + " version: "
              + pojo.getVersion()
              + " for object: "
              + getId());
    } // do support pojo.getVersion() > CURRENT_VERSION for forward compatibility
    this.hasUnknowns = pojo instanceof UnknownPojo; // reset the unknown flag
    readFromCurrentOrFutureVersion(pojo);
  }

  @VisibleForTesting
  int hashCode0() {
    return Objects.hash(
        super.hashCode(),
        type,
        transformId,
        state,
        startTime,
        completionTime,
        failureReason,
        failureMessage,
        requestInfo,
        contentType,
        contentLength);
  }

  @VisibleForTesting
  boolean equals0(Object obj) {
    if (super.equals(obj) && (obj instanceof AbstractMetadataImpl)) {
      final AbstractMetadataImpl metadata = (AbstractMetadataImpl) obj;

      return (failureReason == metadata.failureReason)
          && (contentLength == metadata.contentLength)
          && Objects.equals(transformId, metadata.transformId)
          && Objects.equals(type, metadata.type)
          && Objects.equals(requestInfo, metadata.requestInfo)
          && Objects.equals(state, metadata.state)
          && Objects.equals(startTime, metadata.startTime)
          && Objects.equals(failureMessage, metadata.failureMessage)
          && Objects.equals(completionTime, metadata.completionTime)
          && Objects.equals(contentType, metadata.contentType);
    }
    return false;
  }

  @VisibleForTesting
  @Nullable
  ErrorCode getFailureReason0() {
    return failureReason;
  }

  @VisibleForTesting
  void setTransformId(String transformId) {
    this.transformId = transformId;
  }

  @VisibleForTesting
  void setType(String type) {
    this.type = type;
  }

  @VisibleForTesting
  void setRequestInfo(RequestInfoImpl requestInfo) {
    this.requestInfo = requestInfo;
  }

  @VisibleForTesting
  void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  @VisibleForTesting
  void setState(State state) {
    this.state = state;
  }

  @VisibleForTesting
  void setFailureReason(ErrorCode reason) {
    this.failureReason = reason;
  }

  @VisibleForTesting
  void setFailureMessage(String msg) {
    this.failureMessage = msg;
  }

  @VisibleForTesting
  void setCompletionTime(@Nullable Instant completionTime) {
    this.completionTime = completionTime;
  }

  @VisibleForTesting
  void setContentType(@Nullable String contentType) {
    this.contentType = contentType;
  }

  @VisibleForTesting
  void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  private void readFromCurrentOrFutureVersion(MetadataPojo pojo) throws PersistenceException {
    setOrFailIfNullOrEmpty("transformId", pojo::getTransformId, this::setTransformId);
    setOrFailIfNullOrEmpty("type", pojo::getType, this::setType);
    convertAndSetOrFailIfNull(
        "requestInfo",
        pojo::getRequestInfo,
        RequestInfoImpl::new,
        this::setRequestInfoAndCheckForUnknown);
    setOrFailIfNull("startTime", pojo::getStartTime, this::setStartTime);
    convertAndSetEnumValueOrFailIfNullOrEmpty(
        "state", State.class, State.UNKNOWN, pojo::getState, this::setState);
    convertAndSetEnumValue(
        ErrorCode.class, null, ErrorCode.UNKNOWN, pojo::getFailureReason, this::setFailureReason);
    setFailureMessage(pojo.getFailureMessage());
    setCompletionTime(pojo.getCompletionTime());
    setContentType(pojo.getContentType());
    setContentLength(Math.max(-1L, pojo.getContentLength()));
  }

  private void setRequestInfoAndCheckForUnknown(RequestInfoImpl requestInfo) {
    this.requestInfo = requestInfo;
    this.hasUnknowns |= requestInfo.hasUnknowns();
  }

  /**
   * Converts the specified metadata into a metadata pojo.
   *
   * @param metadata the metadata to convert
   * @return the corresponding pojo
   * @throws InvalidFieldException if an error occurs while trying to serialize the metadata
   * @throws PersistenceException if an error occurs while serializing the metadata
   */
  public static MetadataPojo toPojo(AbstractMetadataImpl metadata) throws PersistenceException {
    return metadata.writeTo(new MetadataPojo());
  }
}
