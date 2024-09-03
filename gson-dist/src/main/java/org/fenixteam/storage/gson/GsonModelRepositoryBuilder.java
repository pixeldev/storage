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
package org.fenixteam.storage.gson;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import org.fenixteam.storage.codec.ModelDeserializer;
import org.fenixteam.storage.codec.ModelSerializer;
import org.fenixteam.storage.model.Model;
import org.fenixteam.storage.repository.AsyncModelRepository;
import org.fenixteam.storage.repository.builder.AbstractModelRepositoryBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class GsonModelRepositoryBuilder<ModelType extends Model> extends AbstractModelRepositoryBuilder<ModelType> {
  private final Class<ModelType> modelType;
  private Path folderPath;
  private boolean prettyPrinting;
  private ModelSerializer<ModelType, JsonObject> writer;
  private ModelDeserializer<ModelType, JsonObject> reader;

  GsonModelRepositoryBuilder(final @NotNull Class<ModelType> type) {
    this.modelType = type;
  }

  @Contract("_ -> this")
  public @NotNull GsonModelRepositoryBuilder<ModelType> folder(final @NotNull Path folderPath) {
    this.folderPath = folderPath;
    return this;
  }

  @Contract("_ -> this")
  public @NotNull GsonModelRepositoryBuilder<ModelType> prettyPrinting(final boolean prettyPrinting) {
    this.prettyPrinting = prettyPrinting;
    return this;
  }

  @Contract("_ -> this")
  public @NotNull GsonModelRepositoryBuilder<ModelType> modelSerializer(
    final @NotNull ModelSerializer<ModelType, JsonObject> writer
  ) {
    this.writer = writer;
    return this;
  }

  @Contract("_ -> this")
  public @NotNull GsonModelRepositoryBuilder<ModelType> modelDeserializer(
    final @NotNull ModelDeserializer<ModelType, JsonObject> reader
  ) {
    this.reader = reader;
    return this;
  }

  @Contract("_ -> new")
  public @NotNull AsyncModelRepository<ModelType> build(final @NotNull Executor executor) {
    if (Files.notExists(this.folderPath)) {
      try {
        Files.createDirectories(this.folderPath);
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new GsonModelRepository<>(
      executor,
      this.modelType,
      this.folderPath,
      this.prettyPrinting,
      this.writer,
      this.reader);
  }
}
