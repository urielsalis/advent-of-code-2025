import util.readLines
import com.microsoft.z3.Context
import com.microsoft.z3.Status

data class Machine(
    val expectedLights: BooleanArray,
    val connections: List<Circuit>,
    val joltageRequirements: IntArray
)

data class Circuit(val connections: List<Int>)

private val machineRegex = Regex("""^\[(.*)] (.*) \{(.*)}$""")

fun parseMachines(input: List<String>): List<Machine> = input.map { line ->
    val (lights, buttons, joltages) = machineRegex.matchEntire(line)?.destructured
        ?: error("Invalid machine line: $line")

    Machine(
        expectedLights = lights.map { it == '#' }.toBooleanArray(),
        connections = buttons.split(" ").map { button ->
            Circuit(button.removeSurrounding("(", ")").split(",").map(String::toInt))
        },
        joltageRequirements = joltages.split(",").map(String::toInt).toIntArray()
    )
}

fun main() {
    fun solveMachine(machine: Machine): Int {
        val numLights = machine.expectedLights.size
        val numButtons = machine.connections.size

        return (0 until (1 shl numButtons)).minOf { mask ->
            val lights = BooleanArray(numLights)

            machine.connections.indices.forEach { buttonIdx ->
                if ((mask and (1 shl buttonIdx)) != 0) {
                    machine.connections[buttonIdx].connections.forEach { lightIdx ->
                        lights[lightIdx] = !lights[lightIdx]
                    }
                }
            }

            if (lights.contentEquals(machine.expectedLights)) {
                mask.countOneBits()
            } else {
                Int.MAX_VALUE
            }
        }
    }

    fun part1(machines: List<Machine>): Long = machines.sumOf { solveMachine(it).toLong() }

    @Suppress("SpreadOperator")
    fun solveMachineJoltage(machine: Machine): Int = Context().use { ctx ->
        val optimize = ctx.mkOptimize()
        val presses = Array(machine.connections.size) { ctx.mkIntConst("press$it") }

        presses.forEach { optimize.Add(ctx.mkGe(it, ctx.mkInt(0))) }

        machine.joltageRequirements.indices.forEach { counterIdx ->
            val terms = machine.connections.indices
                .filter { counterIdx in machine.connections[it].connections }
                .map { presses[it] }
                .toTypedArray()

            val sum = if (terms.isEmpty()) ctx.mkInt(0) else ctx.mkAdd(*terms)
            optimize.Add(ctx.mkEq(sum, ctx.mkInt(machine.joltageRequirements[counterIdx])))
        }

        optimize.MkMinimize(ctx.mkAdd(*presses))

        if (optimize.Check() == Status.SATISFIABLE) {
            presses.sumOf { optimize.model.eval(it, false).toString().toInt() }
        } else {
            -1
        }
    }

    fun part2(machines: List<Machine>): Long = machines.sumOf { solveMachineJoltage(it).toLong() }

    val testMachines = parseMachines(readLines(10, isTest = true))
    check(part1(testMachines) == 7L)
    check(part2(testMachines) == 33L)

    val machines = parseMachines(readLines(10))
    println("Part 1: ${part1(machines)}")
    println("Part 2: ${part2(machines)}")
    check(part1(machines) == 486L)
    check(part2(machines) == 17820L)
}
