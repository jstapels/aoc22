
fun main() {

    fun listToRange(s: String): List<Int> {
        val (lr, rr) = s.split("-")
        return (lr.toInt()..rr.toInt()).toList()
    }

    fun parse(input: List<String>) =
        input.map { it.split(",") }
            .map { (l, r) -> Pair(listToRange(l), listToRange(r)) }

    fun part1(input: List<String>): Int {
        return parse(input)
            .count { (l, r) -> l.containsAll(r) || r.containsAll(l) }
    }

    fun part2(input: List<String>): Int {
        return parse(input)
            .count { (l, r) -> l.intersect(r).isNotEmpty() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    checkThat(part1(testInput), 2)
    checkThat(part2(testInput), 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
