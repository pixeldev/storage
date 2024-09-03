package team.emptyte.storage.aggregate.infrastructure.gson;

import com.google.gson.JsonObject;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.emptyte.storage.aggregate.domain.AggregateRoot;
import team.emptyte.storage.aggregate.domain.repository.AsyncAggregateRootRepository;
import team.emptyte.storage.aggregate.infrastructure.codec.AggregateRootDeserializer;
import team.emptyte.storage.aggregate.infrastructure.codec.AggregateRootSerializer;

@SuppressWarnings("unused")
public final class GsonAggregateRootRepository<AggregateType extends AggregateRoot> extends AsyncAggregateRootRepository<AggregateType> {
  private final Path folderPath;
  private final boolean prettyPrinting;
  private final AggregateRootSerializer<AggregateType, JsonObject> aggregateRootSerializer;
  private final AggregateRootDeserializer<AggregateType, JsonObject> aggregateRootDeserializer;

  GsonAggregateRootRepository(
    final @NotNull Executor executor,
    final @NotNull Path folderPath,
    final boolean prettyPrinting,
    final @NotNull AggregateRootSerializer<AggregateType, JsonObject> aggregateRootSerializer,
    final @NotNull AggregateRootDeserializer<AggregateType, JsonObject> aggregateRootDeserializer
  ) {
    super(executor);
    this.prettyPrinting = prettyPrinting;
    this.folderPath = folderPath;
    this.aggregateRootSerializer = aggregateRootSerializer;
    this.aggregateRootDeserializer = aggregateRootDeserializer;
  }

  public static <T extends AggregateRoot> @NotNull GsonAggregateRootRepositoryBuilder<T> builder() {
    return new GsonAggregateRootRepositoryBuilder<>();
  }

  @Override
  public @Nullable AggregateType findSync(final @NotNull String id) {
    return this.internalFind(this.resolveChild(id));
  }

  public @NotNull String extractId(final @NotNull Path file) {
    return file.getFileName().toString().substring(0, file.getFileName().toString().length() - 5);
  }

  public @NotNull Path resolveChild(final @NotNull String id) {
    return this.folderPath.resolve(id + ".json");
  }

  @Override
  public @NotNull Set<@NotNull String> findIdsSync() {
    try (final var directoryStream = Files.newDirectoryStream(this.folderPath)) {
      final var foundIds = new HashSet<String>();
      directoryStream.forEach(path -> foundIds.add(this.extractId(path)));
      return foundIds;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <C extends Collection<@NotNull AggregateType>> @NotNull C findAllSync(final @NotNull Consumer<@NotNull AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final var foundModels = factory.apply(1);
    this.forEach(model -> {
      postLoadAction.accept(model);
      foundModels.add(model);
    });
    return foundModels;
  }

  @Override
  public void forEach(final @NotNull Consumer<? super AggregateType> action) {
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
  public @NotNull Iterator<AggregateType> iterator() {
    return new Iterator<>() {
      private final Iterator<String> ids = findIdsSync().iterator();

      @Override
      public boolean hasNext() {
        return this.ids.hasNext();
      }

      @Override
      public AggregateType next() {
        return findSync(this.ids.next());
      }
    };
  }

  @Override
  public @NotNull Iterator<String> iteratorIdsSync() {
    return this.findIdsSync().iterator();
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return Files.exists(this.resolveChild(id));
  }

  @Override
  public @NotNull AggregateType saveSync(final @NotNull AggregateType aggregateType) {
    final var modelPath = this.resolveChild(aggregateType.id());
    try {
      if (Files.notExists(modelPath)) {
        Files.createFile(modelPath);
      }
      try (final var writer = new JsonWriter(Files.newBufferedWriter(modelPath, StandardCharsets.UTF_8))) {
        writer.setSerializeNulls(false);
        if (this.prettyPrinting) {
          writer.setIndent("  ");
        }
        final var jsonObject = this.aggregateRootSerializer.serialize(aggregateType);
        TypeAdapters.JSON_ELEMENT.write(writer, jsonObject);
        return aggregateType;
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean deleteSync(final @NotNull String id) {
    try {
      return Files.deleteIfExists(this.resolveChild(id));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nullable AggregateType deleteAndRetrieveSync(final @NotNull String id) {
    final var model = this.findSync(id);
    if (model != null) {
      this.deleteSync(id);
    }
    return model;
  }

  @Override
  public void deleteAllSync() {
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

  public @Nullable AggregateType internalFind(final @NotNull Path file) {
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
      return this.aggregateRootDeserializer.deserialize(jsonObject);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
