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
package com.connexta.transformation.pojo;

import com.connexta.transformation.pojo.unknown.UnknownRequestInfoPojo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URL;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * This class provides a pojo implementation for the request information associated with a
 * particular transformation capable of reloading all supported fields for all supported versions
 * from persistence. It also provides the capability of persisting back the fields based on the
 * latest version format.
 */
@JsonPropertyOrder({
  "clazz",
  "id",
  "version",
  "final_location",
  "current_location",
  "metacard_location"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(
    use = Id.NAME,
    include = As.PROPERTY,
    property = "clazz",
    defaultImpl = UnknownRequestInfoPojo.class)
@JsonSubTypes(@Type(RequestInfoPojo.class))
@JsonTypeName("request")
public class RequestInfoPojo extends Pojo<RequestInfoPojo> {
  /**
   * Current version format.
   *
   * <p>Version history:
   *
   * <ul>
   *   <li>1 - Initial Ion version.
   * </ul>
   */
  public static final int CURRENT_VERSION = 1;

  /** The oldest version supported by the current code (anything before that will fail). */
  public static final int MINIMUM_VERSION = 1;

  @JsonProperty("metacard_location")
  @Nullable
  private String metacardLocation;

  @JsonProperty("current_location")
  @Nullable
  private String currentLocation;

  @JsonProperty("final_location")
  @Nullable
  private String finalLocation;

  /**
   * Gets a url which can be used to download the metacard that is to be transformed.
   *
   * @return a url string for retrieving the metacard to be transformed or <code>null</code> if none
   *     defined
   */
  @Nullable
  public String getMetacardLocation() {
    return metacardLocation;
  }

  /**
   * Sets a url which can be used to download the metacard that is to be transformed.
   *
   * @param metacardLocation a url string for retrieving the metacard to be transformed or <code>
   *     null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setMetacardLocation(@Nullable String metacardLocation) {
    this.metacardLocation = metacardLocation;
    return this;
  }

  /**
   * Sets a url which can be used to download the metacard that is to be transformed.
   *
   * @param metacardLocation a url string for retrieving the metacard to be transformed or <code>
   *     null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setMetacardLocation(@Nullable URL metacardLocation) {
    this.metacardLocation = (metacardLocation != null) ? metacardLocation.toString() : null;
    return this;
  }

  /**
   * Gets a url which can be used to download the resource that will be used for the transformation.
   *
   * @return a url string for retrieving the resource for a transformation or <code>null</code> if
   *     none defined
   */
  @Nullable
  public String getCurrentLocation() {
    return currentLocation;
  }

  /**
   * Sets a url which can be used to download the resource that will be used for the transformation.
   *
   * @param currentLocation a url string for retrieving the resource for a transformation or <code>
   *     null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setCurrentLocation(@Nullable String currentLocation) {
    this.currentLocation = currentLocation;
    return this;
  }

  /**
   * Sets a url which can be used to download the resource that will be used for the transformation.
   *
   * @param currentLocation a url string for retrieving the resource for a transformation or <code>
   *     null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setCurrentLocation(@Nullable URL currentLocation) {
    this.currentLocation = (currentLocation != null) ? currentLocation.toString() : null;
    return this;
  }

  /**
   * Gets a url string representing the final resting place of a resource for retrieval.
   *
   * @return a url string representing the final resting place of a resource for retrieval or <code>
   *     null</code> if none defined
   */
  @Nullable
  public String getFinalLocation() {
    return finalLocation;
  }

  /**
   * Sets a url representing the final resting place of a resource for retrieval.
   *
   * @param finalLocation a url string representing the final resting place of a resource for
   *     retrieval or <code>null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setFinalLocation(@Nullable String finalLocation) {
    this.finalLocation = finalLocation;
    return this;
  }

  /**
   * Sets a url representing the final resting place of a resource for retrieval.
   *
   * @param finalLocation a url string representing the final resting place of a resource for
   *     retrieval or <code>null</code> if none defined
   * @return this for chaining
   */
  public RequestInfoPojo setFinalLocation(@Nullable URL finalLocation) {
    this.finalLocation = (finalLocation != null) ? finalLocation.toString() : null;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), metacardLocation, currentLocation, finalLocation);
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj) && (obj instanceof RequestInfoPojo)) {
      final RequestInfoPojo pojo = (RequestInfoPojo) obj;

      return Objects.equals(metacardLocation, pojo.metacardLocation)
          && Objects.equals(currentLocation, pojo.currentLocation)
          && Objects.equals(finalLocation, pojo.finalLocation);
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format(
        "RequestInfoPojo[id=%s, version=%d, metadataLocation=%s, currentLocation=%s, finalLocation=%s]",
        getId(), getVersion(), metacardLocation, currentLocation, finalLocation);
  }
}
