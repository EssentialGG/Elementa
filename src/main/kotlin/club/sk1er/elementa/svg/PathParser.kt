package club.sk1er.elementa.svg

import club.sk1er.elementa.svg.data.Point
import club.sk1er.elementa.svg.data.SVGArc
import club.sk1er.elementa.svg.data.SVGElement
import club.sk1er.elementa.svg.data.SVGLine
import kotlin.math.abs

class PathParser(private var dataString: String) {
    private val currentPos = Point(0f, 0f)
    private var firstPos: Point? = null
    private var cursor = 0

    private val char: Char
        get() = dataString[cursor]

    fun parse(): List<SVGElement> {
        val elements = mutableListOf<SVGElement>()

        while (!isDone()) {
            when (consume()) {
                'M' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().last().run {
                        currentPos.x = get(0)
                        currentPos.y = get(1)
                    }
                }
                'm' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().forEach {
                        currentPos.x += it[0]
                        currentPos.y += it[1]
                    }
                }
                'L' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.x = it[0]
                        currentPos.y = it[1]

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'l' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.x += it[0]
                        currentPos.y += it[1]

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'h' -> {
                    parseWhitespace()
                    parseCoordinateSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.x += it

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'H' -> {
                    parseWhitespace()
                    parseCoordinateSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.x = it

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'v' -> {
                    parseWhitespace()
                    parseCoordinateSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.y += it

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'V' -> {
                    parseWhitespace()
                    parseCoordinateSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        currentPos.y = it

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                }
                'z', 'Z' -> {
                    if (firstPos == null)
                        throw PathParseException("${dataString[cursor - 1]} instruction encountered with no previous position data")

                    parseWhitespace()
                    elements.add(SVGLine(currentPos.copy(), firstPos!!))
                    firstPos = null
                }
                'a' -> {
                    parseWhitespace()

                    while (true) {
                        elements.add(parseArc(true))
                        if (!matchCoordinate())
                            break
                    }
                }
                'A' -> {
                    parseWhitespace()

                    while (true) {
                        elements.add(parseArc(false))
                        if (!matchCoordinate())
                            break
                    }
                }
                'C', 'c', 'S', 's', 'Q', 'q', 'T', 't' ->
                    throw PathParseException("Unsupported instruction: $char")
                else -> throw PathParseException("Invalid instruction: ${dataString[cursor - 1]}")
            }
        }

        return elements
    }

    private fun parseArc(relative: Boolean): SVGElement {
        val rX = parseNumber()
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        val rY = parseNumber()
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        val xAxisRotation = parseNumber()
        parseCommaWhitespace()
        val largeArc = parseFlag()
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        val sweep = parseFlag()
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        val x = parseCoordinate()
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        val y = parseCoordinate()

        val originalPos = currentPos.copy()

        if (relative) {
            currentPos.x += x
            currentPos.y += y
        } else {
            currentPos.x = x
            currentPos.y = y
        }

        if (currentPos == originalPos) {
            throw PathParseException("Arc x and y position is the same as the previous position")
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

    private fun parseCoordinate() = (if (match('-', '+')) {
        if (consume() == '-') -1 else 1
    } else 1) * parseNumber()

    private fun parseCoordinatePair() = mutableListOf<Float>().also {
        it.add(parseCoordinate())
        if (matchCommaWhitespace())
            parseCommaWhitespace()
        it.add(parseCoordinate())
    }

    private fun parseCoordinateSequence() = mutableListOf<Float>().also {
        while (true) {
            it.add(parseCoordinate())
            if (matchCommaWhitespace())
                parseCommaWhitespace()
            if (!matchCommaWhitespace() && !matchCoordinate())
                break
        }
    }

    private fun parseCoordinatePairSequence() = mutableListOf<List<Float>>().also {
        while (true) {
            it.add(parseCoordinatePair())
            if (matchCommaWhitespace())
                parseCommaWhitespace()
            if (!matchCommaWhitespace() && !matchCoordinate())
                break
        }
    }

    private fun parseWhitespace(mustMatchOnce: Boolean = false) {
        var matched = false
        while (!isDone() && matchWhitespace()) {
            consume()
            matched = true
        }

        if (mustMatchOnce && !matched)
            throw PathParseException("Expected whitespace at position $cursor, got ${charForPrinting()}")
    }

    private fun parseCommaWhitespace() {
        if (match(',')) {
            consume()
            parseWhitespace()
        } else {
            parseWhitespace(true)
            if (match(','))
                consume()
            parseWhitespace()
        }
    }

    private fun parseNumber(): Float {
        val builder = StringBuilder()

        fun appendDigits() {
            while (!isDone() && char.isDigit())
                builder.append(consume())
        }

        appendDigits()

        if (match('.')) {
            builder.append(consume())
            appendDigits()
        } else if (builder.isEmpty()) {
            throw PathParseException("Expected number at position $cursor, got ${charForPrinting()}")
        }

        val number = builder.toString().toFloatOrNull()
            ?: throw PathParseException("Expected number at position $cursor, got $builder")

        if (match('e', 'E'))
            throw PathParseException("Numeric 'e' format is not supported")

        return number
    }

    private fun parseFlag(): Boolean = if (!match('0', '1')) {
        throw PathParseException("Expected 0 or 1 at position $cursor, got ${charForPrinting()}")
    } else consume() == '1'

    private fun match(vararg chars: Char) = !isDone() && chars.contains(char)

    private fun matchWhitespace() = !isDone() && whitespaceChars.contains(char)

    private fun matchCommaWhitespace() = !isDone() && (whitespaceChars.contains(char) || match(','))

    private fun matchCoordinate() = !isDone() && (char.isDigit() || match('-', ',', '.'))

    private fun consume() = dataString[cursor++]

    private fun isDone() = cursor >= dataString.length

    private fun charForPrinting() = if (isDone()) "<end>" else char.toString()

    companion object {
        private val whitespaceChars = listOf(0x9, 0x20, 0xa, 0xc, 0xd).map { it.toChar() }
    }

    class PathParseException(message: String) : Exception(message)
}