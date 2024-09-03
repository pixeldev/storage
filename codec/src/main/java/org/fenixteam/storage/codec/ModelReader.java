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
package org.fenixteam.storage.codec;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ModelReader<ReadType> {
  @NotNull ReadType raw();

  @Nullable ReadType readThis(final @NotNull String field);

  default @Nullable UUID readUuid(final @NotNull String field) {
    final var uuidString = this.readString(field);
    if (uuidString == null) {
      return null;
    }
    return UUID.fromString(uuidString);
  }

  @Nullable UUID readDetailedUuid(final @NotNull String field);

  @Nullable <C extends Collection<UUID>> C readDetailedUuids(
    final @NotNull String field,
    final @NotNull Function<Integer, C> factory
  );

  default @Nullable Date readDate(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return null;
    }
    return new Date(value.longValue());
  }

  @Nullable String readString(final @NotNull String field);

  @Nullable Number readNumber(final @NotNull String field);

  default int readInt(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.intValue();
  }

  default long readLong(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.longValue();
  }

  default double readDouble(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.doubleValue();
  }

  default float readFloat(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.floatValue();
  }

  default short readShort(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.shortValue();
  }

  default byte readByte(final @NotNull String field) {
    final var value = this.readNumber(field);
    if (value == null) {
      return 0;
    }
    return value.byteValue();
  }

  boolean readBoolean(final @NotNull String field);

  <T, C extends Collection<T>> @Nullable C readRawCollection(
    final @NotNull String field,
    final @NotNull Class<T> clazz,
    final @NotNull Function<Integer, C> collectionFactory
  );

  <T> @Nullable T readObject(
    final @NotNull String field,
    final @NotNull ModelDeserializer<T, ReadType> modelDeserializer
  );

  <K, V> @Nullable Map<K, V> readMap(
    final @NotNull String field,
    final @NotNull Function<V, K> keyParser,
    final @NotNull ModelDeserializer<V, ReadType> modelDeserializer
  );

  <T, C extends Collection<T>> @Nullable C readCollection(
    final @NotNull String field,
    final @NotNull Function<Integer, C> collectionFactory,
    final @NotNull ModelDeserializer<T, ReadType> modelDeserializer
  );
}
