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
package com.connexta.transformation.commons.api.exceptions;

/** Persistence exception indicating an object could not be converted. */
@SuppressWarnings("squid:MaximumInheritanceDepth" /* Exception class hierarchy */)
public class ProcessingException extends NonTransientPersistenceException {
  /**
   * Instantiates a new exception.
   *
   * @param message the message for the exception
   */
  public ProcessingException(String message) {
    super(message);
  }

  /**
   * Instantiates a new exception.
   *
   * @param message the message for the exception
   * @param cause the cause for the exception
   */
  public ProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new exception.
   *
   * @param cause the cause for the exception
   */
  public ProcessingException(Throwable cause) {
    super(cause);
  }
}
