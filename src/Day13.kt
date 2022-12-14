
fun main() {
    val day = 13

    fun parseItems(s: MutableList<Char>): List<Any> {
        val list = mutableListOf<Any>()
        var token: String? = null
        while (s.isNotEmpty()) {
            when (val ch = s.removeFirst()) {
                '[' -> list.add(parseItems(s))
                ']' -> {
                    if (token != null) list.add(token.toInt())
                    return list
                }
                ',' -> {
                    if (token != null) list.add(token.toInt())
                    token = null
                }
                else -> token = (token ?: "") + ch
            }
        }
        return list
    }

    fun parseItems(s: String) = parseItems(s.toMutableList()).first() as List<Any>

    fun parseInput(input: List<String>) =
        input.chunked(3)
            .asSequence()
            .map { parseItems(it[0]) to parseItems(it[1]) }

    fun correct(left: List<Any>, right: List<Any>): Boolean? {
        if (left.isEmpty() && right.isEmpty()) return null
        if (left.isEmpty()) return true
        if (right.isEmpty()) return false

        val lhead = left.first()
        val rhead = right.first()

        val result = when {
            lhead is Int && rhead is Int ->
                if (lhead < rhead) true
                else if (lhead > rhead) false
                else null
            lhead is Int -> correct(listOf(lhead), rhead as List<Any>)
            rhead is Int -> correct(lhead as List<Any>, listOf(rhead))
            else -> correct(lhead as List<Any>, rhead as List<Any>)
        }

        if (result != null) return result
        return correct(left.drop(1), right.drop(1))
    }

    fun part1(input: List<String>) =
        parseInput(input).mapIndexed { i, p -> i + 1 to correct(p.first, p.second)!! }
            .filter { it.second }
            .sumOf { it.first }

    fun part2(input: List<String>): Int {
        val dividers: List<List<Any>> = listOf(listOf(listOf(2)), listOf(listOf(6)))
        return input.filter { it.isNotEmpty() }
            .map { parseItems(it) }
            .toList()
            .plus(dividers)
            .sortedWith { a, b -> when {
                correct(a, b) == true -> -1
                correct(a, b) == false -> 1
                else -> 0
            }}
            .foldIndexed(1) { i, a, v ->
                if (v in dividers) (i+1) * a else a
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(13) { part1(testInput) }
    checkTest(140) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
