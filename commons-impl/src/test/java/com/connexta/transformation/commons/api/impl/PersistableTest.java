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
import com.connexta.transformation.pojo.Pojo;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nullable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PersistableTest {
  private static final String TYPE = "type";
  private static final String ID = "id-1234";
  private static final String STRING = "string-b";
  private static final TestEnum ENUM = TestEnum.ENUM_A;

  private static final TestPersistable PERSISTABLE =
      new TestPersistable(PersistableTest.TYPE, PersistableTest.ID);

  @Rule public ExpectedException exception = ExpectedException.none();

  @Test
  public void testCtorWithType() throws Exception {
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE);

    Assert.assertThat(persistable.getPersistableType(), Matchers.equalTo(PersistableTest.TYPE));
    Assert.assertThat(persistable.getId(), Matchers.notNullValue());
  }

  @Test
  public void testCtorWithTypeAndId() throws Exception {
    Assert.assertThat(
        PersistableTest.PERSISTABLE.getPersistableType(), Matchers.equalTo(PersistableTest.TYPE));
    Assert.assertThat(PersistableTest.PERSISTABLE.getId(), Matchers.equalTo(PersistableTest.ID));
  }

  @Test
  public void testCtorWithTypeAndNullId() throws Exception {
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    Assert.assertThat(persistable.getPersistableType(), Matchers.equalTo(PersistableTest.TYPE));
    Assert.assertThat(persistable.getId(), Matchers.nullValue());
  }

  @Test
  public void testGetId() throws Exception {
    Assert.assertThat(PersistableTest.PERSISTABLE.getId(), Matchers.equalTo(PersistableTest.ID));
  }

  @Test
  public void testWriteTo() throws Exception {
    final TestPojo pojo = new TestPojo();

    final TestPojo written = PersistableTest.PERSISTABLE.writeTo(pojo);

    Assert.assertThat(written, Matchers.sameInstance(pojo));
    Assert.assertThat(pojo.getId(), Matchers.equalTo(PersistableTest.ID));
  }

  @Test
  public void testWriteToWithNullId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(".*missing.*" + PersistableTest.TYPE + ".*id.*"));

    final TestPojo pojo = new TestPojo();
    final TestPersistable persistable = new TestPersistable("type", null);

    persistable.writeTo(pojo);
  }

  @Test
  public void testWriteToWithEmptyId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*" + PersistableTest.TYPE + ".*id.*"));

    final TestPojo pojo = new TestPojo();
    final TestPersistable persistable = new TestPersistable("type", "");

    persistable.writeTo(pojo);
  }

  @Test
  public void testReadFrom() throws Exception {
    final TestPojo pojo =
        new TestPojo().setId(PersistableTest.ID).setVersion(TestPojo.CURRENT_VERSION);
    final TestPersistable persistable = new TestPersistable();

    persistable.readFrom(pojo);

    Assert.assertThat(persistable.getId(), Matchers.equalTo(PersistableTest.ID));
  }

  @Test
  public void testReadFromWithNullId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(".*missing.*" + PersistableTest.TYPE + ".*id.*"));

    final TestPojo pojo = new TestPojo().setId(null).setVersion(TestPojo.CURRENT_VERSION);
    final TestPersistable persistable = new TestPersistable();

    persistable.readFrom(pojo);
  }

  @Test
  public void testReadFromWithEmptyId() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern(".*empty.*" + PersistableTest.TYPE + ".*id.*"));

    final TestPojo pojo = new TestPojo().setId("").setVersion(TestPojo.CURRENT_VERSION);
    final TestPersistable persistable = new TestPersistable();

    persistable.readFrom(pojo);
  }

  @Test
  public void testConvertAndSet() throws Exception {
    final TestPojo pojo = new TestPojo().setString("false");

    PersistableTest.PERSISTABLE.convertAndSet(
        "field", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testSetOrFailIfNullWhenNullAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " string.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString(null);

    PersistableTest.PERSISTABLE.setOrFailIfNull(
        "string", pojo::getString, PersistableTest.PERSISTABLE::setString);
  }

  @Test
  public void testSetOrFailIfNullWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern("missing " + PersistableTest.TYPE + " string$"));

    final TestPojo pojo = new TestPojo().setString(null);
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.setOrFailIfNull("string", pojo::getString, persistable::setString);
  }

  @Test
  public void testSetOrFailIfNullWhenValid() throws Exception {
    final TestPojo pojo = new TestPojo().setString(PersistableTest.STRING);

    PersistableTest.PERSISTABLE.setOrFailIfNull(
        "string-dummy", pojo::getString, PersistableTest.PERSISTABLE::setString);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getString(), Matchers.equalTo(PersistableTest.STRING));
  }

  @Test
  public void testSetOrFailIfNullOrEmptyWhenNullAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " string.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString(null);

    PersistableTest.PERSISTABLE.setOrFailIfNullOrEmpty(
        "string", pojo::getString, PersistableTest.PERSISTABLE::setString);
  }

  @Test
  public void testSetOrFailIfNullOrEmptyWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern("missing " + PersistableTest.TYPE + " string$"));

    final TestPojo pojo = new TestPojo().setString(null);
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.setOrFailIfNullOrEmpty("string", pojo::getString, persistable::setString);
  }

  @Test
  public void testSetOrFailIfNullOrEmptyWhenEmptyAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "empty " + PersistableTest.TYPE + " string.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString("");

    PersistableTest.PERSISTABLE.setOrFailIfNullOrEmpty(
        "string", pojo::getString, PersistableTest.PERSISTABLE::setString);
  }

  @Test
  public void testSetOrFailIfEmptyOrEmptyWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern("empty " + PersistableTest.TYPE + " string$"));

    final TestPojo pojo = new TestPojo().setString("");
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.setOrFailIfNullOrEmpty("string", pojo::getString, persistable::setString);
  }

  @Test
  public void testSetOrFailIfNullOrEmptyWhenValid() throws Exception {
    final TestPojo pojo = new TestPojo().setString(PersistableTest.STRING);

    PersistableTest.PERSISTABLE.setOrFailIfNullOrEmpty(
        "string-dummy", pojo::getString, PersistableTest.PERSISTABLE::setString);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getString(), Matchers.equalTo(PersistableTest.STRING));
  }

  @Test
  public void testConvertAndSetOrFailIfNullOrEmpty() throws Exception {
    final TestPojo pojo = new TestPojo().setString("false");

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfNullWhenNullAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " bool.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString(null);

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfNullWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " bool.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString(null);
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfNullWhenValid() throws Exception {
    final TestPojo pojo = new TestPojo().setString("true");

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool-dummy", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);

    Assert.assertThat(PersistableTest.PERSISTABLE.getBool(), Matchers.equalTo(true));
  }

  @Test
  public void testConvertAndSetOrFailIfNullOrEmptyWhenNullAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " bool.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString(null);

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testConvertAndSetSetOrFailIfNullOrEmptyWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern("missing " + PersistableTest.TYPE + " bool"));

    final TestPojo pojo = new TestPojo().setString(null);
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, persistable::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfNullOrEmptyWhenEmptyAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "empty " + PersistableTest.TYPE + " bool.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setString("");

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfEmptyOrEmptyWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern("empty " + PersistableTest.TYPE + " bool$"));

    final TestPojo pojo = new TestPojo().setString("");
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.convertAndSetOrFailIfNullOrEmpty(
        "bool", pojo::getString, Boolean::parseBoolean, persistable::setBool);
  }

  @Test
  public void testConvertAndSetOrFailIfNullOrEmptyWhenValid() throws Exception {
    final TestPojo pojo = new TestPojo().setString("true");

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNullOrEmpty(
        "bool-dummy", pojo::getString, Boolean::parseBoolean, PersistableTest.PERSISTABLE::setBool);

    Assert.assertThat(PersistableTest.PERSISTABLE.getBool(), Matchers.equalTo(true));
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullWhenNullAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "missing " + PersistableTest.TYPE + " enum.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setEnum(null);

    PersistableTest.PERSISTABLE.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullWhenNullAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern("missing " + PersistableTest.TYPE + " enum.*$"));

    final TestPojo pojo = new TestPojo().setEnum(null);
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullWhenEmptyAndIdNotNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(
        Matchers.matchesPattern(
            "empty " + PersistableTest.TYPE + " enum.*: " + PersistableTest.ID + "$"));

    final TestPojo pojo = new TestPojo().setEnum("");

    PersistableTest.PERSISTABLE.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullWhenEmptyAndIdIsNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern("empty " + PersistableTest.TYPE + " enum.*$"));

    final TestPojo pojo = new TestPojo().setEnum("");
    final TestPersistable persistable = new TestPersistable(PersistableTest.TYPE, null);

    persistable.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullOrEmptyWhenValid() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum(TestEnum.ENUM_A.name());

    PersistableTest.PERSISTABLE.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum-dummy",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_A));
  }

  @Test
  public void testConvertAndSetEnumValueOrFailIfNullOrEmptyWithNewValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum("what-is-this");

    PersistableTest.PERSISTABLE.convertAndSetEnumValueOrFailIfNullOrEmpty(
        "enum-dummy",
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetEnumValueWhenNotSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum(TestEnum.ENUM_A.name());

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class, TestEnum.ENUM_UNKNOWN, pojo::getEnum, PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_A));
  }

  @Test
  public void testConvertAndSetEnumValueWithNewValueWhenNotSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum("what-is-this");

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class, TestEnum.ENUM_UNKNOWN, pojo::getEnum, PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetEnumValueWithNullWhenNotSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum(null);

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class, TestEnum.ENUM_UNKNOWN, pojo::getEnum, PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetEnumValueWithEmptyWhenNotSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum("");

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class, TestEnum.ENUM_UNKNOWN, pojo::getEnum, PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetEnumValueWhenSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum(TestEnum.ENUM_A.name());

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_A));
  }

  @Test
  public void testConvertAndSetEnumValueWithNewValueWhenSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum("what-is-this");

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetEnumValueWithNullWhenSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum(null);

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class,
        null,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(PersistableTest.PERSISTABLE.getEnum(), Matchers.nullValue());
  }

  @Test
  public void testConvertAndSetEnumValueWithEmptyWhenSpecifyingANullValue() throws Exception {
    final TestPojo pojo = new TestPojo().setEnum("");

    PersistableTest.PERSISTABLE.convertAndSetEnumValue(
        TestEnum.class,
        TestEnum.ENUM_UNKNOWN,
        TestEnum.ENUM_UNKNOWN,
        pojo::getEnum,
        PersistableTest.PERSISTABLE::setEnum);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.getEnum(), Matchers.equalTo(TestEnum.ENUM_UNKNOWN));
  }

  @Test
  public void testConvertAndSetOrFailIfNull() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.matchesPattern("missing.*url.*$"));

    final TestPojo pojo = new TestPojo().setUrl(null);

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNull(
        "url", pojo::getUrl, PersistableTest::toUrl, PersistableTest.PERSISTABLE::setUrl);
  }

  @Test
  public void testConvertAndSetOrFailIfInvalidUrl() throws Exception {
    exception.expect(InvalidFieldException.class);
    exception.expectMessage(Matchers.equalTo("testing invalid url"));
    exception.expectCause(Matchers.instanceOf(MalformedURLException.class));

    final TestPojo pojo = new TestPojo().setUrl("invalid-url");

    PersistableTest.PERSISTABLE.convertAndSetOrFailIfNull(
        "url", pojo::getUrl, PersistableTest::toUrl, PersistableTest.PERSISTABLE::setUrl);
  }

  @Test
  public void testHasUnknowns() throws Exception {
    Assert.assertThat(PersistableTest.PERSISTABLE.hasUnknowns(), Matchers.equalTo(false));
  }

  @Test
  public void testHashCodeWhenEquals() throws Exception {
    final TestPersistable persistable2 =
        new TestPersistable(PersistableTest.TYPE, PersistableTest.ID);

    Assert.assertThat(
        PersistableTest.PERSISTABLE.hashCode(), Matchers.equalTo(persistable2.hashCode()));
  }

  @Test
  public void testHashCodeWhenDifferent() throws Exception {
    final TestPersistable persistable2 =
        new TestPersistable(PersistableTest.TYPE, PersistableTest.ID + "2");

    Assert.assertThat(
        PersistableTest.PERSISTABLE.hashCode(),
        Matchers.not(Matchers.equalTo(persistable2.hashCode())));
  }

  @Test
  public void testEqualsWhenEquals() throws Exception {
    final TestPersistable persistable2 =
        new TestPersistable(PersistableTest.TYPE, PersistableTest.ID);

    Assert.assertThat(PersistableTest.PERSISTABLE.equals(persistable2), Matchers.equalTo(true));
  }

  @Test
  public void testEqualsWhenIdentical() throws Exception {
    Assert.assertThat(
        PersistableTest.PERSISTABLE.equals(PersistableTest.PERSISTABLE), Matchers.equalTo(true));
  }

  @SuppressWarnings("PMD.EqualsNull" /* purposely testing equals() when called with null */)
  @Test
  public void testEqualsWhenNull() throws Exception {
    Assert.assertThat(PersistableTest.PERSISTABLE.equals(null), Matchers.equalTo(false));
  }

  @SuppressWarnings(
      "PMD.PositionLiteralsFirstInComparisons" /* purposely testing equals() when call with something else than expected */)
  @Test
  public void testEqualsWhenNotARequestInfoImpl() throws Exception {
    Assert.assertThat(PersistableTest.PERSISTABLE.equals("test"), Matchers.equalTo(false));
  }

  @Test
  public void testEqualsWhenIdIsDifferent() throws Exception {
    final TestPersistable persistable2 =
        new TestPersistable(PersistableTest.TYPE, PersistableTest.ID + "2");

    Assert.assertThat(
        PersistableTest.PERSISTABLE.equals(persistable2), Matchers.not(Matchers.equalTo(true)));
  }

  private static URL toUrl(String url) throws InvalidFieldException {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new InvalidFieldException("testing invalid url", e);
    }
  }

  private static class TestPersistable extends Persistable<TestPojo> {
    @Nullable private String string;
    @Nullable private TestEnum anEnum;
    @Nullable private URL url;
    private boolean bool;

    TestPersistable() {
      super(PersistableTest.TYPE);
    }

    TestPersistable(String type) {
      super(type);
    }

    TestPersistable(String type, @Nullable String id) {
      super(type, id);
    }

    @Nullable
    public String getString() {
      return string;
    }

    public void setString(@Nullable String string) {
      this.string = string;
    }

    @Nullable
    public TestEnum getEnum() {
      return anEnum;
    }

    public void setEnum(@Nullable TestEnum anEnum) {
      this.anEnum = anEnum;
    }

    @Nullable
    public URL getUrl() {
      return url;
    }

    public void setUrl(@Nullable URL url) {
      this.url = url;
    }

    public boolean getBool() {
      return bool;
    }

    public void setBool(boolean bool) {
      this.bool = bool;
    }
  }

  private static class TestPojo extends Pojo<TestPojo> {
    public static final int CURRENT_VERSION = 1;

    @Nullable private String string;
    @Nullable private String anEnum;
    @Nullable private String url;

    public TestPojo() {
      super.setVersion(TestPojo.CURRENT_VERSION);
    }

    @Nullable
    public String getString() {
      return string;
    }

    public TestPojo setString(@Nullable String string) {
      this.string = string;
      return this;
    }

    @Nullable
    public String getEnum() {
      return anEnum;
    }

    public TestPojo setEnum(@Nullable String anEnum) {
      this.anEnum = anEnum;
      return this;
    }

    @Nullable
    public String getUrl() {
      return url;
    }

    public TestPojo setUrl(@Nullable String url) {
      this.url = url;
      return this;
    }
  }

  private static enum TestEnum {
    ENUM_A,
    ENUM_B,
    ENUM_UNKNOWN
  }
}
