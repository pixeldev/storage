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
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.fenixteam.storage.model.Model;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is a {@link ModelRepository} that uses two {@link ModelRepository}s to store the {@link ModelType}s.
 * The first one is the {@link #mainModelRepository}, which is used to store the {@link ModelType}s in a persistent way.
 * The second one is the {@link #fallbackModelRepository}, which is mainly used to cache the {@link ModelType}s.
 *
 * @param <ModelType> The type of the {@link Model} that this {@link ModelRepository} will store.
 * @see ModelRepository
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class WithFallbackModelRepository<ModelType extends Model> extends AsyncModelRepository<ModelType> {
  /**
   * The {@link ModelRepository} that will be used as a fallback if the {@link #mainModelRepository} doesn't have the
   * {@link ModelType} that we're looking for. Mainly used to cache the {@link ModelType}s.
   */
  protected final ModelRepository<ModelType> fallbackModelRepository;
  /**
   * The {@link ModelRepository} that will be used as the main repository. Mainly used to store the {@link ModelType}s
   * in a persistent way.
   */
  protected final ModelRepository<ModelType> mainModelRepository;

  /**
   * This constructor creates a new {@link WithFallbackModelRepository} with the specified {@link Executor}, {@link ModelRepository}
   * as the {@link #fallbackModelRepository} and {@link #mainModelRepository}.
   *
   * @param executor                The {@link Executor} to use.
   * @param fallbackModelRepository The {@link ModelRepository} to use as the {@link #fallbackModelRepository}.
   * @param mainModelRepository     The {@link ModelRepository} to use as the {@link #mainModelRepository}.
   * @since 1.0.0
   */
  public WithFallbackModelRepository(final @NotNull Executor executor, final @NotNull ModelRepository<ModelType> fallbackModelRepository, final @NotNull ModelRepository<ModelType> mainModelRepository) {
    super(executor);
    this.fallbackModelRepository = fallbackModelRepository;
    this.mainModelRepository = mainModelRepository;
  }

  /**
   * This method uses the {@link ModelRepository#delete(String)} of the {@link #mainModelRepository} to delete the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link Model} to delete.
   * @return {@code true} if the {@link ModelType} was deleted successfully, {@code false} otherwise.
   * @see ModelRepository#delete(String)
   * @since 1.0.0
   */
  @Override
  public boolean delete(final @NotNull String id) {
    return this.mainModelRepository.delete(id);
  }

  /**
   * This method uses the {@link ModelRepository#deleteAll()} of the {@link #mainModelRepository} to delete all the
   * {@link ModelType}s in the {@link #mainModelRepository}.
   *
   * @see ModelRepository#deleteAll()
   * @since 1.0.0
   */
  @Override
  public void deleteAll() {
    this.mainModelRepository.deleteAll();
  }

  /**
   * This method uses the {@link ModelRepository#deleteAll()} of the {@link #fallbackModelRepository} to delete all the
   * {@link ModelType}s in the repository.
   *
   * @see ModelRepository#deleteAll()
   * @since 1.0.0
   */
  public void deleteAllInFallback() {
    this.fallbackModelRepository.deleteAll();
  }

  /**
   * This method executes and wraps the {@link #deleteAllInFallback()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete when all the {@link ModelType}s in the repository are deleted.
   * @see #deleteAllInFallback()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> deleteAllInFallbackAsync() {
    return CompletableFuture.runAsync(this::deleteAllInFallback, this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#deleteAndRetrieve(String)} of the {@link #mainModelRepository} to delete the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link Model} to delete.
   * @return The deleted {@link ModelType}, or {@code null} if it doesn't exist.
   * @see ModelRepository#deleteAndRetrieve(String)
   * @since 1.0.0
   */
  @Override
  public @Nullable ModelType deleteAndRetrieve(final @NotNull String id) {
    return this.mainModelRepository.deleteAndRetrieve(id);
  }

  /**
   * This method uses the {@link ModelRepository#deleteAndRetrieve(String)} of the {@link #fallbackModelRepository} to delete the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return The deleted {@link ModelType}, or {@code null} if it doesn't exist.
   * @see ModelRepository#deleteAndRetrieve(String)
   * @since 1.0.0
   */
  public @Nullable ModelType deleteAndRetrieveInFallback(final @NotNull String id) {
    return this.fallbackModelRepository.deleteAndRetrieve(id);
  }

  /**
   * This method executes and wraps the {@link #deleteAndRetrieveInFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return A {@link CompletableFuture} that will complete with the deleted {@link ModelType}, or {@code null} if it doesn't exist.
   * @see #deleteAndRetrieveInFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> deleteAndRetrieveInFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteAndRetrieveInFallback(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#delete(String)} of the {@link #fallbackModelRepository} and the
   * {@link ModelRepository#delete(String)} of the {@link #mainModelRepository} to delete the {@link ModelType}
   * with the specified id.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return {@code true} if the {@link ModelType} was deleted successfully in both repositories, {@code false} otherwise.
   * @see ModelRepository#delete(String)
   * @since 1.0.0
   */
  public boolean deleteInBoth(final @NotNull String id) {
    return this.fallbackModelRepository.delete(id) && this.mainModelRepository.delete(id);
  }

  /**
   * This method executes and wraps the {@link #deleteInBoth(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link ModelType} was deleted successfully in both repositories, {@code false} otherwise.
   * @see #deleteInBoth(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteInBoth(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#delete(String)} of the {@link #fallbackModelRepository} to delete the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return {@code true} if the {@link ModelType} was deleted successfully, {@code false} otherwise.
   * @see ModelRepository#delete(String)
   * @since 1.0.0
   */
  public boolean deleteInFallback(final @NotNull String id) {
    return this.fallbackModelRepository.delete(id);
  }

  /**
   * This method executes and wraps the {@link #deleteInFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType} to delete.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link ModelType} was deleted successfully, {@code false} otherwise.
   * @see #deleteInFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteInFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteInFallback(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#exists(String)} of the {@link #mainModelRepository} to check if
   * the {@link ModelType} with the specified id exists.
   *
   * @param id The id of the model.
   * @return {@code true} if the model exists, {@code false} otherwise.
   * @see ModelRepository#exists(String)
   * @since 1.0.0
   */
  @Override
  public boolean exists(final @NotNull String id) {
    return this.mainModelRepository.exists(id);
  }

  /**
   * This method uses the {@link ModelRepository#exists(String)} of the {@link #fallbackModelRepository} to check if
   * the {@link ModelType} with the specified id exists, and if it doesn't, it uses the {@link ModelRepository#exists(String)}
   * of the {@link #mainModelRepository} to check if the {@link ModelType} with the specified id exists.
   *
   * @param id The id of the {@link ModelType}.
   * @return {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository} or in the {@link #mainModelRepository}, {@code false} otherwise.
   * @see ModelRepository#exists(String)
   * @since 1.0.0
   */
  public boolean existsInAny(final @NotNull String id) {
    return this.existsInFallback(id) || this.mainModelRepository.exists(id);
  }

  /**
   * This method executes and wraps the {@link #existsInAny(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository} or in the {@link #mainModelRepository}, {@code false} otherwise.
   * @see #existsInAny(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInAnyAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInAny(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#exists(String)} of the {@link #fallbackModelRepository} and the
   * {@link ModelRepository#exists(String)} of the {@link #mainModelRepository} to check if the {@link ModelType}
   * with the specified id exists in both repositories.
   *
   * @param id The id of the {@link ModelType}.
   * @return {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository} and in the {@link #mainModelRepository}, {@code false} otherwise.
   * @see ModelRepository#exists(String)
   * @since 1.0.0
   */
  public boolean existsInBoth(final @NotNull String id) {
    return this.existsInFallback(id) && this.mainModelRepository.exists(id);
  }

  /**
   * This method executes and wraps the {@link #existsInBoth(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository} and in the {@link #mainModelRepository}, {@code false} otherwise.
   * @see #existsInBoth(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInBoth(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#exists(String)} of the {@link #fallbackModelRepository} to check if
   * the {@link ModelType} with the specified id exists.
   *
   * @param id The id of the {@link ModelType}.
   * @return {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository}, {@code false} otherwise.
   * @see ModelRepository#exists(String)
   * @since 1.0.0
   */
  public boolean existsInFallback(final @NotNull String id) {
    return this.fallbackModelRepository.exists(id);
  }

  /**
   * This method executes and wraps the {@link #existsInFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link ModelType} with the specified id exists in the {@link #fallbackModelRepository}, {@code false} otherwise.
   * @see #existsInFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInFallback(id), this.executor);
  }

  /**
   * This method returns the {@link #fallbackModelRepository}.
   *
   * @return The {@link #fallbackModelRepository}.
   * @since 1.0.0
   */
  public @NotNull ModelRepository<ModelType> fallbackModelRepository() {
    return this.fallbackModelRepository;
  }

  /**
   * This method uses the {@link ModelRepository#find(String)} of the {@link #mainModelRepository} to find the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#find(String)
   * @since 1.0.0
   */
  @Override
  public @Nullable ModelType find(final @NotNull String id) {
    return this.mainModelRepository.find(id);
  }

  /**
   * This method uses the {@link ModelRepository#findAll(IntFunction)} of the {@link #mainModelRepository} to find the
   * {@link ModelType}s in the repository.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see ModelRepository#findAll(IntFunction)
   * @since 1.0.0
   */
  @Override
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAll(final @NotNull Consumer<ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return this.mainModelRepository.findAll(postLoadAction, factory);
  }

  /**
   * This method uses the {@link ModelRepository#findIds()} of the {@link #fallbackModelRepository} to find the
   * ids of the repository.
   *
   * @return A {@link Collection} containing all the ids of the repository.
   * @see ModelRepository#findIds()
   * @since 1.0.0
   */
  public @Nullable Collection<@NotNull String> findAllIdsInFallback() {
    return this.fallbackModelRepository.findIds();
  }

  /**
   * This method executes and wraps the {@link #findAllIdsInFallback()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the ids of the repository.
   * @see #findAllIdsInFallback()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable Collection<@NotNull String>> findAllIdsInFallbackAsync() {
    return CompletableFuture.supplyAsync(this::findAllIdsInFallback, this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#findAll(IntFunction)} of the {@link #fallbackModelRepository} to find the
   * {@link ModelType}s in the repository.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see ModelRepository#findAll(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAllInFallback(final @NotNull IntFunction<@NotNull C> factory) {
    return this.fallbackModelRepository.findAll(factory);
  }

  /**
   * This method executes and wraps the {@link #findAllInFallback(IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see #findAllInFallback(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @NotNull CompletableFuture<@Nullable C> findAllInFallbackAsync(final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAllInFallback(factory), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#findAll(Consumer, IntFunction)} of the {@link #fallbackModelRepository} to find the
   * {@link ModelType}s in the repository.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see ModelRepository#findAll(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAllInFallback(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return this.fallbackModelRepository.findAll(postLoadAction, factory);
  }

  /**
   * This method executes and wraps the {@link #findAllInFallback(Consumer, IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see #findAllInFallback(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @NotNull CompletableFuture<@Nullable C> findAllInFallbackAsync(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAllInFallback(postLoadAction, factory), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#find(String)} of the {@link #mainModelRepository} to find the
   * {@link ModelType} with the specified id, and if it exists, it saves it to the {@link #fallbackModelRepository}.
   * If it doesn't exist, it returns {@code null}.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#find(String)
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  public @Nullable ModelType findAndSaveToFallback(final @NotNull String id) {
    final var model = this.find(id);
    if (model == null) {
      return null;
    }
    this.fallbackModelRepository.save(model);
    return model;
  }

  /**
   * This method executes and wraps the {@link #findAndSaveToFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findAndSaveToFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> findAndSaveToFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findAndSaveToFallback(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#findIds()} of the {@link #mainModelRepository} to find the
   * ids of the repository.
   *
   * @return A {@link Collection} containing all the ids of the repository.
   * @see ModelRepository#findIds()
   * @since 1.0.0
   */
  @Override
  public @Nullable Collection<@NotNull String> findIds() {
    return this.mainModelRepository.findIds();
  }

  /**
   * This method uses the {@link ModelRepository#findIds(IntFunction)} of the {@link #mainModelRepository} to find the
   * ids of the repository.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the ids of the repository.
   * @see ModelRepository#findIds(IntFunction)
   * @since 1.0.0
   */
  @Override
  public <C extends Collection<@NotNull String>> @Nullable C findIds(final @NotNull IntFunction<@NotNull C> factory) {
    return this.mainModelRepository.findIds(factory);
  }

  /**
   * This method uses the {@link ModelRepository#find(String)} of the {@link #fallbackModelRepository} to find the
   * {@link ModelType} with the specified id, and if it doesn't exist, it uses the {@link ModelRepository#find(String)}
   * of the {@link #mainModelRepository} to find the {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#find(String)
   * @since 1.0.0
   */
  public @Nullable ModelType findInBoth(final @NotNull String id) {
    final var model = this.findInFallback(id);
    if (model != null) {
      return model;
    }
    return this.find(id);
  }

  /**
   * This method executes and wraps the {@link #findInBoth(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInBoth(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> findInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInBoth(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#find(String)} of the {@link #fallbackModelRepository} to find the
   * {@link ModelType} with the specified id, and if it doesn't exist, it uses the {@link ModelRepository#find(String)}
   * of the {@link #mainModelRepository} to find the {@link ModelType} with the specified id, and if it exists, it saves
   * it to the {@link #fallbackModelRepository}.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#find(String)
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  public @Nullable ModelType findInBothAndSaveToFallback(final @NotNull String id) {
    final var cachedModel = this.findInFallback(id);
    if (cachedModel != null) {
      return cachedModel;
    }
    final var foundModel = this.find(id);
    if (foundModel == null) {
      return null;
    }
    this.fallbackModelRepository.save(foundModel);
    return foundModel;
  }

  /**
   * This method executes and wraps the {@link #findInBothAndSaveToFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInBothAndSaveToFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> findInBothAndSaveToFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInBothAndSaveToFallback(id), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#find(String)} of the {@link #fallbackModelRepository} to find the
   * {@link ModelType} with the specified id.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#find(String)
   * @since 1.0.0
   */
  public @Nullable ModelType findInFallback(final @NotNull String id) {
    return this.fallbackModelRepository.find(id);
  }

  /**
   * This method executes and wraps the {@link #findInFallback(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInFallback(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> findInFallbackAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInFallback(id), this.executor);
  }

  /**
   * This method executes the {@link ModelRepository#forEachIds(Consumer)} of the {@link #mainModelRepository} to iterate over
   * the ids of the repository.
   *
   * @param action The action to execute for each id.
   * @see ModelRepository#forEachIds(Consumer)
   * @since 1.0.0
   */
  public void forEachIdsInFallback(final @NotNull Consumer<? super @NotNull String> action) {
    this.fallbackModelRepository.forEachIds(action);
  }

  /**
   * This method executes and wraps the {@link #forEachIdsInFallback(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each id.
   * @return A {@link CompletableFuture} that will complete when all the ids in the repository are iterated over.
   * @see #forEachIdsInFallback(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> forEachIdsInFallbackAsync(final @NotNull Consumer<? super @NotNull String> action) {
    return CompletableFuture.runAsync(() -> this.forEachIdsInFallback(action), this.executor);
  }

  /**
   * This method executes the {@link ModelRepository#forEach(Consumer)} of the {@link #mainModelRepository} to iterate over
   * the {@link ModelType}s in the repository.
   *
   * @param action The action to execute for each {@link ModelType}.
   * @see ModelRepository#forEach(Consumer)
   * @since 1.0.0
   */
  public void forEachInFallback(final @NotNull Consumer<? super @NotNull ModelType> action) {
    this.fallbackModelRepository.forEach(action);
  }

  /**
   * This method executes and wraps the {@link #forEachInFallback(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete when all the {@link ModelType}s in the repository are iterated over.
   * @see #forEachInFallback(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> forEachInFallbackAsync(final @NotNull Consumer<? super @NotNull ModelType> action) {
    return CompletableFuture.runAsync(() -> this.forEachInFallback(action), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#iterator()} of the {@link #mainModelRepository} to return the
   * {@link Iterator} of the {@link ModelType}s in the repository.
   *
   * @return The {@link Iterator} of the {@link ModelType}s in the repository.
   * @see ModelRepository#iterator()
   * @since 1.0.0
   */
  @Override
  public @NotNull Iterator<ModelType> iterator() {
    return this.mainModelRepository.iterator();
  }

  /**
   * This method uses the {@link ModelRepository#iterator()} of the {@link #fallbackModelRepository} to return the
   * {@link Iterator} of the {@link ModelType}s in the repository.
   *
   * @return The {@link Iterator} of the {@link ModelType}s in the repository.
   * @see ModelRepository#iterator()
   * @since 1.0.0
   */
  public @NotNull Iterator<ModelType> iteratorInFallback() {
    return this.fallbackModelRepository.iterator();
  }

  /**
   * This method uses the {@link ModelRepository#iteratorIds()} of the {@link #mainModelRepository} to return the
   * {@link Iterator} of the ids of the repository.
   *
   * @return The {@link Iterator} of the ids of the repository.
   * @see ModelRepository#iteratorIds()
   * @since 1.0.0
   */
  @Override
  public @NotNull Iterator<String> iteratorIds() {
    return this.mainModelRepository.iteratorIds();
  }

  /**
   * This method uses the {@link ModelRepository#iteratorIds()} of the {@link #fallbackModelRepository} to return the
   * {@link Iterator} of the ids of the repository.
   *
   * @return The {@link Iterator} of the ids of the repository.
   * @see ModelRepository#iteratorIds()
   * @since 1.0.0
   */
  public @NotNull Iterator<String> iteratorIdsInFallback() {
    return this.fallbackModelRepository.iteratorIds();
  }

  /**
   * This method uses the {@link ModelRepository#findAll(Consumer, IntFunction)} of the {@link #mainModelRepository} to find the
   * {@link ModelType}s in the repository, and if they exist, it saves them to the {@link #fallbackModelRepository}.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see ModelRepository#findAll(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @Nullable C loadAll(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final var models = this.mainModelRepository.findAll(postLoadAction, factory);
    if (models == null) {
      return null;
    }
    for (final var model : models) {
      this.fallbackModelRepository.save(model);
    }
    return models;
  }

  /**
   * This method executes and wraps the {@link #loadAll(Consumer, IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link ModelType}s in the repository.
   * @see #loadAll(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull ModelType>> @NotNull CompletableFuture<@Nullable C> loadAllAsync(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.loadAll(postLoadAction, factory), this.executor);
  }

  /**
   * This method returns the {@link #mainModelRepository}.
   *
   * @return The {@link #mainModelRepository}.
   * @since 1.0.0
   */
  public @NotNull ModelRepository<ModelType> mainModelRepository() {
    return this.mainModelRepository;
  }

  /**
   * This method uses the {@link ModelRepository#save(Model)} of the {@link #mainModelRepository} to save the
   * {@link ModelType}.
   *
   * @param model The {@link Model} to save. It must have an id.
   * @return The saved {@link ModelType}.
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  @Override
  public @NotNull ModelType save(final @NotNull ModelType model) {
    return this.mainModelRepository.save(model);
  }

  /**
   * This method uses iterates over the {@link #fallbackModelRepository} and saves them to the {@link #mainModelRepository}.
   *
   * @param preSaveAction The action to execute for each model before it's saved.
   * @see ModelRepository#iterator()
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  public void saveAll(final @NotNull Consumer<ModelType> preSaveAction) {
    for (final var model : this.fallbackModelRepository) {
      preSaveAction.accept(model);
      this.mainModelRepository.save(model);
    }
  }

  /**
   * This method executes and wraps the {@link #saveAll(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param preSaveAction The action to execute for each {@link ModelType} before it's saved.
   * @return A {@link CompletableFuture} that will complete when all the {@link ModelType}s in the repository are saved.
   * @see #saveAll(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> saveAllAsync(final @NotNull Consumer<ModelType> preSaveAction) {
    return CompletableFuture.runAsync(() -> this.saveAll(preSaveAction), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#save(Model)} of the {@link #mainModelRepository} and the
   * {@link ModelRepository#save(Model)} of the {@link #fallbackModelRepository} to save the {@link ModelType}.
   *
   * @param model The {@link ModelType} to save. It must have an id.
   * @return The saved {@link ModelType}.
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  @Contract("_ -> param1")
  public @NotNull ModelType saveInBoth(final @NotNull ModelType model) {
    this.fallbackModelRepository.save(model);
    this.mainModelRepository.save(model);
    return model;
  }

  /**
   * This method executes and wraps the {@link #saveInBoth(Model)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param model The {@link ModelType} to save. It must have an id.
   * @return A {@link CompletableFuture} that will complete with the saved {@link ModelType}.
   * @see #saveInBoth(Model)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull ModelType> saveInBothAsync(final @NotNull ModelType model) {
    return CompletableFuture.supplyAsync(() -> this.saveInBoth(model), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#save(Model)} of the {@link #fallbackModelRepository} to save the
   * {@link ModelType}.
   *
   * @param model The {@link ModelType} to save. It must have an id.
   * @return The saved {@link ModelType}.
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  @Contract("_ -> param1")
  public @NotNull ModelType saveInFallback(final @NotNull ModelType model) {
    this.fallbackModelRepository.save(model);
    return model;
  }

  /**
   * This method executes and wraps the {@link #saveInFallback(Model)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param model The {@link ModelType} to save. It must have an id.
   * @return A {@link CompletableFuture} that will complete with the saved {@link ModelType}.
   * @see #saveInFallback(Model)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull ModelType> saveInFallbackAsync(final @NotNull ModelType model) {
    return CompletableFuture.supplyAsync(() -> this.saveInFallback(model), this.executor);
  }

  /**
   * This method uses the {@link ModelRepository#deleteAndRetrieve(String)} of the {@link #fallbackModelRepository} to delete the
   * {@link ModelType} with the specified id, and if it exists, it saves it to the {@link #mainModelRepository}.
   *
   * @param id The id of the {@link ModelType}.
   * @return The {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see ModelRepository#deleteAndRetrieve(String)
   * @see ModelRepository#save(Model)
   * @since 1.0.0
   */
  public @Nullable ModelType upload(final @NotNull String id) {
    final var modelType = this.fallbackModelRepository.deleteAndRetrieve(id);
    if (modelType == null) {
      return null;
    }
    this.mainModelRepository.save(modelType);
    return modelType;
  }

  /**
   * This method executes and wraps the {@link #upload(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link ModelType}.
   * @return A {@link CompletableFuture} that will complete with the {@link ModelType} with the specified id, or {@code null} if it doesn't exist.
   * @see #upload(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable ModelType> uploadAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.upload(id), this.executor);
  }

  /**
   * This method uses iterates over the {@link #fallbackModelRepository} and saves them to the {@link #mainModelRepository}.
   * After that, it deletes all the {@link ModelType}s from the {@link #fallbackModelRepository}.
   *
   * @param preUploadAction The action to execute for each model before it's uploaded.
   * @see ModelRepository#iterator()
   * @see ModelRepository#save(Model)
   * @see ModelRepository#deleteAll()
   * @since 1.0.0
   */
  public void uploadAll(final @NotNull Consumer<@NotNull ModelType> preUploadAction) {
    for (final var model : this.fallbackModelRepository) {
      preUploadAction.accept(model);
      this.mainModelRepository.save(model);
    }
    this.fallbackModelRepository.deleteAll();
  }

  /**
   * This method executes and wraps the {@link #uploadAll(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param preUploadAction The action to execute for each model before it's uploaded.
   * @return A {@link CompletableFuture} that will complete when all the {@link ModelType}s are uploaded.
   * @see #uploadAll(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<Void> uploadAllAsync(final @NotNull Consumer<@NotNull ModelType> preUploadAction) {
    return CompletableFuture.runAsync(() -> this.uploadAll(preUploadAction), this.executor);
  }
}
