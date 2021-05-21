package gg.essential.elementa.svg.data

import java.nio.FloatBuffer

abstract class SVGElement {
    val attributes: SVGAttributes = SVGAttributes()

    abstract fun getVertexCount(): Int

    open fun applyAttributes() { }

    open fun drawSmoothPoints(): Boolean {
        return true
    }

    abstract fun createBuffer(buffer: FloatBuffer): Int
}