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

import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.connexta.transformation.commons.api.status.Transformation;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InMemoryTransformation implements Transformation {

  private final InMemoryTransformationManager manager;
  private final Map<String, MetadataTransformation> metadataMap;
  private final RequestInfo requestInfo;
  private final String transformId;
  private final Instant startTime;
  private volatile boolean deleted;

  /**
   * Generates a startTime, a Transform ID, and the related {@link RequestInfoImpl}.
   *
   * @param manager the associated manager
   * @param currentLocation the location to retrieve the file
   * @param finalLocation the downloadable location to put on the transformed metadata
   * @param metacardLocation the location of the metacard XML for the file
   */
  public InMemoryTransformation(
      InMemoryTransformationManager manager,
      URI currentLocation,
      URI finalLocation,
      URI metacardLocation) {
    this.manager = manager;
    startTime = Instant.now();
    metadataMap = new ConcurrentHashMap<>();
    requestInfo = new RequestInfoImpl(currentLocation, finalLocation, metacardLocation);
    transformId = UUID.randomUUID().toString();
    this.deleted = false;
  }

  @Override
  public void delete() {
    if (!deleted) {
      try {
        manager.delete(transformId);
      } catch (TransformationNotFoundException e) { // ignore as it shouldn't happen
      }
    }
  }

  @Override
  public MetadataTransformation add(String metadataType) {
    if (deleted) {
      throw new IllegalStateException("transformation [" + transformId + "] was deleted.");
    } else if (isCompleted()) {
      throw new IllegalStateException("transformation [" + transformId + "] is already complete.");
    }
    return metadataMap.computeIfAbsent(
        metadataType,
        t -> new InMemoryMetadataTransformation(this, metadataType, transformId, requestInfo));
  }

  @Override
  public Stream<MetadataTransformation> metadatas() {
    return metadataMap.values().stream();
  }

  @Override
  public MetadataTransformation getMetadata(String metadataType)
      throws TransformationNotFoundException {
    final MetadataTransformation metadata = metadataMap.get(metadataType);

    if (metadata == null) {
      throw new TransformationNotFoundException(
          "No [" + metadataType + "] metadata found for transformation [" + transformId + "]");
    }
    return metadata;
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
  public Duration getDuration() {
    return Duration.between(startTime, getCompletionTime().orElseGet(Instant::now));
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  /** Called by the manager to notify this transformation that it was deleted. */
  void wasDeleted() {
    this.deleted = true;
  }
}
