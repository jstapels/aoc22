import kotlin.math.absoluteValue

fun main() {
    val day = 15

    data class Beacon(val sx: Int, val sy: Int, val bx: Int, val by: Int) {
        val range = (sx - bx).absoluteValue + (sy - by).absoluteValue

        fun isCovered(x: Int, y: Int): Boolean {
            val checkRange = rangeAt(y)
            return (checkRange >= 0) && (x >= (sx - checkRange)) && (x <= (sx + checkRange))
        }

        fun rangeAt(y: Int): Int {
            return range - (sy - y).absoluteValue
        }
    }

    val matcher = """x=(\-?\d+), y=(\-?\d+)""".toRegex()
    fun parseInput(input: List<String>) =
        input.flatMap { matcher.findAll(it) }
            .map { it.groups }
            .map { it[1]!!.value.toInt() to it[2]!!.value.toInt() }
            .chunked(2)
            .map { (sp, bp) -> Beacon(sp.first, sp.second, bp.first, bp.second) }

    fun part1(input: List<String>, row: Int): Int {
        val sensors = parseInput(input)
        val minX = sensors.minOf { it.sx - it.range }
        val maxX = sensors.maxOf { it.sx + it.range }
        val beacons = sensors.filter { it.by == row }
            .map { it.bx }
            .toSet()

        return (minX..maxX).count { x -> beacons.contains(x).not() && sensors.any { it.isCovered(x, row) } }
    }

    fun part2(input: List<String>, max: Int): Long {
        val sensors = parseInput(input)
        var y = 0
        while (y <= max) {
            var x = 0
            while (x <= max) {
                val s = sensors.firstOrNull { it.isCovered(x, y) }
                    ?: return x * 4000000L + y
                x = s.sx + s.rangeAt(y) + 1
            }
            y++
        }

        throw IllegalStateException("Too many possible locations!")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(26) { part1(testInput, 10) }
    checkTest(56000011) { part2(testInput, 20) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input, 2000000) }
    solution { part2(input, 4000000) }
}
