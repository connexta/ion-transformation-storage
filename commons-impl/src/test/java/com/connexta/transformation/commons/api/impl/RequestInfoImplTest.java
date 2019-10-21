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
import com.connexta.transformation.commons.api.exceptions.UnsupportedVersionException;
import com.connexta.transformation.pojo.RequestInfoPojo;
import com.connexta.transformation.pojo.unknown.UnknownRequestInfoPojo;
import java.net.MalformedURLException;
import java.net.URL;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class RequestInfoImplTest {
  private static final String ID = "1234";
  private static final URL CURRENT_LOCATION;
  private static final URL FINAL_LOCATION;
  private static final URL METACARD_LOCATION;
  private static final URL LOCATION;

  static {
    try {
      CURRENT_LOCATION = new URL("https://localhost/current");
      FINAL_LOCATION = new URL("https://localhost/final");
      METACARD_LOCATION = new URL("https://localhost/metacard");
      LOCATION = new URL("https://localhost/other");
    } catch (MalformedURLException e) {
      throw new AssertionError(e);
    }
  }

  private static final RequestInfoImpl PERSISTABLE =
      new RequestInfoImpl(
          RequestInfoImplTest.CURRENT_LOCATION,
          RequestInfoImplTest.FINAL_LOCATION,
          RequestInfoImplTest.METACARD_LOCATION);

  static {
    RequestInfoImplTest.PERSISTABLE.setId(RequestInfoImplTest.ID);
  }

  @Rule public ExpectedException exception = ExpectedException.none();

  private final RequestInfoImpl persistable2 =
      new RequestInfoImpl(
          RequestInfoImplTest.CURRENT_LOCATION,
          RequestInfoImplTest.FINAL_LOCATION,
          RequestInfoImplTest.METACARD_LOCATION);

  private final RequestInfoPojo pojo =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojo.CURRENT_VERSION)
          .setId(RequestInfoImplTest.ID)
          .setMetacardLocation(RequestInfoImplTest.METACARD_LOCATION)
          .setCurrentLocation((RequestInfoImplTest.CURRENT_LOCATION))
          .setFinalLocation(RequestInfoImplTest.FINAL_LOCATION);

  public RequestInfoImplTest() {
    persistable2.setId(RequestInfoImplTest.ID);
  }

  @Test
  public void testConstructor() throws Exception {
    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.getCurrentLocation(),
        Matchers.equalTo(RequestInfoImplTest.CURRENT_LOCATION));
    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.getFinalLocation(),
        Matchers.equalTo(RequestInfoImplTest.FINAL_LOCATION));
    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.getMetacardLocation(),
        Matchers.equalTo(RequestInfoImplTest.METACARD_LOCATION));
  }

  @Test
  public void testCopyConstructor() throws Exception {
    final RequestInfoImpl request = new RequestInfoImpl(RequestInfoImplTest.PERSISTABLE);

    Assert.assertThat(
        request.getCurrentLocation(), Matchers.equalTo(RequestInfoImplTest.CURRENT_LOCATION));
    Assert.assertThat(
        request.getFinalLocation(), Matchers.equalTo(RequestInfoImplTest.FINAL_LOCATION));
    Assert.assertThat(
        request.getMetacardLocation(), Matchers.equalTo(RequestInfoImplTest.METACARD_LOCATION));
  }

  @Test
  public void testWriteTo() throws Exception {
    final RequestInfoPojo pojo = new RequestInfoPojo();

    RequestInfoImplTest.PERSISTABLE.writeTo(pojo);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(RequestInfoImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(RequestInfoPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getMetacardLocation(),
        Matchers.equalTo(RequestInfoImplTest.METACARD_LOCATION.toString()));
    Assert.assertThat(
        pojo.getCurrentLocation(),
        Matchers.equalTo(RequestInfoImplTest.CURRENT_LOCATION.toString()));
    Assert.assertThat(
        pojo.getFinalLocation(), Matchers.equalTo(RequestInfoImplTest.FINAL_LOCATION.toString()));
  }

  @Test
  public void testWriteToWhenMetacardLocationIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*metacardLocation.*"));

    final RequestInfoPojo pojo = new RequestInfoPojo();

    persistable2.setMetacardLocation(null);

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenCurrentLocationIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*currentLocation.*"));

    final RequestInfoPojo pojo = new RequestInfoPojo();

    persistable2.setCurrentLocation(null);

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenFinalLocationIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*finalLocation.*"));

    final RequestInfoPojo pojo = new RequestInfoPojo();

    persistable2.setFinalLocation(null);

    persistable2.writeTo(pojo);
  }

  @Test
  public void testWriteToWhenHasUnknowns() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.equalTo("unknown request info"));

    final RequestInfoPojo unknownPojo =
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojo.CURRENT_VERSION)
            .setId(RequestInfoImplTest.ID)
            .setMetacardLocation(RequestInfoImplTest.METACARD_LOCATION)
            .setCurrentLocation((RequestInfoImplTest.CURRENT_LOCATION))
            .setFinalLocation(RequestInfoImplTest.FINAL_LOCATION);
    final RequestInfoImpl persistable3 = new RequestInfoImpl();

    persistable3.readFrom(unknownPojo);

    final RequestInfoPojo pojo = new RequestInfoPojo();

    persistable3.writeTo(pojo);
  }

  @Test
  public void testReadFromCurrentVersion() throws Exception {
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getMetacardLocation().toString(), Matchers.equalTo(pojo.getMetacardLocation()));
    Assert.assertThat(
        persistable.getCurrentLocation().toString(), Matchers.equalTo(pojo.getCurrentLocation()));
    Assert.assertThat(
        persistable.getFinalLocation().toString(), Matchers.equalTo(pojo.getFinalLocation()));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testReadFromFutureVersion() throws Exception {
    pojo.setVersion(9999999);
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(pojo.getId()));
    Assert.assertThat(
        persistable.getMetacardLocation().toString(), Matchers.equalTo(pojo.getMetacardLocation()));
    Assert.assertThat(
        persistable.getCurrentLocation().toString(), Matchers.equalTo(pojo.getCurrentLocation()));
    Assert.assertThat(
        persistable.getFinalLocation().toString(), Matchers.equalTo(pojo.getFinalLocation()));
  }

  @Test
  public void testReadFromUnsupportedVersion() throws Exception {
    exception.expect(UnsupportedVersionException.class);
    exception.expectMessage(Matchers.matchesPattern(".*unsupported.*version.*"));

    pojo.setVersion(-1);
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullMetacardLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*metacardLocation.*"));

    pojo.setMetacardLocation((String) null);
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyMetacardLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*metacardLocation.*"));

    pojo.setMetacardLocation("");
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullCurrentLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*currentLocation.*"));

    pojo.setCurrentLocation((String) null);
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyCurrentLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*currentLocation.*"));

    pojo.setCurrentLocation("");
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithNullFinalLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*missing.*finalLocation.*"));

    pojo.setFinalLocation((String) null);
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromCurrentVersionWithEmptyFinalLocation() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*finalLocation.*"));

    pojo.setFinalLocation("");
    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromUnknownPojo() throws Exception {
    final RequestInfoPojo unknownPojo =
        new UnknownRequestInfoPojo()
            .setVersion(RequestInfoPojo.CURRENT_VERSION)
            .setId(RequestInfoImplTest.ID)
            .setMetacardLocation(RequestInfoImplTest.METACARD_LOCATION)
            .setCurrentLocation((RequestInfoImplTest.CURRENT_LOCATION))
            .setFinalLocation(RequestInfoImplTest.FINAL_LOCATION);

    final RequestInfoImpl persistable = new RequestInfoImpl();

    persistable.readFrom(unknownPojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(unknownPojo.getId()));
    Assert.assertThat(
        persistable.getMetacardLocation().toString(),
        Matchers.equalTo(unknownPojo.getMetacardLocation()));
    Assert.assertThat(
        persistable.getCurrentLocation().toString(),
        Matchers.equalTo(unknownPojo.getCurrentLocation()));
    Assert.assertThat(
        persistable.getFinalLocation().toString(),
        Matchers.equalTo(unknownPojo.getFinalLocation()));
    Assert.assertThat(persistable.hasUnknowns(), Matchers.equalTo(true));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.hashCode(), Matchers.equalTo(persistable2.hashCode()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    persistable2.setCurrentLocation(RequestInfoImplTest.LOCATION);

    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.hashCode(),
        Matchers.not(Matchers.equalTo(persistable2.hashCode())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    Assert.assertThat(RequestInfoImplTest.PERSISTABLE.equals(persistable2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.equals(RequestInfoImplTest.PERSISTABLE),
        Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(RequestInfoImplTest.PERSISTABLE.equals(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotTheSameClass() throws Exception {
    Assert.assertThat(RequestInfoImplTest.PERSISTABLE.equals("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    persistable2.setId(RequestInfoImplTest.ID + "2");

    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.equals(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenCurrentLocationIsDifferent() throws Exception {
    persistable2.setCurrentLocation(RequestInfoImplTest.LOCATION);

    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.equals(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFinalLocationIsDifferent() throws Exception {
    persistable2.setFinalLocation(RequestInfoImplTest.LOCATION);

    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.equals(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenMetacardLocationIsDifferent() throws Exception {
    persistable2.setMetacardLocation(RequestInfoImplTest.LOCATION);

    Assert.assertThat(
        RequestInfoImplTest.PERSISTABLE.equals(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testWrapWhenInfoNotARequestInfoImpl() throws Exception {
    final RequestInfo info = Mockito.mock(RequestInfo.class);

    Mockito.when(info.getMetacardLocation()).thenReturn(RequestInfoImplTest.METACARD_LOCATION);
    Mockito.when(info.getCurrentLocation()).thenReturn(RequestInfoImplTest.CURRENT_LOCATION);
    Mockito.when(info.getFinalLocation()).thenReturn(RequestInfoImplTest.FINAL_LOCATION);

    final RequestInfoImpl request = RequestInfoImpl.wrap(info);

    Assert.assertThat(request, Matchers.not(Matchers.sameInstance(info)));
    Assert.assertThat(
        request.getMetacardLocation(), Matchers.equalTo(RequestInfoImplTest.METACARD_LOCATION));
    Assert.assertThat(
        request.getCurrentLocation(), Matchers.equalTo(RequestInfoImplTest.CURRENT_LOCATION));
    Assert.assertThat(
        request.getFinalLocation(), Matchers.equalTo(RequestInfoImplTest.FINAL_LOCATION));
  }

  @Test
  public void testWrapWhenInfoIsARequestInfoImpl() throws Exception {
    final RequestInfoImpl request = RequestInfoImpl.wrap(RequestInfoImplTest.PERSISTABLE);

    Assert.assertThat(request, Matchers.sameInstance(RequestInfoImplTest.PERSISTABLE));
  }

  @Test
  public void testToPojo() throws Exception {
    final RequestInfoPojo pojo = RequestInfoImpl.toPojo(RequestInfoImplTest.PERSISTABLE);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(RequestInfoImplTest.ID));
    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(RequestInfoPojo.CURRENT_VERSION));
    Assert.assertThat(
        pojo.getMetacardLocation(),
        Matchers.equalTo(RequestInfoImplTest.METACARD_LOCATION.toString()));
    Assert.assertThat(
        pojo.getCurrentLocation(),
        Matchers.equalTo(RequestInfoImplTest.CURRENT_LOCATION.toString()));
    Assert.assertThat(
        pojo.getFinalLocation(), Matchers.equalTo(RequestInfoImplTest.FINAL_LOCATION.toString()));
  }
}
