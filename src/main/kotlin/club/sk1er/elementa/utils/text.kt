package club.sk1er.elementa.utils

import club.sk1er.mods.core.universal.UniversalGraphicsHandler

fun getStringSplitToWidth(string: String, width: Float): List<String> {
    if (string.isEmpty()) return listOf(string)

    var currentString = string
    val lines = mutableListOf<String>()

    while (currentString.isNotEmpty()) {
        val i = sizeStringToWidth(string, width)

        if (currentString.length <= i) {
            lines.add(currentString)
            break
        }

        val chunk = currentString.substring(0, i)
        lines.add(chunk)
        val tmp = currentString
        currentString = currentString.substring(i)

        if (tmp == currentString) {
            break
        }
    }

    return lines
}

fun sizeStringToWidth(string: String, width: Float): Int {
    val i = string.length
    var j = 0f
    var k = 0
    var l = -1

    var flag = false
    while (k < i) {
        val c0: Char = string[k]

        when (c0) {
            '\n' -> k--
            ' ' -> {
                l = k
                j += UniversalGraphicsHandler.getCharWidth(c0)

                if (flag) j++
            }
            '\u00a7' -> if (k < i - 1) {
                k++
                val c1 = string[k]
                if (c1.toInt() != 108 && c1.toInt() != 76) {
                    if (c1.toInt() == 114 || c1.toInt() == 82 || isFormatColor(c1.toInt())) {
                        flag = false
                    }
                } else {
                    flag = true
                }
            }
            else -> {
                j += UniversalGraphicsHandler.getCharWidth(c0)

                if (flag) j++
            }
        }

        if (c0.toInt() == 10) {
            k++
            l = k
            break
        }

        if (j > width) break

        k++
    }

    return if (k != i && l != -1 && l < k) l else k
}

fun isFormatColor(char: Int) = char in 48..57 || char in 97..102 || char in 65..70