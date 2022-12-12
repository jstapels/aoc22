
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

    val best = mutableMapOf<Pos, List<Pos>>()

    fun printMap(map: List<List<Char>>) {
        map.forEach {
            it.forEach { c -> print(c) }
            println()
        }
    }

    fun move(map: List<List<Char>>, moves: List<Pos>) {
        val pos = moves.last()
        val bestForPos = best[pos]
        if (bestForPos != null && bestForPos.size <= moves.size) {
            return
        } else {
            best[pos] = moves
        }

        val maxCol = map[0].size - 1
        val maxRow = map.size - 1

        if (map.get(pos) == 'E') {
            if (bestForPos == null || moves.size < bestForPos.size) {
                best[pos] = moves
                return
            }
        }

        val valid = map.height(pos) + 1

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

    fun findAll(map: List<List<Char>>, ch: Char) =
        map.flatMapIndexed { row, line ->
            line.withIndex()
                .filter { it.value == ch }
                .map { (col, _) -> Pos(row, col) }
        }

    fun parseInput(input: List<String>) =
        input.map { it.toList() }

    fun invertMap(map: List<List<Char>>) =
        map.map { line ->
            line.map { when (it) {
                'E' -> 'S'
                'S', 'a' -> 'E'
                else -> 'a' + ('z' - it)
            }}
        }

    fun part1(input: List<String>): Int {
        best.clear()
        val map = parseInput(input)
        val start = findAll(map, 'S').first()
        val finish = findAll(map, 'E').first()
        move(map, listOf(start))
        return best[finish]!!.size - 1
    }

    fun part2(input: List<String>): Int {
        best.clear()
        val map = invertMap(parseInput(input))
        val start = findAll(map, 'S').first()
        val finish = findAll(map, 'E')
        move(map, listOf(start))
        return finish.filter { it in best }
            .minOf { best[it]!!.size - 1 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    checkThat(part1(testInput), 31)
    checkThat(part2(testInput), 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
