plugins {
  id("net.kyori.indra.publishing")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

indra {
  javaVersions {
    target(21)
    minimumToolchain(21)
    strictVersions(true)
  }
  checkstyle(libs.versions.checkstyle.get())

  github("emptyte-team", "storage") {
    ci(true)
  }
  mitLicense()

  configurePublications {
    pom {
      developers {
        developer {
          id.set("pixeldev")
          name.set("Angel Miranda")
          url.set("https://github.com/pixeldev")
          email.set("pixel@fenixteam.org")
        }

        developer {
          id.set("srvenient")
          name.set("Nelson Rodriguez Roa")
          url.set("https://github.com/srvenient")
          email.set("srvenient@gmail.com")
        }
      }
    }
  }
}
