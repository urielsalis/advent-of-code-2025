fun main() {
    fun generateRanges(input: String): List<LongRange> {
        return input.split(",").map { section ->
            val (start, end) = section.split("-").map { it.toLong() }
            start..end
        }
    }

    fun part1(input: String): Long {
        val ranges = generateRanges(input)
        val regex = Regex("^(\\d+?)\\1\$")
        return ranges.flatMap { it.filter { regex.matches(it.toString()) } }.sum()
    }

    fun part2(input: String): Long {
        val ranges = generateRanges(input)
        val regex = Regex("^(\\d+?)\\1+\$")
        return ranges.flatMap { it.filter { regex.matches(it.toString()) } }.sum()
    }


    val testInput = readInput("Day02_test").first()
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)

    val input = readInput("Day02").first()
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
    check(part1(input) == 55916882972L)
    check(part2(input) == 76169125915L)
}
