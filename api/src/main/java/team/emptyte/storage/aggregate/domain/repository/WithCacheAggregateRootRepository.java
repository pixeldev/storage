
package team.emptyte.storage.aggregate.domain.repository;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.emptyte.storage.aggregate.domain.AggregateRoot;

/**
 * This class is a {@link AggregateRootRepository} that uses two {@link AggregateRootRepository}s to store the {@link AggregateType}s.
 * The first one is the {@link #storageRepository}, which is used to store the {@link AggregateType}s in a persistent way.
 * The second one is the {@link #cacheRepository}, which is mainly used to cache the {@link AggregateType}s.
 *
 * @param <AggregateType> The type of the {@link AggregateRoot} that this {@link AggregateRootRepository} will store.
 * @see AggregateRootRepository
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class WithCacheAggregateRootRepository<AggregateType extends AggregateRoot> extends AsyncAggregateRootRepository<AggregateType> {
  /**
   * The {@link AggregateRootRepository} that will be used as a fallback if the {@link #storageRepository} doesn't have the
   * {@link AggregateType} we are looking for. Mainly used to cache the {@link AggregateType}s.
   */
  private final AggregateRootRepository<AggregateType> cacheRepository;
  /**
   * The {@link AggregateRootRepository} that will be used as the main repository. Mainly used to store the {@link AggregateType}s
   * in a persistent way.
   */
  private final AggregateRootRepository<AggregateType> storageRepository;

  /**
   * This constructor creates a new {@link WithCacheAggregateRootRepository} with the specified {@link Executor}, {@link AggregateRootRepository}
   * as the {@link #cacheRepository} and {@link #storageRepository}.
   *
   * @param executor          The {@link Executor} to use.
   * @param cacheRepository   The {@link AggregateRootRepository} to use as the {@link #cacheRepository}.
   * @param storageRepository The {@link AggregateRootRepository} to use as the {@link #storageRepository}.
   * @since 1.0.0
   */
  public WithCacheAggregateRootRepository(
    final @NotNull Executor executor,
    final @NotNull AggregateRootRepository<AggregateType> cacheRepository,
    final @NotNull AggregateRootRepository<AggregateType> storageRepository
  ) {
    super(executor);
    this.cacheRepository = cacheRepository;
    this.storageRepository = storageRepository;
  }

  @Override
  public boolean deleteSync(final @NotNull String id) {
    return this.storageRepository.deleteSync(id);
  }

  @Override
  public void deleteAllSync() {
    this.storageRepository.deleteAllSync();
  }

  /**
   * This method uses the {@link AggregateRootRepository#deleteAllSync()} of the {@link #cacheRepository} to delete all the
   * {@link AggregateType}s in the repository.
   *
   * @see AggregateRootRepository#deleteAllSync()
   * @since 1.0.0
   */
  public void deleteAllInCache() {
    this.cacheRepository.deleteAllSync();
  }

  /**
   * This method executes and wraps the {@link #deleteAllInCache()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete when all the {@link AggregateType}s in the repository are deleted.
   * @see #deleteAllInCache()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> deleteAllInCacheAsync() {
    return CompletableFuture.runAsync(this::deleteAllInCache, this.executor());
  }

  @Override
  public @Nullable AggregateType deleteAndRetrieveSync(final @NotNull String id) {
    return this.storageRepository.deleteAndRetrieveSync(id);
  }

  /**
   * This method uses the {@link AggregateRootRepository#deleteAndRetrieveSync(String)} of the {@link #cacheRepository} to delete the
   * {@link AggregateType} with the specified id.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return The deleted {@link AggregateType}, or {@code null} if it doesn't exist.
   * @see AggregateRootRepository#deleteAndRetrieveSync(String)
   * @since 1.0.0
   */
  public @Nullable AggregateType deleteAndRetrieveInCacheSync(final @NotNull String id) {
    return this.cacheRepository.deleteAndRetrieveSync(id);
  }

  /**
   * This method executes and wraps the {@link #deleteAndRetrieveInCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return A {@link CompletableFuture} that will complete with the deleted {@link AggregateType}, or {@code null} if it doesn't exist.
   * @see #deleteAndRetrieveInCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> deleteAndRetrieveInCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteAndRetrieveInCacheSync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#deleteSync(String)} (String)} of the {@link #cacheRepository} and the
   * {@link AggregateRootRepository#deleteSync(String)} of the {@link #storageRepository} to delete the {@link AggregateType}
   * with the specified id.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return {@code true} if the {@link AggregateType} was deleted successfully in both repositories, {@code false} otherwise.
   * @see AggregateRootRepository#deleteSync(String)
   * @since 1.0.0
   */
  public boolean deleteInBothSync(final @NotNull String id) {
    return this.cacheRepository.deleteSync(id) && this.storageRepository.deleteSync(id);
  }

  /**
   * This method executes and wraps the {@link #deleteInBothSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link AggregateType} was deleted successfully in both repositories, {@code false} otherwise.
   * @see #deleteInBothSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteInBothSync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#deleteSync(String)} of the {@link #cacheRepository} to delete the
   * {@link AggregateType} with the specified id.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return {@code true} if the {@link AggregateType} was deleted successfully, {@code false} otherwise.
   * @see AggregateRootRepository#deleteSync(String)
   * @since 1.0.0
   */
  public boolean deleteInCacheSync(final @NotNull String id) {
    return this.cacheRepository.deleteSync(id);
  }

  /**
   * This method executes and wraps the {@link #deleteInCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType} to delete.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link AggregateType} was deleted successfully, {@code false} otherwise.
   * @see #deleteInCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> deleteInCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.deleteInCacheSync(id), this.executor());
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.storageRepository.existsSync(id);
  }

  /**
   * This method uses the {@link AggregateRootRepository#existsSync(String)} of the {@link #cacheRepository} to check if
   * the {@link AggregateType} with the specified id exists, and if it doesn't, it uses the {@link AggregateRootRepository#existsSync(String)}
   * of the {@link #storageRepository} to check if the {@link AggregateType} with the specified id exists.
   *
   * @param id The id of the {@link AggregateType}.
   * @return {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository} or in the {@link #storageRepository}, {@code false} otherwise.
   * @see AggregateRootRepository#existsSync(String)
   * @since 1.0.0
   */
  public boolean existsInAnySync(final @NotNull String id) {
    return this.existsInCacheSync(id) || this.storageRepository.existsSync(id);
  }

  /**
   * This method executes and wraps the {@link #existsInAnySync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository} or in the {@link #storageRepository}, {@code false} otherwise.
   * @see #existsInAnySync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInAnyAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInAnySync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#existsSync(String)} of the {@link #cacheRepository} and the
   * {@link AggregateRootRepository#existsSync(String)} of the {@link #storageRepository} to check if the {@link AggregateType}
   * with the specified id exists in both repositories.
   *
   * @param id The id of the {@link AggregateType}.
   * @return {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository} and in the {@link #storageRepository}, {@code false} otherwise.
   * @see AggregateRootRepository#existsSync(String)
   * @since 1.0.0
   */
  public boolean existsInBothSync(final @NotNull String id) {
    return this.existsInCacheSync(id) && this.storageRepository.existsSync(id);
  }

  /**
   * This method executes and wraps the {@link #existsInBothSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository} and in the {@link #storageRepository}, {@code false} otherwise.
   * @see #existsInBothSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInBothSync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#existsSync(String)} of the {@link #cacheRepository} to check if
   * the {@link AggregateType} with the specified id exists.
   *
   * @param id The id of the {@link AggregateType}.
   * @return {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository}, {@code false} otherwise.
   * @see AggregateRootRepository#existsSync(String)
   * @since 1.0.0
   */
  public boolean existsInCacheSync(final @NotNull String id) {
    return this.cacheRepository.existsSync(id);
  }

  /**
   * This method executes and wraps the {@link #existsInCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with {@code true} if the {@link AggregateType} with the specified id exists in the {@link #cacheRepository}, {@code false} otherwise.
   * @see #existsInCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Boolean> existsInCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.existsInCacheSync(id), this.executor());
  }

  /**
   * This method returns the {@link #cacheRepository}.
   *
   * @return The {@link #cacheRepository}.
   * @since 1.0.0
   */
  public @NotNull AggregateRootRepository<AggregateType> cacheAggregateRootRepository() {
    return this.cacheRepository;
  }

  @Override
  public @Nullable AggregateType findSync(final @NotNull String id) {
    return this.storageRepository.findSync(id);
  }

  @Override
  public <C extends Collection<@NotNull AggregateType>> @Nullable C findAllSync(final @NotNull Consumer<AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return this.storageRepository.findAllSync(postLoadAction, factory);
  }

  /**
   * This method uses the {@link AggregateRootRepository#findIdsSync()} of the {@link #cacheRepository} to find the
   * ids of the repository.
   *
   * @return A {@link Collection} containing all the ids of the repository.
   * @see AggregateRootRepository#findIdsSync()
   * @since 1.0.0
   */
  public @NotNull Set<@NotNull String> findAllIdsInCacheSync() {
    return this.cacheRepository.findIdsSync();
  }

  /**
   * This method executes and wraps the {@link #findAllIdsInCacheSync()} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the ids of the repository.
   * @see #findAllIdsInCacheSync()
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable Collection<@NotNull String>> findAllIdsInCacheAsync() {
    return CompletableFuture.supplyAsync(this::findAllIdsInCacheSync, this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#findAllSync(IntFunction)} of the {@link #cacheRepository} to find the
   * {@link AggregateType}s in the repository.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see AggregateRootRepository#findAllSync(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @Nullable C findAllInCacheSync(final @NotNull IntFunction<@NotNull C> factory) {
    return this.cacheRepository.findAllSync(factory);
  }

  /**
   * This method executes and wraps the {@link #findAllInCacheSync(IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param factory The factory to create the {@link Collection} to return.
   * @param <C>     The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see #findAllInCacheSync(IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @NotNull CompletableFuture<@Nullable C> findAllInCacheAsync(final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAllInCacheSync(factory), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#findAllSync(Consumer, IntFunction)} of the {@link #cacheRepository} to find the
   * {@link AggregateType}s in the repository.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see AggregateRootRepository#findAllSync(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @Nullable C findAllInCacheSync(final @NotNull Consumer<@NotNull AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return this.cacheRepository.findAllSync(postLoadAction, factory);
  }

  /**
   * This method executes and wraps the {@link #findAllInCacheSync(Consumer, IntFunction)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param postLoadAction The action to execute for each model after it's loaded.
   * @param factory        The factory to create the {@link Collection} to return.
   * @param <C>            The type of the {@link Collection} to return.
   * @return A {@link CompletableFuture} that will complete with a {@link Collection} containing all the {@link AggregateType}s in the repository.
   * @see #findAllInCacheSync(Consumer, IntFunction)
   * @since 1.0.0
   */
  public <C extends Collection<@NotNull AggregateType>> @NotNull CompletableFuture<@Nullable C> findAllInCacheAsync(final @NotNull Consumer<@NotNull AggregateType> postLoadAction, final @NotNull IntFunction<@NotNull C> factory) {
    return CompletableFuture.supplyAsync(() -> this.findAllInCacheSync(postLoadAction, factory), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#findSync(String)} of the {@link #storageRepository} to find the
   * {@link AggregateType} with the specified id, and if it exists, it saves it to the {@link #cacheRepository}.
   * If it doesn't exist, it returns {@code null}.
   *
   * @param id The id of the {@link AggregateType}.
   * @return The {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see AggregateRootRepository#findSync(String)
   * @see AggregateRootRepository#saveSync(AggregateRoot)
   * @since 1.0.0
   */
  public @Nullable AggregateType findAndSaveToCacheSync(final @NotNull String id) {
    final AggregateType model = this.findSync(id);
    if (model == null) {
      return null;
    }
    this.cacheRepository.saveSync(model);
    return model;
  }

  /**
   * This method executes and wraps the {@link #findAndSaveToCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findAndSaveToCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> findAndSaveToCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findSync(id), this.executor());
  }

  @Override
  public @NotNull Set<@NotNull String> findIdsSync() {
    return this.storageRepository.findIdsSync();
  }

  /**
   * This method uses the {@link AggregateRootRepository#findSync(String)} of the {@link #cacheRepository} to find the
   * {@link AggregateType} with the specified id, and if it doesn't exist, it uses the {@link AggregateRootRepository#findSync(String)}
   * of the {@link #storageRepository} to find the {@link AggregateType} with the specified id.
   *
   * @param id The id of the {@link AggregateType}.
   * @return The {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see AggregateRootRepository#findSync(String)
   * @since 1.0.0
   */
  public @Nullable AggregateType findInBothSync(final @NotNull String id) {
    final AggregateType model = this.findInCacheSync(id);
    if (model != null) {
      return model;
    }
    return this.findSync(id);
  }

  /**
   * This method executes and wraps the {@link #findInBothSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInBothSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> findInBothAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInBothSync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#findSync(String)} of the {@link #cacheRepository} to find the
   * {@link AggregateType} with the specified id, and if it doesn't exist, it uses the {@link AggregateRootRepository#findSync(String)}
   * of the {@link #storageRepository} to find the {@link AggregateType} with the specified id, and if it exists, it saves
   * it to the {@link #cacheRepository}.
   *
   * @param id The id of the {@link AggregateType}.
   * @return The {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see AggregateRootRepository#findSync(String)
   * @see AggregateRootRepository#saveSync(AggregateRoot)
   * @since 1.0.0
   */
  public @Nullable AggregateType findInBothAndSaveToCacheSync(final @NotNull String id) {
    final AggregateType cachedModel = this.findInCacheSync(id);
    if (cachedModel != null) {
      return cachedModel;
    }
    final var foundModel = this.findSync(id);
    if (foundModel == null) {
      return null;
    }
    this.cacheRepository.saveSync(foundModel);
    return foundModel;
  }

  /**
   * This method executes and wraps the {@link #findInBothAndSaveToCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInBothAndSaveToCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> findInBothAndSaveToCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInBothAndSaveToCacheSync(id), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#findSync(String)} of the {@link #cacheRepository} to find the
   * {@link AggregateType} with the specified id.
   *
   * @param id The id of the {@link AggregateType}.
   * @return The {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see AggregateRootRepository#findSync(String)
   * @since 1.0.0
   */
  public @Nullable AggregateType findInCacheSync(final @NotNull String id) {
    return this.cacheRepository.findSync(id);
  }

  /**
   * This method executes and wraps the {@link #findInCacheSync(String)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param id The id of the {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete with the {@link AggregateType} with the specified id, or {@code null} if it doesn't exist.
   * @see #findInCacheSync(String)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@Nullable AggregateType> findInCacheAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> this.findInCacheSync(id), this.executor());
  }

  /**
   * This method executes the {@link AggregateRootRepository#forEachIdsSync(Consumer)} of the {@link #cacheRepository} to iterate over
   * the ids of the repository.
   *
   * @param action The action to execute for each id.
   * @see AggregateRootRepository#forEachIdsSync(Consumer)
   * @since 1.0.0
   */
  public void forEachIdsInCacheSync(final @NotNull Consumer<? super @NotNull String> action) {
    this.cacheRepository.forEachIdsSync(action);
  }

  /**
   * This method executes and wraps the {@link #forEachIdsInCacheSync(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each id.
   * @return A {@link CompletableFuture} that will complete when all the ids in the repository are iterated over.
   * @see #forEachIdsInCacheSync(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> forEachIdsInCacheAsync(final @NotNull Consumer<? super @NotNull String> action) {
    return CompletableFuture.runAsync(() -> this.forEachIdsInCacheSync(action), this.executor());
  }

  /**
   * This method executes the {@link AggregateRootRepository#forEach(Consumer)} of the {@link #cacheRepository} to iterate over
   * the {@link AggregateType}s in the repository.
   *
   * @param action The action to execute for each {@link AggregateType}.
   * @see AggregateRootRepository#forEach(Consumer)
   * @since 1.0.0
   */
  public void forEachInCacheSync(final @NotNull Consumer<? super @NotNull AggregateType> action) {
    this.cacheRepository.forEach(action);
  }

  /**
   * This method executes and wraps the {@link #forEachInCacheSync(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param action The action to execute for each {@link AggregateType}.
   * @return A {@link CompletableFuture} that will complete when all the {@link AggregateType}s in the repository are iterated over.
   * @see #forEachInCacheSync(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> forEachInFallbackAsync(final @NotNull Consumer<? super @NotNull AggregateType> action) {
    return CompletableFuture.runAsync(() -> this.forEachInCacheSync(action), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#iterator()} of the {@link #storageRepository} to return the
   * {@link Iterator} of the {@link AggregateType}s in the repository.
   *
   * @return The {@link Iterator} of the {@link AggregateType}s in the repository.
   * @see AggregateRootRepository#iterator()
   * @since 1.0.0
   */
  @Override
  public @NotNull Iterator<AggregateType> iterator() {
    return this.storageRepository.iterator();
  }

  /**
   * This method uses the {@link AggregateRootRepository#iterator()} of the {@link #cacheRepository} to return the
   * {@link Iterator} of the {@link AggregateType}s in the repository.
   *
   * @return The {@link Iterator} of the {@link AggregateType}s in the repository.
   * @see AggregateRootRepository#iterator()
   * @since 1.0.0
   */
  public @NotNull Iterator<AggregateType> iteratorInCacheSync() {
    return this.cacheRepository.iterator();
  }

  @Override
  public @NotNull Iterator<String> iteratorIdsSync() {
    return this.storageRepository.iteratorIdsSync();
  }

  /**
   * This method uses the {@link AggregateRootRepository#iteratorIdsSync()} of the {@link #cacheRepository} to return the
   * {@link Iterator} of the ids of the repository.
   *
   * @return The {@link Iterator} of the ids of the repository.
   * @see AggregateRootRepository#iteratorIdsSync()
   * @since 1.0.0
   */
  public @NotNull Iterator<String> iteratorIdsInCacheSync() {
    return this.cacheRepository.iteratorIdsSync();
  }

  /**
   * This method returns the {@link #storageRepository}.
   *
   * @return The {@link #storageRepository}.
   * @since 1.0.0
   */
  public @NotNull AggregateRootRepository<AggregateType> mainAggregateRootRepository() {
    return this.storageRepository;
  }


  @Override
  public @NotNull AggregateType saveSync(final @NotNull AggregateType aggregateType) {
    return this.storageRepository.saveSync(aggregateType);
  }

  /**
   * This method uses iterates over the {@link #cacheRepository} and saves them to the {@link #storageRepository}.
   *
   * @param preSaveAction The action to execute for each model before it's saved.
   * @see AggregateRootRepository#iterator()
   * @see AggregateRootRepository#saveSync(AggregateRoot)
   * @since 1.0.0
   */
  public void saveAllSync(final @NotNull Consumer<AggregateType> preSaveAction) {
    for (final var model : this.cacheRepository) {
      preSaveAction.accept(model);
      this.storageRepository.saveSync(model);
    }
  }

  /**
   * This method executes and wraps the {@link #saveAllSync(Consumer)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param preSaveAction The action to execute for each {@link AggregateType} before it's saved.
   * @return A {@link CompletableFuture} that will complete when all the {@link AggregateType}s in the repository are saved.
   * @see #saveAllSync(Consumer)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull Void> saveAllAsync(final @NotNull Consumer<AggregateType> preSaveAction) {
    return CompletableFuture.runAsync(() -> this.saveAllSync(preSaveAction), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#saveSync(AggregateRoot)} of the {@link #storageRepository} and the
   * {@link AggregateRootRepository#saveSync(AggregateRoot)} of the {@link #cacheRepository} to save the {@link AggregateType}.
   *
   * @param aggregateType The {@link AggregateType} to save. It must have an id.
   * @return The saved {@link AggregateType}.
   * @see AggregateRootRepository#saveSync(AggregateRoot)
   * @since 1.0.0
   */
  @Contract("_ -> param1")
  public @NotNull AggregateType saveInBothSync(final @NotNull AggregateType aggregateType) {
    this.cacheRepository.saveSync(aggregateType);
    this.storageRepository.saveSync(aggregateType);
    return aggregateType;
  }

  /**
   * This method executes and wraps the {@link #saveInBothSync(AggregateRoot)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param aggregateType The {@link AggregateType} to save. It must have an id.
   * @return A {@link CompletableFuture} that will complete with the saved {@link AggregateType}.
   * @see #saveInBothSync(AggregateRoot)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull AggregateType> saveInBothAsync(final @NotNull AggregateType aggregateType) {
    return CompletableFuture.supplyAsync(() -> this.saveInBothSync(aggregateType), this.executor());
  }

  /**
   * This method uses the {@link AggregateRootRepository#saveSync(AggregateRoot)} of the {@link #cacheRepository} to save the
   * {@link AggregateType}.
   *
   * @param aggregateType The {@link AggregateType} to save. It must have an id.
   * @return The saved {@link AggregateType}.
   * @see AggregateRootRepository#saveSync(AggregateRoot)
   * @since 1.0.0
   */
  @Contract("_ -> param1")
  public @NotNull AggregateType saveInCacheSync(final @NotNull AggregateType aggregateType) {
    this.cacheRepository.saveSync(aggregateType);
    return aggregateType;
  }

  /**
   * This method executes and wraps the {@link #saveInCacheSync(AggregateRoot)} method in a {@link CompletableFuture} with
   * the {@link Executor} specified in the constructor.
   *
   * @param aggregateType The {@link AggregateRoot} to save. It must have an id.
   * @return A {@link CompletableFuture} that will complete with the saved {@link AggregateType}.
   * @see #saveInCacheSync(AggregateRoot)
   * @since 1.0.0
   */
  public @NotNull CompletableFuture<@NotNull AggregateType> saveInCacheAsync(final @NotNull AggregateType aggregateType) {
    return CompletableFuture.supplyAsync(() -> this.saveInCacheSync(aggregateType), this.executor());
  }
}
