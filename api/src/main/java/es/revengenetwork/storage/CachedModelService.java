package es.revengenetwork.storage;

import es.revengenetwork.storage.model.Model;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface CachedModelService<ModelType extends Model>
  extends ModelService<ModelType> {

  @Nullable ModelType getSync(@NotNull String id);

  @Nullable ModelType getOrFindSync(@NotNull String id);

  @Nullable List<ModelType> getAllSync();

  void uploadSync(@NotNull ModelType model);

  void uploadAllSync(@NotNull Consumer<ModelType> preUploadAction);

  default void uploadAllSync() {
    uploadAllSync(modelType -> { });
  }

  void saveInCacheSync(@NotNull ModelType model);

  boolean deleteInCacheSync(@NotNull String id);

  default boolean deleteInCacheSync(@NotNull ModelType model) {
    return deleteInCacheSync(model.getId());
  }

  @Contract(pure = true)
  void saveAllSync(@NotNull Consumer<ModelType> preSaveAction);

  @Contract(pure = true)
  default void saveAllSync() {
    saveAllSync(modelType -> { });
  }
}
