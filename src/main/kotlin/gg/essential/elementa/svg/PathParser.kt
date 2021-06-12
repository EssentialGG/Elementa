package gg.essential.elementa.svg

import gg.essential.elementa.svg.data.*

class PathParser(private var dataString: String) {
    private val currentPos = Point(0f, 0f)
    private var firstPos: Point? = null
    private var cursor = 0

    private val char: Char
        get() = dataString[cursor]

    fun parse(): List<SVGElement> {
        val elements = mutableListOf<SVGElement>()

        while (!isDone()) {
            val command = consume()
            val absolute = command.uppercaseChar() == command
            when (command.uppercaseChar()) {
                'M' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().forEach { (x, y) ->
                        if (absolute) {
                            currentPos.x = x
                            currentPos.y = y
                        } else {
                            currentPos.x += x
                            currentPos.y += y
                        }
                    }
                    parseWhitespace()
                }
                'L' -> {
                    parseWhitespace()
                    parseCoordinatePairSequence().forEach { (x, y) ->
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        if (absolute) {
                            currentPos.x = x
                            currentPos.y = y
                        } else {
                            currentPos.x += x
                            currentPos.y += y
                        }

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                    parseWhitespace()
                }
                'H', 'V' -> {
                    parseWhitespace()
                    val field = if (command.uppercaseChar() == 'H') currentPos::x else currentPos::y

                    parseCoordinateSequence().forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        if (absolute) {
                            field.set(it)
                        } else {
                            field.set(field.get() + it)
                        }

                        elements.add(SVGLine(originalPoint, currentPos.copy()))
                    }
                    parseWhitespace()
                }
                'Z' -> {
                    if (firstPos == null)
                        throw PathParseException("$command instruction encountered with no previous position data")

                    parseWhitespace()
                    elements.add(SVGLine(currentPos.copy(), firstPos!!))
                    firstPos = null
                    parseWhitespace()
                }
                'A' -> {
                    parseWhitespace()

                    while (true) {
                        elements.add(parseArc(absolute))
                        if (!matchCoordinate())
                            break
                    }
                    parseWhitespace()
                }
                'C' -> {
                    parseWhitespace()
                    parseCurveto(::parseCoordinatePairTriple).forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        val points = it.map { (x, y) ->
                            if (absolute) {
                                currentPos.x = x
                                currentPos.y = y
                            } else {
                                currentPos.x += x
                                currentPos.y += y
                            }
                            currentPos.copy()
                        }

                        elements.add(SVGCubicCurve(originalPoint, points[0], points[1], points[2]))
                    }
                    parseWhitespace()
                }
                'S' -> {
                    parseWhitespace()
                    parseCurveto(::parseCoordinatePairDouble).forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        val points = it.map { (x, y) ->
                            if (absolute) {
                                currentPos.x = x
                                currentPos.y = y
                            } else {
                                currentPos.x += x
                                currentPos.y += y
                            }
                            currentPos.copy()
                        }

                        val firstControlPoint = elements.lastOrNull()?.let { element ->
                            if (element !is SVGCubicCurve)
                                return@let null

                            element.lastControlPoint reflectedAround originalPoint
                        } ?: originalPoint

                        elements.add(SVGCubicCurve(originalPoint, firstControlPoint, points[0], points[1]))
                    }
                    parseWhitespace()
                }
                'Q' -> {
                    parseWhitespace()
                    parseCurveto(::parseCoordinatePairDouble).forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        val points = it.map { (x, y) ->
                            if (absolute) {
                                currentPos.x = x
                                currentPos.y = y
                            } else {
                                currentPos.x += x
                                currentPos.y += y
                            }
                            currentPos.copy()
                        }

                        elements.add(SVGQuadraticCurve(originalPoint, points[0], points[1]))
                    }
                    parseWhitespace()
                }
                'T' -> {
                    parseWhitespace()
                    parseCurveto(::parseCoordinatePair).forEach {
                        val originalPoint = currentPos.copy()

                        if (firstPos == null)
                            firstPos = originalPoint

                        if (absolute) {
                            currentPos.x = it.x
                            currentPos.y = it.y
                        } else {
                            currentPos.x += it.x
                            currentPos.y += it.y
                        }

                        val firstControlPoint = elements.lastOrNull()?.let { element ->
                            if (element !is SVGQuadraticCurve)
                                return@let null

                            element.lastControlPoint reflectedAround originalPoint
                        } ?: originalPoint

                        elements.add(SVGQuadraticCurve(originalPoint, firstControlPoint, currentPos))
                    }
                    parseWhitespace()
                }
                else -> throw PathParseException("Invalid instruction: $command")
            }
        }

        return elements
    }

    private fun <T> parseCurveto(generator: () -> T): List<T> {
        val list = mutableListOf<T>()
        list.add(generator())
        if (matchCommaWhitespace()) {
            val prevCursor = cursor
            parseCommaWhitespace()
            if (matchCoordinate()) {
                list.addAll(parseCurveto(generator))
            } else {
                cursor = prevCursor
            }
        }
        return list
    }

    private fun parseArc(absolute: Boolean): SVGElement {
        val rX = parseNumber()
        optionalParseCommaWhitespace()
        val rY = parseNumber()
        optionalParseCommaWhitespace()
        val xAxisRotation = parseNumber()
        parseCommaWhitespace()
        val largeArc = parseFlag()
        optionalParseCommaWhitespace()
        val sweep = parseFlag()
        optionalParseCommaWhitespace()
        val x = parseCoordinate()
        optionalParseCommaWhitespace()
        val y = parseCoordinate()

        val originalPos = currentPos.copy()

        if (absolute) {
            currentPos.x = x
            currentPos.y = y
        } else {
            currentPos.x += x
            currentPos.y += y
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

    private fun parseCoordinatePairTriple(): List<Point> {
        return mutableListOf<Point>().apply {
            add(parseCoordinatePair())
            optionalParseCommaWhitespace()
            add(parseCoordinatePair())
            optionalParseCommaWhitespace()
            add(parseCoordinatePair())
        }
    }

    private fun parseCoordinatePairDouble(): List<Point> {
        return mutableListOf<Point>().apply {
            add(parseCoordinatePair())
            optionalParseCommaWhitespace()
            add(parseCoordinatePair())
        }
    }

    private fun parseCoordinatePair(): Point {
        val x = parseCoordinate()
        optionalParseCommaWhitespace()
        return Point(x, parseCoordinate())
    }

    private fun parseCoordinateSequence() = mutableListOf<Float>().also {
        while (true) {
            it.add(parseCoordinate())
            optionalParseCommaWhitespace()
            if (!matchCommaWhitespace() && !matchCoordinate())
                break
        }
    }

    private fun parseCoordinatePairSequence() = mutableListOf<Point>().also {
        while (true) {
            it.add(parseCoordinatePair())
            optionalParseCommaWhitespace()
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

    private fun optionalParseCommaWhitespace() {
        if (matchCommaWhitespace())
            parseCommaWhitespace()
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