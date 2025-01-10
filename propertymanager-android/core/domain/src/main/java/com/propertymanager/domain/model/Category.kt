package com.propertymanager.domain.model

data class Category(
    val id: String = "",
    val name: String = "",
    val subcategories: List<String> = listOf()
) {
    constructor() : this(
        name = "",
        subcategories = emptyList()
    )
}
