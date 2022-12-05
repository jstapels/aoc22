typealias Dirs = Map<String, Long>

fun main() {

    fun dirSizes(path: String, data: Dirs) =
        data.filterKeys { it.startsWith(path) }
            .values
            .sum()

    fun dirSizes(data: Dirs) =
        data.keys
            .associateWith { dirSizes(it, data) }

    fun makePath(path: List<String>) = "/" + path.joinToString("/")

    fun parseLine(path: List<String>, data: Dirs, input: String) =
        when {
            input.startsWith("$ cd /") -> emptyList<String>() to data
            input.startsWith("$ cd ..") -> path.dropLast(1) to data
            input.startsWith("$ cd ") -> (path + input.split(" ")[2]) to data
            input.startsWith("$ ls") -> path to data
            input.startsWith("dir") -> path to makePath(path).let { data + (it to (data[it] ?: 0)) }
            else -> path to makePath(path).let { data + (it to (data[it] ?: 0) + input.split(" ")[0].toInt()) }
        }

    fun part1(input: List<String>) =
        dirSizes(input.fold(emptyList<String>() to emptyMap<String, Long>()) { (path, data), line -> parseLine(path, data, line) }.second)
            .values
            .filter{ it <= 100000L }
            .sum()

    fun part2(input: List<String>) =
        dirSizes(input.fold(emptyList<String>() to emptyMap<String, Long>()) { (path, data), line -> parseLine(path, data, line) }.second)
            .let { sizes ->
                val spaceNeeded = 30000000L - (70000000L - sizes["/"]!!)
                sizes.filterValues { it >= spaceNeeded }
                    .values
                    .min()
            }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    checkThat(part1(testInput), 95437)
    checkThat(part2(testInput), 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
