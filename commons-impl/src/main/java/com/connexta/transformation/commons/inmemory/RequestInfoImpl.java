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

import com.connexta.transformation.commons.api.RequestInfo;
import java.net.URI;

public class RequestInfoImpl implements RequestInfo {

  private final URI metacardLocation;
  private final URI currentLocation;
  private final URI finalLocation;

  public RequestInfoImpl(URI currentLocation, URI finalLocation, URI metacardLocation) {
    this.currentLocation = currentLocation;
    this.finalLocation = finalLocation;
    this.metacardLocation = metacardLocation;
  }

  @Override
  public URI getMetacardLocation() {
    return metacardLocation;
  }

  @Override
  public URI getCurrentLocation() {
    return currentLocation;
  }

  @Override
  public URI getFinalLocation() {
    return finalLocation;
  }
}
