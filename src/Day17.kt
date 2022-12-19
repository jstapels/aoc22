
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

    val window = 100000

    fun List<Int>.free(rock: Rock, pos: Pos) =
        rock.coords(pos)
            .all { it.y >= 0 && it.x >= 0 && it.x < 7 && (this[(it.y % window).toInt()] and (1 shl it.x)) == 0 }

    fun MutableList<Int>.apply(rock: Rock, pos: Pos) =
        rock.coords(pos)
            .forEach {
                this[(it.y % window).toInt()] = this[(it.y % window).toInt()] or (1 shl it.x)
            }

    fun MutableList<Int>.dump(height: Long) {
        ((height - 1) downTo  0)
            .map { this[it.toInt()] }
            .forEach { row ->
                print("|")
                (1..7).forEach {
                    print(if ((row and (1 shl it)) != 0) "#" else ".") }
                println("|")
            }
        println("-".repeat(9))
    }

    fun drop(jets: List<Char>, cycles: Long = 2022, chamber: MutableList<Int> = mutableListOf()): Long {
        // Preallocate
        while (chamber.size < window) { chamber.add(0) }

        var cache = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        var jetIdx = 0L
        var num = 0L
        var height = 0L
        var repeated = false
        var hitCount = 0L
        var cacheReady = false
        while(num < cycles) {


            if (repeated && num % cache.size == 0L) {
                val hm = cache.values.sumOf { it.first }
                val om = cache.values.sumOf { it.second }

                if (num / cache.size > 2) {
                    val mul = ((cycles - num) / cache.size)
                    num += (cache.size * mul)
                    height += (hm * mul)
                    jetIdx += (om * mul)
                }
            }

            val rockIdx = (num % rocks.size).toInt()
            val rock = rocks[rockIdx]

            var nextPos = 2 by height + 3
            var pos = nextPos

            val startJetIdx = jetIdx

            if (repeated) {
                val cv = cache[(startJetIdx % jets.size).toInt() to rockIdx]!!
                height += cv.first
                jetIdx += cv.second
            } else {
                repeat(10) { row ->
                    chamber[((height + row) % window).toInt()] = 0
                }

                while (chamber.free(rock, nextPos)) {
                    pos = nextPos
                    val jet = jets[(jetIdx % jets.size).toInt()]
                    jetIdx += 1
                    nextPos = when (jet) {
                        '<' -> pos.x - 1 by pos.y
                        else -> pos.x + 1 by pos.y
                    }
                    if (chamber.free(rock, nextPos)) pos = nextPos

                    nextPos = pos.x by pos.y - 1
                }

                chamber.apply(rock, pos)

                val newHeight = maxOf(height, rock.coords(pos).maxOf { it.y } + 1)

                val hDiff = (newHeight - height).toInt()
                val pDiff = (jetIdx - startJetIdx).toInt()

                height = newHeight

                val cacheIdx = (startJetIdx % jets.size).toInt() to rockIdx
                if (cacheIdx !in cache) {
                    cache[cacheIdx] = hDiff to pDiff
                    hitCount = 0
                } else {
                    val ocv = cache[cacheIdx]!!
                    if (ocv == (hDiff to pDiff)) {
                        hitCount++
                        if (hitCount.toInt() == cache.size) {
                            if (! cacheReady) {
                                cache.clear()
                                hitCount = 0
                                cacheReady = true
                            } else {
                                repeated = true
                            }
                        }
                    } else {
                        cache.clear()
                        hitCount = 0
                    }
                }
            }

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
