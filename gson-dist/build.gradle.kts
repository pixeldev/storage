plugins {
  id("storage.publishing-conventions")
}

dependencies {
  api(project(":storage-api-codec"))
  compileOnly(libs.gson)
}