import util.merge
import util.readLines

fun parseInput(input: List<String>): Pair<List<LongRange>, List<Long>> {
    val emptyLineIndex = input.indexOfFirst { it.isEmpty() }
    val rangeLines = input.subList(0, emptyLineIndex)
    val valueLines = input.subList(emptyLineIndex + 1, input.size)

    val ranges = rangeLines.map { line ->
        val (start, end) = line.split("-").map { it.toLong() }
        start..end
    }
    val values = valueLines.map { it.toLong() }

    return ranges to values
}

fun main() {
    fun part1(ranges: List<LongRange>, values: List<Long>): Int =
        values.count { value -> ranges.any { value in it } }

    fun part2(ranges: List<LongRange>): Long =
        merge(ranges).sumOf { it.last - it.first + 1 }

    val testInput = readLines(5, isTest = true)
    val (testRanges, testValues) = parseInput(testInput)
    check(part1(testRanges, testValues) == 3)
    check(part2(testRanges) == 14L)

    val input = readLines(5)
    val (ranges, values) = parseInput(input)
    println("Part 1: ${part1(ranges, values)}")
    println("Part 2: ${part2(ranges)}")
    check(part1(ranges, values) == 661)
    check(part2(ranges) == 359526404143208)
}
