package util

import kotlin.collections.isNotEmpty

class Graph {
    private val adjacencyList = mutableMapOf<String, MutableList<String>>()

    fun addEdge(from: String, to: String) {
        adjacencyList.computeIfAbsent(from) { mutableListOf() }.add(to)
    }

    fun shortestPath(start: String, end: String): Int? {
        val queue = ArrayDeque<Pair<String, Int>>()
        val visited = mutableSetOf<String>()
        queue.add(Pair(start, 0))
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (current, distance) = queue.removeFirst()
            if (current == end) {
                return distance
            }
            for (neighbor in adjacencyList[current] ?: emptyList()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(Pair(neighbor, distance + 1))
                }
            }
        }
        return null
    }
}
