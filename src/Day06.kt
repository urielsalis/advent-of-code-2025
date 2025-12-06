import util.readLines

fun main() {
    fun applyOperation(operation: Char, numbers: List<Long>): Long = when (operation) {
        '+' -> numbers.sum()
        '*' -> numbers.reduce(Long::times)
        else -> error("Unknown operation: $operation")
    }

    fun part1(input: List<String>): Long {
        val operations = input.last().split("\\s+".toRegex())
        val numbers = input.dropLast(1).map { row ->
            row.split("\\s+".toRegex()).map { it.toLong() }
        }

        return operations.indices.sumOf { i ->
            val column = numbers.map { it[i] }
            applyOperation(operations[i].single(), column)
        }
    }

    fun part2(input: List<String>): Long {
        val operations = input.last()
        val grid = input.dropLast(1).map { it.toCharArray() }
        val maxColumn = grid.maxOf { it.size } - 1

        var sum = 0L
        val batch = mutableListOf<Long>()
        var col = maxColumn

        while (col >= 0) {
            val number = buildString {
                grid.forEach { row ->
                    row.getOrNull(col)?.digitToIntOrNull()?.let { append(it) }
                }
            }.toLong()

            batch.add(number)

            operations.getOrNull(col)?.takeIf { it != ' ' }?.let { operation ->
                sum += applyOperation(operation, batch)
                batch.clear()
                col--
            }
            col--
        }

        return sum
    }

    val testInput = readLines(6, isTest = true)
    check(part1(testInput) == 4277556L)
    check(part2(testInput) == 3263827L)

    val input = readLines(6)
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
    check(part1(input) == 4719804927602)
    check(part2(input) == 9608327000261L)
}
