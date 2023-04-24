import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("com.google.protobuf") version "0.8.19"
    id("io.ktor.plugin") version "2.3.0"
    application
}

group = "jpm.movie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // AWS
    implementation("aws.sdk.kotlin:s3:0.17.5-beta")

    implementation("aws.sdk.kotlin:sso:0.19.2-beta")
    implementation("aws.sdk.kotlin:ssooidc:0.19.2-beta")

    // FP
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")

    // gRPC
    // Must be exposed as `api(...)` for generated Protobuf models
    api("io.grpc:grpc-protobuf:1.53.0")
    api("io.grpc:grpc-stub:1.53.0")
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    api("com.google.protobuf:protobuf-kotlin:3.22.2")

    // ktor
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")

    // Guice
    implementation("com.google.inject:guice:5.1.0")

    // log4j
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    // test
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}

application {
    mainClass.set("jpm.movie.MainKt")
}

configurations.forEach {
    if ("proto" in it.name.toLowerCase()) {
        it.attributes.attribute(USAGE_ATTRIBUTE, objects.named("java-runtime"))
    }
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.22.2" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.53.0" }
        id("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}