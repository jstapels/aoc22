import java.util.*

fun main() {
    val day = 14

    data class Pos(val x: Int, val y: Int) {
        fun to(end: Pos): List<Pos> {
            val minX = minOf(x, end.x)
            val minY = minOf(y, end.y)
            val maxX = maxOf(x, end.x)
            val maxY = maxOf(y, end.y)

            return (minX..maxX).flatMap { nx ->
                (minY..maxY).map { ny -> Pos(nx, ny) }
            }
        }

        val left get() = Pos(this.x - 1, this.y + 1)
        val right get() = Pos(this.x + 1, this.y + 1)
    }

    fun String.toPos() =
        this.split(",")
            .let { Pos(it[0].toInt(), it[1].toInt()) }

    fun Iterable<Pos>.fallTo(p: Pos) =
        this.filter { it.x == p.x && it.y >= p.y}
            .minByOrNull { it.y }
            ?.let { Pos(it.x, it.y - 1) }


    fun List<Pos>.pourSand(start: Pos = Pos(500, 0)): Int {
        val fill = this.toMutableSet()
        var sand = 0

        var cur = fill.fallTo(start)
        while (cur != null) {
            cur = when {
                ! fill.contains(cur.left) -> fill.fallTo(cur.left)
                ! fill.contains(cur.right) -> fill.fallTo(cur.right)
                else -> {
                    fill.add(cur)
                    sand++
                    fill.fallTo(start)
                }
            }
        }

        return sand
    }

    fun parseInput(input: List<String>) =
        input.flatMap { line ->
            line.split(" -> ")
                .map { it.toPos() }
                .windowed(2)
                .flatMap { it[0].to(it[1]) }
                .distinct()
        }

    fun part1(input: List<String>) =
        parseInput(input)
            .pourSand()


    fun MutableMap<Int, NavigableSet<Int>>.add(p: Pos) =
        this.getOrPut(p.x) { TreeSet() }.add(p.y)

    fun MutableMap<Int, NavigableSet<Int>>.fallToFloor(p: Pos, height: Int) =
        this[p.x]?.ceiling(p.y)
            ?.let { Pos(p.x, it - 1) }
            ?: Pos(p.x, height - 1)

    fun MutableMap<Int, NavigableSet<Int>>.contains(p: Pos) =
        this[p.x]?.contains(p.y) ?: false


    fun List<Pos>.pourMoreSand(start: Pos = Pos(500, -1)): Int {
        val fill = mutableMapOf<Int, NavigableSet<Int>>()
        this.map { fill.getOrPut(it.x) { TreeSet() }.add(it.y) }
        var sand = 0
        val height = this.maxOf { it.y } + 2

        var cur = fill.fallToFloor(start, height)
        while (cur != start) {
            cur = when {
                ! fill.contains(cur.left) && cur.y < (height - 1) -> fill.fallToFloor(cur.left, height)
                ! fill.contains(cur.right) && cur.y < (height - 1) -> fill.fallToFloor(cur.right, height)
                else -> {
                    fill.add(cur)
                    sand++
                    fill.fallToFloor(start, height)
                }
            }
        }

        return sand
    }


    fun part2(input: List<String>) =
        parseInput(input)
            .pourMoreSand()


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(24) { part1(testInput) }
    checkTest(93) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
