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
package team.emptyte.storage.aggregate.infrastructure.caffeine;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.emptyte.storage.aggregate.domain.AggregateRoot;
import team.emptyte.storage.aggregate.domain.repository.AggregateRootRepository;

/**
 * This class is the implementation of the {@link AggregateRootRepository} interface, it uses the
 * {@link Cache} class from the Caffeine library to store the models. So, you're able to add
 * listeners, set the maximum size of the cache, set the expiration time of the cache, etc.
 *
 * @param <AggregateType> The type of the {@link AggregateRoot} that this repository will store.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class CaffeineAggregateRootRepository<AggregateType extends AggregateRoot> implements AggregateRootRepository<AggregateType> {
  private final Cache<String, AggregateType> cache;

  /**
   * Creates a new {@link CaffeineAggregateRootRepository} with the given {@link Cache}.
   *
   * @param cache The {@link Cache} that will be used to store the {@link AggregateType}s.
   * @since 1.0.0
   */
  protected CaffeineAggregateRootRepository(final @NotNull Cache<String, AggregateType> cache) {
    this.cache = cache;
  }

  @Contract(value = "_ -> new")
  public static <T extends AggregateRoot> @NotNull CaffeineAggregateRootRepository<T> create(final @NotNull Cache<String, T> cache) {
    return new CaffeineAggregateRootRepository<>(cache);
  }


  @Override
  public boolean deleteSync(final @NotNull String id) {
    return this.deleteAndRetrieveSync(id) != null;
  }

  @Override
  public void deleteAllSync() {
    this.cache.invalidateAll();
  }

  @Override
  public @Nullable AggregateType deleteAndRetrieveSync(final @NotNull String id) {
    return this.cache.asMap().remove(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.asMap().containsKey(id);
  }

  @Override
  public @Nullable AggregateType findSync(final @NotNull String id) {
    return this.cache.getIfPresent(id);
  }

  @Override
  public <C extends Collection<@NotNull AggregateType>> @Nullable C findAllSync(final @NotNull Consumer<@NotNull AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final var collection = factory.apply(this.cache.asMap().size());
    for (final var aggregate : this.cache.asMap().values()) {
      postLoadAction.accept(aggregate);
      collection.add(aggregate);
    }
    return collection;
  }

  @Override
  public @NotNull Set<@NotNull String> findIdsSync() {
    return this.cache.asMap()
      .keySet();
  }

  @Override
  public @NotNull Iterator<AggregateType> iterator() {
    return this.cache.asMap()
      .values()
      .iterator();
  }

  @Override
  public @NotNull Iterator<String> iteratorIdsSync() {
    return this.cache.asMap()
      .keySet()
      .iterator();
  }

  @Override
  public @NotNull AggregateType saveSync(@NotNull final AggregateType aggregateType) {
    this.cache.put(aggregateType.id(), aggregateType);
    return aggregateType;
  }
}
