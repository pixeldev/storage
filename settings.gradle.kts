pluginManagement {
  includeBuild("build-logic")
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal()
    mavenCentral()
  }
}

rootProject.name = "storage-parent"

sequenceOf("api", "codec", "bom", "caffeine-dist", "gson-dist", "yaml-dist").forEach {
  include("storage-$it")
  project(":storage-$it").projectDir = file(it)
}
