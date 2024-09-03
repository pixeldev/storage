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
package org.fenixteam.storage.repository.builder;

import java.util.concurrent.Executor;
import org.fenixteam.storage.model.Model;
import org.fenixteam.storage.repository.AsyncModelRepository;
import org.fenixteam.storage.repository.ModelRepository;
import org.fenixteam.storage.repository.WithFallbackModelRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class is the base for all the {@link AsyncModelRepository} builders. It contains the essential methods to
 * build a {@link AsyncModelRepository} with a {@link Executor} and optionally a fallback {@link ModelRepository}.
 *
 * @param <ModelType> The {@link Model} type that the repository will handle.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractModelRepositoryBuilder<ModelType extends Model> {
  /**
   * Builds a new {@link AsyncModelRepository} with the specified {@link Executor}.
   *
   * @param executor The {@link Executor} that will be used to execute the asynchronous operations.
   * @return A new {@link AsyncModelRepository} with the specified {@link Executor}.
   * @since 1.0.0
   */
  @Contract("_ -> new")
  public abstract @NotNull AsyncModelRepository<ModelType> build(final @NotNull Executor executor);

  /**
   * Builds a new {@link WithFallbackModelRepository} with the specified {@link Executor} and fallback {@link ModelRepository}.
   *
   * @param executor                The {@link Executor} that will be used to execute the asynchronous operations.
   * @param fallbackModelRepository The fallback {@link ModelRepository} to use.
   * @return A new {@link WithFallbackModelRepository} with the specified {@link Executor} and fallback {@link ModelRepository}.
   * @since 1.0.0
   */
  @Contract("_, _ -> new")
  public @NotNull WithFallbackModelRepository<ModelType> buildWithFallback(final @NotNull Executor executor, final @NotNull ModelRepository<ModelType> fallbackModelRepository) {
    return new WithFallbackModelRepository<>(executor, fallbackModelRepository, this.build(executor));
  }
}
