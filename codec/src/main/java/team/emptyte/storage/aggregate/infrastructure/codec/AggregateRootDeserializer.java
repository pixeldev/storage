package team.emptyte.storage.aggregate.infrastructure.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AggregateRootDeserializer<AggregateType, ReadType> {
  @NotNull AggregateType deserialize(final @NotNull ReadType serialized);
}
