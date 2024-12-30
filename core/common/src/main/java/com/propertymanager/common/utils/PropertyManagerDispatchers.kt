package com.propertymanager.common.utils

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val pmDispatcher: PropertyManagerDispatchers)

enum class PropertyManagerDispatchers {
    Default,
    IO,
}
