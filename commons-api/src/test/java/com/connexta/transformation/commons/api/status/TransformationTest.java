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
package com.connexta.transformation.commons.api.status;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.connexta.transformation.commons.api.status.TransformationStatus.State;
import com.github.npathai.hamcrestopt.OptionalMatchers;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class TransformationTest {

  private final List<MetadataTransformation> metadata = new ArrayList<>();

  private final Transformation transformation = mockTransformation(metadata);

  // lets us use Mocktio.CALLS_REAL_METHODS which will call the implemented methods and mock others
  // normally
  abstract class TestMetadataTransformation implements MetadataTransformation {}

  // Creates Metadatas and automatically adds them to the metadata List.
  private MetadataTransformation createMetadata(State state, String type) {
    MetadataTransformation metadataTransformation =
        mock(
            TestMetadataTransformation.class,
            Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS));
    when(metadataTransformation.getMetadataType()).thenReturn(type);
    when(metadataTransformation.getState()).thenReturn(state);
    this.metadata.add(metadataTransformation);
    return metadataTransformation;
  }

  abstract class TestTransformation implements Transformation {}

  private Transformation mockTransformation(List<MetadataTransformation> metadata) {
    TestTransformation transformInfo =
        mock(
            TestTransformation.class,
            Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS));
    when(transformInfo.metadatas()).thenAnswer((Answer) invocation -> metadata.stream());
    return transformInfo;
  }

  @Test
  public void metadataTypes() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    List<String> types = transformation.metadataTypes().collect(Collectors.toList());
    assertThat(types.get(0), Matchers.equalTo("one"));
    assertThat(types.get(1), Matchers.equalTo("two"));
  }

  @Test
  public void getStateNoMetadataMeansInProgress() {
    assertThat(transformation.getState(), Matchers.equalTo(State.IN_PROGRESS));
  }

  @Test
  public void getStateUnknown() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.UNKNOWN, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertThat(transformation.getState(), Matchers.equalTo(State.UNKNOWN));
  }

  @Test
  public void getStateInProgress() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.FAILED, "two");
    createMetadata(State.IN_PROGRESS, "three");
    assertThat(transformation.getState(), Matchers.equalTo(State.IN_PROGRESS));
  }

  @Test
  public void getStateFailure() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.FAILED, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertThat(transformation.getState(), Matchers.equalTo(State.FAILED));
  }

  @Test
  public void getState() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertThat(transformation.getState(), Matchers.equalTo(State.SUCCESSFUL));
  }

  @Test
  public void getStateNoMetadata() {
    assertThat(transformation.getState(), Matchers.equalTo(State.IN_PROGRESS));
  }

  @Test
  public void isCompletedTrue() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertTrue(transformation.isCompleted());
  }

  @Test
  public void isCompletedFalse() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.IN_PROGRESS, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertFalse(transformation.isCompleted());
  }

  @Test
  public void hasFailedTrue() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.FAILED, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertTrue(transformation.hasFailed());
  }

  @Test
  public void hasFailedFalse() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertFalse(transformation.hasFailed());
  }

  @Test
  public void hasFailedWhenInProgress() {
    createMetadata(State.FAILED, "one");
    createMetadata(State.FAILED, "two");
    createMetadata(State.IN_PROGRESS, "three");
    assertFalse(transformation.hasFailed());
  }

  @Test
  public void hasFailedWithUnknown() {
    createMetadata(State.FAILED, "one");
    createMetadata(State.UNKNOWN, "two");
    createMetadata(State.FAILED, "three");
    assertFalse(transformation.hasFailed());
  }

  @Test
  public void wasSuccessfulTrue() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.SUCCESSFUL, "three");
    assertTrue(transformation.wasSuccessful());
  }

  @Test
  public void wasSuccessfulFalse() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.IN_PROGRESS, "three");
    assertFalse(transformation.wasSuccessful());
  }

  @Test
  public void isUnknownTrue() {
    createMetadata(State.UNKNOWN, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.FAILED, "three");
    assertTrue(transformation.isUnknown());
  }

  @Test
  public void isUnknownFalse() {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.SUCCESSFUL, "two");
    createMetadata(State.FAILED, "three");
    assertFalse(transformation.isUnknown());
  }

  @Test
  public void isUnknownWhenInProgress() {
    createMetadata(State.UNKNOWN, "one");
    createMetadata(State.FAILED, "two");
    createMetadata(State.IN_PROGRESS, "three");
    assertFalse(transformation.isUnknown());
  }

  @Test
  public void testCompletionTimeWhenCompleted() throws Exception {
    when(createMetadata(State.SUCCESSFUL, "one").getCompletionTime())
        .thenReturn(Optional.of(Instant.ofEpochMilli(1L)));
    when(createMetadata(State.FAILED, "two").getCompletionTime())
        .thenReturn(Optional.of(Instant.ofEpochMilli(3L)));
    when(createMetadata(State.SUCCESSFUL, "three").getCompletionTime())
        .thenReturn(Optional.of(Instant.ofEpochMilli(2L)));

    Assert.assertThat(
        transformation.getCompletionTime(),
        OptionalMatchers.isPresentAndIs(Instant.ofEpochMilli(3L)));
  }

  @Test
  public void testCompletionTimeWhenNotCompleted() throws Exception {
    createMetadata(State.SUCCESSFUL, "one");
    createMetadata(State.IN_PROGRESS, "two");
    createMetadata(State.SUCCESSFUL, "three");

    Assert.assertThat(transformation.getCompletionTime(), OptionalMatchers.isEmpty());
  }
}
