package club.sk1er.elementa.svg

import club.sk1er.elementa.svg.data.*
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.lang.UnsupportedOperationException

object SVGParser {
    fun parseFromResource(file: String): SVG {
        val reader = SAXReader()
        val document = reader.read(this::class.java.getResourceAsStream(file))

        return parseDocument(document)
    }

    private fun parseDocument(document: Document): SVG {
        val svg = document.rootElement
        val svgWidth = svg.attributeValue("width", "24").toInt()
        val svgHeight = svg.attributeValue("height", "24").toInt()
        val svgStrokeWidth = svg.attributeValue("stroke-width", "1").toFloat()
        val svgRoundLineCaps = svg.attributeValue("stroke-linecap") != null
        val svgRoundLineJoins = svg.attributeValue("stroke-linejoin") != null

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

        return SVG(elements, svgWidth, svgHeight, svgStrokeWidth, svgRoundLineCaps, svgRoundLineJoins)
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

    private fun parsePath(el: Element): List<SVGElement> {
        return PathParser(el.attributeValue("d").trim()).parse()
    }
}