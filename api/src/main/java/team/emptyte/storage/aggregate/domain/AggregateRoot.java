package team.emptyte.storage.aggregate.domain;

import org.jetbrains.annotations.NotNull;

public abstract class AggregateRoot {
  private final String id;

  public AggregateRoot(final @NotNull String id) {
    this.id = id;
  }

  public @NotNull String id() {
    return this.id;
  }
}
