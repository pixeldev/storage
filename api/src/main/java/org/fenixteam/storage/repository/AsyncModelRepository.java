/*
 * This file is part of storage, licensed under the MIT License
 *
 * Copyright (c) 2023 FenixTeam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.fenixteam.storage.repository;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.fenixteam.storage.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is the base for all the asynchronous repositories, it contains the essential methods to
 * interact with the database, cache, or whatever you want to use to store your data using {@link CompletableFuture}
 * to handle when the operations are finished successfully or not. It's important
 * to note that this class is not a singleton, so you can create as many instances as you want.
 *
 * @param <ModelType> The {@link Model} type that this repository will handle.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AsyncModelRepository<ModelType extends Model> implements ModelRepository<ModelType> {
  protected final Executor executor;

  /**
   * Creates a new {@link AsyncModelRepository} with the specified {@link Executor}.
   *
   * @param executor The {@link Executor} that will be used to execute the asynchronous operations.
   * @since 1.0.0
   */
  public AsyncModelRepository(final @NotNull Executor executor) {
    this.executor = executor;
  }

  /**
   * This method executes and wraps the {@link #deleteAll()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #deleteAll()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> deleteAllAsync() {
    return CompletableFuture.runAsync(this::deleteAll, this.executor);
  }

  /**
   * This method executes and wraps the {@link #deleteAndRetrieve(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return A {@link CompletableFuture} that will complete with the deleted {@link ModelType}, or {@code null} if it doesn't exist.
   * @see #deleteAndRetrieve(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> deleteAndRetrieveAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteAndRetrieve(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #delete(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return A {@link CompletableFuture} that will complete with true if the {@link ModelType} was deleted successfully, false otherwise.
   * @see #delete(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.delete(id), this.executor);
  }

  /**
   * Returns the {@link Executor} that will be used to execute the asynchronous operations.
   *
   * @return The {@link Executor} that will be used to execute the asynchronous operations.
   * @see CompletableFuture#runAsync(Runnable, Executor)
   * @see CompletableFuture#supplyAsync(Supplier, Executor)
   * @since 1.0.0
   */
  public @NotNull Executor executor() {
    return this.executor;
  }

  /**
   * This method executes and wraps the {@link #find(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #find(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.exists(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #find(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #find(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> findAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.find(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findIds()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the ids of the repository.
   * @see #findIds()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable Collection<@NotNull String>> findIdsAsync() {
    return CompletableFuture.supplyAsync(this::findIds, this.executor);
  }

  /**
   * This method executes and wraps the {@link #findIds(IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the ids of the repository.
   * @see #findIds(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull String>> @NotNull CompletableFuture<@Nullable C> findIdsAsync(final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findIds(factory), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findAll(IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see #findAll(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @NotNull CompletableFuture<@Nullable C> findAllAsync(final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAll(factory), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findAll(Consumer, IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see #findAll(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @NotNull CompletableFuture<@Nullable C> findAllAsync(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAll(postLoadAction, factory), this.executor);
  }

  /**
   * This method iterates over all the {@link ModelType}s in the repository and executes the specified action for each one
   * in a {@link CompletableFuture} with the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #forEach(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<Void> forEachAsync(final @NotNull Consumer<? super @NotNull ModelType> action) {
    return CompletableFuture.runAsync(() -> this.forEach(action), this.executor);
  }

  /**
   * This method iterates over all the {@link ModelType}s id's in the repository and executes the specified action for each one
   * in a {@link CompletableFuture} with the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #forEachIds(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<Void> forEachIdsAsync(final @NotNull Consumer<? super String> action) {
    return CompletableFuture.runAsync(() -> this.forEachIds(action), this.executor);
  }

  /**
   * This method executes and wraps the {@link #save(Model)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param model The {@link ModelType} to save.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} that was saved.
   * @see #save(Model)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull ModelType> saveAsync(final @NotNull ModelType model) {
    return CompletableFuture.supplyAsync(() -> this.save(model), this.executor);
  }
}
