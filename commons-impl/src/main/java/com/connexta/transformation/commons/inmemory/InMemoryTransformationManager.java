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
import com.connexta.transformation.commons.inmemory.status.InMemoryTransformation;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTransformationManager implements TransformationManager {
  private final Map<String, Transformation> store = new HashMap<>();

  @Override
  public Transformation createTransform(
      URI currentLocation, URI finalLocation, URI metadataLocation) throws TransformationException {
    Transformation transformation =
        new InMemoryTransformation(currentLocation, finalLocation, metadataLocation);
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
  public MetadataTransformation get(String transformId, String type)
      throws TransformationException {
    return get(transformId)
        .metadatas()
        .filter(m -> m.getMetadataType().equals(type))
        .findFirst()
        .orElseThrow(
            () ->
                new TransformationNotFoundException(
                    "No [" + type + "] metadata found for transformation [" + transformId + "]"));
  }

  @Override
  public void delete(String transformId) throws TransformationException {
    Transformation transformation = store.remove(transformId);
    if (transformation == null) {
      throw new TransformationNotFoundException(
          "Transformation [" + transformId + "] cannot be found");
    }
  }
}
