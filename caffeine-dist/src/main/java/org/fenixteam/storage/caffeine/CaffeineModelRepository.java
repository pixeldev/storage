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
package org.fenixteam.storage.caffeine;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.fenixteam.storage.model.Model;
import org.fenixteam.storage.repository.ModelRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is the implementation of the {@link ModelRepository} interface, it uses the
 * {@link Cache} class from the Caffeine library to store the models. So, you're able to add
 * listeners, set the maximum size of the cache, set the expiration time of the cache, etc.
 *
 * @param <ModelType> The type of the {@link Model} that this repository will store.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class CaffeineModelRepository<ModelType extends Model> implements ModelRepository<ModelType> {
  private final Cache<String, ModelType> cache;

  /**
   * Creates a new {@link CaffeineModelRepository} with the given {@link Cache}.
   *
   * @param cache The {@link Cache} that will be used to store the {@link ModelType}s.
   * @since 1.0.0
   */
  protected CaffeineModelRepository(final @NotNull Cache<String, ModelType> cache) {
    this.cache = cache;
  }

  @Contract(value = "_ -> new")
  public static <T extends Model> @NotNull CaffeineModelRepository<T> create(final @NotNull Cache<String, T> cache) {
    return new CaffeineModelRepository<>(cache);
  }


  @Override
  public boolean delete(final @NotNull String id) {
    return this.deleteAndRetrieve(id) != null;
  }

  @Override
  public void deleteAll() {
    this.cache.invalidateAll();
  }

  @Override
  public @Nullable ModelType deleteAndRetrieve(final @NotNull String id) {
    return this.cache.asMap().remove(id);
  }

  @Override
  public boolean exists(final @NotNull String id) {
    return this.cache.asMap().containsKey(id);
  }

  @Override
  public @Nullable ModelType find(final @NotNull String id) {
    return this.cache.getIfPresent(id);
  }

  @Override
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAll(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return null;
  }

  @Override
  public @Nullable Collection<@NotNull String> findIds() {
    return null;
  }

  @Override
  public <C extends Collection<@NotNull String>> @Nullable C findIds(final @NotNull IntFunction<@NotNull C> factory) {
    return null;
  }

  @Override
  public @NotNull Iterator<ModelType> iterator() {
    return null;
  }

  @Override
  public @NotNull Iterator<String> iteratorIds() {
    return null;
  }

  @Override
  public @NotNull ModelType save(@NotNull final ModelType model) {
    return null;
  }
}
