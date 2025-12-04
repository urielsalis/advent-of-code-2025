import util.pow
import util.readLines

fun findBiggestCombination(
    bank: String, cache: MutableMap<Pair<String, Int>, Long>, size: Int
): Long = cache.getOrPut(bank to size) {
    when {
        size == 1 -> bank.maxOf { it.digitToInt() }.toLong()
        bank.length < size -> -1L
        bank.length == size -> bank.toLong()
        else -> {
            val firstDigit = bank.first().digitToInt()
            val rest = bank.drop(1)
            val withFirst =
                firstDigit * 10.pow(size - 1) + findBiggestCombination(rest, cache, size - 1)
            val withoutFirst = findBiggestCombination(rest, cache, size)
            maxOf(withFirst, withoutFirst)
        }
    }
}

fun main() {

    val cache = mutableMapOf<Pair<String, Int>, Long>()

    fun part1(input: List<String>): Long =
        input.sumOf { bank -> findBiggestCombination(bank, cache, 2) }

    fun part2(input: List<String>): Long =
        input.sumOf { bank -> findBiggestCombination(bank, cache, 12) }

    val testInput = readLines(3, isTest = true)
    check(part1(testInput) == 357L)
    check(part2(testInput) == 3121910778619L)

    val input = readLines(3)
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
    check(part1(input) == 17766L)
    check(part2(input) == 176582889354075L)
}
