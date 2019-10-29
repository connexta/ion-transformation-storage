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

import com.connexta.transformation.commons.api.exceptions.InvalidFieldException;

/**
 * Represents an operation that accepts a single input argument and returns no result. Unlike most
 * other functional interfaces, {@code FieldConsumer} is expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface FieldConsumer<T> {
  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   * @throws InvalidFieldException if a failure occurs while invoking this consumer
   */
  public void accept(T t) throws InvalidFieldException;
}
