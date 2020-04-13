package club.sk1er.elementa.svg.data

import java.nio.FloatBuffer

abstract class SVGElement {
    val attributes: SVGAttributes = SVGAttributes()

    abstract fun getVertexCount(): Int

    abstract fun createBuffer(buffer: FloatBuffer): Int
}