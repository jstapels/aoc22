import java.util.concurrent.atomic.AtomicInteger

private data class Valve(val id: String, val rate: Int, val tunnels: List<String>)

private val routes = mutableMapOf<Set<String>, Int>()
private fun Map<String, Valve>.calcRouteCost(start: String, end: String): Int {
    val nodes = setOf(start, end)
    if (nodes in routes) return routes[nodes]!!

    val parents = mutableMapOf<String, String>()
    val explored = mutableSetOf(start)
    val search = mutableListOf(start)

    while (search.isNotEmpty()) {
        val node = search.removeFirst()

        if (node == end) {
            var distance = 0
            var n = node
            while (n != start) {
                distance++
                n = parents[n]!!
            }

            routes[nodes] = distance
            return distance
        }

        getValue(node).tunnels.forEach {
            if (it !in explored) {
                explored.add(it)
                search.add(it)
                parents[it] = node
            }
        }
    }

    throw IllegalStateException()
}

private data class Worker(val name: String, val minutes: Int = 30, val valve: String = "AA")

fun main() {
    val day = 16

    fun parseValve(line: String): Valve {
        val re = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.*)""".toRegex()
        val match = re.matchEntire(line) ?: throw IllegalStateException("No match for $line")

        val id = match.groups[1]!!.value
        val rate = match.groups[2]!!.value.toInt()
        val tunnels = match.groups[3]!!.value.split(", ")
        return Valve(id, rate, tunnels)
    }

    fun parseInput(input: List<String>) =
        input.map { parseValve(it) }
            .associateBy { it.id }

    fun Map<String, Valve>.potential(workers: List<Worker>, remaining: Set<String>): Int {
        var potential = workers.sumOf { (it.minutes - 1) * this[it.valve]!!.rate }
        remaining.forEach { v ->
            val pressure = workers.maxOf { (it.minutes - 1 - calcRouteCost(it.valve, v)) * this[v]!!.rate }
            if (pressure > 0) potential += pressure
        }

        return potential
    }

    fun Map<String, Valve>.usefulValves() =
        values.filter { it.rate >0 }
            .map { it.id }
            .toSet()

    fun Map<String, Valve>.calcPressure(workers: List<Worker>, remaining: Set<String> = usefulValves(), pressureSoFar: Int = 0, best: AtomicInteger = AtomicInteger(0)): Int {
        if (workers.isEmpty()) return pressureSoFar

        // Pick next worker by minutes left
        val worker = workers.maxBy { it.minutes }
        val otherWorkers = workers - worker

        val name = worker.name
        val valve = worker.valve
        val minutes = worker.minutes

        // No time left
        if (minutes <= 1) return pressureSoFar

        val myPressure = this[valve]!!.rate * (minutes - 1)
        val pressure = myPressure + pressureSoFar

        if (remaining.isEmpty()) {
            return calcPressure(otherWorkers, remaining, pressure, best)
        }

        val possiblePressure = pressureSoFar + potential(workers, remaining)
        if (possiblePressure < best.get()) {
            return pressure
        }

        val openTime = if (myPressure > 0) 1 else 0
        val bestPressure = remaining.sortedByDescending { (minutes - 2 - calcRouteCost(valve, it)) * this[it]!!.rate }
            .maxOf {
            val timeLeft = minutes - openTime - calcRouteCost(valve, it)
            val newWorker = Worker(name, timeLeft, it)
            val p = calcPressure(otherWorkers + newWorker, remaining - it, pressure, best)
            p
        }

        if (bestPressure > best.get()) {
            best.set(bestPressure)
//            println("New best pressure $bestPressure")
        }

        return bestPressure
    }

    fun part1(input: List<String>): Int {
        val valves = parseInput(input)

        val me = Worker("Me")
        val out = valves.calcPressure(listOf(me))

        return out
    }

    fun part2(input: List<String>): Int {
        val valves = parseInput(input)

        val me = Worker("Me", 26)
        val elephant = Worker("Elephant", 26)
        val out = valves.calcPressure(listOf(me, elephant))

        return out
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(1651) { part1(testInput) }
    checkTest(1707) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
