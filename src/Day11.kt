import util.readLines
import util.Graph

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
        graph: Graph,
        visited: Set<String>,
        requiredNodesMask: Int,
        requiredNodes: List<String>,
        memo: MutableMap<Pair<String, Int>, Long>
    ): Long {
        if (current == "out") {
            val allRequiredMask = (1 shl requiredNodes.size) - 1
            return if (requiredNodesMask == allRequiredMask) 1L else 0L
        }

        if (current in visited) return 0L

        memo[current to requiredNodesMask]?.let { return it }

        val updatedMask = requiredNodes.indexOf(current).takeIf { it >= 0 }
            ?.let { requiredNodesMask or (1 shl it) }
            ?: requiredNodesMask

        val pathCount = graph.getNeighbors(current).sumOf { neighbor ->
            countPaths(neighbor, graph, visited + current, updatedMask, requiredNodes, memo)
        }

        return pathCount.also { memo[current to requiredNodesMask] = it }
    }

    fun part1(input: List<String>): Long {
        val graph = buildGraph(input)
        return countPaths("you", graph, emptySet(), 0, emptyList(), mutableMapOf())
    }

    fun part2(input: List<String>): Long {
        val graph = buildGraph(input)
        val requiredNodes = listOf("dac", "fft")
        return countPaths("svr", graph, emptySet(), 0, requiredNodes, mutableMapOf())
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
