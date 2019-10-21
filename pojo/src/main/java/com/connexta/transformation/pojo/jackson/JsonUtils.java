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
package com.connexta.transformation.pojo.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** This class provides utility functions for dealing with Json objects. */
public class JsonUtils {
  @VisibleForTesting
  static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new ParameterNamesModule())
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule());

  /** Prevents instantiation of this class. */
  private JsonUtils() {}

  /**
   * Deserializes JSON content from a given JSON content string.
   *
   * @param <D> the type of value to retrieve
   * @param clazz the class of the value to retrieve
   * @param content the Json content string to deserialize from
   * @return the deserialized value
   * @throws JsonParseException if underlying input contains invalid content
   * @throws com.fasterxml.jackson.databind.JsonMappingException if the input JSON structure does
   *     not match structure expected for result type (or has other mismatch issues)
   * @throws JsonProcessingException if a failure occurs while deserializing the value
   */
  public static <D> D read(Class<D> clazz, String content) throws JsonProcessingException {
    return JsonUtils.MAPPER.readValue(content, clazz);
  }

  /**
   * Deserializes JSON content from a given JSON stream.
   *
   * @param <D> the type of value to retrieve
   * @param clazz the class of the value to retrieve
   * @param stream the Json content stream to deserialize from
   * @return the deserialized value
   * @throws IOException if an I/O error occurs while reading the stream
   * @throws JsonParseException if underlying input contains invalid content
   * @throws com.fasterxml.jackson.databind.JsonMappingException if the input JSON structure does
   *     not match structure expected for result type (or has other mismatch issues)
   * @throws JsonProcessingException if a failure occurs while deserializing the value
   */
  public static <D> D read(Class<D> clazz, InputStream stream)
      throws IOException, JsonProcessingException {
    return JsonUtils.MAPPER.readValue(stream, clazz);
  }

  /**
   * Serializes any Java value as a string.
   *
   * @param value the object to serialize into a Json string
   * @return the corresponding Json content string
   * @throws JsonProcessingException if a failure occurs while serializing the value
   */
  public static String write(Object value) throws JsonProcessingException {
    return JsonUtils.MAPPER.writeValueAsString(value);
  }

  /**
   * Serializes any Java value to the given output stream.
   *
   * @param value the object to serialize into the specified output stream
   * @param stream the output stream to serializes the object to
   * @throws IOException if an I/O error occurs while writing to the output stream
   * @throws JsonProcessingException if a failure occurs while serializing the value
   */
  public static void write(Object value, OutputStream stream)
      throws IOException, JsonProcessingException {
    JsonUtils.MAPPER.writeValue(stream, value);
  }
}
