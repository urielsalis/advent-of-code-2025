import util.Position
import util.Rectangle
import util.readLines

fun main() {
    fun parsePositions(input: List<String>) = input.map { line ->
        val (row, col) = line.split(",").map(String::toInt)
        Position(row, col)
    }

    fun part1(positions: List<Position>): Long {
        var maxArea = 0L

        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val rect = Rectangle(positions[i], positions[j])
                maxArea = maxOf(maxArea, rect.area())
            }
        }

        return maxArea
    }

    fun part2(positions: List<Position>): Long {
        val edges = positions.indices.map { i ->
            Rectangle(positions[i], positions[(i + 1) % positions.size])
        }

        var maxArea = 0L

        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val rect = Rectangle(positions[i], positions[j])

                if (!edges.any { it.intersects(rect) }) {
                    maxArea = maxOf(maxArea, rect.area())
                }
            }
        }

        return maxArea
    }

    val testPositions = parsePositions(readLines(9, isTest = true))
    check(part1(testPositions) == 50L)
    check(part2(testPositions) == 24L)

    val positions = parsePositions(readLines(9))
    println("Part 1: ${part1(positions)}")
    println("Part 2: ${part2(positions)}")
    check(part1(positions) == 4774877510L)
    check(part2(positions) == 1560475800L)
}
