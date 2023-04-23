package jpm.movie

import com.google.inject.MembersInjector
import com.google.inject.TypeLiteral
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import kotlin.annotation.Retention
import kotlin.annotation.Target
import java.lang.reflect.Field
import javax.inject.Scope
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Scope
@MustBeDocumented
@Retention
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class Log

class SLF4JMembersInjector<T>(field: Field) : MembersInjector<T> {
    private val field: Field
    private val logger: Logger

    init {
        this.field = field
        logger = LoggerFactory.getLogger(field.getDeclaringClass())
        field.setAccessible(true)
    }

    override fun injectMembers(t: T) {
        try {
            field.set(t, logger)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }
}

class SLF4JTypeListener : TypeListener {
    override fun <T> hear(typeLiteral: TypeLiteral<T>, typeEncounter: TypeEncounter<T>) {
        for (field in typeLiteral.rawType.declaredFields) {
            if (field.type == Logger::class.java
                && field.isAnnotationPresent(Log::class.java)
            ) {
                typeEncounter.register(SLF4JMembersInjector(field))
            }
        }
    }
}