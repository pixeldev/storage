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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.fenixteam.storage.model.Model;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a simple implementation of {@link ModelRepository} that uses a {@link Map} as the storage.
 * It's designed to be used with the {@link WithFallbackModelRepository} class to provide a simple and
 * efficient way to store your data in memory.
 *
 * @param <ModelType> The {@link Model} type that this repository will handle.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class MapModelRepository<ModelType extends Model> implements ModelRepository<ModelType> {
  private final Map<String, ModelType> cache;

  /**
   * Creates a new {@link MapModelRepository} with the specified {@link Map} as the storage.
   *
   * @param cache The {@link Map} to use as the storage.
   * @since 1.0.0
   */
  private MapModelRepository(final @NotNull Map<String, ModelType> cache) {
    this.cache = cache;
  }

  /**
   * Creates a new {@link MapModelRepository} with a {@link HashMap} as the storage.
   *
   * @param <T> The {@link Model} type that this repository will handle.
   * @return A new {@link MapModelRepository} with a {@link HashMap} as the storage.
   * @since 1.0.0
   */
  @Contract(" -> new")
  public static <T extends Model> @NotNull MapModelRepository<T> hashMap() {
    return MapModelRepository.create(new HashMap<>());
  }

  /**
   * Creates a new {@link MapModelRepository} with a {@link ConcurrentHashMap} as the storage.
   *
   * @param <T> The {@link Model} type that this repository will handle.
   * @return A new {@link MapModelRepository} with a {@link ConcurrentHashMap} as the storage.
   * @since 1.0.0
   */
  @Contract(" -> new")
  public static <T extends Model> @NotNull MapModelRepository<T> concurrentHashMap() {
    return MapModelRepository.create(new ConcurrentHashMap<>());
  }

  /**
   * Creates a new {@link MapModelRepository} with the specified {@link Map} as the storage.
   *
   * @param cache The {@link Map} to use as the storage.
   * @param <T>   The {@link Model} type that this repository will handle.
   * @return A new {@link MapModelRepository} with the specified {@link Map} as the storage.
   * @since 1.0.0
   */
  @Contract("_ -> new")
  public static <T extends Model> @NotNull MapModelRepository<T> create(final @NotNull Map<String, T> cache) {
    return new MapModelRepository<>(cache);
  }

  /**
   * Returns the {@link Map} used as the storage.
   *
   * @return The {@link Map} used as the storage.
   * @since 1.0.0
   */
  public @NotNull Map<String, ModelType> cache() {
    return this.cache;
  }

  /**
   * Deletes the {@link Model} with the specified id from the repository.
   *
   * @param id The id of the {@link Model} to delete.
   * @return True if the {@link Model} was deleted, false otherwise.
   * @since 1.0.0
   */
  @Override
  public boolean delete(final @NotNull String id) {
    return this.cache.remove(id) != null;
  }

  @Override
  public void deleteAll() {
    this.cache.clear();
  }

  @Override
  public @Nullable ModelType deleteAndRetrieve(final @NotNull String id) {
    return this.cache.remove(id);
  }

  /**
   * Returns true if the model with the specified id exists, false otherwise.
   *
   * @param id The id of the model.
   * @return True if the model with the specified id exists, false otherwise.
   * @since 1.0.0
   */
  @Override
  public boolean exists(final @NotNull String id) {
    return this.cache.containsKey(id);
  }

  /**
   * Returns the {@link ModelType} with the specified id using {@link Map#get(Object)}.
   *
   * @param id The id of the model.
   * @return The model with the specified id, or null if it doesn't exist.
   * @since 1.0.0
   */
  @Override
  public @Nullable ModelType find(final @NotNull String id) {
    return this.cache.get(id);
  }

  /**
   * Maps all the models in the internal {@link Map} and returns them in the specified {@link Collection}.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the models in the repository.
   * @since 1.0.0
   */
  @Override
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAll(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final var values = this.cache.values();
    if (values.isEmpty()) {
      return null;
    }
    final var collection = factory.apply(values.size());
    for (final var value : values) {
      postLoadAction.accept(value);
      collection.add(value);
    }
    return collection;
  }

  /**
   * Returns a the {@link Map#keySet()} of the {@link Map} used as the storage.
   *
   * @return The {@link Map#keySet()} of the {@link Map} used as the storage.
   * @see #findIds(IntFunction)
   * @since 1.0.0
   */
  @Override
  public @NotNull Collection<@NotNull String> findIds() {
    return this.cache.keySet();
  }

  /**
   * Maps all the ids of the models in the internal {@link Map} and returns them in the specified {@link Collection}.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return The created {@link Collection} with the factory containing all the ids of the models in the repository.
   * @since 1.0.0
   */
  @Override
  public <C extends Collection<@NotNull String>> @Nullable C findIds(final @NotNull IntFunction<@NotNull C> factory) {
    final var keys = this.cache.keySet();
    if (keys.isEmpty()) {
      return null;
    }
    final var collection = factory.apply(keys.size());
    collection.addAll(keys);
    return collection;
  }

  @Override
  public @NotNull Iterator<ModelType> iterator() {
    return this.cache.values().iterator();
  }

  @Override
  public @NotNull Iterator<String> iteratorIds() {
    return this.cache.keySet().iterator();
  }

  /**
   * Saves the specified {@link Model} in the repository.
   *
   * @param model The {@link Model} to save.
   * @return The saved {@link Model}.
   * @since 1.0.0
   */
  @Override
  public @NotNull ModelType save(final @NotNull ModelType model) {
    this.cache.put(model.id(), model);
    return model;
  }
}
