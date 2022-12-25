import java.math.BigInteger

fun main() {

    fun parseNum(line: String): Long {
        var num = 0L
        var multiplier = 1L

        for (ch in line.reversed()) {
            num += when (ch) {
                '2' -> 2L * multiplier
                '1' -> 1L * multiplier
                '0' -> 0L
                '-' -> -1L * multiplier
                '=' -> -2L * multiplier
                else -> throw IllegalArgumentException("Unrecognized character: $ch")
            }
            multiplier *= 5
        }

        return num
    }

    fun snafu(num: Long): String {
        if (num == 0L) return "0"

        var rem = num
        var snafu = StringBuilder()

        while (rem > 0L) {
            val ch = when (rem % 5) {
                0L -> { rem /= 5; '0' }
                1L -> { rem /= 5; '1' }
                2L -> { rem /= 5; '2' }
                3L -> { rem = rem / 5 + 1; '=' }
                else -> { rem = rem / 5 + 1; '-' }
            }
            snafu.insert(0, ch)
        }

        return snafu.toString()
    }

    fun parseInput(input: List<String>) =
        input.asSequence()
            .onEach { print("$it -> ") }
            .map { parseNum(it) }
            .onEach { println(it) }

    fun part1(input: List<String>): String {
        val data = parseInput(input)
        val total = data.sum()
        println("TOTAL: $total")
        return snafu(total)
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        return 1
    }

    val day = 25

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest("2=-1=0") { part1(testInput) }
    checkTest(1) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
