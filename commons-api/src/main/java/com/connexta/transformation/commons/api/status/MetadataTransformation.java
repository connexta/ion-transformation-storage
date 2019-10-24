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
package com.connexta.transformation.commons.api.status;

import com.connexta.transformation.commons.api.ErrorCode;
import com.connexta.transformation.commons.api.exceptions.TransformationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.OptionalLong;
import org.apache.commons.io.input.ReaderInputStream;

/**
 * Handles information about a piece of metadata that has been, or will be generated through the
 * transformation process. The metadata can also be saved or retrieved using this object..
 */
public interface MetadataTransformation extends TransformationStatus {

  /**
   * Returns the type of metadata being created (e.g. metacard, irm...).
   *
   * @return the type of metadata being created
   */
  String getMetadataType();

  /**
   * Returns an optional {@link InputStream} for retrieving the metadata. If the metadata hasn't
   * been created yet, or failed to be created, the optional will be empty.
   *
   * @return an input stream for this metadata or empty if not available yet or if no metadata was
   *     generated
   * @throws TransformationException if an error occurs while executing this method
   * @throws IOException if an I/O error occurred while retrieving the content
   * @throws IllegalStateException if the transformation was deleted already
   */
  Optional<InputStream> getContent() throws TransformationException, IOException;

  /**
   * Returns an optional {@link Reader} for retrieving the metadata. If the metadata hasn't been
   * created yet, or failed to be created, the optional will be empty.
   *
   * @param charset the character set to use for retrieving the content
   * @return an input stream for this metadata or empty if not available yet or if no metadata was
   *     generated
   * @throws TransformationException if an error occurs while executing this method
   * @throws IOException if an I/O error occurred while retrieving the content
   * @throws IllegalStateException if the transformation was deleted already
   */
  default Optional<Reader> getContent(Charset charset) throws TransformationException, IOException {
    return getContent().map(is -> new InputStreamReader(is, charset));
  }

  /**
   * Returns an optional containing the content type of the metadata. If the metadata hasn't been
   * created yet, or failed to be created, the optional will be empty.
   *
   * @return the content type of the metadata or empty if the metadata is still being generated or
   *     failed to be generated
   */
  Optional<String> getContentType();

  /**
   * Returns an optional containing the length of the metadata. If the metadata hasn't been created
   * yet, or failed to be created, the optional will be empty.
   *
   * @return the length of the metadata or empty if the metadata hasn't been generated or failed to
   *     be generated
   */
  OptionalLong getContentLength();

  /**
   * Used to signify the successful creation of the metadata for this MetadataTransformation. The
   * given {@link InputStream} will be read and closed within this method.
   *
   * @param contentType the content type of the metadata
   * @param contentStream an {@link InputStream} for reading the metadata
   * @throws TransformationException if an error occurs while executing this method
   * @throws IOException if an IOException occurs while reading the provided stream
   * @throws IllegalStateException if this method is called when this metadata transformation has
   *     already been completed or if the transformation was deleted already
   */
  void succeed(String contentType, InputStream contentStream)
      throws TransformationException, IOException;

  /**
   * Used to signify the successful creation of the metadata for this MetadataTransformation. The
   * given {@link InputStream} will be read and closed within this method.
   *
   * @param contentType the content type of the metadata
   * @param charset the character set used for the content
   * @param contentStream a {@link Reader} for reading the metadata
   * @throws TransformationException if an error occurs while executing this method
   * @throws IOException if an IOException occurs while reading the provided stream
   * @throws IllegalStateException if this method is called when this metadata transformation has
   *     already been completed or if the transformation was deleted already
   */
  default void succeed(String contentType, Charset charset, Reader contentStream)
      throws TransformationException, IOException {
    succeed(contentType, new ReaderInputStream(contentStream, charset));
  }

  /**
   * Used to signify that the creation of this metadata failed.
   *
   * @param reason an {@link ErrorCode} indicating why the metadata creation failed
   * @param message a message including information about why the metadata creation failed
   * @throws TransformationException if an error occurs while executing this method
   * @throws IllegalStateException if this method is called when this metadata transformation has
   *     already been completed or if the transformation was deleted already
   */
  void fail(ErrorCode reason, String message) throws TransformationException;

  /**
   * Returns an optional containing an {@link ErrorCode} indicating why this metadata failed to be
   * created. If the creation is still in progress or the metadata was created successfully, the
   * optional will be empty.
   *
   * @return an {@link ErrorCode} indicating why this metadata failed to be created or empty if the
   *     metadata hasn't been generated or failed to be successful
   */
  Optional<ErrorCode> getFailureReason();

  /**
   * Returns an optional containing a message indicating why this metadata failed to be created. If
   * the creation is still in progress or the metadata was created successfully, the optional will
   * be empty.
   *
   * @return a message indicating why this metadata failed to be created or empty if no message is
   *     available. This could mean the metadata is still being generated or was generated
   *     successfully.
   */
  Optional<String> getFailureMessage();
}
