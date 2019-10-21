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

import com.connexta.transformation.commons.api.MetadataTransformation;
import com.connexta.transformation.commons.api.Transformation;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import com.connexta.transformation.commons.api.impl.AbstractMetadataImpl;
import com.connexta.transformation.commons.api.impl.AbstractTransformationImpl;
import com.connexta.transformation.commons.api.impl.RequestInfoImpl;
import com.connexta.transformation.pojo.MetadataPojo;
import java.net.URL;

/** An implementation of {@link Transformation} that stores all of the data in memory. */
public class InMemoryTransformation extends AbstractTransformationImpl {
  private final InMemoryTransformationManager manager;
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
      URL currentLocation,
      URL finalLocation,
      URL metacardLocation) {
    super(currentLocation, finalLocation, metacardLocation, manager.getClock());
    this.manager = manager;
    this.deleted = false;
  }

  @Override
  public void delete() {
    if (!deleted) {
      try {
        manager.delete(getTransformId());
      } catch (TransformationNotFoundException e) { // ignore as it shouldn't happen
      }
    }
  }

  @Override
  public MetadataTransformation add(String metadataType) {
    if (deleted) {
      throw new IllegalStateException("transformation [" + getTransformId() + "] was deleted.");
    } else if (isCompleted()) {
      throw new IllegalStateException(
          "transformation [" + getTransformId() + "] is already complete.");
    }
    return metadatas.computeIfAbsent(
        metadataType,
        t ->
            new InMemoryMetadataTransformation(
                this, metadataType, getTransformId(), getRequestInfo()));
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  @Override
  protected AbstractMetadataImpl fromPojo(MetadataPojo pojo) throws PersistenceException {
    return new InMemoryMetadataTransformation(pojo, this);
  }

  /** Called by the manager to notify this transformation that it was deleted. */
  void wasDeleted() {
    this.deleted = true;
  }
}
