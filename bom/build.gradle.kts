plugins {
  id("java-platform")
  id("storage.base-conventions")
}

indra {
  configurePublications {
    from(components["javaPlatform"])
  }
}

dependencies {
  constraints {
    sequenceOf(
      "api",
      "codec",
      "caffeine-dist",
      "gson-dist",
      "mongo-legacy-dist",
      "redis-dist"
    ).forEach {
      api(project(":storage-$it"))
    }
  }
}
