
enum class Result {
    LOSE, DRAW, WIN;

    companion object {
        fun of(char: String) = Result.values()[char.single() - 'X']
    }
}

enum class Choice {
    PAPER, ROCK, SCISSORS;

    private val value get() = when (this) {ROCK -> 1; PAPER -> 2; SCISSORS -> 3 }

    fun against(other: Choice) = (4 + other.ordinal - ordinal) % 3 - 1
    fun score(other: Choice) = (against(other) + 1) * 3 + value

    companion object {
        fun of(char: String) = when (char) {
            "A", "X" -> ROCK
            "B", "Y" -> PAPER
            "C", "Z" -> SCISSORS
            else -> throw IllegalArgumentException("$char not valid")
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.map { it.split(' ') }
            .map { Pair(Choice.of(it[0]), Choice.of(it[1])) }
            .sumOf { (them, you) -> you.score(them) }
    }

    fun wanted(them: Choice, result: Result) =
        Choice.values()
            .first { it.against(them) == result.ordinal - 1 }

    fun part2(input: List<String>): Int {
        return input.map { it.split(' ') }
            .map { Pair(Choice.of(it[0]), Result.of(it[1])) }
            .map { (them, result) -> Pair(them, wanted(them, result)) }
            .sumOf { (them, you) -> you.score(them) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    checkThat(part1(testInput), 15)
    checkThat(part2(testInput), 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
