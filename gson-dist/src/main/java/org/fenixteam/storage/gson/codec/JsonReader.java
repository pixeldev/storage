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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.fenixteam.storage.codec.ModelDeserializer;
import org.fenixteam.storage.codec.ModelReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JsonReader implements ModelReader<JsonObject> {
  public static final Function<JsonObject, JsonReader> FACTORY = JsonReader::create;
  private static final Map<Class<?>, Function<JsonElement, Object>> READERS = new HashMap<>();

  static {
    READERS.put(String.class, JsonElement::getAsString);
    READERS.put(Integer.class, JsonElement::getAsInt);
    READERS.put(Long.class, JsonElement::getAsLong);
    READERS.put(Double.class, JsonElement::getAsDouble);
    READERS.put(Float.class, JsonElement::getAsFloat);
    READERS.put(Boolean.class, JsonElement::getAsBoolean);
    READERS.put(Byte.class, JsonElement::getAsByte);
    READERS.put(Short.class, JsonElement::getAsShort);
  }

  protected final JsonObject jsonObject;

  protected JsonReader(final @NotNull JsonObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  @Contract("_ -> new")
  public static @NotNull JsonReader create(final @NotNull JsonObject jsonObject) {
    return new JsonReader(jsonObject);
  }

  @Override
  public @NotNull JsonObject raw() {
    return this.jsonObject;
  }

  @Override
  public @Nullable JsonObject readThis(final @NotNull String field) {
    return this.jsonObject.getAsJsonObject(field);
  }

  @Override
  public @Nullable String readString(final @NotNull String field) {
    final var element = this.jsonObject.get(field);
    if (element == null) {
      return null;
    }
    return element.getAsString();
  }

  @Override
  public @Nullable Number readNumber(final @NotNull String field) {
    final var element = this.jsonObject.get(field);
    if (element == null) {
      return null;
    }
    return element.getAsNumber();
  }

  @Override
  public boolean readBoolean(final @NotNull String field) {
    final var element = this.jsonObject.get(field);
    if (element == null) {
      return false;
    }
    return element.getAsBoolean();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T, C extends Collection<T>> @Nullable C readRawCollection(
    final @NotNull String field,
    final @NotNull Class<T> clazz,
    final @NotNull Function<Integer, C> collectionFactory
  ) {
    final var element = this.jsonObject.get(field);
    if (element == null) {
      return null;
    }
    final var array = element.getAsJsonArray();
    final var collection = collectionFactory.apply(array.size());
    final Function<JsonElement, Object> reader = READERS.get(clazz);
    for (final JsonElement jsonElement : array) {
      collection.add((T) reader.apply(jsonElement));
    }
    return collection;
  }

  @Override
  public <T> @Nullable T readObject(
    final @NotNull String field,
    final @NotNull ModelDeserializer<T, JsonObject> modelDeserializer
  ) {
    final JsonElement element = this.jsonObject.get(field);
    if (element == null) {
      return null;
    }
    return modelDeserializer.deserialize(element.getAsJsonObject());
  }

  @Override
  public @Nullable <K, V> Map<K, V> readMap(
    final @NotNull String field,
    final @NotNull Function<V, K> keyParser,
    final @NotNull ModelDeserializer<V, JsonObject> modelDeserializer
  ) {
    final var element = this.jsonObject.get(field);
    if (element == null) {
      return null;
    }
    final var array = element.getAsJsonArray();
    final var map = new HashMap<K, V>(array.size());
    for (final var arrayElement : array) {
      final var value = modelDeserializer.deserialize(arrayElement.getAsJsonObject());
      map.put(keyParser.apply(value), value);
    }
    return map;
  }

  @Override
  public <T, C extends Collection<T>> @Nullable C readCollection(
    final @NotNull String field,
    final @NotNull Function<Integer, C> collectionFactory,
    final @NotNull ModelDeserializer<T, JsonObject> modelDeserializer
  ) {
    final var array = this.jsonObject.getAsJsonArray(field);
    if (array == null) {
      return null;
    }
    final var objects = collectionFactory.apply(array.size());
    for (final var element : array) {
      final var object = modelDeserializer.deserialize(element.getAsJsonObject());
      objects.add(object);
    }
    return objects;
  }

  public <T, C extends Collection<T>> @Nullable C readPrimitiveCollection(
    final @NotNull String field,
    final @NotNull Function<JsonElement, T> reader,
    final @NotNull Function<Integer, C> collectionFactory
  ) {
    final var array = this.jsonObject.getAsJsonArray(field);
    if (array == null) {
      return null;
    }
    final var objects = collectionFactory.apply(array.size());
    for (final var element : array) {
      final var object = reader.apply(element);
      objects.add(object);
    }
    return objects;
  }

  public <T> @Nullable T[] readPrimitiveArray(
    final @NotNull String field,
    final @NotNull Function<JsonElement, T> reader,
    final @NotNull Function<Integer, T[]> arrayFactory
  ) {
    final var array = this.jsonObject.getAsJsonArray(field);
    if (array == null) {
      return null;
    }
    final var objects = arrayFactory.apply(array.size());
    for (int i = 0; i < array.size(); i++) {
      final var element = array.get(i);
      final var object = reader.apply(element);
      objects[i] = object;
    }
    return objects;
  }

  @Override
  public @Nullable UUID readDetailedUuid(final @NotNull String field) {
    final var element = this.jsonObject.get(field);
    if (!(element instanceof JsonObject serializedUuid)) {
      return null;
    }
    return this.readDetailedUuid(serializedUuid);
  }

  @Override
  public @Nullable <C extends Collection<UUID>> C readDetailedUuids(
    final @NotNull String field,
    final @NotNull Function<Integer, C> factory
  ) {
    final var array = this.jsonObject.getAsJsonArray(field);
    if (array == null) {
      return null;
    }
    final var uuids = factory.apply(array.size());
    for (final var element : array) {
      if (!(element instanceof JsonObject serializedUuid)) {
        continue;
      }
      final var uuid = this.readDetailedUuid(serializedUuid);
      uuids.add(uuid);
    }
    return uuids;
  }

  public <K, V, M extends Map<K, V>> @Nullable M readPrimitiveMap(
    final @NotNull String field,
    final @NotNull Function<String, K> keyParser,
    final @NotNull Function<JsonElement, V> valueParser,
    final @NotNull Function<Integer, M> mapFactory
  ) {
    final var element = this.jsonObject.get(field);
    if (!(element instanceof JsonObject object)) {
      return null;
    }
    final var entrySet = object.entrySet();
    final var map = mapFactory.apply(entrySet.size());
    for (final var entry : entrySet) {
      final var key = keyParser.apply(entry.getKey());
      final var value = valueParser.apply(entry.getValue());
      map.put(key, value);
    }
    return map;
  }

  public @Nullable UUID readDetailedUuid(final @NotNull JsonObject serializedUuid) {
    final var mostSignificantBitsElement = serializedUuid.get("most");
    final var leastSignificantBitsElement = serializedUuid.get("least");
    if (mostSignificantBitsElement == null || leastSignificantBitsElement == null) {
      return null;
    }
    return new UUID(mostSignificantBitsElement.getAsLong(), leastSignificantBitsElement.getAsLong());
  }
}
