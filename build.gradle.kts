import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.google.protobuf") version "0.8.19"
    id("io.ktor.plugin") version "2.3.0"
    application

    // jooq codegen
    id("com.avast.gradle.docker-compose") version "0.16.12"
    id("nu.studer.jooq") version "5.2.2"
    id("org.flywaydb.flyway") version "9.8.1"
    id("java")
    id("java-library")

}

group = "jpm.movie"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

/** JOOQ Gen */
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jooq:jooq-codegen:3.14.16")
        classpath("org.postgresql:postgresql:42.5.4")
    }
}
/** JOOQ Gen */

dependencies {

    // AWS
    implementation("aws.sdk.kotlin:s3:0.18.0-beta")

    implementation("aws.sdk.kotlin:sso:0.19.2-beta")
    implementation("aws.sdk.kotlin:ssooidc:0.19.2-beta")

    implementation("aws.sdk.kotlin:dynamodb:0.18.0-beta")
    implementation("aws.sdk.kotlin:iam:0.18.0-beta")
    implementation("aws.sdk.kotlin:cloudwatch:0.18.0-beta")
    implementation("aws.sdk.kotlin:cognitoidentityprovider:0.18.0-beta")
    implementation("aws.sdk.kotlin:sns:0.18.0-beta")
    implementation("aws.sdk.kotlin:pinpoint:0.18.0-beta")

    implementation("com.amazonaws:aws-java-sdk-sqs:1.12.454")

    implementation("aws.sdk.kotlin:sqs-jvm:0.21.3-beta")

    // kotlin
//    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // FP
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")

    // gRPC
    // Must be exposed as `api(...)` for generated Protobuf models
    api("io.grpc:grpc-protobuf:1.53.0")
    api("io.grpc:grpc-stub:1.53.0")
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    api("com.google.protobuf:protobuf-kotlin:3.22.2")
    api("com.google.protobuf:protobuf-java-util:3.22.3")

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

    // DB
    implementation("org.flywaydb:flyway-core:9.16.0")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("com.zaxxer:HikariCP:3.4.5")
    // DB - jooq
    implementation("org.jooq:jooq:3.14.16")
    implementation("org.jooq:jooq-meta:3.14.16")
    implementation("org.jooq:jooq-codegen:3.14.16")
    jooqGenerator("org.postgresql:postgresql:42.5.4")
    implementation("com.opentable.components:otj-pg-embedded:1.0.1")
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
// Inform IDEs like IntelliJ IDEA, Eclipse or NetBeans about the generated code.
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
            srcDirs("build/generated/source/proto/main/grpckt")
            srcDirs("build/generated/source/proto/main/kotlin")
        }
    }
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

// jooq generator
flyway {
    url = "jdbc:postgresql://localhost:5433/moviedb"
    user = "movieuser"
    password = "qwe123"
}

tasks.create("generateJooq") {
    GenerationTool.generate(
        Configuration()
            .withJdbc(
                Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl("jdbc:postgresql://localhost:5432/moviedb")
                    /**
                     * Alternatively: -Djooq.codegen.jdbc.user -Djooq.codegen.jdbc.password
                     */
                    .withUser("movieuser")
                    .withPassword("qwe123")
            )
            .withGenerator(
                Generator()
                    .withDatabase(Database().withInputSchema("public"))
                    .withGenerate(Generate())
                    .withTarget(
                        Target()
                            .withPackageName("jpm.movie.db")
                            .withDirectory("src/generated/jooq")
                    )
            )
    )
}

//tasks.named("compileKotlin") {
//    doFirst {
//        //create embedded postgresql
//        EmbeddedPostgres.builder().setPort(5400).start().use {
//            //migrate embedded posrtgresql
//            org.flywaydb.core.Flyway.configure()
//                .locations("filesystem:$projectDir/migrations/")
//                .schemas("public")
//                .dataSource(it.postgresDatabase)
//                .load()
//                .migrate()
//
//            // here comes the GenerationTool section, after the PG is running
//        }
//    }
//}