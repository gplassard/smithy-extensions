import net.researchgate.release.ReleaseExtension

plugins {
    id("java")
    id("net.researchgate.release")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.smithy.model)
    implementation(libs.smithy.openapi)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gplassard/smithy-extensions")
            credentials {
                username = project.findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key")?.toString() ?: System.getenv("GITHUB_TOKEN")
            }
        }
        val codeartifactUrl = project.findProperty("codeartifact.url")?.toString() ?: System.getenv("CODE_ARTIFACT_URL")
        if (codeartifactUrl != null) {
            maven {
                name = "CodeArtifact"
                url = uri(codeartifactUrl)
                credentials {
                    username = "aws"
                    password = project.findProperty("codeartifact.token")?.toString() ?: System.getenv("CODEARTIFACT_AUTH_TOKEN")
                }
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            val baseModule = project.path.split(":")[1]

            groupId = "fr.gplassard.smithyextensions"
            artifactId = baseModule
            from(components["java"])
        }
    }
}

configure<ReleaseExtension>  {
    val module = project.path.split(":")[1]

    tagTemplate.set("$module-\${version}")
    preTagCommitMessage.set("release($module) - pre tag commit: ")
    tagCommitMessage.set("release($module) - creating tag: ")
    newVersionCommitMessage.set("release($module) - new version commit: ")
}
