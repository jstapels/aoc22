
fun main() {

    data class Pos(val row: Int, val col: Int)

    fun getCol(nums: List<List<Int>>, col: Int) =
        nums.indices.map { nums[it][col] }

    fun visible(trees: List<List<Int>>, pos: Pos) =
        { h:Int -> h < trees[pos.row][pos.col] }.let {
            trees[pos.row].take(pos.col).all(it) ||
                    trees[pos.row].takeLast(trees[pos.row].size - pos.col - 1).all(it) ||
                    getCol(trees, pos.col).take(pos.row).all(it) ||
                    getCol(trees, pos.col).takeLast(trees[pos.row].size - pos.row - 1).all(it)
        }

    fun parseInput(input: List<String>) =
        input.map { it.toCharArray().map { c -> c.digitToInt() } }

    fun part1(input: List<String>): Int {
        val trees = parseInput(input)
        return trees.indices
            .flatMap { row -> trees[row].indices.map { col -> Pos(row, col) } }
            .count { visible(trees, it) }
    }

    fun treeCount(trees: List<Int>, height: Int) =
        trees.indexOfFirst { it >= height }
            .let { if (it == -1) trees.size else it + 1 }

    fun scenic(trees: List<List<Int>>, pos: Pos): Int {
        val (row, col) = pos
        val height = trees[row][col]
        val maxRow = trees.size - 1
        val maxCol = trees[0].size - 1

        return treeCount(trees[row].take(col).reversed(), height) *
                treeCount(trees[row].takeLast(maxCol - col), height) *
                treeCount(getCol(trees, col).take(row).reversed(), height) *
                treeCount(getCol(trees, col).takeLast(maxRow - row), height)
    }

    fun part2(input: List<String>): Int {
        val trees = parseInput(input)
        return trees.indices
            .flatMap { row -> trees[row].indices.map { col -> Pos(row, col) } }
            .maxOf { scenic(trees, it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    checkThat(part1(testInput), 21)
    checkThat(part2(testInput), 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
