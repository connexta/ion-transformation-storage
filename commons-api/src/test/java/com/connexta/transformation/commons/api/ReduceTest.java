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
package com.connexta.transformation.commons.api;

import static org.junit.Assert.assertEquals;

import com.connexta.transformation.commons.api.TransformationStatus.State;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ReduceTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {
          {State.SUCCESSFUL, State.SUCCESSFUL, State.SUCCESSFUL},
          {State.SUCCESSFUL, State.IN_PROGRESS, State.IN_PROGRESS},
          {State.SUCCESSFUL, State.FAILED, State.FAILED},
          {State.SUCCESSFUL, State.UNKNOWN, State.UNKNOWN},
          {State.IN_PROGRESS, State.SUCCESSFUL, State.IN_PROGRESS},
          {State.IN_PROGRESS, State.IN_PROGRESS, State.IN_PROGRESS},
          {State.IN_PROGRESS, State.FAILED, State.IN_PROGRESS},
          {State.IN_PROGRESS, State.UNKNOWN, State.IN_PROGRESS},
          {State.FAILED, State.SUCCESSFUL, State.FAILED},
          {State.FAILED, State.IN_PROGRESS, State.IN_PROGRESS},
          {State.FAILED, State.FAILED, State.FAILED},
          {State.FAILED, State.UNKNOWN, State.UNKNOWN},
          {State.UNKNOWN, State.SUCCESSFUL, State.UNKNOWN},
          {State.UNKNOWN, State.IN_PROGRESS, State.IN_PROGRESS},
          {State.UNKNOWN, State.FAILED, State.UNKNOWN},
          {State.UNKNOWN, State.UNKNOWN, State.UNKNOWN}
        });
  }

  @Parameter public State state;

  @Parameter(1)
  public State state2;

  @Parameter(2)
  public State expected;

  @Test
  public void reduce() {
    assertEquals(expected, State.reduce(state, state2));
  }
}
