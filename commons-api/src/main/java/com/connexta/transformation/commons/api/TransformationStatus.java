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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * An interface for classes which track the status of transformation processes. A
 * TransformationStatus contains information about the original transformation request and the ID of
 * the transformation the status is associated with.
 */
public interface TransformationStatus {
  /**
   * Returns the ID of the transformation that this status is associated with.
   *
   * @return the ID of the transformation that this status is associated with
   */
  String getTransformId();

  /**
   * Returns information about the request that started this transformation.
   *
   * @return information about the request that started this transformation
   */
  RequestInfo getRequestInfo();

  /**
   * Returns the start time of the transformation.
   *
   * @return the time the transformation started
   */
  Instant getStartTime();

  /**
   * Returns the completion time of the transformation.
   *
   * @return the completion time of the transformation
   */
  Optional<Instant> getCompletionTime();

  /**
   * Gets the current state of the transformation.
   *
   * @return the current transformation state
   */
  State getState();

  /**
   * Returns the duration of the transformation. If the transformation is ongoing, the difference
   * between the start time and now will be returned, otherwise, the difference between the start
   * and completion times will be returned.
   *
   * @return the duration of the transformation.
   */
  Duration getDuration();

  /**
   * Checks if the associated transformation has been deleted.
   *
   * @return <code>true</code> if the associated transformation has been deleted; <code>false</code>
   *     otherwise
   */
  boolean isDeleted();

  /**
   * Returns true if the transformation has finished, otherwise, returns false.
   *
   * @return true if the transformation has been completed, otherwise false
   */
  default boolean isCompleted() {
    final State state = getState();

    return state == State.SUCCESSFUL || state == State.FAILED;
  }

  /**
   * Returns true if the transformation failed, otherwise, returns false.
   *
   * @return true if the transformation failed, otherwise false
   */
  default boolean hasFailed() {
    return getState() == State.FAILED;
  }

  /**
   * Checks if this transformation has successfully completed.
   *
   * @return true if the transformation completed successfully, otherwise false
   */
  default boolean wasSuccessful() {
    return getState() == State.SUCCESSFUL;
  }

  /**
   * Checks if the transformation is in an unknown state.
   *
   * @return true if the transformation is in an unknown state, otherwise false
   */
  default boolean isUnknown() {
    return getState() == State.UNKNOWN;
  }

  /** The various states a transformation can be in. */
  enum State {
    /** Indicates the transformation is still active. */
    IN_PROGRESS,

    /** Indicates the transformation failed. */
    FAILED,

    /** Indicates the transformation has successfully completed. */
    SUCCESSFUL,

    /**
     * The unknown value is used for forward compatibility where the current code might not be able
     * to understand a new state and would map this new state to <code>UNKNOWN</code> and most
     * likely ignore it.
     */
    UNKNOWN;

    /**
     * Reduces two states into one.
     *
     * @param state the first state
     * @param state2 the second state
     * @return the aggregate state of the two given
     */
    public static State reduce(State state, State state2) {
      if (state == IN_PROGRESS || state2 == IN_PROGRESS) {
        return State.IN_PROGRESS;
      } else if (state == UNKNOWN || state2 == UNKNOWN) {
        return State.UNKNOWN;
      } else if (state == SUCCESSFUL && state2 == SUCCESSFUL) {
        return State.SUCCESSFUL;
      }
      return State.FAILED;
    }
  }
}
