
fun main() {

    val dirUp = Pos(0, -1)
    val dirRight = Pos(1, 0)
    val dirDown = Pos(0, 1)
    val dirLeft = Pos(-1, 0)
    val dirNone = Pos(0, 0)
    val allDirs = listOf(dirUp, dirRight, dirDown, dirLeft, dirNone)

    abstract class Element(val pos: Pos) {
        override fun toString(): String {
            return "${this::class.simpleName}(pos=$pos)"
        }
    }

    class Wall(pos: Pos): Element(pos)

    class Blizzard(val dir: Pos, pos: Pos): Element(pos) {
    }

    data class Valley(val elements: List<Element>, val maxX: Int, val maxY: Int) {
        val sizeX get() = maxX + 1
        val sizeY get() = maxY + 1

        constructor(elements: List<Element>): this(elements, elements.maxOf { it.pos.x }, elements.maxOf { it.pos.y })

        val map = elements.associateBy { it.pos }

        fun validDirs(pos: Pos) =
            allDirs.map { pos + it }
                .filter {
                    it.x in 0..maxX
                            && it.y in 0..maxY
                            && it !in map
                }

        fun tick() = Valley(elements.map { tickElement(it) }, maxX, maxY)

        fun tickElement(e: Element): Element {
            return when (e) {
                is Blizzard -> {
                    val newPos = e.pos + e.dir
                    val wrapX = (newPos.x - 1).mod(maxX - 1) + 1
                    val wrapY = (newPos.y - 1).mod(maxY - 1) + 1
                    Blizzard(e.dir, Pos(wrapX, wrapY))
                }
                else -> e
            }
        }

        fun dump() {
            (0..maxY).forEach { y ->
                (0..maxX).forEach { x ->
                    val ch = when(val elem = map[x by y]) {
                        is Wall -> '#'
                        is Blizzard -> when (elem.dir) {
                            dirUp -> '^'
                            dirRight -> '>'
                            dirDown -> 'v'
                            dirLeft -> '<'
                            else -> '?'
                        }
                        else -> '.'
                    }
                    print(ch)
                }
                println()
            }
        }
    }

    fun parseValley(c: Char, pos: Pos) = when(c) {
        '#' -> Wall(pos)
        '^' -> Blizzard(dirUp, pos)
        '>' -> Blizzard(dirRight, pos)
        'v' -> Blizzard(dirDown, pos)
        '<' -> Blizzard(dirLeft, pos)
        else -> null
    }

    fun gcd(a: Int, b:Int): Int = if (b == 0) a else gcd(b, a % b)
    fun lcm(a: Int, b: Int) = a / gcd(a, b) * b

    fun findExit(valley: Valley, targets: List<Pos>, start: Pos = Pos(1, 0), startRound: Long = 0L): Long {
        val goals = targets.toMutableList()
        var rounds = startRound

        val repeat = lcm(valley.sizeX - 2, valley.sizeY - 2)

//        println("Valley repeats on $repeat")
//        println("Initial state:")
//        valley.dump()

        val visited = mutableSetOf<Pair<Pos, Long>>()
        var nextValley = valley.tick()
        var nextSearch = ArrayDeque(listOf(start))
        var goal = goals.removeFirst()


        while (nextSearch.isNotEmpty()) {
            val search = nextSearch
            nextSearch = ArrayDeque()
//            println("Minute ${rounds + 1}")
//            nextValley.dump()
            while (search.isNotEmpty()) {
                val pos = search.removeFirst()

                if (pos == goal) {
//                    println("Found exit!")
                    return if (goals.isEmpty()) rounds
                        else findExit(nextValley, goals, goal, rounds + 1)
                }

                nextValley.validDirs(pos)
                    .map { it to (rounds % repeat) }
                    .filter { it !in visited }
                    .forEach {
                        visited.add(it)
                        nextSearch.add(it.first)
                    }

            }
            nextValley = nextValley.tick()
            rounds++
        }

        throw IllegalStateException("No exit found! 😞")
    }


    val day = 24

    fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> parseValley(c, Pos(x, y)) }
        }.let { Valley(it) }

    fun part1(input: List<String>): Long {
        val valley = parseInput(input)
        val start = Pos(1, 0)
        val goal = Pos(valley.maxX - 1, valley.maxY)

        return findExit(valley, listOf(goal), start)
    }

    fun part2(input: List<String>): Long {
        val valley = parseInput(input)
        val start = Pos(1, 0)
        val goal = Pos(valley.maxX - 1, valley.maxY)

        return findExit(valley, listOf(goal, start, goal), start)
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(18L) { part1(testInput) }
    checkTest(54L) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
