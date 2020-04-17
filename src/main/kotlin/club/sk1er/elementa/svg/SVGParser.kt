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

        val elements = svg.elements().flatMap {
            val els = when (it.name) {
                "circle" -> listOf(SVGCircle.from(it))
                "line" -> listOf(SVGLine.from(it))
                "polyline" -> listOf(SVGPolyline.from(it))
                "path" -> parsePath(it)
                "rect" -> listOf(SVGRect.from(it))
                else -> throw UnsupportedOperationException("Element type ${it.name} is not supported!")
            }

            it.attributeValue("transform")?.let { attribute ->
                val transform = parseTransform(attribute)
                els.forEach { el -> el.attributes.transform = transform }
            }

            els
        }

        return SVG(elements, svgWidth, svgHeight, svgStrokeWidth)
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