package club.sk1er.elementa.svg

import club.sk1er.elementa.svg.data.Point
import club.sk1er.elementa.svg.data.SVGArc
import club.sk1er.elementa.svg.data.SVGElement
import club.sk1er.elementa.svg.data.SVGLine
import java.lang.IllegalStateException
import kotlin.math.abs

class PathParser(private var dataString: String) {
    private val currentPos = Point(0f, 0f)
    private var firstPos: Point? = null

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

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.x = parseFloat()
                    currentPos.y = parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'l' -> {
                    val originalPoint = currentPos.copy()

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.x += parseFloat()
                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'v' -> {
                    val originalPoint = currentPos.copy()

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'V' -> {
                    val originalPoint = currentPos.copy()

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.x = 0f
                    currentPos.y += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'h' -> {
                    val originalPoint = currentPos.copy()

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.x += parseFloat()

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'H' -> {
                    val originalPoint = currentPos.copy()

                    if (firstPos == null) {
                        firstPos = originalPoint
                    }

                    currentPos.x += parseFloat()
                    currentPos.y = 0f

                    elements.add(SVGLine(originalPoint, currentPos.copy()))
                }
                'z' -> {
                    if (firstPos == null) {
                        throw IllegalStateException("Can't use 'z' as first draw!")
                    }

                    elements.add(SVGLine(currentPos.copy(), firstPos!!))
                }
                'a' -> {
                    parseArc(true)?.let { elements.add(it) }
                }
                'A' -> {
                    parseArc(false)?.let { elements.add(it) }
                }
            }
        }

        return elements
    }

    private fun parseArc(relative: Boolean): SVGElement? {
        val rX = abs(parseFloat())
        val rY = abs(parseFloat())
        val xAxisRotation = parseFloat()
        val largeArc = parseBoolean()
        val sweep = parseBoolean()
        val x = parseFloat()
        val y = parseFloat()

        val originalPos = currentPos.copy()

        if (relative) {
            currentPos.x += x
            currentPos.y += y
        } else {
            currentPos.x = x
            currentPos.y = y
        }

        if (currentPos == originalPos) {
            return null
        }

        if (rX == 0f || rY == 0f) {
            return SVGLine(originalPos, currentPos.copy())
        }

        return SVGArc(
            originalPos,
            rX,
            rY,
            xAxisRotation.toInt(),
            largeArc,
            sweep,
            currentPos.copy()
        )
    }

    private fun parseCommand(): Char {
        val command = dataString[0]

        dataString = dataString.drop(1).dropWhile { it.isWhitespace() }

        return command
    }

    private fun parseBoolean(): Boolean {
        val num = dataString[0]

        dataString = dataString.substring(1).dropWhile { it.isWhitespace() }

        return num != '0'
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