
fun main() {
    val day = 16

    data class Valve(
        val id: String,
        val rate: Int,
        val tunnels: List<String>
    )

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

    fun Map<String, Valve>.calcRouteCost(start: String, end: String): Int {
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

    fun Map<String, Valve>.valvesLeft(valve: String, checked: Set<String>) =
        this.values
            .asSequence()
            .filter { it.rate > 0 }
            .map { it.id }
            .filter { it !in checked }
            .toList()


    fun Map<String, Valve>.pressure(valve: String, minutes: Int = 30, visited: Set<String> = emptySet()): Pair<List<String>, Int> {
        // No time left
        if (minutes <= 1) return listOf(valve) to 0

        val checked = visited + valve

        // Hit all nodes
        val valvesLeft = valvesLeft(valve, checked)

        val myPressure = this[valve]!!.rate * (minutes - 1)

        val openTime = if (myPressure > 0) 1 else 0

        val otherPressure = valvesLeft
            .map { pressure(it, minutes - openTime - calcRouteCost(valve, it), checked) }
            .maxByOrNull { it.second } ?: return listOf(valve) to myPressure

        return listOf(valve) + otherPressure.first to myPressure + otherPressure.second
    }

    fun part1(input: List<String>): Int {
        val valves = parseInput(input)

        val me = valves.pressure("AA", 30)
        println("Me - $me")

        return me.second
    }

    data class Worker(val name: String, val valve: String = "AA", val minutes: Int = 26)

    fun Map<String, Valve>.pressure2(workers: List<Worker>, visited: Set<String> = emptySet()): Pair<List<String>, Int> {

        val worker = workers.maxBy { it.minutes }
        val workersLeft = workers - worker

        val name = worker.name
        val valve = worker.valve
        val minutes = worker.minutes

//        println("Checking $name ($minutes) @ $valve [$visited | $workers]")

        // No time left
        if (minutes <= 1) return listOf(valve) to 0

        val checking = visited + workers.map { it.valve }

        // Hit all nodes
        val valvesLeft = valvesLeft(valve, checking)

        val myPressure = this[valve]!!.rate * (minutes - 1)
        val openTime = if (myPressure > 0) 1 else 0

        if (valvesLeft.isEmpty()) {
            val otherWorker = workersLeft.first()
            val otherValve = otherWorker.valve
            if (otherValve !in visited) {
                // One node left, who has more value
                val meDoingIt = minutes - 1 - calcRouteCost(valve, otherValve)
                val otherMins = otherWorker.minutes
                val otherPressure = maxOf(meDoingIt, this[otherValve]!!.rate * (otherMins - 1))
                return listOf(valve, otherValve) to myPressure + otherPressure
            }
        }

        val otherPressure = valvesLeft
            .map {
                val timeLeft = minutes - openTime - calcRouteCost(valve, it)
                val newWorker = Worker(name, it, timeLeft)
                val p = pressure2(workersLeft + newWorker, checking)
                if (minutes == 26) { println("Checking $name($valve) -> $it == $p") }
                p
            }
            .maxByOrNull { it.second } ?: return (listOf(valve) to myPressure)

        return listOf(valve) + otherPressure.first to myPressure + otherPressure.second
    }

    fun part2(input: List<String>): Int {
        val valves = parseInput(input)

        val me = Worker("Me")
        val elephant = Worker("Elephant")
        val out = valves.pressure2(listOf(me, elephant))

        println("Out - $out")

        return out.second
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(1651) { part1(testInput) }
    checkTest(1706) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
