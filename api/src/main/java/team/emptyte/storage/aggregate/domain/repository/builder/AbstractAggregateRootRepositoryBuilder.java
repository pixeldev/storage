package team.emptyte.storage.aggregate.domain.repository.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.emptyte.storage.aggregate.domain.AggregateRoot;
import team.emptyte.storage.aggregate.domain.repository.AggregateRootRepository;
import team.emptyte.storage.aggregate.domain.repository.AsyncAggregateRootRepository;
import team.emptyte.storage.aggregate.domain.repository.WithCacheAggregateRootRepository;

import java.util.concurrent.Executor;

/**
 * This class is the base for all the {@link AsyncAggregateRootRepository} builders. It contains the essential methods to
 * build a {@link AsyncAggregateRootRepository} with a {@link Executor} and optionally a fallback {@link AggregateRootRepository}.
 *
 * @param <AggregateType> The {@link AggregateRoot} type that the repository will handle.
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractAggregateRootRepositoryBuilder<AggregateType extends AggregateRoot> {
  /**
   * Builds a new {@link AsyncAggregateRootRepository} with the specified {@link Executor}.
   *
   * @param executor The {@link Executor} that will be used to execute the asynchronous operations.
   * @return A new {@link AsyncAggregateRootRepository} with the specified {@link Executor}.
   * @since 1.0.0
   */
  @Contract("_ -> new")
  public abstract @NotNull AsyncAggregateRootRepository<AggregateType> build(final @NotNull Executor executor);

  /**
   * Builds a new {@link WithCacheAggregateRootRepository} with the specified {@link Executor} and fallback {@link AggregateRootRepository}.
   *
   * @param executor        The {@link Executor} that will be used to execute the asynchronous operations.
   * @param cacheRepository The cache {@link AggregateRootRepository} to use.
   * @return A new {@link WithCacheAggregateRootRepository} with the specified {@link Executor} and fallback {@link AggregateRootRepository}.
   * @since 1.0.0
   */
  @Contract("_, _ -> new")
  public @NotNull WithCacheAggregateRootRepository<AggregateType> buildWithFallback(
    final @NotNull Executor executor,
    final @NotNull AggregateRootRepository<AggregateType> cacheRepository
  ) {
    return new WithCacheAggregateRootRepository<>(executor, cacheRepository, this.build(executor));
  }
}
