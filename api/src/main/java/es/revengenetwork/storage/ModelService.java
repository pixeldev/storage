package es.revengenetwork.storage;

import es.revengenetwork.storage.model.Model;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface ModelService<ModelType extends Model> {

  String ID_FIELD = "id";

  @Nullable ModelType findSync(@NotNull String id);

  @Nullable List<ModelType> findSync(@NotNull String field, @NotNull String value);

  default @Nullable List<ModelType> findAllSync() {
    return findAllSync(modelType -> { });
  }

  @Nullable List<ModelType> findAllSync(@NotNull Consumer<ModelType> postLoadAction);

  @Contract(pure = true)
  void saveSync(@NotNull ModelType model);

  default void deleteSync(@NotNull ModelType model) {
    deleteSync(model.getId());
  }

  boolean deleteSync(@NotNull String id);
}
