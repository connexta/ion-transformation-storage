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

import com.connexta.transformation.commons.api.TransformationManager;
import com.connexta.transformation.commons.api.exceptions.TransformationException;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.status.MetadataTransformation;
import com.connexta.transformation.commons.api.status.Transformation;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link TransformationManager} that stores all of the data in memory. An
 * internal {@link Map} is used to store the {@link Transformation}s and their associated {@link
 * MetadataTransformation}s. The store is threadsafe, but will allow multiple requests with the
 * exact same "locations" as input.
 */
public class InMemoryTransformationManager implements TransformationManager {
  private final Map<String, InMemoryTransformation> store = new HashMap<>();

  @Override
  public Transformation createTransform(
      URI currentLocation, URI finalLocation, URI metadataLocation) throws TransformationException {
    InMemoryTransformation transformation =
        new InMemoryTransformation(this, currentLocation, finalLocation, metadataLocation);
    store.put(transformation.getTransformId(), transformation);
    return transformation;
  }

  @Override
  public Transformation get(String transformId) throws TransformationException {
    Transformation transformation = store.get(transformId);
    if (transformation == null) {
      throw new TransformationNotFoundException(
          "Transformation [" + transformId + "] cannot be found");
    } else {
      return transformation;
    }
  }

  @Override
  public MetadataTransformation get(String transformId, String metadataType)
      throws TransformationException {
    return get(transformId).getMetadata(metadataType);
  }

  @Override
  public void delete(String transformId) throws TransformationNotFoundException {
    final InMemoryTransformation transformation = store.remove(transformId);

    if (transformation == null) {
      throw new TransformationNotFoundException(
          "Transformation [" + transformId + "] cannot be found");
    }
    transformation.wasDeleted();
  }
}
