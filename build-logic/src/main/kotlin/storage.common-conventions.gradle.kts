import com.diffplug.gradle.spotless.FormatExtension
import java.util.*

plugins {
  id("storage.base-conventions")
  id("net.kyori.indra")
  id("net.kyori.indra.crossdoc")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.licenser.spotless")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

dependencies {
  api(platform(project(":storage-bom")))
  checkstyle(libs.stylecheck)
}

spotless {
  fun FormatExtension.applyCommon() {
    trimTrailingWhitespace()
    endWithNewline()
    indentWithSpaces(2)
  }
  java {
    importOrderFile(rootProject.file(".spotless/fenix.importorder"))
    applyCommon()
  }
  kotlinGradle {
    applyCommon()
  }
}

indraCrossdoc {
  baseUrl().set(providers.gradleProperty("javadocPublishRoot"))
  nameBasedDocumentationUrlProvider {
    projectNamePrefix.set("storage-")
  }
}

java {
  withJavadocJar()
}

tasks {
  generateOfflineLinks {
  }

  jar {
    manifest {
      attributes(
        "Specification-Version" to project.version,
        "Specification-Vendor" to "fenix-team",
        "Implementation-Build-Date" to Date()
      )
    }
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    dependsOn("spotlessApply")
    options.compilerArgs.add("-parameters")
  }
}
