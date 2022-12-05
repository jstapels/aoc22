
fun main() {
    val OP_REGEX = Regex("""move (\d+) from (\d+) to (\d+)""")

    fun parseCrates(input: List<String>) =
        input.takeWhile { it.contains('[') }
            .flatMap { l ->
                l.chunked(4) { it.trim('[', ']', ' ') }
                    .mapIndexed { i, v -> i + 1 to v }
            }
            .filter { it.second.isNotEmpty() }
            .groupBy({it.first}) { it.second }

    data class Operation(val cnt: Int, val src: Int, val dst: Int)

    fun parseOperations(input: List<String>) =
        input.filter { it.startsWith("move")  }
            .map { OP_REGEX.find(it)!!.destructured }
            .map { (cnt, src, dst) -> Operation(cnt.toInt(), src.toInt(), dst.toInt()) }

    fun folder(crates: Map<Int, List<CharSequence>>, ops: Operation, reversed: Boolean = true): Map<Int, List<CharSequence>> {
        val reverser = if (reversed) { l: List<CharSequence> -> l.reversed() } else { l -> l }
        val (cnt, src, dst) = ops
        return crates + (dst to reverser(crates[src]!!.take(cnt)) + crates[dst]!!) + (src to crates[src]!!.drop(cnt))
    }

    fun part1(input: List<String>): String {
        val crates = parseCrates(input)
        return parseOperations(input)
            .fold(crates) { c, op -> folder(c, op) }
            .toSortedMap()
            .values
            .joinToString("") { it.first() }
    }

    fun part2(input: List<String>): String {
        val crates = parseCrates(input)
        return parseOperations(input)
            .fold(crates) { c, op -> folder(c, op, false) }
            .toSortedMap()
            .values
            .joinToString("") { it.first() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    checkThat(part1(testInput), "CMZ")
    checkThat(part2(testInput), "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
