
fun main() {
    fun findUnique(input: String, size: Int) =
        input.windowed(size)
            .withIndex()
            .first { (_, seq) -> seq.toSet().size == size }
            .index + size


    fun part1(input: String) =
        findUnique(input, 4)

    fun part2(input: String) =
        findUnique(input, 14)


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test").first()
    checkThat(part1(testInput), 7)
    checkThat(part2(testInput), 19)

    val input = readInput("Day06").first()
    println(part1(input))
    println(part2(input))
}
