package gg.essential.elementa.utils

open class Matrix2<T>(
    val numRows: Int,
    val numColumns: Int,
    private val defaultT: () -> T
) {
    private val data: List<MutableList<T>>

    val rows: List<List<T>>
        get() = data

    val columns: List<List<T>>
        get() = (0 until numColumns).map { c ->
            data.map { it[c] }
        }

    val items: List<T>
        get() = data.flatten()

    init {
        if (numRows <= 0)
            throw IllegalArgumentException("Rows must be an integer greater than 0")
        if (numColumns <= 0)
            throw IllegalArgumentException("Column must be an integer greater than 0")

        data = (0 until numRows).map {
            (0 until numColumns).map { defaultT() }.toMutableList()
        }
    }

    constructor(rows: Int, columns: Int, initialValue: T) : this(rows, columns, { initialValue })

    fun row(row: Int): List<T> {
        rowBoundsCheck(row)
        return data[row]
    }

    fun column(column: Int): List<T> {
        columnBoundsCheck(column)
        return data.map { it[column] }
    }

    operator fun get(row: Int, column: Int): T {
        boundsCheck(row, column)
        return data[row][column]
    }

    operator fun set(row: Int, column: Int, value: T) = apply {
        boundsCheck(row, column)
        data[row][column] = value
    }

    private fun boundsCheck(row: Int, column: Int) {
        rowBoundsCheck(row)
        columnBoundsCheck(column)
    }

    private fun rowBoundsCheck(row: Int) {
        if (row < 0 || row >= numRows)
            throw IllegalStateException("row must be in the range [0, ${numRows-1}]")
    }

    private fun columnBoundsCheck(column: Int) {
        if (column < 0 || column >= numColumns)
            throw IllegalStateException("column must be in the range [0, ${numColumns-1}]")
    }
}

class Matrix2b(rows: Int, columns: Int) : Matrix2<Boolean>(rows, columns, false)
class Matrix2f(rows: Int, columns: Int) : Matrix2<Float>(rows, columns, 0f)
class Matrix2d(rows: Int, columns: Int) : Matrix2<Double>(rows, columns, 0.0)
class Matrix2i(rows: Int, columns: Int) : Matrix2<Int>(rows, columns, 0)
