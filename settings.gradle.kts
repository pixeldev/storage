pluginManagement {
  includeBuild("build-logic")
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    maven("https://repo.revengenetwork.es/repository/libs/")
  }
}

rootProject.name = "storage-parent"

sequenceOf("api", "codec", "bom", "caffeine-dist", "gson-dist", "mongo-legacy-dist", "redis-dist").forEach {
  include("storage-$it")
  project(":storage-$it").projectDir = file(it)
}
