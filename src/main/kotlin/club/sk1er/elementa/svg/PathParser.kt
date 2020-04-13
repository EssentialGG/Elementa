package club.sk1er.elementa.svg

import club.sk1er.elementa.svg.data.Point
import club.sk1er.elementa.svg.data.SVGElement
import club.sk1er.elementa.svg.data.SVGLine

class PathParser(private var dataString: String) {
    private val currentPos = Point(0f, 0f)

    fun parse(): List<SVGElement> {
        val elements = mutableListOf<SVGElement>()

        while (dataString.isNotBlank()) {
            when (parseCommand()) {
                'M' -> {
                    currentPos.x = parseFloat()
                    currentPos.y = parseFloat()
                }
                'm' -> {
                    currentPos.x += parseFloat()
                    currentPos.y += parseFloat()
                }
                'L' -> {
                    val originalPoint = currentPos.copy()

                    currentPos.x = parseFloat()
                    currentPos.y = parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'l' -> {
                    val originalPoint = currentPos.copy()

                    currentPos.x += parseFloat()
                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'v' -> {
                    val originalPoint = currentPos.copy()

                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'V' -> {
                    val originalPoint = currentPos.copy()

                    currentPos.x = 0f
                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
            }
        }

        return elements
    }

    private fun parseCommand(): Char {
        val command = dataString[0]

        dataString = dataString.drop(1).dropWhile { it.isWhitespace() }

        return command
    }

    private fun parseFloat(): Float {
        val end = dataString.indexOfFirst { !it.isDigit() && it != '.' && it != '-' }

        val num = if (end == -1) {
            dataString.substring(0).toFloat()
        } else {
            dataString.substring(0, end).toFloat()
        }

        dataString = if (end == -1) {
            ""
        } else {
            dataString.substring(end).dropWhile { it.isWhitespace() }
        }

        return num
    }
}