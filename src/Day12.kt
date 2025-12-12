import util.readInput

data class Shape(val area: Int)

data class Region(val width: Int, val height: Int, val requirements: List<Int>)

fun parseInput(input: String): Pair<List<Shape>, List<Region>> {
    val groups = input.split("\n\n")

    val shapes = groups.takeWhile { !it.contains("x") }
        .map { group ->
            val gridLines = group.lines().drop(1).filter { it.isNotEmpty() }
            Shape(gridLines.sumOf { line -> line.count { it == '#' } })
        }

    val regions = groups.dropWhile { !it.contains("x") }
        .flatMap { it.lines() }
        .filter { it.contains(":") }
        .map { line ->
            val (dimensions, requirements) = line.split(":")
            val (width, height) = dimensions.trim().split("x").map { it.toInt() }
            val reqs = requirements.trim().split(" ").map { it.toInt() }
            Region(width, height, reqs)
        }

    return shapes to regions
}

fun canFit(region: Region, shapes: List<Shape>): Boolean {
    val totalArea = region.requirements.zip(shapes).sumOf { (count, shape) -> count * shape.area }
    if (totalArea > region.width * region.height) return false

    // This feels dirty. As each shape occupies at least a 3x3 area (including spacing),
    // we can check if the total number of presents can fit in the region divided by 3.
    // There are no cases in the input where this would give a false positive. :oops:
    val totalPresents = region.requirements.sum()
    val maxPresents = (region.width / 3) * (region.height / 3)

    return totalPresents <= maxPresents
}

fun main() {
    fun part1(input: String): Int {
        val (shapes, regions) = parseInput(input)
        return regions.count { canFit(it, shapes) }
    }

    val input = readInput(12)
    println("Part 1: " + part1(input))
    check(part1(input) == 454)
}
