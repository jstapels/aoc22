
fun main() {
    val day = 17

    data class Pos(val x: Int, val y: Long)

    infix fun Int.by(that: Int) = Pos(this, that.toLong())

    infix fun Int.by(that: Long) = Pos(this, that)

    data class Rock(val shape: List<Pos>) {
        fun coords(pos: Pos) =
            shape.map { pos.x + it.x by pos.y + it.y }
    }

    val rocks = listOf(
        Rock(listOf(0 by 0, 1 by 0, 2 by 0, 3 by 0)),
        Rock(listOf(1 by 0, 0 by 1, 1 by 1, 2 by 1, 1 by 2)),
        Rock(listOf(0 by 0, 1 by 0, 2 by 0, 2 by 1, 2 by 2)),
        Rock(listOf(0 by 0, 0 by 1, 0 by 2, 0 by 3)),
        Rock(listOf(0 by 0, 1 by 0, 0 by 1, 1 by 1))
    )

    val window = 10000

    fun List<List<Boolean>>.free(rock: Rock, pos: Pos) =
        rock.coords(pos)
            .all { it.y >= 0 && it.x >= 0 && it.x < 7 && ! this[(it.y % window).toInt()][it.x] }

    fun MutableList<MutableList<Boolean>>.apply(rock: Rock, pos: Pos) =
        rock.coords(pos)
            .forEach {
                this[(it.y % window).toInt()][it.x] = true
            }

    fun List<List<Boolean>>.dump(height: Long) {
        ((height - 1) downTo  0).forEach {row ->
            print("|")
            this[row.toInt()].forEach { print(if (it) "#" else ".") }
            println("|")
        }
        println("-".repeat(9))
    }

    fun drop(jets: List<Char>, cycles: Long = 2022, chamber: MutableList<MutableList<Boolean>> = mutableListOf()): Long {
        // Preallocate
        while (chamber.size < window) { chamber.add(MutableList(7) { false }) }

        var jetIdx = 0
        var num = 0L
        var height = 0L
        while(num < cycles) {
//            println("Cycle $num")
//            chamber.dump(height)
//            if (num > 10) return 1

            if (num % 100000 == 0L) { println("About ${num.toDouble() / cycles}% complete") }
            val rock = rocks[(num % rocks.size).toInt()]

            var nextPos = 2 by height + 3
            var pos = nextPos

            repeat(10) { row ->
                chamber[((height + row) % window).toInt()].fill(false)
            }

            while (chamber.free(rock, nextPos)) {
                pos = nextPos
                val jet = jets[jetIdx]
                jetIdx = (jetIdx + 1) % jets.size
                nextPos = when (jet) {
                    '<' -> pos.x - 1 by pos.y
                    else -> pos.x + 1 by pos.y
                }
                if (chamber.free(rock, nextPos)) pos = nextPos

                nextPos = pos.x by pos.y - 1
            }

            chamber.apply(rock, pos)

            height = maxOf(height, rock.coords(pos).maxOf { it.y } + 1)

            num++
        }

        return height
    }

    fun parseInput(input: List<String>) =
        input.first().toList()

    fun part1(input: List<String>): Long {
        val jets = parseInput(input)
        return drop(jets)
    }

    fun part2(input: List<String>): Long {
        val jets = parseInput(input)
        return drop(jets, 1000000000000L)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(3068L) { part1(testInput) }
    checkTest(1514285714288L) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
