import util.Position3D
import util.UnionFind
import util.distanceTo
import util.readLines

fun parseJunctionBox(line: String): JunctionBox {
    val coords = line.split(",").map { it.toInt() }
    return JunctionBox(Position3D(coords[0], coords[1], coords[2]))
}

@Suppress("CyclomaticComplexMethod")
fun main() {

    fun mergeCircuits(
        box1: JunctionBox,
        box2: JunctionBox,
        circuits: MutableList<CircuitBoard>
    ) {
        val circuit1 = circuits.find { it.junctionBoxes.contains(box1) }
        val circuit2 = circuits.find { it.junctionBoxes.contains(box2) }

        when {
            circuit1 == null && circuit2 == null -> circuits.add(CircuitBoard(mutableSetOf(box1, box2)))
            circuit1 != null && circuit2 == null -> circuit1.junctionBoxes.add(box2)
            circuit1 == null && circuit2 != null -> circuit2.junctionBoxes.add(box1)
            circuit1 != null && circuit2 != null && circuit1 != circuit2 -> {
                circuit1.junctionBoxes.addAll(circuit2.junctionBoxes)
                circuits.remove(circuit2)
            }
        }
    }

    fun part1(input: List<String>, connections: Int): Int {
        val list = input.map { parseJunctionBox(it) }
        val circuits = mutableListOf<CircuitBoard>()

        val pairsByDistance = list.indices.flatMap { i ->
            (i + 1 until list.size).map { j ->
                val box = list[i]
                val otherBox = list[j]
                Pair(box, otherBox) to box.position3D.distanceTo(otherBox.position3D)
            }
        }

        val sortedPairs = pairsByDistance.sortedBy { it.second }.map { it.first }

        sortedPairs.take(connections).forEach { (box1, box2) ->
            mergeCircuits(box1, box2, circuits)
        }

        val circuitsOrdered = circuits.sortedByDescending { it.junctionBoxes.size }
        return circuitsOrdered[0].junctionBoxes.size *
                circuitsOrdered[1].junctionBoxes.size *
                circuitsOrdered[2].junctionBoxes.size
    }

    fun part2(input: List<String>): Int {
        val list = input.map { parseJunctionBox(it) }
        val uf = UnionFind(list.size)

        val pairsByDistance = list.indices.flatMap { i ->
            (i + 1 until list.size).map { j ->
                Triple(i, j, list[i].position3D.distanceTo(list[j].position3D))
            }
        }
        val sortedPairs = pairsByDistance.sortedBy { it.third }

        sortedPairs.forEach { (i, j, _) ->
            if (uf.union(i, j) && uf.countComponents(list.size) == 1) {
                return list[i].position3D.x * list[j].position3D.x
            }
        }

        error("Never reached single component")
    }

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
