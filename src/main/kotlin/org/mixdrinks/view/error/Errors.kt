package org.mixdrinks.view.error

class CocktailNotFound(val id: Int) : Exception() {
    override fun toString(): String {
        return "Cocktails with id: $id not found"
    }
}

class QueryRequireException(private val queryName: String) : Exception() {
    override fun toString(): String {
        return "$queryName is require"
    }
}

class VoteError : Exception()

class SortTypeNotFound : Exception()
