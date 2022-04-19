package gg.essential.elementa.svg

import gg.essential.elementa.impl.dom4j.Document
import gg.essential.elementa.impl.dom4j.Element
import gg.essential.elementa.impl.dom4j.io.SAXReader
import gg.essential.elementa.svg.data.*
import java.lang.UnsupportedOperationException

object SVGParser {
    private val VIEWBOX_WHITESPACE = "[ ,]+".toRegex()

    fun parseFromResource(file: String): SVG {
        val reader = SAXReader()
        val document = reader.read(this::class.java.getResourceAsStream(file))

        return parseDocument(document)
    }

    private fun parseDocument(document: Document): SVG {
        val svg = document.rootElement
        val svgStrokeWidth = svg.attributeValue("stroke-width", "1").toFloat()
        val svgRoundLineCaps = svg.attributeValue("stroke-linecap") != null
        val svgRoundLineJoins = svg.attributeValue("stroke-linejoin") != null
        val viewBox = parseViewbox(svg.attributeValue("viewBox"))

        val elements = mutableListOf<SVGElement>()

        loop@ for (element in svg.elements()) {
            val els = when (element.name) {
                "circle" -> listOf(SVGCircle.from(element))
                "line" -> listOf(SVGLine.from(element))
                "polyline" -> listOf(SVGPolyline.from(element))
                "path" -> try {
                    parsePath(element)
                } catch (e: PathParser.PathParseException) {
                    e.printStackTrace()
                    break@loop
                }
                "rect" -> listOf(SVGRect.from(element))
                else -> throw UnsupportedOperationException("Element type ${element.name} is not supported!")
            }

            element.attributeValue("transform")?.let { attribute ->
                val transform = parseTransform(attribute)
                els.forEach { el -> el.attributes.transform = transform }
            }

            elements.addAll(els)
        }

        return SVG(elements, viewBox?.width, viewBox?.height, svgStrokeWidth, svgRoundLineCaps, svgRoundLineJoins)
    }

    private fun parseTransform(attributeString: String): Transform {
        var attributes = attributeString
        val transform = Transform()

        while (attributes.isNotEmpty()) {
            val firstOpenParen = attributes.indexOfFirst { it == '(' }
            val firstCloseParen = attributes.indexOfFirst { it == ')' }

            when (attributes.substring(0, firstOpenParen)) {
                "rotate" -> {
                    val parameters = attributes.substring(firstOpenParen + 1, firstCloseParen).split(" ")

                    transform.rotation = Rotation(
                        parameters.first().toInt(),
                        parameters.getOrNull(1)?.toFloatOrNull(),
                        parameters.getOrNull(2)?.toFloatOrNull()
                    )
                }
                else -> TODO()
            }

            attributes = attributes.substring(firstCloseParen + 1)
        }

        return transform
    }

    private fun parseViewbox(attributeString: String?): Viewbox? {
        if (attributeString == null)
            return null

        try {
            val splits = attributeString.replace(VIEWBOX_WHITESPACE, " ").split(" ").map { it.toFloat() }
            if (splits.size >= 3 && splits[2] <= 0f)
                return null
            if (splits.size >= 4 && splits[3] <= 0f)
                return null
            return Viewbox(splits[0], splits[1], splits[2], splits[3])
        } catch (e: Exception) {
            return null
        }
    }

    private fun parsePath(el: Element): List<SVGElement> {
        return PathParser(el.attributeValue("d").trim()).parse()
    }

    // TODO: Should we do anything with the x and y? They're unused right now as they
    // don't really make sense outside of the DOM
    private data class Viewbox(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float
    )
}