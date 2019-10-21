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

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class RequestInfoPojoTest {
  private static final int VERSION = 1;
  private static final String ID = "1234";
  private static final String METACARD_LOCATION = "http://localhost/metacard";
  private static final String CURRENT_LOCATION = "http://localhost/current";
  private static final String FINAL_LOCATION = "http://localhost/final";

  private static final RequestInfoPojo POJO =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojoTest.VERSION)
          .setId(RequestInfoPojoTest.ID)
          .setMetacardLocation(RequestInfoPojoTest.METACARD_LOCATION)
          .setCurrentLocation(RequestInfoPojoTest.CURRENT_LOCATION)
          .setFinalLocation(RequestInfoPojoTest.FINAL_LOCATION);

  private final RequestInfoPojo pojo2 =
      new RequestInfoPojo()
          .setVersion(RequestInfoPojoTest.VERSION)
          .setId(RequestInfoPojoTest.ID)
          .setMetacardLocation(RequestInfoPojoTest.METACARD_LOCATION)
          .setCurrentLocation(RequestInfoPojoTest.CURRENT_LOCATION)
          .setFinalLocation(RequestInfoPojoTest.FINAL_LOCATION);

  @Test
  public void testSetAndGetId() throws Exception {
    final RequestInfoPojo pojo = new RequestInfoPojo().setId(RequestInfoPojoTest.ID);

    Assert.assertThat(pojo.getId(), Matchers.equalTo(RequestInfoPojoTest.ID));
  }

  @Test
  public void testSetAndGetVersion() throws Exception {
    final RequestInfoPojo pojo = new RequestInfoPojo().setVersion(RequestInfoPojoTest.VERSION);

    Assert.assertThat(pojo.getVersion(), Matchers.equalTo(RequestInfoPojoTest.VERSION));
  }

  @Test
  public void testSetAndGetMetacardLocation() throws Exception {
    final RequestInfoPojo pojo =
        new RequestInfoPojo().setMetacardLocation(RequestInfoPojoTest.METACARD_LOCATION);

    Assert.assertThat(
        pojo.getMetacardLocation(), Matchers.equalTo(RequestInfoPojoTest.METACARD_LOCATION));
  }

  @Test
  public void testSetAndGetCurrentLocation() throws Exception {
    final RequestInfoPojo pojo =
        new RequestInfoPojo().setCurrentLocation(RequestInfoPojoTest.CURRENT_LOCATION);

    Assert.assertThat(
        pojo.getCurrentLocation(), Matchers.equalTo(RequestInfoPojoTest.CURRENT_LOCATION));
  }

  @Test
  public void testSetAndGetFinalLocation() throws Exception {
    final RequestInfoPojo pojo =
        new RequestInfoPojo().setFinalLocation(RequestInfoPojoTest.FINAL_LOCATION);

    Assert.assertThat(
        pojo.getFinalLocation(), Matchers.equalTo(RequestInfoPojoTest.FINAL_LOCATION));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    Assert.assertThat(RequestInfoPojoTest.POJO.hashCode(), Matchers.equalTo(pojo2.hashCode()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    pojo2.setId(RequestInfoPojoTest.ID + "2");

    Assert.assertThat(
        RequestInfoPojoTest.POJO.hashCode(), Matchers.not(Matchers.equalTo(pojo2.hashCode())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(
        RequestInfoPojoTest.POJO.equals(RequestInfoPojoTest.POJO), Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(RequestInfoPojoTest.POJO.equals(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotARequestInfoPojo() throws Exception {
    Assert.assertThat(RequestInfoPojoTest.POJO.equals("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    pojo2.setId(RequestInfoPojoTest.ID + "2");

    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenVersionIsDifferent() throws Exception {
    pojo2.setVersion(RequestInfoPojoTest.VERSION + 2);

    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenMetacardLocationIsDifferent() throws Exception {
    pojo2.setMetacardLocation(RequestInfoPojoTest.METACARD_LOCATION + "2");

    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenCurrentLocationIsDifferent() throws Exception {
    pojo2.setCurrentLocation(RequestInfoPojoTest.CURRENT_LOCATION + "2");

    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }

  @Test
  public void testEqualsWhenFinalLocationIsDifferent() throws Exception {
    pojo2.setFinalLocation(RequestInfoPojoTest.FINAL_LOCATION + "2");

    Assert.assertThat(RequestInfoPojoTest.POJO.equals(pojo2), Matchers.not(Matchers.equalTo(true)));
  }
}
