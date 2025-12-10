import util.Position3D
import util.UnionFind
import util.distanceTo
import util.readLines

fun parseJunctionBox(line: String): JunctionBox {
    val coords = line.split(",").map { it.toInt() }
    return JunctionBox(Position3D(coords[0], coords[1], coords[2]))
}

fun part1(input: List<String>, connections: Int): Int {
    val list = input.map { parseJunctionBox(it) }
    val circuits = mutableListOf<CircuitBoard>()
    val pairsByDistance = mutableListOf<Pair<Pair<JunctionBox, JunctionBox>, Double>>()
    for(i in list.indices) {
        for(j in i + 1 until list.size) {
            val box = list[i]
            val otherBox = list[j]
            pairsByDistance.add(Pair(box, otherBox) to box.position3D.distanceTo(otherBox.position3D))
        }
    }
    val sortedPairs = pairsByDistance.sortedBy { it.second }.map { it.first }
    for (i in 0 until connections) {
        val (box1, box2) = sortedPairs[i]
        val circuit1 = circuits.find { it.junctionBoxes.contains(box1) }
        val circuit2 = circuits.find { it.junctionBoxes.contains(box2) }
        if (circuit1 == null && circuit2 == null) {
            val newCircuit = CircuitBoard(mutableSetOf(box1, box2))
            circuits.add(newCircuit)
        } else if (circuit1 != null && circuit2 == null) {
            circuit1.junctionBoxes.add(box2)
        } else if (circuit1 == null && circuit2 != null) {
            circuit2.junctionBoxes.add(box1)
        } else if (circuit1 != null && circuit2 != null && circuit1 != circuit2) {
            circuit1.junctionBoxes.addAll(circuit2.junctionBoxes)
            circuits.remove(circuit2)
        }
    }
    val circuitsOrdered = circuits.sortedByDescending { it.junctionBoxes.size }
    return circuitsOrdered[0].junctionBoxes.size *
            circuitsOrdered[1].junctionBoxes.size *
            circuitsOrdered[2].junctionBoxes.size
}

fun part2(input: List<String>): Int {
    val list = input.map { parseJunctionBox(it) }
    val uf = UnionFind(list.size)

    val pairsByDistance = mutableListOf<Triple<Int, Int, Double>>()
    for(i in list.indices) {
        for(j in i + 1 until list.size) {
            pairsByDistance.add(Triple(i, j, list[i].position3D.distanceTo(list[j].position3D)))
        }
    }
    val sortedPairs = pairsByDistance.sortedBy { it.third }

    for ((i, j, _) in sortedPairs) {
        if (uf.union(i, j)) {
            if (uf.countComponents(list.size) == 1) {
                return list[i].position3D.x * list[j].position3D.x
            }
        }
    }

    error("Never reached single component")
}

fun main() {
    val testInput = readLines(8, isTest = true)
    check(part1(testInput, 10) == 40)
    check(part2(testInput) == 25272)

    val input = readLines(8)
    println("Part 1: " + part1(input, 1000))
    check(part1(input, 1000) == 135169)
    println("Part 2: " + part2(input))
    check(part2(input) == 302133440)
}

data class JunctionBox(val position3D: Position3D)
data class CircuitBoard(val junctionBoxes: MutableSet<JunctionBox>)
