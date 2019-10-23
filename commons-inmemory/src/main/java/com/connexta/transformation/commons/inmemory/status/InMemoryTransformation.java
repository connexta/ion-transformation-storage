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

import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.TransformationException;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.connexta.transformation.commons.api.status.Transformation;
import com.connexta.transformation.commons.inmemory.RequestInfoImpl;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class InMemoryTransformation implements Transformation {

  private final Map<String, MetadataTransformation> metadataMap;
  private final RequestInfo requestInfo;
  private final String transformId;
  private final Instant startTime;

  /**
   * Generates a startTime, a Transform ID, and the related {@link RequestInfoImpl}.
   *
   * @param currentLocation the location to retrieve the file
   * @param finalLocation the downloadable location to put on the transformed metadata
   * @param metacardLocation the location of the metacard XML for the file
   */
  public InMemoryTransformation(URI currentLocation, URI finalLocation, URI metacardLocation) {
    startTime = Instant.now();
    metadataMap = new HashMap<>();
    requestInfo = new RequestInfoImpl(currentLocation, finalLocation, metacardLocation);
    transformId = UUID.randomUUID().toString();
  }

  @Override
  public MetadataTransformation add(String metadataType)
      throws TransformationException, IllegalArgumentException {
    MetadataTransformation metadata =
        new InMemoryMetadataTransformation(metadataType, transformId, requestInfo);
    if (metadataMap.get(metadataType) != null) {
      throw new IllegalStateException(
          "["
              + metadataType
              + "] metadata already exists for transformation ["
              + transformId
              + "]");
    } else if (isCompleted()) {
      throw new IllegalStateException("transformation [" + transformId + "] is already complete.");
    } else {
      metadataMap.put(metadataType, metadata);
    }
    return metadata;
  }

  @Override
  public Stream<MetadataTransformation> metadatas() {
    return metadataMap.values().stream();
  }

  @Override
  public Optional<MetadataTransformation> getMetadata(String metadataType) {
    return metadatas().filter(m -> m.getMetadataType().equals(metadataType)).findFirst();
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
}