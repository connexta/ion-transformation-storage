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

import com.connexta.transformation.commons.api.RequestInfo;
import com.connexta.transformation.commons.api.exceptions.InvalidFieldException;
import com.connexta.transformation.commons.api.exceptions.PersistenceException;
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.unknown.UnknownPojo;
import com.google.common.annotations.VisibleForTesting;
import java.net.URL;
import java.util.Objects;

/** A data structure to hold the information of an incoming transformation request. */
public class RequestInfoImpl extends Persistable<RequestInfoPojo> implements RequestInfo {
  private static final String PERSISTABLE_TYPE = "request info";

  private URL metacardLocation;
  private URL currentLocation;
  private URL finalLocation;

  private boolean hasUnknowns = false;

  /**
   * Creates a structure that will hold the information for the incoming files.
   *
   * @param currentLocation the location to retrieve the file
   * @param finalLocation the downloadable location to put on the transformed metadata
   * @param metacardLocation the location of the metacard XML for the file
   */
  public RequestInfoImpl(URL currentLocation, URL finalLocation, URL metacardLocation) {
    super(RequestInfoImpl.PERSISTABLE_TYPE);
    this.currentLocation = currentLocation;
    this.finalLocation = finalLocation;
    this.metacardLocation = metacardLocation;
  }

  /**
   * Instantiates a request info based on the information provided by the specified pojo.
   *
   * @param pojo the pojo to initializes the request info with
   * @throws InvalidFieldException if an error occurs while trying to deserialize the pojo object
   * @throws PersistenceException if an error occurs while deserializing this pojo
   */
  public RequestInfoImpl(RequestInfoPojo pojo) throws PersistenceException {
    super(RequestInfoImpl.PERSISTABLE_TYPE, null);
    readFrom(pojo);
  }

  /**
   * Instantiates a request info based on the provided information.
   *
   * @param info the request info to be cloned
   */
  public RequestInfoImpl(RequestInfo info) {
    super(RequestInfoImpl.PERSISTABLE_TYPE);
    this.metacardLocation = info.getMetacardLocation();
    this.currentLocation = info.getCurrentLocation();
    this.finalLocation = info.getFinalLocation();
  }

  @VisibleForTesting
  RequestInfoImpl() {
    super(RequestInfoImpl.PERSISTABLE_TYPE);
  }

  @Override
  public URL getMetacardLocation() {
    return metacardLocation;
  }

  @Override
  public URL getCurrentLocation() {
    return currentLocation;
  }

  @Override
  public URL getFinalLocation() {
    return finalLocation;
  }

  @Override
  public boolean hasUnknowns() {
    return hasUnknowns;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), metacardLocation, currentLocation, finalLocation);
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj) && (obj instanceof RequestInfoImpl)) {
      final RequestInfoImpl request = (RequestInfoImpl) obj;

      return Objects.equals(metacardLocation, request.metacardLocation)
          && Objects.equals(currentLocation, request.currentLocation)
          && Objects.equals(finalLocation, request.finalLocation);
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format(
        "RequestInfoImpl[id=%s, metacardLocation=%s, currentLocation=%s, finalLocation=%s]",
        getId(), metacardLocation, currentLocation, finalLocation);
  }

  @Override
  protected RequestInfoPojo writeTo(RequestInfoPojo pojo) throws PersistenceException {
    if (hasUnknowns()) { // cannot serialize if it contains unknowns
      throw new InvalidFieldException("unknown request info");
    }
    super.writeTo(pojo);
    setOrFailIfNull("metacardLocation", this::getMetacardLocation, pojo::setMetacardLocation);
    setOrFailIfNull("currentLocation", this::getCurrentLocation, pojo::setCurrentLocation);
    setOrFailIfNull("finalLocation", this::getFinalLocation, pojo::setFinalLocation);
    return pojo.setVersion(RequestInfoPojo.CURRENT_VERSION);
  }

  @Override
  protected final void readFrom(RequestInfoPojo pojo) throws PersistenceException {
    super.readFrom(pojo);
    if (pojo.getVersion() < RequestInfoPojo.MINIMUM_VERSION) {
      throw new UnsupportedVersionException(
          "unsupported "
              + RequestInfoImpl.PERSISTABLE_TYPE
              + " version: "
              + pojo.getVersion()
              + " for object: "
              + getId());
    } // do support pojo.getVersion() > CURRENT_VERSION for forward compatibility
    this.hasUnknowns = pojo instanceof UnknownPojo; // reset the unknown flag
    readFromCurrentOrFutureVersion(pojo);
  }

  @VisibleForTesting
  void setCurrentLocation(URL currentLocation) {
    this.currentLocation = currentLocation;
  }

  @VisibleForTesting
  void setFinalLocation(URL finalLocation) {
    this.finalLocation = finalLocation;
  }

  @VisibleForTesting
  void setMetacardLocation(URL metacardLocation) {
    this.metacardLocation = metacardLocation;
  }

  private void readFromCurrentOrFutureVersion(RequestInfoPojo pojo) throws PersistenceException {
    convertAndSetOrFailIfNullOrEmpty(
        "metacardLocation", pojo::getMetacardLocation, URL::new, this::setMetacardLocation);
    convertAndSetOrFailIfNullOrEmpty(
        "currentLocation", pojo::getCurrentLocation, URL::new, this::setCurrentLocation);
    convertAndSetOrFailIfNullOrEmpty(
        "finalLocation", pojo::getFinalLocation, URL::new, this::setFinalLocation);
  }

  /**
   * Converts the specified request info into a request info pojo.
   *
   * @param requestInfo the request info to convert
   * @return the corresponding pojo
   * @throws InvalidFieldException if an error occurs while trying to serialize the request info
   * @throws PersistenceException if an error occurs while serializing the request info
   */
  public static RequestInfoPojo toPojo(RequestInfo requestInfo) throws PersistenceException {
    return RequestInfoImpl.wrap(requestInfo).writeTo(new RequestInfoPojo());
  }

  /**
   * Ensures that the provided request info is an instance of this implementation class by either
   * returning it as is or by cloning it.
   *
   * @param requestInfo the request info to be wrapped
   * @return the corresponding implementation or <code>requestInfo</code> if it was already an
   *     instance of this class
   */
  public static RequestInfoImpl wrap(RequestInfo requestInfo) {
    return (requestInfo instanceof RequestInfoImpl)
        ? (RequestInfoImpl) requestInfo
        : new RequestInfoImpl(requestInfo);
  }
}
