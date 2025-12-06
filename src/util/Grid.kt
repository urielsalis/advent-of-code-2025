package util

import kotlin.math.atan2

typealias Position = Pair<Int, Int>

val Position.row: Int get() = first
val Position.col: Int get() = second

data class Grid<T : GridCell>(val content: List<MutableList<T>>) {
    val numRows: Int = content.size
    val numCols: Int = content.firstOrNull()?.size ?: 0

    operator fun get(position: Position): T = content[position.row][position.col]

    operator fun set(position: Position, value: T) {
        content[position.row][position.col] = value
    }

    fun getRow(row: Int): List<T> = content[row]

    fun getCol(col: Int): List<T> = content.map { it[col] }

    fun neighbors(position: Position, diagonals: Boolean = false): List<T> {
        val deltas = if (diagonals) DIAGONAL_DELTAS else ORTHOGONAL_DELTAS

        return deltas.mapNotNull { (deltaRow, deltaCol) ->
            val neighborRow = position.row + deltaRow
            val neighborCol = position.col + deltaCol
            content.getOrNull(neighborRow)?.getOrNull(neighborCol)
        }
    }

    fun <R> flatMapIndexed(transform: (row: Int, col: Int, cell: T) -> R): List<R> =
        content.flatMapIndexed { row, rowList ->
            rowList.mapIndexed { col, cell ->
                transform(row, col, cell)
            }
        }

    fun printGrid() {
        for (row in content) {
            val line = row.map { it.toChar() }.joinToString("")
            println(line)
        }
    }

    fun clone(): Grid<T> = Grid(content.map { it.toMutableList() })

    companion object {
        private val ORTHOGONAL_DELTAS = listOf(
            -1 to 0, 0 to -1, 0 to 1, 1 to 0
        )
        private val DIAGONAL_DELTAS = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1, 0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )
    }
}


fun Position.angleTo(other: Position): Double {
    val deltaX = other.col - col
    val deltaY = row - other.row
    val angle = Math.toDegrees(atan2(deltaX.toDouble(), deltaY.toDouble()))
    return if (angle < 0) angle + 360 else angle
}

fun Position.distanceTo(other: Position): Int {
    val dx = other.col - col
    val dy = other.row - row
    return dx * dx + dy * dy
}

interface GridCell {
    fun toChar(): Char
}