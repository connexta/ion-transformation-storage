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
package com.connexta.transformation.commons.api;

import com.connexta.transformation.commons.api.exceptions.TransformationException;
import com.connexta.transformation.commons.api.exceptions.TransformationNotFoundException;
import java.net.URL;

/**
 * Provides the ability to save transformation request info, retrieve objects containing
 * transformation status and results, and delete data associated with a transformation.
 */
public interface TransformationManager {

  /**
   * Saves transformation request information, and creates a {@link Transformation} containing
   * information about the transformation, such as completion status and the transformation ID.
   *
   * @param currentLocation the current location of the resource provided in a transform request
   * @param finalLocation the final location of the resource provided in a transform request
   * @param metadataLocation the metadata location provided in a transform request
   * @return A {@link Transformation} representing the transformation.
   * @throws TransformationException if an error occurs
   */
  Transformation createTransform(URL currentLocation, URL finalLocation, URL metadataLocation)
      throws TransformationException;

  /**
   * Gets the {@link Transformation} for a transformation.
   *
   * @param transformId The ID of the transformation
   * @return the {@link Transformation} with the given ID
   * @throws TransformationException if an error occurs while fetching the {@link Transformation}
   * @throws TransformationNotFoundException if nothing associated with the given ID can be found
   */
  Transformation get(String transformId) throws TransformationException;

  /**
   * Gets the {@link MetadataTransformation} for the given transformation ID and type.
   *
   * @param transformId the transformation ID associated with the metadata
   * @param type the metadata type (e.g. metacard, irm...)
   * @return the {@link MetadataTransformation} with the given ID and type
   * @throws TransformationException if an error occurs while fetching the {@link
   *     MetadataTransformation}
   * @throws TransformationNotFoundException if nothing with the given type and ID can be found
   */
  MetadataTransformation get(String transformId, String type) throws TransformationException;

  /**
   * Deletes everything stored that is associated with the given transformation ID.
   *
   * @param transformId the transformation ID for which to delete all associated files
   * @throws TransformationNotFoundException if nothing associated with the given ID can be found
   * @throws TransformationException if an error occurs while trying to delete
   */
  void delete(String transformId) throws TransformationException;
}
