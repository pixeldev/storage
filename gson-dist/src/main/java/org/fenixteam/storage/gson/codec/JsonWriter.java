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
package org.fenixteam.storage.gson.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.fenixteam.storage.codec.ModelSerializer;
import org.fenixteam.storage.codec.ModelWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JsonWriter implements ModelWriter<JsonObject> {
  private final JsonObject jsonObject;

  protected JsonWriter(final @NotNull JsonObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  @Contract(" -> new")
  public static @NotNull JsonWriter create() {
    return new JsonWriter(new JsonObject());
  }

  @Contract("_ -> new")
  public static @NotNull JsonWriter create(final @NotNull JsonObject jsonObject) {
    return new JsonWriter(jsonObject);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeDetailedUuid(
    final @NotNull String key,
    final @Nullable UUID uuid
  ) {
    final var serializedUuid = this.writeDetailedUuid(uuid);
    if (serializedUuid == null) {
      return this;
    }
    this.jsonObject.add(key, serializedUuid);
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeDetailedUuids(
    final @NotNull String key,
    final @Nullable Collection<@NotNull UUID> uuids
  ) {
    if (uuids == null) {
      return this;
    }
    final var array = new JsonArray(uuids.size());
    for (final var uuid : uuids) {
      final var serializedUuid = this.writeDetailedUuid(uuid);
      if (serializedUuid == null) {
        continue;
      }
      array.add(serializedUuid);
    }
    this.jsonObject.add(key, array);
    return this;
  }

  public @Nullable JsonObject writeDetailedUuid(final @Nullable UUID uuid) {
    if (uuid == null) {
      return null;
    }
    final var serializedUuid = new JsonObject();
    serializedUuid.addProperty("least", uuid.getLeastSignificantBits());
    serializedUuid.addProperty("most", uuid.getMostSignificantBits());
    return serializedUuid;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeThis(
    final @NotNull String key,
    final @Nullable JsonObject value
  ) {
    this.jsonObject.add(key, value);
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeUuid(final @NotNull String field, final @Nullable UUID uuid) {
    if (uuid == null) {
      return this;
    }
    this.jsonObject.addProperty(field, uuid.toString());
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeString(
    final @NotNull String field,
    final @Nullable String value
  ) {
    if (value == null) {
      return this;
    }
    this.jsonObject.addProperty(field, value);
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeNumber(
    final @NotNull String field,
    final @Nullable Number value
  ) {
    if (value == null) {
      return this;
    }
    this.jsonObject.addProperty(field, value);
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull JsonWriter writeBoolean(
    final @NotNull String field,
    final @Nullable Boolean value
  ) {
    if (value == null) {
      return this;
    }
    this.jsonObject.addProperty(field, value);
    return this;
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull JsonWriter writeObject(
    final @NotNull String field,
    final @Nullable T child,
    final @NotNull ModelSerializer<T, JsonObject> modelSerializer
  ) {
    if (child == null) {
      return this;
    }
    this.jsonObject.add(field, modelSerializer.serialize(child));
    return this;
  }

  @Override
  @Contract("_, _ -> this")
  public <T> @NotNull JsonWriter writeRawCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children
  ) {
    if (children == null) {
      return this;
    }
    final var array = new JsonArray(children.size());
    for (final var child : children) {
      if (child == null) {
        continue;
      }
      array.add(child.toString());
    }
    this.jsonObject.add(field, array);
    return this;
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull JsonWriter writeCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children,
    final @NotNull ModelSerializer<T, JsonObject> modelSerializer
  ) {
    if (children == null) {
      return this;
    }
    final var array = new JsonArray(children.size());
    for (final var child : children) {
      array.add(modelSerializer.serialize(child));
    }
    this.jsonObject.add(field, array);
    return this;
  }

  @Contract("_, _, _ -> this")
  public <T> @NotNull JsonWriter writePrimitiveCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children,
    final @NotNull Function<T, JsonElement> writer
  ) {
    if (children == null) {
      return this;
    }
    final var array = new JsonArray(children.size());
    for (final var child : children) {
      array.add(writer.apply(child));
    }
    this.jsonObject.add(field, array);
    return this;
  }

  public <T> @NotNull JsonWriter writePrimitiveArray(
    final @NotNull String field,
    final @Nullable T[] children,
    final @NotNull Function<T, JsonElement> writer
  ) {
    if (children == null) {
      return this;
    }
    final var array = new JsonArray(children.length);
    for (final var child : children) {
      array.add(writer.apply(child));
    }
    this.jsonObject.add(field, array);
    return this;
  }

  public <K, V> @NotNull JsonWriter writePrimitiveMap(
    final @NotNull String field,
    final @Nullable Map<K, V> map,
    final @NotNull Function<K, String> keyWriter,
    final @NotNull Function<V, JsonElement> valueWriter
  ) {
    if (map == null) {
      return this;
    }
    final var object = new JsonObject();
    for (final var entry : map.entrySet()) {
      object.add(keyWriter.apply(entry.getKey()), valueWriter.apply(entry.getValue()));
    }
    this.jsonObject.add(field, object);
    return this;
  }

  @Override
  public @NotNull JsonObject current() {
    return this.jsonObject;
  }

  @Override
  public @NotNull JsonObject end() {
    return this.jsonObject;
  }
}
