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
package team.emptyte.storage.aggregate.infrastructure.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import team.emptyte.storage.aggregate.domain.AggregateRoot;
import team.emptyte.storage.aggregate.domain.repository.AsyncAggregateRootRepository;
import team.emptyte.storage.aggregate.domain.repository.builder.AbstractAggregateRootRepositoryBuilder;

@SuppressWarnings("unused")
public final class YamlAggregateRootRepositoryBuilder<AggregateType extends AggregateRoot> extends AbstractAggregateRootRepositoryBuilder<AggregateType> {
  private final Class<AggregateType> aggregateType;
  private Path folderPath;
  private boolean prettyPrinting;
  private TypeSerializerCollection typeSerializers;

  YamlAggregateRootRepositoryBuilder(final @NotNull Class<AggregateType> type) {
    this.aggregateType = type;
  }

  @Contract("_ -> this")
  public @NotNull YamlAggregateRootRepositoryBuilder<AggregateType> folder(final @NotNull Path folderPath) {
    this.folderPath = folderPath;
    return this;
  }

  @Contract("_ -> this")
  public @NotNull YamlAggregateRootRepositoryBuilder<AggregateType> prettyPrinting(final boolean prettyPrinting) {
    this.prettyPrinting = prettyPrinting;
    return this;
  }

  @Contract("_ -> this")
  public @NotNull YamlAggregateRootRepositoryBuilder<AggregateType> typeSerializers(
    final @NotNull TypeSerializerCollection typeSerializers
  ) {
    this.typeSerializers = typeSerializers;
    return this;
  }

  @Contract("_ -> new")
  public @NotNull AsyncAggregateRootRepository<AggregateType> build(final @NotNull Executor executor) {
    if (Files.notExists(this.folderPath)) {
      try {
        Files.createDirectories(this.folderPath);
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }

    return new YamlAggregateRootRepository<>(
      executor,
      this.aggregateType,
      this.folderPath,
      this.prettyPrinting,
      this.typeSerializers);
  }
}
