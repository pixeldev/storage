package team.emptyte.storage.aggregate.infrastructure.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.emptyte.storage.aggregate.domain.AggregateRoot;
import team.emptyte.storage.aggregate.domain.repository.AsyncAggregateRootRepository;

@SuppressWarnings("unused")
public final class YamlAggregateRootRepository<AggregateType extends AggregateRoot> extends AsyncAggregateRootRepository<AggregateType> {
  private static final String FILE_EXTENSION = ".yml";
  private static final String FILE_FORMAT = "%s" + FILE_EXTENSION;

  private final Class<AggregateType> aggregateType;
  private final Path folderPath;
  private final boolean prettyPrinting;
  private final UnaryOperator<ConfigurationOptions> defaultOptions;

  YamlAggregateRootRepository(
    final @NotNull Executor executor,
    final @NotNull Class<AggregateType> aggregateType,
    final @NotNull Path folderPath,
    final boolean prettyPrinting,
    final @NotNull TypeSerializerCollection typeSerializers
  ) {
    super(executor);
    this.aggregateType = aggregateType;
    this.prettyPrinting = prettyPrinting;
    this.folderPath = folderPath;
    this.defaultOptions = configurationOptions -> configurationOptions.serializers(typeSerializers);
  }

  public static <T extends AggregateRoot> @NotNull YamlAggregateRootRepositoryBuilder<T> builder(final @NotNull Class<T> type) {
    return new YamlAggregateRootRepositoryBuilder<>(type);
  }

  public @NotNull String formatFileName(final @NotNull String id) {
    return String.format(FILE_FORMAT, id);
  }

  public @NotNull Path filePath(final @NotNull String id) {
    return this.folderPath.resolve(this.formatFileName(id));
  }

  public @NotNull YamlConfigurationLoader loader(final @NotNull String id) {
    return this.loader(this.filePath(id));
  }

  public @NotNull YamlConfigurationLoader loader(final @NotNull Path path) {
    return YamlConfigurationLoader.builder()
      .path(path)
      .defaultOptions(this.defaultOptions)
      .nodeStyle(this.prettyPrinting ? NodeStyle.BLOCK : NodeStyle.FLOW)
      .build();
  }

  public @NotNull CommentedConfigurationNode loadNode(final @NotNull String id) {
    try {
      return this.loader(id).load();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public @NotNull CommentedConfigurationNode loadNode(final @NotNull Path path) {
    try {
      return this.loader(path).load();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public @Nullable AggregateType loadSync(final @NotNull String id) {
    try {
      return this.loadNode(id)
        .get(this.aggregateType);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public @Nullable AggregateType loadSync(final @NotNull Path path) {
    try {
      return this.loadNode(path)
        .get(this.aggregateType);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean deleteSync(final @NotNull String id) {
    try {
      return Files.deleteIfExists(this.filePath(id));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAllSync() {
    try (final Stream<Path> paths = Files.list(this.folderPath)) {
      paths.filter(path -> path.toString().endsWith(FILE_EXTENSION))
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

  @Override
  public @Nullable AggregateType deleteAndRetrieveSync(final @NotNull String id) {
    final AggregateType aggregateType = this.loadSync(id);
    if (aggregateType != null) {
      this.deleteSync(id);
    }
    return aggregateType;
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return Files.exists(this.filePath(id));
  }

  @Override
  public @Nullable AggregateType findSync(final @NotNull String id) {
    return this.loadSync(id);
  }

  @Override
  public <C extends Collection<@NotNull AggregateType>> @Nullable C findAllSync(final @NotNull Consumer<@NotNull AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    final C foundModels = factory.apply(1);
    this.forEach(model -> {
      postLoadAction.accept(model);
      foundModels.add(model);
    });
    return foundModels;
  }

  @Override
  public void forEach(final @NotNull Consumer<? super AggregateType> action) {
    try (final Stream<Path> paths = Files.list(this.folderPath)) {
      paths.filter(path -> path.toString().endsWith(FILE_EXTENSION))
        .forEach(path -> {
          final AggregateType aggregateType = this.loadSync(path);
          if (aggregateType != null) {
            action.accept(aggregateType);
          }
        });
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @NotNull Set<@NotNull String> findIdsSync() {
    try (final Stream<Path> paths = Files.list(this.folderPath)) {
      return paths.filter(path -> path.toString().endsWith(FILE_EXTENSION))
        .map(path -> path.getFileName().toString().replace(FILE_EXTENSION, ""))
        .collect(Collectors.toSet());
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
  public @NotNull AggregateType saveSync(@NotNull final AggregateType aggregateType) {
    try {
      final YamlConfigurationLoader loader = this.loader(aggregateType.id());
      loader.save(loader.createNode().set(this.aggregateType, aggregateType));
      return aggregateType;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
