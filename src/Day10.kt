

sealed class Instruction(private val cycles: Int) {
    abstract fun apply(reg: Int): Int

    fun expanded(): List<Instruction> =
        ((1 until cycles).map { NoOp() }) + this
}

class NoOp: Instruction(1) {
    override fun apply(reg: Int) = reg
}

class AddX(private val arg: Int): Instruction(2) {
    override fun apply(reg: Int) = reg + arg
}

fun main() {

    fun execute(cmds: List<Instruction>, reg: Int = 1) =
        cmds.scan(reg) { r, c -> c.apply(r) }
            .toList()

    fun parseCmd(line: String) = when {
        line.startsWith("noop") -> NoOp()
        line.startsWith("addx") -> AddX(line.split(" ")[1].toInt())
        else -> throw IllegalArgumentException("unexpected: $line")
    }.expanded()

    fun parseInput(input: List<String>) =
        input.flatMap { parseCmd(it) }


    fun part1(input: List<String>): Int {
        return execute(parseInput(input))
            .withIndex()
            .fold(0) { sum, (i, v) -> if ((i + 21) % 40 == 0) sum + ((i + 1) * v) else sum }
    }

    fun printLcd(sprite: List<Int>) {
        sprite.withIndex()
            .forEach { (cycle, s) ->
                val pixel = cycle % 40
                print(if ((pixel - s) in (-1..1)) "#" else ".")
                if (pixel == 39) println()
            }
        println()
    }

    fun part2(input: List<String>): Int {
        val out = execute(parseInput(input))
        printLcd(out)
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    checkThat(part1(testInput), 13140)
    checkThat(part2(testInput), 1)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
