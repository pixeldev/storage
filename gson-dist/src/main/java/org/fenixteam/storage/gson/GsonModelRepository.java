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
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.fenixteam.storage.codec.ModelDeserializer;
import org.fenixteam.storage.codec.ModelSerializer;
import org.fenixteam.storage.model.Model;
import org.fenixteam.storage.repository.AsyncModelRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class GsonModelRepository<ModelType extends Model> extends AsyncModelRepository<ModelType> {
  protected final Class<ModelType> modelType;
  protected final Path folderPath;
  protected final boolean prettyPrinting;
  protected final ModelSerializer<ModelType, JsonObject> modelSerializer;
  protected final ModelDeserializer<ModelType, JsonObject> modelDeserializer;

  protected GsonModelRepository(final @NotNull Executor executor, final @NotNull Class<ModelType> modelType, final @NotNull Path folderPath, final boolean prettyPrinting, final @NotNull ModelSerializer<ModelType, JsonObject> modelSerializer, final @NotNull ModelDeserializer<ModelType, JsonObject> modelDeserializer) {
    super(executor);
    this.prettyPrinting = prettyPrinting;
    this.modelType = modelType;
    this.folderPath = folderPath;
    this.modelSerializer = modelSerializer;
    this.modelDeserializer = modelDeserializer;
  }

  @Contract("_ -> new")
  public static <T extends Model> @NotNull GsonModelRepositoryBuilder<T> builder(final @NotNull Class<T> type) {
    return new GsonModelRepositoryBuilder<>(type);
  }

  @Override
  public @Nullable ModelType find(final @NotNull String id) {
    return this.internalFind(this.resolveChild(id));
  }

  @Override
  public @Nullable Collection<@NotNull String> findIds() {
    return this.findIds(ArrayList::new);
  }

  @Override
  public <C extends Collection<@NotNull String>> @Nullable C findIds(final @NotNull IntFunction<@NotNull C> factory) {
    try (final var directoryStream = Files.newDirectoryStream(this.folderPath)) {
      final var foundIds = factory.apply(1);
      directoryStream.forEach(path -> foundIds.add(this.extractId(path)));
      return foundIds;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <C extends Collection<@NotNull ModelType>> @Nullable C findAll(final @NotNull Consumer<@NotNull ModelType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final var foundModels = factory.apply(1);
    this.forEachSync(model -> {
      postLoadAction.accept(model);
      foundModels.add(model);
    });
    return foundModels;
  }

  @Override
  public void forEachSync(final @NotNull Consumer<@NotNull ModelType> action) {
    try (final var directoryStream = Files.newDirectoryStream(this.folderPath)) {
      directoryStream.forEach(path -> {
        final var model = this.internalFind(path);
        if (model != null) {
          action.accept(model);
        }
      });
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean exists(final @NotNull String id) {
    return Files.exists(this.resolveChild(id));
  }

  @Override
  public @NotNull ModelType save(final @NotNull ModelType model) {
    final var modelPath = this.resolveChild(model.id());
    try {
      if (Files.notExists(modelPath)) {
        Files.createFile(modelPath);
      }
      try (final var writer = new JsonWriter(Files.newBufferedWriter(modelPath, StandardCharsets.UTF_8))) {
        writer.setSerializeNulls(false);
        if (this.prettyPrinting) {
          writer.setIndent("  ");
        }
        final var jsonObject = this.modelSerializer.serialize(model);
        TypeAdapters.JSON_ELEMENT.write(writer, jsonObject);
        return model;
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean delete(final @NotNull String id) {
    try {
      return Files.deleteIfExists(this.resolveChild(id));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nullable ModelType deleteAndRetrieve(final @NotNull String id) {
    final var model = this.find(id);
    if (model != null) {
      this.delete(id);
    }
    return model;
  }

  @Override
  public void deleteAll() {
    try (final var walk = Files.walk(this.folderPath, 1)) {
      walk.filter(Files::isRegularFile)
        .forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (final IOException e) {
            throw new RuntimeException(e);
          }
        });
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public @NotNull String extractId(final @NotNull Path file) {
    return file.getFileName().toString().substring(0, file.getFileName().toString().length() - 5);
  }

  public @NotNull Path resolveChild(final @NotNull String id) {
    return this.folderPath.resolve(id + ".json");
  }

  public @Nullable ModelType internalFind(final @NotNull Path file) {
    if (Files.notExists(file)) {
      return null;
    }
    try (final var reader = new JsonReader(Files.newBufferedReader(file))) {
      final var jsonObject = new JsonObject();
      reader.beginObject();
      while (reader.hasNext()) {
        jsonObject.add(reader.nextName(), TypeAdapters.JSON_ELEMENT.read(reader));
      }
      reader.endObject();
      return this.modelDeserializer.deserialize(jsonObject);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
