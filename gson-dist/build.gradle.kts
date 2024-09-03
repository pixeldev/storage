plugins {
  id("storage.common-conventions")
}

dependencies {
  api(project(":storage-api"))
  api(project(":storage-codec"))
  compileOnlyApi("com.google.code.gson:gson:2.9.0")
}
