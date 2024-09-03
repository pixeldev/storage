package team.emptyte.storage.aggregate.domain.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.emptyte.storage.aggregate.domain.AggregateRoot;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * This class is the base for all the asynchronous repositories, it contains the essential methods to
 * interact with the database, cache, or whatever you want to use to store your data using {@link CompletableFuture}
 * to handle when the operations are finished successfully or not. It's important
 * to note that this class is not a singleton, so you can create as many instances as you want.
 *
 * @param <AggregateType> The {@link AggregateRoot} type that this repository will handle.
 * @since 1.0.0
 */
public abstract class AsyncAggregateRootRepository<AggregateType extends AggregateRoot> implements AggregateRootRepository<AggregateType> {
  private final Executor executor;

  /**
   * Creates a new {@link AsyncAggregateRootRepository} with the specified {@link Executor}.
   *
   * @param executor The {@link Executor} that will be used to execute the asynchronous operations.
   * @since 1.0.0
   */
  protected AsyncAggregateRootRepository(final @NotNull Executor executor) {
    this.executor = executor;
  }

  /**
   * Returns the {@link Executor} that will be used to execute the asynchronous operations.
   *
   * @return The {@link Executor} that will be used to execute the asynchronous operations.
   * @see CompletableFuture#runAsync(Runnable, Executor)
   * @see CompletableFuture#supplyAsync(Supplier, Executor)
   * @since 1.0.0
   */
  public @NotNull Executor executor() {
    return this.executor;
  }

  /**
   * This method executes and wraps the {@link #deleteSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return A {@link CompletableFuture} that will complete with true if the {@link AggregateType} was deleted successfully, false otherwise.
   * @see #deleteSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteSync(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #deleteAllSync()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #deleteAllSync()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> deleteAllAsync() {
    return CompletableFuture.runAsync(this::deleteAllSync, this.executor);
  }

  /**
   * This method executes and wraps the {@link #deleteAndRetrieveSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return A {@link CompletableFuture} that will complete with the deleted {@link AggregateType}, or {@code null} if it doesn't exist.
   * @see #deleteAndRetrieveSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> deleteAndRetrieveAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteAndRetrieveSync(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #existsSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with true if the {@link AggregateType} with the specified id exists, false otherwise.
   * @see #existsSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsSync(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> findAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findSync(id), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findAllSync(IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see #findAllSync(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @NotNull CompletableFuture<@Nullable C> findAllAsync(final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAllSync(factory), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findAllSync(Consumer, IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see #findAllSync(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @NotNull CompletableFuture<@Nullable C> findAllAsync(
    final @NotNull Consumer<@NotNull AggregateType> postLoadAction,
    final @NotNull IntFunction<@NotNull C> factory
  ) {
    return CompletableFuture.supplyAsync(() -> this.findAllSync(postLoadAction, factory), this.executor);
  }

  /**
   * This method executes and wraps the {@link #findIdsSync()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete with a {@link Set} containing all the ids of the repository.
   * @see #findIdsSync()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable Set<@NotNull String>> findIdsAsync() {
    return CompletableFuture.supplyAsync(this::findIdsSync, this.executor);
  }

  /**
   * This method iterates over all the {@link AggregateType}s in the repository and executes the specified action for each one
   * in a {@link CompletableFuture} with the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #forEach(Consumer)
   * @since 1.0.0
   */
  public CompletableFuture<@NotNull Void> forEachAsync(final @NotNull Consumer<? super AggregateType> action) {
    return CompletableFuture.runAsync(() -> this.forEach(action), this.executor);
  }

  /**
   * This method iterates over all the {@link AggregateType}s id's in the repository and executes the specified action for each one
   * in a {@link CompletableFuture} with the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete when the operation is finished.
   * @see #forEachIdsSync(Consumer)
   * @since 1.0.0
   */
  public CompletableFuture<@NotNull Void> forEachIdsAsync(final @NotNull Consumer<? super String> action) {
    return CompletableFuture.runAsync(() -> this.forEachIdsSync(action), this.executor);
  }

  /**
   * This method executes and wraps the {@link #saveSync(AggregateRoot)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param aggregateType The {@link AggregateType} to save.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} that was saved.
   * @see #saveSync(AggregateRoot)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> saveAsync(final @NotNull AggregateType aggregateType) {
    return CompletableFuture.supplyAsync(() -> this.saveSync(aggregateType), this.executor);
  }
}
