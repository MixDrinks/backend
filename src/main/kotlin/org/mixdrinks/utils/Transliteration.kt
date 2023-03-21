package org.mixdrinks.utils

import java.util.Locale

fun slug(str: String): String {
    return translit(str)
        .lowercase(Locale.ENGLISH)
        .replace(Regex("[^a-zA-Z0-9]"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
}

fun translit(str: String): String {
    return str.mapIndexed { index, char ->
        tranlit(char, str.getOrNull(index - 1))
    }.joinToString("")
}

@Suppress("ComplexMethod", "LongMethod")
private fun tranlit(current: Char, previous: Char?): String {
    val isStartOfWord = previous == null || previous == ' '
    return when (current) {
        ' ' -> " "
        'ь', 'Ь', '\'' -> ""
        'а' -> "a"
        'А' -> "A"
        'б' -> "b"
        'Б' -> "B"
        'в' -> "v"
        'В' -> "V"
        'г' -> {
            if (previous == 'з' || previous == 'З') {
                "gh"
            } else {
                "h"
            }
        }

        'Г' -> {
            if (previous == 'з' || previous == 'З') {
                "Gh"
            } else {
                "H"
            }
        }

        'ґ' -> "g"
        'Ґ' -> "G"
        'д' -> "d"
        'Д' -> "D"
        'е' -> "e"
        'Е' -> "E"
        'є' -> {
            if (isStartOfWord) {
                "ye"
            } else {
                "ie"
            }
        }

        'Є' -> {
            if (isStartOfWord) {
                "Ye"
            } else {
                "Ie"
            }
        }

        'ж' -> "zh"
        'Ж' -> "Zh"
        'з' -> "z"
        'З' -> "Z"
        'и' -> "y"
        'И' -> "Y"
        'і' -> "i"
        'І' -> "I"
        'ї' -> "i"
        'Ї' -> "Yi"
        'й' -> {
            if (isStartOfWord) {
                "y"
            } else {
                "i"
            }
        }

        'Й' -> {
            if (isStartOfWord) {
                "Y"
            } else {
                "I"
            }
        }

        'к' -> "k"
        'К' -> "K"
        'л' -> "l"
        'Л' -> "L"
        'м' -> "m"
        'М' -> "M"
        'н' -> "n"
        'Н' -> "N"
        'о' -> "o"
        'О' -> "O"
        'п' -> "p"
        'П' -> "P"
        'р' -> "r"
        'Р' -> "R"
        'с' -> "s"
        'С' -> "S"
        'т' -> "t"
        'Т' -> "T"
        'у' -> "u"
        'У' -> "U"
        'ф' -> "f"
        'Ф' -> "F"
        'х' -> "kh"
        'Х' -> "Kh"
        'ц' -> "ts"
        'Ц' -> "Ts"
        'ч' -> "ch"
        'Ч' -> "Ch"
        'ш' -> "sh"
        'Ш' -> "Sh"
        'щ' -> "shch"
        'Щ' -> "Shch"
        'ю' -> {
            if (isStartOfWord) {
                "yu"
            } else {
                "iu"
            }
        }

        'Ю' -> {
            if (isStartOfWord) {
                "Yu"
            } else {
                "Iu"
            }
        }

        'я' -> {
            if (isStartOfWord) {
                "ya"
            } else {
                "ia"
            }
        }

        'Я' -> {
            if (isStartOfWord) {
                "Ya"
            } else {
                "Ia"
            }
        }

        else -> {
            current.toString()
        }
    }
}
