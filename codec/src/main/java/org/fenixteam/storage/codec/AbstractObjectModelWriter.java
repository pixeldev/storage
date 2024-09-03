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

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractObjectModelWriter<WriteType> implements ModelWriter<WriteType> {
  @Override
  @Contract("_, _ -> this")
  public @NotNull ModelWriter<WriteType> writeThis(final @NotNull String key, final @Nullable WriteType value) {
    return this.writeObject(key, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull ModelWriter<WriteType> writeUuid(final @NotNull String field, final @Nullable UUID uuid) {
    if (uuid == null) {
      return this.writeString(field, null);
    }
    return this.writeObject(field, uuid.toString());
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull ModelWriter<WriteType> writeString(final @NotNull String field, final @Nullable String value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull ModelWriter<WriteType> writeNumber(final @NotNull String field, final @Nullable Number value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull ModelWriter<WriteType> writeBoolean(final @NotNull String field, final @Nullable Boolean value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull ModelWriter<WriteType> writeObject(
    final @NotNull String field,
    final @Nullable T child,
    final @NotNull ModelSerializer<T, WriteType> modelSerializer
  ) {
    if (child == null) {
      return this.writeObject(field, null);
    }
    return this.writeObject(field, modelSerializer.serialize(child));
  }

  @Override
  @Contract("_, _ -> this")
  public <T> @NotNull ModelWriter<WriteType> writeRawCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children
  ) {
    return this.writeObject(field, children);
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull ModelWriter<WriteType> writeCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children,
    final @NotNull ModelSerializer<T, WriteType> modelSerializer
  ) {
    if (children == null) {
      return this.writeObject(field, null);
    }
    final var documents = new ArrayList<WriteType>(children.size());
    for (final var child : children) {
      documents.add(modelSerializer.serialize(child));
    }
    return this.writeObject(field, documents);
  }

  protected abstract @NotNull ModelWriter<WriteType> writeObject(
    final @NotNull String field,
    final @Nullable Object value
  );
}
