

fun main() {

    fun parseInput(input: List<String>): List<List<Int>> {
        return input.fold(emptyList()) { acc, cal -> when {
                cal.isBlank() -> acc.plusElement(emptyList())
                else -> acc.dropLast(1).plusElement((acc.lastOrNull() ?: emptyList()) + cal.toInt())
            }
        }
    }

    fun part1(input: List<String>): Int {
        return parseInput(input).maxOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        return parseInput(input).map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    checkThat(part1(testInput), 24000)
    checkThat(part2(testInput), 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
