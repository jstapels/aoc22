import java.lang.IllegalArgumentException

data class Monkey(
    val id: Int,
    val startItems: List<Long>,
    val operation: (Long) -> Long,
    val divisor: Int,
    val trueTo: Int,
    val falseTo: Int
) {
    val items = startItems.toMutableList()
    var inspected = 0

    fun throwItems(monkeys: Map<Int, Monkey>, modulus: Int) {
        items.forEach {
            val worry = operation(it) % modulus
            if (worry % divisor == 0L)
                monkeys[trueTo]!!.items.add(worry)
            else
                monkeys[falseTo]!!.items.add(worry)
        }
        inspected += items.size
        items.clear()
    }
}

fun main() {

    fun makeList(s: String) =
        s.replace("  Starting items: ", "")
            .split(", ")
            .map { it.toLong() }

    val opRegex = """  Operation: new = (old|\d+) (\+|\*) (old|\d+)""".toRegex()
    fun makeOp(s: String, worryLevel: Int = 3): (Long) -> Long {
        val match = opRegex.matchEntire(s) ?: throw IllegalArgumentException()
        val (in1, op, in2) = match.destructured
        return { old ->
            val l = if (in1 == "old") old else in1.toLong()
            val r = if (in2 == "old") old else in2.toLong()
            val out = if (op == "+") l + r else l * r
            (out / worryLevel)
        }
    }

    fun divisor(s: String) =
        s.replace("  Test: divisible by ", "")
            .toInt()

    fun trueMonkey(s: String) =
        s.replace("    If true: throw to monkey ", "")
            .toInt()

    fun falseMonkey(s: String) =
        s.replace("    If false: throw to monkey ", "")
            .toInt()

    fun parseInput(input: List<String>, worryLevel: Int = 3) =
        input.chunked(7)
            .mapIndexed { i, it ->
                val items = makeList(it[1])
                val op = makeOp(it[2], worryLevel)
                val div = divisor(it[3])
                val tm = trueMonkey(it[4])
                val fm = falseMonkey(it[5])
                Monkey(i, items, op, div, tm, fm)
            }

    fun doRound(monkeys: Map<Int, Monkey>, modulus: Int) {
        monkeys.keys
            .sorted()
            .forEach { monkeys[it]!!.throwItems(monkeys, modulus) }
    }

    fun dumpMonkeys(monkeys: Map<Int, Monkey>) {
        monkeys.keys
            .sorted()
            .map { monkeys[it]!! }
            .forEach { println("Monkey ${it.id}: inspected items ${it.inspected} | ${it.items}") }
    }

    fun doIt(monkeys: Map<Int, Monkey>, rounds: Int): Long {
        val modulus = monkeys.values
            .map { it.divisor }
            .fold(1) { m, v -> m * v }
        repeat(rounds) { doRound(monkeys, modulus) }
        return monkeys.values
            .map { it.inspected }
            .sortedDescending()
            .take(2)
            .let { it[0].toLong() * it[1].toLong() }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseInput(input)
            .associateBy { it.id }
        return doIt(monkeys, 20)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseInput(input, 1)
            .associateBy { it.id }
        return doIt(monkeys, 10000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    checkThat(part1(testInput), 10605)
    checkThat(part2(testInput), 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
