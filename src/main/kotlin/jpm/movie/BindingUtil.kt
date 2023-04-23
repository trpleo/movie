package jpm.movie

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder

@Suppress("UNCHECKED_CAST")
abstract class BindingUtil : AbstractModule() {

    protected inline fun <reified T> bind() = bind(T::class.java)!!

    protected inline fun <reified T> bind(instance: T) {
        bind<T>().toInstance(instance)
    }

    protected inline fun <reified T, reified TAnn : Annotation> annotatedBind(): LinkedBindingBuilder<T> =
        bind<T>().annotatedWith(TAnn::class.java)

    protected inline fun <reified T, reified TAnn : Annotation> annotatedBind(instance: T) {
        annotatedBind<T, TAnn>().toInstance(instance)
    }

    protected inline fun <reified T> LinkedBindingBuilder<*>.to() = to(T::class.java as Class<Nothing>)!!

    protected inline fun <reified T, reified TAnn : Annotation, reified R> typedBind() =
        annotatedBind<T, TAnn>().to<R>()

    protected fun ScopedBindingBuilder.asSingleton() = `in`(Scopes.SINGLETON)

    protected inline fun <reified T> singleton() = bind(T::class.java).asEagerSingleton()
}
