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

import static com.connexta.transformation.util.ExceptionHandler.unwrap;
import static com.connexta.transformation.util.ExceptionHandler.wrap;

import com.connexta.transformation.commons.api.MetadataTransformation;
import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.Transformation;
import com.connexta.transformation.commons.api.exceptions.InvalidFieldException;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.MetadataPojo;
import com.connexta.transformation.pojo.TransformationPojo;
import com.connexta.transformation.pojo.unknown.UnknownPojo;
import com.connexta.transformation.util.ExceptionHandler;
import com.google.common.annotations.VisibleForTesting;
import io.micrometer.core.instrument.Clock;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides an abstraction implementation for the {@link Transformation} interface which adds
 * persistence support to be used by all concrete implementations.
 */
public abstract class AbstractTransformationImpl extends Persistable<TransformationPojo>
    implements Transformation {
  private static final String PERSISTABLE_TYPE = "transformation";

  protected final Clock clock;

  private RequestInfo requestInfo;
  private Instant startTime;

  private boolean hasUnknowns = false;

  protected Map<String, AbstractMetadataImpl> metadatas = new ConcurrentHashMap<>();

  /**
   * Instantiates a new transformation with the specified information.
   *
   * @param currentLocation the location to retrieve the file
   * @param finalLocation the downloadable location to put on the transformed metadata
   * @param metacardLocation the location of the metacard XML for the file
   * @param clock the clock to use for retrieving wall and monotonic times
   */
  public AbstractTransformationImpl(
      URL currentLocation, URL finalLocation, URL metacardLocation, Clock clock) {
    super(AbstractTransformationImpl.PERSISTABLE_TYPE);
    this.clock = clock;
    this.requestInfo = new RequestInfoImpl(currentLocation, finalLocation, metacardLocation);
    this.startTime = Instant.ofEpochMilli(clock.wallTime());
  }

  /**
   * Instantiates a transformation based on the information provided by the specified pojo.
   *
   * @param pojo the pojo to initializes the transformation with
   * @param clock the clock to use for retrieving wall and monotonic times
   * @throws InvalidFieldException if an error occurs while trying to deserialize the pojo object
   * @throws PersistenceException if an error occurs while deserializing the pojo object
   */
  public AbstractTransformationImpl(TransformationPojo pojo, Clock clock)
      throws PersistenceException {
    this(clock);
    readFrom(pojo);
  }

  @VisibleForTesting
  AbstractTransformationImpl(RequestInfo requestInfo, Instant startTime, Clock clock) {
    this(clock);
    this.requestInfo = requestInfo;
    this.startTime = startTime;
  }

  @VisibleForTesting
  AbstractTransformationImpl(Clock clock) {
    super(AbstractTransformationImpl.PERSISTABLE_TYPE, null);
    this.clock = clock;
  }

  @Override
  public String getTransformId() {
    return getId();
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
  public Duration getDuration() {
    return Duration.between(
        startTime, getCompletionTime().orElseGet(() -> Instant.ofEpochMilli(clock.wallTime())));
  }

  @Override
  public boolean hasUnknowns() {
    return hasUnknowns;
  }

  @Override
  public Stream<MetadataTransformation> metadatas() {
    return metadatas.values().stream().map(MetadataTransformation.class::cast);
  }

  @Override
  public MetadataTransformation get(String type) throws TransformationNotFoundException {
    final MetadataTransformation metadata = metadatas.get(type);

    if (metadata == null) {
      throw new TransformationNotFoundException(
          "No [" + type + "] metadata found for transformation [" + getId() + "]");
    }
    return metadata;
  }

  /**
   * Gets the clock to use for retrieving wall and monotonic times.
   *
   * @return the clock to use for retrieving wall and monotonic times
   */
  public Clock getClock() {
    return clock;
  }

  /**
   * Adds the given set of metadata to the set contained in this transformation.
   *
   * @param metadatas the set of metadata to combine with the contained set
   */
  public void add(Set<? super AbstractMetadataImpl> metadatas) {
    add(metadatas.stream());
  }

  /**
   * Adds the given metadata to the set contained in this transformation.
   *
   * @param metadatas the set of metadata to add to the contained set
   */
  public void add(AbstractMetadataImpl... metadatas) {
    add(Stream.of(metadatas));
  }

  /**
   * Adds the given metadata to the set contained in this transformation.
   *
   * @param metadatas the stream of metadata to add to the contained set
   */
  public void add(Stream<? super AbstractMetadataImpl> metadatas) {
    metadatas
        .map(AbstractMetadataImpl.class::cast)
        .forEach(m -> this.metadatas.put(m.getMetadataType(), m));
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
        "AbstractTransformationImpl[id=%s, requestInfo=%s, startTime=%s, metadatas=%s]",
        getId(), requestInfo, startTime, metadatas);
  }

  @Override
  protected TransformationPojo writeTo(TransformationPojo pojo) throws PersistenceException {
    if (hasUnknowns()) { // cannot serialize if it contains unknowns
      throw new InvalidFieldException("unknown transformation");
    }
    super.writeTo(pojo);
    convertAndSetOrFailIfNull(
        "requestInfo", this::getRequestInfo, RequestInfoImpl::toPojo, pojo::setRequestInfo);
    setOrFailIfNull("startTime", this::getStartTime, pojo::setStartTime);
    return pojo.setVersion(TransformationPojo.CURRENT_VERSION)
        .setMetadatas(
            unwrap(() -> metadatas.values().stream().map(wrap(AbstractMetadataImpl::toPojo))));
  }

  @Override
  protected final void readFrom(TransformationPojo pojo) throws PersistenceException {
    super.readFrom(pojo);
    if (pojo.getVersion() < TransformationPojo.MINIMUM_VERSION) {
      throw new UnsupportedVersionException(
          "unsupported "
              + AbstractTransformationImpl.PERSISTABLE_TYPE
              + " version: "
              + pojo.getVersion()
              + " for object: "
              + getId());
    } // do support pojo.getVersion() > CURRENT_VERSION for forward compatibility
    this.hasUnknowns = pojo instanceof UnknownPojo; // reset the unknown flag
    readFromCurrentOrFutureVersion(pojo);
  }

  /**
   * Converts the specified metadata pojo into a metadata object.
   *
   * @param pojo the pojo to be converted
   * @return the corresponding metadata object
   * @throws InvalidFieldException if an error occurs while trying to deserialize the pojo object
   * @throws PersistenceException if an error occurs while deserializing the pojo object
   */
  protected abstract AbstractMetadataImpl fromPojo(MetadataPojo pojo) throws PersistenceException;

  @VisibleForTesting
  void setRequestInfo(RequestInfoImpl requestInfo) {
    this.requestInfo = requestInfo;
  }

  @VisibleForTesting
  void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  @VisibleForTesting
  int hashCode0() {
    return Objects.hash(super.hashCode(), startTime, requestInfo, metadatas);
  }

  @VisibleForTesting
  boolean equals0(Object obj) {
    if (super.equals(obj) && (obj instanceof AbstractTransformationImpl)) {
      final AbstractTransformationImpl transformation = (AbstractTransformationImpl) obj;

      return Objects.equals(startTime, transformation.startTime)
          && Objects.equals(requestInfo, transformation.requestInfo)
          && Objects.equals(metadatas, transformation.metadatas);
    }
    return false;
  }

  private void readFromCurrentOrFutureVersion(TransformationPojo pojo) throws PersistenceException {
    convertAndSetOrFailIfNull(
        "requestInfo",
        pojo::getRequestInfo,
        RequestInfoImpl::new,
        this::setRequestInfoAndCheckForUnknown);
    setOrFailIfNull("startTime", pojo::getStartTime, this::setStartTime);
    setMetadatasAndCheckForUnknowns(
        ExceptionHandler.unwrap(() -> pojo.metadatas().map(ExceptionHandler.wrap(this::fromPojo))));
  }

  private void setRequestInfoAndCheckForUnknown(RequestInfoImpl requestInfo) {
    this.requestInfo = requestInfo;
    this.hasUnknowns |= requestInfo.hasUnknowns();
  }

  @SuppressWarnings("squid:S3864" /* peek() designed to 'or' all unknown metadatas found */)
  private void setMetadatasAndCheckForUnknowns(Stream<? super AbstractMetadataImpl> metadatas) {
    this.metadatas =
        metadatas
            .map(AbstractMetadataImpl.class::cast)
            .peek(this::checkForUnknowns)
            .collect(
                Collectors.toConcurrentMap(
                    AbstractMetadataImpl::getMetadataType, Function.identity()));
  }

  private void checkForUnknowns(AbstractMetadataImpl metadata) {
    this.hasUnknowns |= metadata.hasUnknowns();
  }
}
