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

import com.connexta.transformation.commons.api.exceptions.NonTransientPersistenceException;
import com.connexta.transformation.commons.api.exceptions.ParsingException;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.exceptions.ProcessingException;
import com.connexta.transformation.pojo.TransformationPojo;
import com.connexta.transformation.pojo.jackson.JsonUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.micrometer.core.instrument.Clock;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/** A transformation manager capable of handling persistence operations for transformations. */
public class TransformationPersistenceManager {
  private final Clock clock;

  /**
   * Instantiates a new transformation persistence manager.
   *
   * @param clock the clock to use for retrieving wall and monotonic times
   */
  public TransformationPersistenceManager(Clock clock) {
    this.clock = clock;
  }

  /**
   * Deserializes JSON content from a given JSON content string into a transformation.
   *
   * @param clazz the class of transformation to deserialize to
   * @param content the Json content string to deserialize a transformation from
   * @return the deserialized transformation
   * @throws ParsingException if underlying input contains invalid content or the JSON structure
   *     does not match the expected task structure (or has other mismatch issues)
   * @throws ProcessingException if a failure occurs while deserializing the value
   * @throws PersistenceException if any other error occurs while trying to deserialize the object
   */
  public <T extends AbstractTransformationImpl> T readFrom(Class<T> clazz, String content)
      throws ProcessingException, PersistenceException {
    final Constructor<T> ctor = findConstructor(clazz);
    final TransformationPojo pojo;

    try {
      pojo = JsonUtils.read(TransformationPojo.class, content);
    } catch (JsonParseException | JsonMappingException e) {
      throw new ParsingException(e);
    } catch (JsonProcessingException e) {
      throw new ProcessingException(e);
    }
    return instantiate(clazz, ctor, pojo);
  }

  /**
   * Deserializes JSON content from a given JSON content string into a transformation.
   *
   * @param clazz the class of transformation to deserialize to
   * @param stream the Json content stream to deserialize a transformation from
   * @return the deserialized transformation
   * @throws ParsingException if underlying input contains invalid content or the JSON structure
   *     does not match the expected task structure (or has other mismatch issues)
   * @throws ProcessingException if a failure occurs while deserializing the value
   * @throws PersistenceException if any other error occurs while trying to deserialize the object
   */
  public <T extends AbstractTransformationImpl> T readFrom(Class<T> clazz, InputStream stream)
      throws ProcessingException, PersistenceException {
    final Constructor<T> ctor = findConstructor(clazz);
    final TransformationPojo pojo;

    try {
      pojo = JsonUtils.read(TransformationPojo.class, stream);
    } catch (JsonParseException | JsonMappingException e) {
      throw new ParsingException(e);
    } catch (JsonProcessingException e) {
      throw new ProcessingException(e);
    } catch (IOException e) {
      throw new NonTransientPersistenceException(e);
    }
    return instantiate(clazz, ctor, pojo);
  }

  /**
   * Serializes a transformation into a JSON content string.
   *
   * @param transformation the transformation object to serialize to a string
   * @return the corresponding Json content string
   * @throws IllegalArgumentException if the task implementation is not one that can be saved
   * @throws ProcessingException if a failure occurs while serializing the value
   * @throws PersistenceException if any other error occurs while trying to serialize the object
   */
  public String writeTo(AbstractTransformationImpl transformation)
      throws ProcessingException, PersistenceException {
    try {
      return JsonUtils.write(transformation.writeTo(new TransformationPojo()));
    } catch (JsonProcessingException e) {
      throw new ProcessingException(e);
    }
  }

  /**
   * Serializes a transformation into the given output stream.
   *
   * @param transformation the transformation object to serialize to the specified output stream
   * @param stream the output stream to serializes the object to
   * @param transformation the transformation object to serialize to a string
   * @throws IllegalArgumentException if the task implementation is not one that can be saved
   * @throws ProcessingException if a failure occurs while serializing the value
   * @throws PersistenceException if any other error occurs while trying to serialize the object
   */
  public void writeTo(AbstractTransformationImpl transformation, OutputStream stream)
      throws ProcessingException, PersistenceException {
    try {
      JsonUtils.write(transformation.writeTo(new TransformationPojo()), stream);
    } catch (JsonProcessingException e) {
      throw new ProcessingException(e);
    } catch (IOException e) {
      throw new NonTransientPersistenceException(e);
    }
  }

  private <T extends AbstractTransformationImpl> Constructor<T> findConstructor(Class<T> clazz) {
    try {
      return clazz.getConstructor(TransformationPojo.class, Clock.class);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("missing expected constructor", e);
    }
  }

  private <T extends AbstractTransformationImpl> T instantiate(
      Class<T> clazz, Constructor<T> ctor, TransformationPojo pojo)
      throws ProcessingException, PersistenceException {
    try {
      return ctor.newInstance(pojo, clock);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("invalid abstract class: " + clazz.getName(), e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          "constructor is not accessible for class: " + clazz.getName(), e);
    } catch (InvocationTargetException e) {
      final Throwable t = e.getTargetException();

      if (t instanceof ProcessingException) {
        throw (ProcessingException) t;
      } else if (t instanceof PersistenceException) {
        throw (ProcessingException) t;
      }
      throw new ProcessingException("failed to instantiate class: " + clazz.getName(), t);
    }
  }
}
