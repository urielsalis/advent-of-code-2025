import util.Grid
import util.GridCell
import util.Position
import util.readGrid

enum class PaperGrid(private val char: Char) : GridCell {
    EMPTY('.'),
    PAPER('@');

    override fun toChar(): Char = char

    companion object {
        fun fromChar(char: Char): GridCell = when (char) {
            '.' -> EMPTY
            '@' -> PAPER
            else -> error("Unknown grid character: $char")
        }
    }
}

fun main() {
    fun part1(input: Grid<GridCell>): Int = input.flatMapIndexed { row, col, cell ->
        if (cell == PaperGrid.PAPER &&
            input.neighbors(Position(row, col), diagonals = true).count { it == PaperGrid.PAPER } < 4) {
            1
        } else {
            0
        }
    }.sum()

    fun part2(input: Grid<GridCell>): Int {
        val grid = input.clone()
        var total = 0

        while (true) {
            val accessiblePositions = buildList {
                grid.flatMapIndexed { row, col, cell ->
                    if (cell == PaperGrid.PAPER &&
                        grid.neighbors(Position(row, col), diagonals = true).count { it == PaperGrid.PAPER } < 4) {
                        add(Position(row, col))
                    }
                }
            }

            if (accessiblePositions.isEmpty()) break

            accessiblePositions.forEach { position -> grid[position] = PaperGrid.EMPTY }
            total += accessiblePositions.size
        }

        return total
    }

    val testInput = readGrid(4, isTest = true, PaperGrid::fromChar)
    check(part1(testInput) == 13)
    check(part2(testInput) == 43)

    val input = readGrid(4, isTest = false, PaperGrid::fromChar)
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
    check(part1(input) == 1527)
    check(part2(input) == 8690)
}
