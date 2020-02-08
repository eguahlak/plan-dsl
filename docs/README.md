Plan-dsl is written in Kotlin.
Plan-dsl is ment to deal with various documentation issues on
Cphbusiness, and to create consistent sites for students using GitHub Pages.  

You need the following in your `build.gradle` file of your project:  

```groovy
repositories {
  // ...
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/eguahlak/plan-dsl")
    credentials {
      username = System.getenv("GPR_USER") ?: GITHUB_USER
      password = System.getenv("GPR_API_KEY") ?: GITHUB_PACKAGES
      }
    }
  }

dependencies {
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
  implementation "dk.kalhauge:plan-dsl:2.1.20"
  // ...
  }
```

and you have to modify or create a `~/.gradle/gradle.properties` file
with the following entries:  

```
GITHUB_USER=eguahlak
GITHUB_PACKAGES=a011e...
```

