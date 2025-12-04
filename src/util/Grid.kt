package util

data class Grid<T : GridCell>(val content: List<MutableList<T>>) {
    val numRows: Int = content.size
    val numCols: Int = content.firstOrNull()?.size ?: 0

    operator fun get(row: Int, col: Int): T = content[row][col]

    operator fun set(row: Int, col: Int, value: T) {
        content[row][col] = value
    }

    fun getRow(row: Int): List<T> = content[row]

    fun getCol(col: Int): List<T> = content.map { it[col] }

    fun neighbors(row: Int, col: Int, diagonals: Boolean = false): List<T> {
        val deltas = if (diagonals) DIAGONAL_DELTAS else ORTHOGONAL_DELTAS

        return deltas.mapNotNull { (deltaRow, deltaCol) ->
            val neighborRow = row + deltaRow
            val neighborCol = col + deltaCol
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

interface GridCell {
    fun toChar(): Char
}