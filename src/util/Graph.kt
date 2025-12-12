package util

import kotlin.collections.isNotEmpty

class Graph {
    private val adjacencyList = mutableMapOf<String, MutableList<String>>()

    fun addEdge(from: String, to: String) {
        adjacencyList.computeIfAbsent(from) { mutableListOf() }.add(to)
    }

    fun getNeighbors(node: String): List<String> {
        return adjacencyList[node] ?: emptyList()
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

class UnionFind(size: Int) {
    private val parent = IntArray(size) { it }

    fun find(x: Int): Int {
        if (parent[x] != x) {
            parent[x] = find(parent[x])
        }
        return parent[x]
    }

    fun union(x: Int, y: Int): Boolean {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) {
            parent[rootX] = rootY
            return true
        }
        return false
    }

    fun countComponents(size: Int): Int = (0 until size).map { find(it) }.toSet().size
}
