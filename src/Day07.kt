import util.Grid
import util.GridCell
import util.readGrid

enum class TachyonGrid(private val char: Char) : GridCell {
    EMPTY('.'),
    SPLITTER('^'),
    START('S');

    override fun toChar(): Char = char

    companion object {
        fun fromChar(char: Char): GridCell = TachyonGrid.entries.first { it.char == char }
    }
}

fun main() {
    fun solve(input: Grid<GridCell>): Pair<Int, Long> {
        val startCol = input.find(TachyonGrid.START).col

        val splitters = input.findAllPositions { it == TachyonGrid.SPLITTER }.map { it.col }

        val timelinesEntering = buildList<Long> {
            for (i in splitters.indices) {
                val col = splitters[i]
                var count = 0L

                for (j in (i - 1) downTo 0) {
                    val prevCol = splitters[j]
                    if (prevCol == col) break
                    if (kotlin.math.abs(col - prevCol) == 1) {
                        count += this[j]
                    }
                }

                val isFirstInStartColumn = col == startCol && splitters.take(i).none { it == col }
                if (isFirstInStartColumn) count += 1L

                add(count)
            }
        }

        return timelinesEntering.count { it > 0 } to timelinesEntering.sum() + 1
    }

    val testInput = readGrid(7, isTest = true, TachyonGrid::fromChar)
    val (testPart1, testPart2) = solve(testInput)
    check(testPart1 == 21)
    check(testPart2 == 40L)

    val input = readGrid(7, false, TachyonGrid::fromChar)
    val (part1, part2) = solve(input)
    println("Part 1: $part1")
    println("Part 2: $part2")
    check(part1 == 1633)
    check(part2 == 34339203133559L)
}
