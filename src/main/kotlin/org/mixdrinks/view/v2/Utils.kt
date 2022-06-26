package org.mixdrinks.view.v2

import java.math.BigDecimal
import java.math.RoundingMode

fun roundScore(score: Float): Float {
    return BigDecimal(score.toDouble()).setScale(1, RoundingMode.FLOOR).toFloat()
}