
fun main() {

    data class Pos(val row: Int, val col: Int)

    fun List<List<Char>>.get(pos: Pos) = this[pos.row][pos.col]
    fun List<List<Char>>.height(pos: Pos) =
        this[pos.row][pos.col]
            .let { when (it) {
                'S' -> 'a'
                'E' -> 'z'
                else -> it
            } }

    var best: List<Pos>? = null

    fun move(map: List<List<Char>>, moves: List<Pos>) {
        if (best != null && best!!.size < moves.size) return

        val maxCol = map[0].size - 1
        val maxRow = map.size - 1
        val pos = moves.last()
        val height = map.height(pos)

        if (map.get(pos) == 'E') {
            if (best == null || moves.size < best!!.size) {
                best = moves
                return
            }
        }

        val valid = height + 1

        val up = Pos(pos.row - 1, pos.col)
        val down = Pos(pos.row + 1, pos.col)
        val left = Pos(pos.row, pos.col - 1)
        val right = Pos(pos.row, pos.col + 1)


        if (pos.row > 0 && map.height(up) <= valid && up !in moves) {
            move(map, moves + up)
        }
        if (pos.row < maxRow && map.height(down) <= valid && down !in moves) {
            move(map, moves + down)
        }
        if (pos.col > 0 && map.height(left) <= valid && left !in moves) {
            move(map, moves + left)
        }
        if (pos.col < maxCol && map.height(right) <= valid && right !in moves) {
            move(map, moves + right)
        }
    }

    fun start(map: List<List<Char>>): Pos {
        map.indices
            .forEach { y ->
                map[y].indices
                    .forEach { x ->
                        if (map[y][x] == 'S') return Pos(y, x)
                    }
            }
        throw IllegalStateException()
    }

    fun parseInput(input: List<String>) =
        input.map { it.toList() }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        val start = start(map)
        move(map, listOf(start))
        println("Best - $best")
        return best!!.size - 1
    }

    fun part2(input: List<String>) = 1

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    checkThat(part1(testInput), 31)
    checkThat(part2(testInput), 1)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
