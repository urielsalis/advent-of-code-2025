import util.GridBounds
import util.Shape
import util.canPlaceShape
import util.readInput
import util.updateGrid

data class Region(val width: Int, val height: Int, val requirements: List<Int>)

fun parseInput(input: String): Pair<List<Shape>, List<Region>> {
    val groups = input.split("\n\n")

    val shapes = groups.takeWhile { !it.contains("x") }.map { group ->
        val cells = group.lines().drop(1)
            .flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, c -> if (c == '#') x to y else null }
            }.toSet()
        Shape(cells)
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

class PlacementContext(
    val grid: Array<BooleanArray>,
    val shapesToPlace: List<Shape>,
    val bounds: GridBounds
)

fun tryPlaceShapes(context: PlacementContext, index: Int, callCount: Int = 0): Boolean {
    if (callCount > 1_000_000 || index >= context.shapesToPlace.size) {
        return index == context.shapesToPlace.size
    }

    val shape = context.shapesToPlace[index]
    return shape.allOrientations().any { orientation ->
        tryAllPositions(context, orientation, index, callCount)
    }
}

data class PlacementAttempt(val orientation: Shape, val x: Int, val y: Int)

fun tryAllPositions(context: PlacementContext, orientation: Shape, index: Int, callCount: Int): Boolean {
    val positions = (0 until context.bounds.height).flatMap { y ->
        (0 until context.bounds.width).map { x -> PlacementAttempt(orientation, x, y) }
    }

    return positions.any { attempt ->
        canPlaceShape(context.grid, attempt.orientation, attempt.x, attempt.y, context.bounds) &&
            tryWithPlacement(context, attempt, index, callCount)
    }
}

fun tryWithPlacement(
    context: PlacementContext,
    attempt: PlacementAttempt,
    index: Int,
    callCount: Int
): Boolean {
    updateGrid(context.grid, attempt.orientation, attempt.x, attempt.y, true)
    val success = tryPlaceShapes(context, index + 1, callCount + 1)
    updateGrid(context.grid, attempt.orientation, attempt.x, attempt.y, false)
    return success
}

fun canFitExact(region: Region, shapes: List<Shape>): Boolean {
    val shapesToPlace = region.requirements.flatMapIndexed { idx, count ->
        List(count) { shapes[idx] }
    }

    if (shapesToPlace.isEmpty()) return true

    val context = PlacementContext(
        grid = Array(region.height) { BooleanArray(region.width) },
        shapesToPlace = shapesToPlace,
        bounds = GridBounds(region.width, region.height)
    )
    return tryPlaceShapes(context, 0)
}

fun canFit(region: Region, shapes: List<Shape>): Boolean {
    val totalArea = region.requirements.zip(shapes).sumOf { (count, shape) -> count * shape.area }
    val totalPresents = region.requirements.sum()

    // Quick heuristic: As all gifts are 3x3 shapes with spaces in them, if the region could fit
    // all presents as solid 3x3 blocks, then it has to be able to fit the actual shapes and we
    // don't need to do the full backtracking search.
    // Unfortunately, the main input of the solution has no cases where this is not true and we
    // never do the backtracking search there, so this is only useful for the example inputs.
    return when {
        totalArea > region.width * region.height -> false
        totalPresents <= (region.width / 3) * (region.height / 3) -> true
        else -> canFitExact(region, shapes)
    }
}

fun main() {
    fun part1(input: String): Int {
        val (shapes, regions) = parseInput(input)
        return regions.count { canFit(it, shapes) }
    }

    val testInput = readInput(12, true)
    check(part1(testInput) == 2)

    val input = readInput(12)
    println("Part 1: " + part1(input))
    check(part1(input) == 454)
}
