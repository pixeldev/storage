plugins {
  id("storage.common-conventions")
}

dependencies {
  api(project(":storage-api"))
  api(project(":storage-codec"))
  compileOnlyApi(libs.configurate.yaml)
}
