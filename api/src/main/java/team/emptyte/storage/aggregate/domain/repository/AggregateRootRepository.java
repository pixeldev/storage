package team.emptyte.storage.aggregate.domain.repository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.emptyte.storage.aggregate.domain.AggregateRoot;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * This class is the base of our repositories, it contains the essential methods to interact with
 * the database, cache, or whatever you want to use to store your data.
 *
 * @param <AggregateType> The {@link AggregateRoot} type that this repository will handle.
 * @since 1.0.0
 */
public interface AggregateRootRepository<AggregateType extends AggregateRoot> extends Iterable<AggregateType> {
  /**
   * Deletes the specified {@link AggregateType} from the repository. Note that this method doesn't return
   * the deleted {@link AggregateType}, if you want to retrieve the deleted {@link AggregateType} use
   * {@link #deleteAndRetrieveSync(String)}. Also, this method can delete permanently the {@link AggregateType}
   * from the repository, so it's important to note that this method can be dangerous if you don't
   * know what you're doing.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return {@code true} if the {@link AggregateType} was deleted successfully, {@code false} otherwise.
   * @since 1.0.0
   */
  boolean deleteSync(final @NotNull String id);

  /**
   * Deletes all the {@link AggregateType}s from the repository.
   *
   * @since 1.0.0
   */
  void deleteAllSync();

  /**
   * Deletes the specified {@link AggregateType} from the repository and returns it.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return The deleted {@link AggregateType}, or {@code null} if it doesn't exist.
   * @since 1.0.0
   */
  @Nullable AggregateType deleteAndRetrieveSync(final @NotNull String id);

  /**
   * Checks if the {@link AggregateType} with the specified id exists in the repository.
   *
   * @param id The id of the {@link AggregateType}.
   * @return {@code true} if the {@link AggregateType} with the specified id exists, {@code false} otherwise.
   * @since 1.0.0
   */
  boolean existsSync(final @NotNull String id);

  /**
   * Returns the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   *
   * @param id The id of the {@link AggregateType}.
   * @return The {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @since 1.0.0
   */
  @Nullable AggregateType findSync(final @NotNull String id);

  /**
   * Finds all the {@link AggregateType} in the repository and returns them in the specified {@link Collection}.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link AggregateType}s in the repository or {@code null} if the repository is empty.
   * @since 1.0.0
   */
  default <C extends Collection<@NotNull AggregateType>> @Nullable C findAllSync(final @NotNull IntFunction<@NotNull C> factory) {
    return findAllSync(__ -> {}, factory);
  }

  /**
   * Finds all the models in the repository and returns them in the specified {@link Collection}, it also executes
   * the specified action for each model after it's loaded, so you mustn't iterate over the returned {@link Collection}.
   *
   * @param postLoadAction The action to execute for each {@link AggregateType} after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link AggregateType} in the repository or {@code null} if the repository is empty.
   * @since 1.0.0
   */
  <C extends Collection<@NotNull AggregateType>> @Nullable C findAllSync(
    final @NotNull Consumer<@NotNull AggregateType> postLoadAction,
    final @NotNull IntFunction<@NotNull C> factory
  );

  /**
   * Returns a {@link Set} of all the ids of the {@link AggregateType}s in the repository.
   *
   * @return A {@link Set} containing all the ids of the {@link AggregateType}s in the repository or {@code null} if the repository is empty.
   * @since 1.0.0
   */
  @NotNull Set<@NotNull String> findIdsSync();

  /**
   * Iterates over the {@link AggregateType}s in the repository and executes the specified action for each one.
   *
   * @param action The action to be performed for each element in the repository.
   * @since 1.0.0
   */
  @Override
  default void forEach(final @NotNull Consumer<? super AggregateType> action) {
    for (final AggregateType aggregate : this) {
      action.accept(aggregate);
    }
  }

  /**
   * Iterates over the ids of the {@link AggregateType}s in the repository and executes the specified action for each one.
   *
   * @param action The action to be performed for each element in the repository.
   * @since 1.0.0
   */
  default void forEachIdsSync(final @NotNull Consumer<? super String> action) {
    final var iterator = this.iteratorIdsSync();
    while (iterator.hasNext()) {
      action.accept(iterator.next());
    }
  }

  /**
   * Returns the {@link Iterator} of the {@link AggregateType}s in the repository.
   *
   * @return The {@link Iterator} of the {@link AggregateType}s in the repository.
   * @since 1.0.0
   */
  @Override
  @NotNull Iterator<AggregateType> iterator();

  /**
   * Returns the {@link Iterator} of the ids of the ids which represents the {@link AggregateType}s in the repository.
   *
   * @return The {@link Iterator} of the ids of the ids which represents the {@link AggregateType}s in the repository.
   * @since 1.0.0
   */
  @NotNull Iterator<String> iteratorIdsSync();

  /**
   * Saves the specified {@link AggregateType} in the repository.
   *
   * @param aggregateType The {@link AggregateType} to save.
   * @return The saved {@link AggregateType}.
   * @since 1.0.0
   */
  @Contract("_ -> param1")
  @NotNull AggregateType saveSync(final @NotNull AggregateType aggregateType);
}
