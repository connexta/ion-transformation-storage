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
package com.connexta.transformation.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class provides a neat way for wrapping code that declares they throw checked exceptions from
 * within Java streaming or mapping methods where checked exceptions are not allowed. Once the code
 * used is wrapped using one of the <code>wrap()</code> it can then be unwrap back to the original
 * checked exception outside of the streaming chain.
 */
public final class ExceptionHandler {
  /** Prevents instantiation of this class. */
  private ExceptionHandler() {}

  /**
   * Provides a function that wraps around another one where checked exceptions will be wrapped in
   * such a way that they can later be unwrapped using {@link #unwrap(UnwrappingCallable)}.
   *
   * <p><i>Note:</i> Only checked exceptions are wrapped. Runtime exceptions and errors will
   * continue to bubble out as normal.
   *
   * @param <P> the type of parameter for the function
   * @param <R> the type for the result from the function
   * @param function the function to invoke an wrap checked exceptions from
   * @return a function that will wrap all checked exceptions from the provided function
   */
  public static <P, R> Function<P, R> wrap(WrappingFunction<P, R> function) {
    return p -> {
      try {
        return function.apply(p);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new WrappedException(e);
      }
    };
  }

  /**
   * Provides a supplier that wraps around another one where checked exceptions will be wrapped in
   * such a way that they can later be unwrapped using {@link #unwrap(UnwrappingCallable)}.
   *
   * <p><i>Note:</i> Only checked exceptions are wrapped. Runtime exceptions and errors will
   * continue to bubble out as normal.
   *
   * @param <R> the type for the result from the supplier
   * @param supplier the supplier to invoke an wrap checked exceptions from
   * @return a supplier that will wrap all checked exceptions from the provided supplier
   */
  public static <R> Supplier<R> wrap(WrappingSupplier<R> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new WrappedException(e);
      }
    };
  }

  /**
   * Provides a callable that wraps around another one where checked exceptions will be wrapped in
   * such a way that they can later be unwrapped using {@link #unwrap(UnwrappingCallable)}.
   *
   * <p><i>Note:</i> Only checked exceptions are wrapped. Runtime exceptions and errors will
   * continue to bubble out as normal.
   *
   * @param <R> the type for the result from the callable
   * @param callable the callable to invoke an wrap checked exceptions from
   * @return a callable that will wrap all checked exceptions from the provided callable
   */
  public static <R> Callable<R> wrap(WrappingCallable<R> callable) {
    return () -> {
      try {
        return callable.call();
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new WrappedException(e);
      }
    };
  }

  /**
   * Provides a consumer that wraps around another one where checked exceptions will be wrapped in
   * such a way that they can later be unwrapped using {@link #unwrap(UnwrappingRunnable)}.
   *
   * <p><i>Note:</i> Only checked exceptions are wrapped. Runtime exceptions and errors will
   * continue to bubble out as normal.
   *
   * @param <P> the type for the value passed to the consumer
   * @param consumer the consumer to invoke an wrap checked exceptions from
   * @return a consumer that will wrap all checked exceptions from the provided consumer
   */
  public static <P> Consumer<P> wrap(WrappingConsumer<P> consumer) {
    return p -> {
      try {
        consumer.accept(p);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new WrappedException(e);
      }
    };
  }

  /**
   * Provides a runnable that wraps around another one where checked exceptions will be wrapped in
   * such a way that they can later be unwrapped using {@link #unwrap(UnwrappingRunnable)}.
   *
   * <p><i>Note:</i> Only checked exceptions are wrapped. Runtime exceptions and errors will
   * continue to bubble out as normal.
   *
   * @param run the runnable to invoke an wrap checked exceptions from
   * @return a runnable that will wrap all checked exceptions from the provided runnable
   */
  public static Runnable wrap(WrappingRunnable run) {
    return () -> {
      try {
        run.run();
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new WrappedException(e);
      }
    };
  }

  /**
   * Unwraps checked exceptions that would have been wrapped by one of the <code>wrap()</code>>
   * methods invoked by the specified code.
   *
   * <p><i>Note:</i> This version of the <code>unwrap()</code> methods allows a result to be
   * retrieved from the specified code.
   *
   * @param <T> the type of checked exception to be unwrapped
   * @param <R> the type of result to be returned from the specified code
   * @param callable the code to execute and unwrap any wrapped exceptions thrown out from
   * @return the result from the code
   * @throws T the checked exception that is wrapped somewhere in the execution of the specified
   *     callable
   */
  public static <T extends Exception, R> R unwrap(UnwrappingCallable<R> callable) throws T {
    try {
      return callable.call();
    } catch (WrappedException e) {
      throw (T) e.getCause();
    }
  }

  /**
   * Unwraps checked exceptions that would have been wrapped by one of the <code>wrap()</code>>
   * methods invoked by the specified code.
   *
   * <p><i>Note:</i> This version of the <code>unwrap()</code> methods allows a result to be
   * retrieved from the specified callable.
   *
   * @param <T> the type of checked exception to be unwrapped
   * @param run the code to execute and unwrap any wrapped exceptions thrown out from
   * @throws T the checked exception that is wrapped somewhere in the execution of the specified
   *     code
   */
  public static <T extends Exception> void unwrap(UnwrappingRunnable run) throws T {
    try {
      run.run();
    } catch (WrappedException e) {
      throw (T) e.getCause();
    }
  }

  /**
   * Internal functional interface representing code from which to unwrap checked exceptions that
   * would have been wrapped or return normal results from.
   */
  @FunctionalInterface
  public interface UnwrappingCallable<R> {
    /**
     * Computes a result.
     *
     * @return computed result
     */
    R call();
  }

  /**
   * Internal functional interface representing code from which to unwrap checked exceptions that
   * would have been wrapped.
   */
  @FunctionalInterface
  public interface UnwrappingRunnable {
    /** Executes some code. */
    void run();
  }

  @FunctionalInterface
  public interface WrappingFunction<P, R> {
    /**
     * Applies this function to the given argument.
     *
     * @param p the function argument
     * @return the function result
     * @throws Exception if a failure occurs while applying this function
     */
    public R apply(P p) throws Exception;
  }

  public interface WrappingCallable<R> {
    /**
     * Computes a result.
     *
     * @return computed result
     * @throws Exception if a failure occurs while calling this callable
     */
    R call() throws Exception;
  }

  @FunctionalInterface
  public interface WrappingSupplier<R> {
    /**
     * Gets a result.
     *
     * @return a result
     * @throws Exception if a failure occurs while retrieving the result from the supplier
     */
    R get() throws Exception;
  }

  @FunctionalInterface
  public interface WrappingConsumer<P> {
    /**
     * Performs this operation on the given argument.
     *
     * @param p the input argument
     * @throws Exception if a failure occurs while performing the operation
     */
    void accept(P p) throws Exception;
  }

  @FunctionalInterface
  public interface WrappingRunnable {
    /** Executes some code. */
    void run() throws Exception;
  }

  /**
   * Internal runtime exception used to translate a checked exception into a runtime one so it can
   * be unwrapped later.
   */
  private static class WrappedException extends RuntimeException {
    public WrappedException(Exception cause) {
      super(cause);
    }
  }
}
