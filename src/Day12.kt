
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

    fun find(map: List<List<Char>>, ch: Char) =
        map.flatMapIndexed { row, line ->
            line.withIndex()
                .filter { it.value == ch }
                .map { (col, _) -> Pos(row, col) }
        }.first()

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

    fun adjacents(map: List<List<Char>>, pos: Pos): List<Pos> {
        val maxCol = map[0].size - 1
        val maxRow = map.size - 1
        val up = Pos(pos.row - 1, pos.col)
        val down = Pos(pos.row + 1, pos.col)
        val left = Pos(pos.row, pos.col - 1)
        val right = Pos(pos.row, pos.col + 1)
        val valid = map.height(pos) + 1

        val isValid = { p: Pos -> p.row in (0.. maxRow) && p.col in (0..maxCol) && map.height(p) <= valid }

        return listOf(up, down, left, right)
            .filter { isValid(it) }
    }
    
    fun bfs(map: List<List<Char>>, start: Pos): List<Pos> {
        val parents = mutableMapOf<Pos, Pos>()
        val explored = mutableListOf(start)
        val search = mutableListOf(start)

        while (search.isNotEmpty()) {
            val node = search.removeFirst()
            if (map.get(node) == 'E') {
                val path = mutableListOf(node)
                var n = node
                while (n in parents) {
                    n = parents[n]!!
                    path.add(n)
                }
                return path
            }
            adjacents(map, node).forEach {
                if (it !in explored) {
                    explored.add(it)
                    parents[it] = node
                    search.add(it)
                }
            }
        }

        throw IllegalStateException()
    }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        val start = find(map, 'S')
        val path = bfs(map, start)
        return path.size - 1
    }

    fun part2(input: List<String>): Int {
        val map = invertMap(parseInput(input))
        val start = find(map, 'S')
        val path = bfs(map, start)
        return path.size - 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    checkTest(31) { part1(testInput) }
    checkTest(29) { part2(testInput) }

    val input = readInput("Day12")
    solution { part1(input) }
    solution { part2(input) }
}
