package jpm.movie

import com.google.inject.Guice
import com.google.inject.Injector
import jpm.movie.core.HttpApi
import jpm.movie.core.HttpApiConfig

//val REGION = "us-west-2"
//val BUCKET = "bucket-${UUID.randomUUID()}"
//val KEY = "key"

fun main() {
    fun buildConfig(): MovieSvcConfig {
        return MovieSvcConfig(HttpApiConfig())
    }

    val config = buildConfig()
    val injector: Injector = Guice.createInjector(MovieSvcModule(config))

    injector.getInstance(HttpApi::class.java)

    injector.allBindings.forEach { println("===>>> $it") }
}

//fun main() = runBlocking {
//    S3Client
//        .fromEnvironment { region = REGION }
//        .use { s3 ->
//            setupTutorial(s3)
//
//            println("Creating object $BUCKET/$KEY...")
//
//            s3.putObject {
//                bucket = BUCKET
//                key = KEY
//                body = ByteStream.fromString("Testing with the Kotlin SDK")
//            }
//
//            println("Object $BUCKET/$KEY created successfully!")
//
//            cleanUp(s3)
//        }
//}
//
//suspend fun setupTutorial(s3: S3Client) {
//    println("Creating bucket $BUCKET...")
//    s3.createBucket {
//        bucket = BUCKET
//        createBucketConfiguration {
//            locationConstraint = BucketLocationConstraint.fromValue(REGION)
//        }
//    }
//    println("Bucket $BUCKET created successfully!")
//}
//
//suspend fun cleanUp(s3: S3Client) {
//    println("Deleting object $BUCKET/$KEY...")
//    s3.deleteObject {
//        bucket = BUCKET
//        key = KEY
//    }
//    println("Object $BUCKET/$KEY deleted successfully!")
//
//    println("Deleting bucket $BUCKET...")
//    s3.deleteBucket {
//        bucket = BUCKET
//    }
//    println("Bucket $BUCKET deleted successfully!")
//}