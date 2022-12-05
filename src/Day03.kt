
fun main() {

    fun charValue(c: Char) = when (c) {
        in 'A'..'Z' -> c.code - 'A'.code + 27
        in 'a'..'z' -> c.code - 'a'.code + 1
        else -> throw IllegalArgumentException()
    }

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { it.toCharArray() }
            .map { Pair(it.slice(0 until it.size / 2), it.slice(it.size / 2 until it.size)) }
            .map { (l, r) -> l.intersect(r) }
            .map { charValue(it.single()) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.toCharArray() }
            .chunked(3)
            .map { (a, b, c) -> a.intersect(b.toSet()).intersect(c.toSet()) }
            .map { charValue(it.single()) }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    checkThat(part1(testInput), 157)
    checkThat(part2(testInput), 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
