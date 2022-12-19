
data class Cube(val x: Int, val y:Int, val z:Int) {
    val adjacents get() = listOf(
        Cube(x - 1, y, z),
        Cube(x + 1, y, z),
        Cube(x, y - 1, z),
        Cube(x, y + 1, z),
        Cube(x, y, z - 1),
        Cube(x, y, z + 1),
    )
}

typealias Grid = MutableMap<Int, MutableMap<Int, MutableMap<Int, Cube>>>


fun main() {
    val day = 18

    fun parseInput(input: List<String>) =
        input.map { l -> l.split(",").map { it.toInt() } }
            .map { (x, y, z) -> Cube(x, y, z) }

    fun Grid.addCube(cube: Cube) =
        this.getOrPut(cube.x) { mutableMapOf() }
            .getOrPut(cube.y) { mutableMapOf() }
            .put(cube.z, cube)

    fun Grid.getAdjacents(cube: Cube) =
        cube.adjacents
            .mapNotNull { get(it.x)?.get(it.y)?.get(it.z) }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        val grid = mutableMapOf<Int, MutableMap<Int, MutableMap<Int, Cube>>>()
        data.forEach { grid.addCube(it) }
        return data.sumOf { 6 - grid.getAdjacents(it).size }
    }

    fun Grid.getEmpty(data: MutableList<Cube>): MutableSet<Cube> {
        val minX = data.minOf { it.x } - 1
        val maxX = data.maxOf { it.x } + 1
        val minY = data.minOf { it.y } - 1
        val maxY = data.maxOf { it.y } + 1
        val minZ = data.minOf { it.z } - 1
        val maxZ = data.maxOf { it.z } + 1

        val explored = mutableSetOf<Cube>()
        val search = mutableListOf(Cube(minX, minY, minZ))

        while (search.isNotEmpty()) {
            val node = search.removeFirst()

            explored.add(node)
            node.adjacents
                .filter { it !in explored }
                .filter { it !in data }
                .filter { it !in search }
                .filter { it.x >= minX && it.y >= minY && it.z >= minZ }
                .filter { it.x <= maxX && it.y <= maxY && it.z <= maxZ }
                .forEach {
                    search.add(it)
                }
        }

        return explored
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input).toMutableList()
        val grid: Grid = mutableMapOf()
        data.forEach { grid.addCube(it) }

        val minX = data.minOf { it.x }
        val maxX = data.maxOf { it.x }
        val minY = data.minOf { it.y }
        val maxY = data.maxOf { it.y }
        val minZ = data.minOf { it.z }
        val maxZ = data.maxOf { it.z }

        val empties = grid.getEmpty(data)

        (minX..maxX).forEach { x ->
            (minY..maxY).forEach { y ->
                (minZ..maxZ).forEach { z ->
                    val tc = Cube(x, y, z)
                    if (tc !in data && tc !in empties) {
                        grid.addCube(tc)
                    }
                }
            }
        }

        return data.sumOf { 6 - grid.getAdjacents(it).size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(64) { part1(testInput) }
    checkTest(58) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
