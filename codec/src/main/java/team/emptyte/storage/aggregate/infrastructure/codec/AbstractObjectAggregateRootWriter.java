package team.emptyte.storage.aggregate.infrastructure.codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class AbstractObjectAggregateRootWriter<WriteType> implements AggregateRootWriter<WriteType> {
  @Override
  @Contract("_, _ -> this")
  public @NotNull AggregateRootWriter<WriteType> writeThis(final @NotNull String key, final @Nullable WriteType value) {
    return this.writeObject(key, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull AggregateRootWriter<WriteType> writeUuid(final @NotNull String field, final @Nullable UUID uuid) {
    if (uuid == null) {
      return this.writeString(field, null);
    }
    return this.writeObject(field, uuid.toString());
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull AggregateRootWriter<WriteType> writeString(final @NotNull String field, final @Nullable String value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull AggregateRootWriter<WriteType> writeNumber(final @NotNull String field, final @Nullable Number value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _ -> this")
  public @NotNull AggregateRootWriter<WriteType> writeBoolean(final @NotNull String field, final @Nullable Boolean value) {
    return this.writeObject(field, value);
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull AggregateRootWriter<WriteType> writeObject(
    final @NotNull String field,
    final @Nullable T child,
    final @NotNull AggregateRootSerializer<T, WriteType> modelSerializer
  ) {
    if (child == null) {
      return this.writeObject(field, null);
    }
    return this.writeObject(field, modelSerializer.serialize(child));
  }

  @Override
  @Contract("_, _ -> this")
  public <T> @NotNull AggregateRootWriter<WriteType> writeRawCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children
  ) {
    return this.writeObject(field, children);
  }

  @Override
  @Contract("_, _, _ -> this")
  public <T> @NotNull AggregateRootWriter<WriteType> writeCollection(
    final @NotNull String field,
    final @Nullable Collection<T> children,
    final @NotNull AggregateRootSerializer<T, WriteType> modelSerializer
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

  protected abstract @NotNull AggregateRootWriter<WriteType> writeObject(
    final @NotNull String field,
    final @Nullable Object value
  );
}
