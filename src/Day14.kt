fun main() {
    val day = 14

    data class Coord(val x: Int, val y: Int) {
        fun to(end: Coord): List<Coord> {
            val minX = minOf(x, end.x)
            val minY = minOf(y, end.y)
            val maxX = maxOf(x, end.x)
            val maxY = maxOf(y, end.y)

            return (minX..maxX).flatMap { nx ->
                (minY..maxY).map { ny -> Coord(nx, ny) }
            }
        }

        val left get() = Coord(this.x - 1, this.y + 1)
        val right get() = Coord(this.x + 1, this.y + 1)
    }

    fun String.toPos() =
        this.split(",")
            .let { Coord(it[0].toInt(), it[1].toInt()) }

    fun List<List<Char>>.fallTo(p: Coord, height: Boolean = false): Coord? {
        var y = p.y + 1
        val col = this[p.x];
        return (y until col.size).firstOrNull { col[it] != '.'}
            ?.let { Coord(p.x, it - 1) }
            ?: if (height) Coord(p.x, col.size - 1) else null
    }

    fun List<List<Char>>.notBlocked(p: Coord) =
        p.x < this.size && p.y < this[p.x].size && this[p.x][p.y] == '.'

    fun List<MutableList<Char>>.sand(p: Coord) =
        this[p.x].set(p.y, 'o')

    fun MutableList<MutableList<Char>>.pourSand(height: Boolean = false): Int {
        val start = Coord(500, -1)
        var sand = 0
        var cur = this.fallTo(start, height)
        while (cur != start) {
            if (cur == null) return sand
            cur = when {
                this.notBlocked(cur.left) -> this.fallTo(cur.left, height)
                this.notBlocked(cur.right) -> this.fallTo(cur.right, height)
                else -> {
                    this.sand(cur)
                    sand++
                    this.fallTo(start, height)
                }
            }
        }

        return sand
    }

    fun parseInput(input: List<String>): MutableList<MutableList<Char>> {
        val points = input.flatMap { line ->
            line.split(" -> ")
                .map { it.toPos() }
                .windowed(2)
                .flatMap { it[0].to(it[1]) }
                .distinct()
        }
        val maxY = points.maxOf { it.y }
        val maxX = points.maxOf { it.x } + maxY

        val data: MutableList<MutableList<Char>> = MutableList(maxX + 2) { MutableList(maxY + 2) { '.' } }
        points.forEach { (x, y) -> data[x][y] = '#' }
        return data
    }

    fun part1(input: List<String>) =
        parseInput(input)
            .pourSand()

    fun part2(input: List<String>) =
        parseInput(input)
            .pourSand(true)


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(24) { part1(testInput) }
    checkTest(93) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
