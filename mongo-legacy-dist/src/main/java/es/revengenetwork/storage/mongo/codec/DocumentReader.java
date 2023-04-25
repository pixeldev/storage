package es.revengenetwork.storage.mongo.codec;

import es.revengenetwork.storage.codec.ModelDeserializer;
import es.revengenetwork.storage.codec.ModelReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DocumentReader implements ModelReader<Document> {
  public static final Function<Document, DocumentReader> FACTORY = DocumentReader::new;
  protected final Document document;

  protected DocumentReader(final @NotNull Document document) {
    this.document = document;
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull DocumentReader create(final @NotNull Document document) {
    return new DocumentReader(document);
  }

  @Override
  public @NotNull Document raw() {
    return this.document;
  }

  @Override
  public @Nullable Document readThis(final @NotNull String field) {
    return this.document.get(field, Document.class);
  }

  @Override
  public @Nullable UUID readDetailedUuid(final @NotNull String field) {
    return this.readDetailedUuid(this.readThis(field));
  }

  @Override
  public @Nullable <C extends Collection<UUID>> C readDetailedUuids(
    final @NotNull String field,
    final @NotNull Function<Integer, C> factory
  ) {
    final var documents = this.readRawCollection(field, Document.class, ArrayList::new);
    if (documents == null) {
      return null;
    }
    final var uuids = factory.apply(documents.size());
    for (final var document : documents) {
      final var uuid = this.readDetailedUuid(document);
      if (uuid != null) {
        uuids.add(uuid);
      }
    }
    return uuids;
  }

  private @Nullable UUID readDetailedUuid(final @Nullable Document document) {
    if (document == null) {
      return null;
    }
    final var most = document.getLong("most");
    final var least = document.getLong("least");
    return new UUID(most, least);
  }

  @Override
  public @Nullable String readString(final @NotNull String field) {
    return this.document.getString(field);
  }

  @Override
  public @Nullable Number readNumber(final @NotNull String field) {
    return (Number) this.document.get(field);
  }

  @Override
  public boolean readBoolean(final @NotNull String field) {
    final var value = this.document.getBoolean(field);
    if (value == null) {
      return false;
    }
    return value;
  }

  @Override
  public <T, C extends Collection<T>> @Nullable C readRawCollection(
    final @NotNull String field,
    final @NotNull Class<T> clazz,
    final @NotNull Function<Integer, C> collectionFactory
  ) {
    final var value = this.document.get(field, List.class);
    if (value == null) {
      return null;
    }
    final var collection = collectionFactory.apply(value.size());
    for (final var object : value) {
      collection.add(clazz.cast(object));
    }
    return collection;
  }

  @Override
  public <T> @Nullable T readObject(
    final @NotNull String field,
    final @NotNull ModelDeserializer<T, Document> modelDeserializer
  ) {
    final var child = this.document.get(field, Document.class);
    if (child == null) {
      return null;
    }
    return modelDeserializer.deserialize(child);
  }

  @Override
  public @Nullable <K, V> Map<K, V> readMap(
    final @NotNull String field,
    final @NotNull Function<V, K> keyParser,
    final @NotNull ModelDeserializer<V, Document> reader
  ) {
    final var documents = this.readRawCollection(field, Document.class, ArrayList::new);
    if (documents == null) {
      return null;
    }
    final var map = new HashMap<K, V>(documents.size());
    for (final var document : documents) {
      final var value = reader.deserialize(document);
      map.put(keyParser.apply(value), value);
    }
    return map;
  }

  @Override
  public <T, C extends Collection<T>> @Nullable C readCollection(
    final @NotNull String field,
    final @NotNull Function<Integer, C> collectionFactory,
    final @NotNull ModelDeserializer<T, Document> modelDeserializer
  ) {
    final var documents = this.readRawCollection(field, Document.class, ArrayList::new);
    if (documents == null) {
      return null;
    }
    final var children = collectionFactory.apply(documents.size());
    for (final var document : documents) {
      children.add(modelDeserializer.deserialize(document));
    }
    return children;
  }
}
