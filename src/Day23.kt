
fun main() {

    data class Pos(val x: Int, val y: Int) {
        operator fun plus(p: Pos) =
            Pos(x + p.x, y + p.y)
    }

    infix fun Int.by(that: Int) = Pos(this, that)

    data class Dir(val dir: String, val offset: Pos, val checks: List<Pos>) {
        fun valid(pos: Pos, used: Set<Pos>) =
            checks.map { pos + it }
                .none { it in used }
    }

    val startDirs = listOf(
        Dir("N", 0 by -1, listOf(-1 by -1, 0 by -1, 1 by -1)),
        Dir("S", 0 by 1, listOf(-1 by 1, 0 by 1, 1 by 1)),
        Dir("W", -1 by 0, listOf(-1 by -1, -1 by 0, -1 by 1)),
        Dir("E", 1 by 0, listOf(1 by -1, 1 by 0, 1 by 1))
    )
    val doNothing = Dir("*", 0 by 0, listOf(-1 by -1, 0 by -1, 1 by -1, -1 by 0, 1 by 0, -1 by 1, 0 by 1, 1 by 1))

    data class Grove(val elves: List<Pos>) {
        val minX = elves.minOf { it.x }
        val minY = elves.minOf { it.y }
        val maxX = elves.maxOf { it.x }
        val maxY = elves.maxOf { it.y }

        fun dump() {
            (minY..maxY).forEach { y ->
                (minX..maxX).forEach { x ->
                    val c = if (x by y in elves) '#' else '.'
                    print(c)
                }
                println()
            }
        }

        fun empties() =
            (minY..maxY).flatMap { y ->
                (minX..maxX).map { x ->
                    if (x by y in elves) 0 else 1
                }
            }.sum()
    }


    fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, d ->
                if (d == '#') Pos(x, y) else null
            }
        }.let { Grove(it) }

    fun doRound(grove: Grove, dirs: List<Dir>): Grove {
        val usedPos = grove.elves.toSet()

        val dirCheck = listOf(doNothing) + dirs

        val newPos = grove.elves
            .map { pos ->
                val dir = dirCheck.firstOrNull { it.valid(pos, usedPos) }
                val offset = dir?.offset ?: (0 by 0)
                pos + offset
            }.toList()

        val dups = newPos.groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        val movedElves = grove.elves
            .mapIndexed { i, p ->
                if (newPos[i] in dups) p
                else newPos[i]
            }

        return Grove(movedElves)
    }

    fun part1(input: List<String>): Int {
        var grove = parseInput(input)
        var dirs = startDirs.toMutableList()

        repeat(10) {
            grove = doRound(grove, dirs)
            dirs.removeFirst().let { dirs.add(it) }
        }

        return grove.empties()
    }

    fun part2(input: List<String>): Int {
        var grove = parseInput(input)
        var dirs = startDirs.toMutableList()

        var count = 0
        var lastGrove = grove
        while(true) {
            count++
            grove = doRound(grove, dirs)
            dirs.removeFirst().let { dirs.add(it) }
            if (grove == lastGrove) return count
            else lastGrove = grove
        }
    }

    val day = 23

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(110) { part1(testInput) }
    checkTest(20) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
