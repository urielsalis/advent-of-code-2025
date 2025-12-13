import util.readLines
import util.Graph

data class PathContext(
    val graph: Graph,
    val requiredNodes: List<String>,
    val memo: MutableMap<Pair<String, Int>, Long>
)

fun main() {
    fun buildGraph(input: List<String>) = Graph().apply {
        input.forEach { line ->
            val (from, toList) = line.split(":")
            toList.split(" ")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { to -> addEdge(from.trim(), to) }
        }
    }

    fun countPaths(
        current: String,
        visited: Set<String>,
        requiredNodesMask: Int,
        context: PathContext
    ): Long {
        val allRequiredMask = (1 shl context.requiredNodes.size) - 1

        return when {
            current == "out" -> if (requiredNodesMask == allRequiredMask) 1L else 0L
            current in visited -> 0L
            else -> context.memo.getOrPut(current to requiredNodesMask) {
                val updatedMask = context.requiredNodes.indexOf(current).takeIf { it >= 0 }
                    ?.let { requiredNodesMask or (1 shl it) }
                    ?: requiredNodesMask

                context.graph.getNeighbors(current).sumOf { neighbor ->
                    countPaths(neighbor, visited + current, updatedMask, context)
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val context = PathContext(buildGraph(input), emptyList(), mutableMapOf())
        return countPaths("you", emptySet(), 0, context)
    }

    fun part2(input: List<String>): Long {
        val requiredNodes = listOf("dac", "fft")
        val context = PathContext(buildGraph(input), requiredNodes, mutableMapOf())
        return countPaths("svr", emptySet(), 0, context)
    }

    val testInput = readLines(11, isTest = true)
    check(part1(testInput) == 8L)
    check(part2(testInput) == 2L)

    val input = readLines(11)
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
    check(part1(input) == 786L)
    check(part2(input) == 495845045016588L)
}
