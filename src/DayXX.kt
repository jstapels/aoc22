
fun main() {
    val day = throw IllegalStateException()

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

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(1) { part1(testInput) }
    checkTest(1) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
