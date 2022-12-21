
fun main() {
    val day = 20

    fun decrypt(data: List<Pair<Int, Long>>): List<Pair<Int, Long>> {
        val nums = data.toMutableList()

        debug(data)

        nums.indices
            .forEach { idx ->
                val offset = nums.indexOfFirst { it.first == idx }
                val s = nums[offset]
                val (_, d) = s
                if (d != 0L) {
                    nums.removeAt(offset)
                    val move = (offset.toLong() + d).mod(nums.size)
                    nums.add(move, s)
                }
            }

        return nums
    }

    fun doIt(nums: List<Long>, r: Int = 1, m: Long = 1): Long {
        var data = nums.mapIndexed { i, v -> i to v * m }

        repeat (r) {
            data = decrypt(data)
            debug("After ${it + 1} round:\n${data.map { it.second }}")
        }

        val nums = data.map { it.second }
        val zeroIdx = nums.indexOf(0)
        return listOf(1000, 2000, 3000)
            .sumOf { nums[(zeroIdx + it).mod(nums.size)] }
    }

    fun parseInput(input: List<String>) =
        input.map { it.toLong() }

    fun part1(input: List<String>) =
        doIt(parseInput(input))

    fun part2(input: List<String>) =
        doIt(parseInput(input), 10, 811589153)


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(3) { part1(testInput) }
    checkTest(1623178306) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
