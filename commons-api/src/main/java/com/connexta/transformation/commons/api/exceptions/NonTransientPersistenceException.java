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

/**
 * Thrown from persistence operations which are not recoverable and retrying would just fail for the
 * same reason.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth" /* Exception class hierarchy */)
public class NonTransientPersistenceException extends PersistenceException {
  /**
   * Instantiates a new exception.
   *
   * @param message the message for the exception
   */
  public NonTransientPersistenceException(String message) {
    super(message);
  }

  /**
   * Instantiates a new exception.
   *
   * @param message the message for the exception
   * @param cause the cause for the exception
   */
  public NonTransientPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new exception.
   *
   * @param cause the cause for the exception
   */
  public NonTransientPersistenceException(Throwable cause) {
    super(cause);
  }
}
