fun extractMovement(movement: String): Pair<Char, Int> {
    val direction = movement[0]
    val count = movement.substring(1).toInt()
    return Pair(direction, count)
}

fun adjust(value: Int): Int {
    return ((value % 100) + 100) % 100
}

fun move(direction: Char, position: Int, count: Int): Int {
    return when (direction) {
        'R' -> adjust(position + count)
        'L' -> adjust(position - count)
        else -> error("Invalid direction")
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var position = 50
        var seenZeros = 0
        for (movement in input) {
            val (direction, count) = extractMovement(movement)
            position = move(direction, position, count)
            if (position == 0) {
                seenZeros++
            }
        }
        return seenZeros
    }

    fun part2(input: List<String>): Int {
        var position = 50
        var passThroughZeros = 0
        for (movement in input) {
            val (direction, count) = extractMovement(movement)
            val stepsToZero = when {
                position == 0 -> 100
                direction == 'L' -> position
                else -> 100 - position
            }
            if (count >= stepsToZero) {
                passThroughZeros++
                passThroughZeros += (count - stepsToZero) / 100
            }
            position = move(direction, position, count)
        }
        return passThroughZeros
    }


    val testInput = readLines(1, isTest = true)
    check(part1(testInput) == 3)
    check(part2(testInput) == 6)

    val input = readLines(1)
    println("Part 1: " + part1(input)) // 1158
    println("Part 2: " + part2(input)) // 6860
    check(part1(input) == 1158)
    check(part2(input) == 6860)
}
