package org.mixdrinks.view.error

class OffsetToBig(
    val listSize: Int,
    val offset: Int,
) : Exception() {
    override fun toString(): String {
        return "Offset biggest than response size, offset: ${offset}, result size $listSize"
    }
}

class CocktailNotFound(val id: Int) : Exception() {
    override fun toString(): String {
        return "Cocktails with id: $id not found"
    }
}

class ItemsNotFound(val id: Int) : Exception() {
    override fun toString(): String {
        return "Item with id: $id not found"
    }
}

class QueryRequire(val queryName: String) : Exception() {
    override fun toString(): String {
        return "$queryName is require"
    }
}

class VoteError() : Exception()