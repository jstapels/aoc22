
fun main() {
    val day = null

    fun parseInput(input: List<String>) =
        input

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return 1
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        return 1
    }

    if (day == null) throw IllegalStateException()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(1) { part1(testInput) }
    checkTest(1) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
