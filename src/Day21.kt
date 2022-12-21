abstract class MonkeyFun {
    abstract val name: String

    abstract fun getValue(): Long

    open fun solveValue(ans: Long) = if (name == "humn") ans else getValue()

    fun isHuman(): Boolean {
        if (name == "humn") return true
        if (this is NumberMonkey) return false

        val nm = this as MathMonkey
        return nm.leftMonkey!!.isHuman() || nm.rightMonkey!!.isHuman()
    }
}

data class NumberMonkey(override val name: String, var num: Long): MonkeyFun() {
    override fun getValue() = num
}

data class MathMonkey(override val name: String, val left: String, val right: String, val op: String): MonkeyFun() {
    var leftMonkey: MonkeyFun? = null
    var rightMonkey: MonkeyFun? = null

    private val leftNum get() = leftMonkey?.getValue() ?: throw IllegalStateException("no $left")
    private val rightNum get() = rightMonkey?.getValue() ?: throw IllegalStateException("no $right")
    override fun getValue(): Long {

        if (name == "human") println("!!!!! Asking human !!!!!       <--")

        return when (op) {
            "/" -> leftNum / rightNum
            "*" -> leftNum * rightNum
            "-" -> leftNum - rightNum
            else -> leftNum + rightNum
        }
    }

    override fun solveValue(ans: Long): Long {
        val human = if (leftMonkey!!.isHuman()) leftMonkey!! else rightMonkey!!
        val monkey = if (leftMonkey == human) rightMonkey!! else leftMonkey!!

        val left = (human == leftMonkey)
        val num = monkey.getValue()

        return when (op) {
            "/" -> human.solveValue(if (left) num * ans else num / ans)
            "*" -> human.solveValue(ans / num)
            "-" -> human.solveValue(if (left) num + ans else num - ans)
            else -> human.solveValue(ans - num)
        }
    }
}

fun main() {
    val day = 21


    val numMonkey = """(\w+): (\d+)""".toRegex()
    val mathMonkey = """(\w+): (\w+) (\S) (\w+)""".toRegex()

    fun parseMonkey(line: String): MonkeyFun =
        numMonkey.matchEntire(line)?.destructured?.let { (name, num) ->
            NumberMonkey(name, num.toLong())
        } ?: mathMonkey.matchEntire(line)?.destructured?.let { (name, lm, op, rm) ->

            MathMonkey(name, lm, rm, op)
        } ?: throw IllegalArgumentException("No match for $line")


    fun parseInput(input: List<String>) =
        input.map { parseMonkey(it) }
            .associateBy { it.name }

    fun mapMonkeys(monkeys: Map<String, MonkeyFun>) {
        monkeys.values
            .onEach {
                if (it is MathMonkey) {
                    it.leftMonkey = monkeys[it.left]!!
                    it.rightMonkey = monkeys[it.right]!!
                }
            }
    }



    fun part1(input: List<String>): Long {
        val data = parseInput(input)
        mapMonkeys(data)
        return data["root"]?.getValue()!!
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input)
        mapMonkeys(data)

        val root = data["root"]!! as MathMonkey

        val human = if (root.leftMonkey!!.isHuman()) root.leftMonkey!! else root.rightMonkey!!
        val monkey = if (root.leftMonkey == human) root.rightMonkey!! else root.leftMonkey!!
        val ans = monkey.getValue()

        return human.solveValue(ans)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(152) { part1(testInput) }
    checkTest(301) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
