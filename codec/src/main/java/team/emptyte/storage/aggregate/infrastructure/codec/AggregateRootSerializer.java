package team.emptyte.storage.aggregate.infrastructure.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AggregateRootSerializer<AggregateType, ReadType> {
  @NotNull ReadType serialize(final @NotNull AggregateType modelType);
}
