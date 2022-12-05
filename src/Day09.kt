import kotlin.math.absoluteValue

//    fun dumpKnots(knots: List<Pos>, locs: Set<Pos>): String {
//        val dump = StringBuilder()
//        val maxX = 20//knots.maxOf { it.x }
//        val maxY = 20//knots.maxOf { it.y }
//        (maxY downTo 0).forEach { y ->
//            (0..maxX).map { Pos(it, y) }
//                .forEach {
//                    knots.withIndex()
//                        .firstOrNull { (i, v) -> v == it }
//                        ?.let { (i, _) -> dump.append(i) }
//                        ?: dump.append(if (it in locs) "#" else ".")
//                }
//            dump.append("\n")
//        }
//        return dump.toString()
//    }
fun main() {

    data class Cmd(val dir: String, val len: Int)

    data class Pos(val x: Int, val y: Int) {
        fun move(cmd: Cmd) = when (cmd.dir) {
            "U" -> Pos(x, y + cmd.len)
            "D" -> Pos(x, y - cmd.len)
            "L" -> Pos(x - cmd.len, y)
            "R" -> Pos(x + cmd.len, y)
            else -> throw IllegalArgumentException("Unrecognized cmd: $cmd")
        }

        fun tail(head: Pos): Pos {
            if (distance(head) <= 1) return this
            val dx = (head.x - x).coerceIn(-1, 1)
            val dy = (head.y - y).coerceIn(-1, 1)
            return Pos(x + dx, y + dy)
        }

        fun distance(pos: Pos) =
            maxOf((x - pos.x).absoluteValue, (y - pos.y).absoluteValue)

    }

    fun parseInput(input: List<String>) =
        input.map { it.split(" ") }
            .map { (d, l) -> Cmd(d, l.toInt()) }

    fun follow(cmds: List<Cmd>, size:Int): Int {
        val knots = MutableList(size) { _ -> Pos(0, 0) }
        val locs = mutableSetOf(Pos(0, 0))

        cmds.flatMap { c -> (1..c.len).map { Cmd(c.dir, 1) } }
            .forEach { cmd -> knots.indices
                    .forEach { i -> when (i) {
                            0 -> knots[0] = knots[0].move(cmd)
                            else -> {
                                knots[i] = knots[i].tail(knots[i - 1])
                                if (i + 1 == size) locs.add(knots[i])
                            }
                        }
                    }
                }

        return locs.size
    }

    fun part1(input: List<String>): Int {
        return follow(parseInput(input), 2)
    }

    fun part2(input: List<String>): Int {
        return follow(parseInput(input), 10)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    checkThat(part1(testInput), 13)
    checkThat(part2(testInput), 1)

    val testInput2 = readInput("Day09_test2")
    checkThat(part2(testInput2), 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
