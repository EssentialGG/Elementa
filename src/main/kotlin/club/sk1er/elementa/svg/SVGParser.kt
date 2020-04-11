package club.sk1er.elementa.svg

import org.dom4j.Document
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

        val elements = svg.elements().map {
            when (it.name) {
                "circle" -> SVGCircle.from(it)
                "line" -> SVGLine.from(it)
                else -> throw UnsupportedOperationException("Element type ${it.name} is not supported!")
            }
        }

        return SVG(elements, svgWidth, svgHeight, svgStrokeWidth)
    }
}