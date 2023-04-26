package jpm.movie.core

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.flatMap
import arrow.core.nel
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import com.google.inject.Inject
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.core.validators.DataProviderMessage
import jpm.movie.core.validators.validate
import jpm.movie.model.Codecs
import jpm.movie.model.GeneralError
import jpm.movie.model.Movie
import jpm.movie.model.MovieSvcError
import jpm.movie.model.ValidationError
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class QueueBridgeImpl @Inject constructor(
    private val sqsConfig: QueueBridgeConfig,
    private val context: CoroutineContext,
    @Log private val logger: Logger,
) : QueueBridge, Codecs {

    init {
        logger.info { "Query Bridge starting with config [$sqsConfig]..." }
    }

    private suspend fun receiveMessages(
        sqsRegion: String,
        queueUrlVal: String?,
        callback: (Movie) -> EitherNel<MovieSvcError, Movie>
    ) {
        logger.info { "Retrieving messages from queue [$queueUrlVal] in [$sqsRegion] region." }

        suspend fun f(): EitherNel<MovieSvcError, Unit?> = EitherNel.catch {
            val receiveMessageRequest = ReceiveMessageRequest {
                queueUrl = queueUrlVal
                maxNumberOfMessages = 5
            }

            SqsClient { region = sqsRegion }.use { sqsClient ->
                val response = sqsClient.receiveMessage(receiveMessageRequest)
                response.messages?.forEach { message ->
                    Either.catch { message.body!! }
                        .mapLeft { ValidationError.DataProviderError("Empty message body. [${it.message}]").nel() }
                        .flatMap(::validateAndConvertMessage)
                        .mapLeft { logger.info { "Errors occurred during message validation [$it]" }; it }
                        .flatMap(callback)
                        .mapLeft { logger.info { "Errors occurred during persisting messages [$it]" }; it }
                }
            }
        }.mapLeft { GeneralError("Error occurred during receiving messages. Cause [${it.message}]").nel() }

        gradualErrorHandler(logger, Long.MAX_VALUE, ::f)
    }

    override fun subscribe(callback: (Movie) -> EitherNel<MovieSvcError, Movie>) {
        CoroutineScope(context).launch {
            receiveMessages(sqsConfig.region, sqsConfig.queueUrlVal, callback)
        }
    }

    companion object {

        fun validateAndConvertMessage(json: String): EitherNel<ValidationError, Movie> {
            val structBuilder = Struct.newBuilder()
            JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder)
            val sBuilder = structBuilder.build()

            val title = EitherNel
                .catch { sBuilder.fieldsMap["title"]!!.stringValue.trim() }
                .mapLeft { ValidationError.DataProviderError("Title error: [${it.message}]") }
            val year = EitherNel
                .catch { sBuilder.fieldsMap["year"]!!.numberValue.toInt().toString() }
                .mapLeft { ValidationError.DataProviderError("Year error: [${it.message}]") }
            val cast = EitherNel
                .catch {
                    sBuilder.fieldsMap["cast"]!!.listValue.valuesList.map { it.stringValue.trim() }
                        .filterNot { it.isBlank() }
                }.mapLeft { ValidationError.DataProviderError("Cast error: [${it.message}]") }
            val genres = EitherNel
                .catch {
                    sBuilder.fieldsMap["genres"]!!.listValue.valuesList.map { it.stringValue.trim().uppercase() }
                        .filterNot { it.isBlank() }
                }.mapLeft { ValidationError.DataProviderError("Genres error: [${it.message}]") }

            return Either
                .zipOrAccumulate(title, year, cast, genres) { t, y, c, g -> DataProviderMessage(y, t, c, g) }
                .flatMap { it.validate() }
        }
    }
}