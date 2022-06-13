package org.mixdrinks.view.cocktail

class OffsetToBig(
    val listSize: Int,
    val offset: Int,
) : Exception() {
    override fun toString(): String {
        return "Offset biggest than response size, offset: ${offset}, result size $listSize"
    }
}