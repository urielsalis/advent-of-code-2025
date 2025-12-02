fun main() {
    fun generateRanges(input: String): List<LongRange> =
        input.split(",").map {
            it.split("-").let { (start, end) -> start.toLong()..end.toLong() }
        }

    fun sumMatchingNumbers(input: String, regex: Regex): Long =
        generateRanges(input).sumOf { range ->
            range.sumOf { num ->
                num.takeIf { regex.matches(it.toString()) } ?: 0L
            }
        }

    fun part1(input: String): Long =
        sumMatchingNumbers(input, Regex("""^(\d+?)\1$"""))

    fun part2(input: String): Long =
        sumMatchingNumbers(input, Regex("""^(\d+?)\1+$"""))



    val testInput = readInput("Day02_test").first()
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)

    val input = readInput("Day02").first()
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
    check(part1(input) == 55916882972L)
    check(part2(input) == 76169125915L)
}
