package gg.essential.elementa.constraints.resolution

import gg.essential.elementa.utils.Matrix2b
import java.util.*

class DirectedAcyclicGraph<T> {
    private val vertices = mutableListOf<T>()
    private var hasAddedEdge = false
    private val edges by lazy { Matrix2b(vertices.size, vertices.size) }

    fun addVertex(vertex: T) = apply {
        if (hasAddedEdge)
            throw IllegalStateException("Cannot add a vertex after adding an edge!")
        vertices.add(vertex)
    }

    fun addVertices(vararg vertices: T) = apply {
        vertices.forEach { addVertex(it) }
    }

    fun addEdge(first: T, second: T): DirectedAcyclicGraph<T> {
        hasAddedEdge = true
        if (first == second)
            throw IllegalArgumentException("Cannot add edge between a vertex and itself: $first")

        val firstIndex = vertices.indexOf(first)
        val secondIndex = vertices.indexOf(second)

        if (firstIndex == -1)
            throw IllegalArgumentException("$first is not a vertex!")
        if (secondIndex == -1)
            throw IllegalArgumentException("$second is not a vertex!")

        edges[firstIndex, secondIndex] = true

        return this
    }

    fun addEdges(vararg edges: Pair<T, T>): DirectedAcyclicGraph<T> {
        edges.forEach { (first, second) -> addEdge(first, second) }
        return this
    }

    fun getCyclicLoop() = CyclicHelper().cyclicLoop()

    // Algorithm taken from https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
    private inner class CyclicHelper {
        private val temporaryMarks = vertices.indices.map { false }.toMutableList()
        private val permanentMarks = vertices.indices.map { false }.toMutableList()
        private val nodeStack = Stack<Int>()

        fun cyclicLoop(): List<T>? {
            while (true) {
                val indexOfNonMarked = permanentMarks.indexOfFirst { !it }
                if (indexOfNonMarked == -1)
                    return null
                if (!visit(indexOfNonMarked))
                    return nodeStack.dropWhile {
                        it != nodeStack.last()
                    }.map { vertices[it] }
            }
        }

        private fun visit(index: Int): Boolean {
            if (permanentMarks[index])
                return true

            nodeStack.push(index)
            if (temporaryMarks[index])
                return false

            temporaryMarks[index] = true

            for ((edgeIndex, connects) in edges.row(index).withIndex()) {
                if (connects && !visit(edgeIndex))
                    return false
            }

            temporaryMarks[index] = false
            permanentMarks[index] = true
            nodeStack.pop()

            return true
        }
    }
}
