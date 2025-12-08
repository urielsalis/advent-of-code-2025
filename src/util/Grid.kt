package util

import kotlin.math.atan2

class Position(val row: Int, val col: Int)

data class Grid<T : GridCell>(private val content: List<MutableList<T>>) {
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

    fun findAllPositions(predicate: (T) -> Boolean): List<Position> =
        content.flatMapIndexed { row, rowList ->
            rowList.mapIndexedNotNull { col, cell ->
                if (predicate(cell)) Position(row, col) else null
            }
        }

    fun printGrid() {
        for (row in content) {
            val line = row.map { it.toChar() }.joinToString("")
            println(line)
        }
    }

    fun clone(): Grid<T> = Grid(content.map { it.toMutableList() })
    fun find(gridCell: GridCell): Position = content.withIndex().flatMap { (rowIndex, row) ->
        row.withIndex().mapNotNull { (colIndex, cell) ->
            if (cell == gridCell) {
                Position(rowIndex, colIndex)
            } else {
                null
            }
        }
    }.first()

    /**
     * Performs a depth-first search starting from the given position.
     * @param start The starting position for the search
     * @param getNextPositions Lambda that returns the next positions to visit given the current position
     * @param onVisit Optional lambda called when visiting each position
     * @return Set of all visited positions
     */
    fun dfs(
        start: Position,
        getNextPositions: (Position, T) -> List<Position>,
        onVisit: ((Position, T) -> Unit)? = null
    ): Set<Position> {
        val visited = mutableSetOf<Position>()

        fun dfsRecursive(position: Position) {
            // Boundary check
            if (position.row !in content.indices || position.col !in content[0].indices) return
            // Already visited check
            if (position in visited) return
            visited.add(position)

            val cell = this[position]
            onVisit?.invoke(position, cell)

            // Get next positions to explore
            val nextPositions = getNextPositions(position, cell)
            nextPositions.forEach { dfsRecursive(it) }
        }

        dfsRecursive(start)
        return visited
    }

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
