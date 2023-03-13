package org.mixdrinks.view.error

class QueryRequireException(private val queryName: String) : Exception() {
    override fun toString(): String {
        return "$queryName is require"
    }
}

class VoteError : Exception()

class SortTypeNotFound : Exception()
