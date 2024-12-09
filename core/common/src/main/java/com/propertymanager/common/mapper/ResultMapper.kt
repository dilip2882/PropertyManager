package com.propertymanager.common.mapper

fun interface ResultMapper<T, R> {
    fun map(input: T): R
}
