package util

import kotlin.math.atan2
import kotlin.math.sqrt

data class Position(val row: Int, val col: Int)

data class Position3D(val x: Int, val y: Int, val z: Int)

data class Rectangle(val corner1: Position, val corner2: Position) {
    val minRow = minOf(corner1.row, corner2.row)
    val maxRow = maxOf(corner1.row, corner2.row)
    val minCol = minOf(corner1.col, corner2.col)
    val maxCol = maxOf(corner1.col, corner2.col)

    fun area() = corner1.rectangleAreaTo(corner2)

    fun intersects(other: Rectangle): Boolean {
        return !(other.maxCol <= minCol || maxCol <= other.minCol ||
                 other.maxRow <= minRow || maxRow <= other.minRow)
    }
}

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

fun Position.rectangleAreaTo(other: Position): Long {
    val width = kotlin.math.abs(other.col - col) + 1L
    val height = kotlin.math.abs(other.row - row) + 1L
    return width * height
}

fun Position3D.distanceTo(other: Position3D): Double {
    val dx = (other.x - x).toDouble()
    val dy = (other.y - y).toDouble()
    val dz = (other.z - z).toDouble()
    return sqrt(dx * dx + dy * dy + dz * dz)
}

interface GridCell {
    fun toChar(): Char
}

data class Shape(val cells: Set<Pair<Int, Int>>) {
    val area = cells.size

    fun allOrientations(): List<Shape> {
        val rotations = generateSequence(this) { it.rotate90() }.take(4).toList()
        return (rotations + rotations.map { it.flip() }).distinct()
    }

    private fun rotate90() = Shape(cells.map { (x, y) -> (-y to x) }.toSet()).normalize()

    private fun flip() = Shape(cells.map { (x, y) -> (cells.maxOf { it.first } - x) to y }.toSet())

    private fun normalize(): Shape {
        val minX = cells.minOf { it.first }
        val minY = cells.minOf { it.second }
        return Shape(cells.map { (x, y) -> (x - minX) to (y - minY) }.toSet())
    }
}

data class GridBounds(val width: Int, val height: Int)

fun canPlaceShape(
    grid: Array<BooleanArray>,
    shape: Shape,
    offsetX: Int,
    offsetY: Int,
    bounds: GridBounds
) = shape.cells.all { (x, y) ->
    val newX = x + offsetX
    val newY = y + offsetY
    newX in 0 until bounds.width && newY in 0 until bounds.height && !grid[newY][newX]
}

fun updateGrid(grid: Array<BooleanArray>, shape: Shape, offsetX: Int, offsetY: Int, value: Boolean) {
    shape.cells.forEach { (x, y) -> grid[y + offsetY][x + offsetX] = value }
}
