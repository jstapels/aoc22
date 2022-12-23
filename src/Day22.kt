
const val right = 0
const val down = 1
const val left = 2
const val up = 3

private data class Pos(val x: Int, val y: Int)
private data class Row(val offset: Int, val path: List<Char>)

private data class Map(val rows: List<Row>) {
    val colOffsets = mutableMapOf<Int, Int>()
    val colSizes = mutableMapOf<Int, Int>()

    val cubeDims = CCube(this)

    fun colOffset(col: Int) =
        colOffsets.getOrPut(col) { rows.indexOfFirst { r -> col in (r.offset until r.offset + r.path.size) } }

    fun colSize(col: Int) =
        colSizes.getOrPut(col) { rows.indexOfLast { r -> col in (r.offset until r.offset + r.path.size) } - colOffset(col) + 1 }

    fun blocked(pos: Pos) =
        rows[pos.y].let { it.path[pos.x - it.offset] == '#' }

    fun move(pos: Pos, dir: Int, move: Int, isCube: Boolean = false): Pair<Pos, Int> {
        if (move == 0) return pos to dir

        val ro = rows[pos.y].offset
        val rs = rows[pos.y].path.size
        val co = colOffset(pos.x)
        val cs = colSize(pos.x)

        var nextDir = dir
        var nextPos: Pos

        if (! isCube) {
            nextPos = when (dir) {
                right -> Pos((pos.x - ro + 1).mod(rs) + ro, pos.y)
                down -> Pos(pos.x, (pos.y - co + 1).mod(cs) + co)
                left -> Pos((pos.x - ro - 1).mod(rs) + ro, pos.y)
                up -> Pos(pos.x, (pos.y - co - 1).mod(cs) + co)
                else -> throw IllegalStateException("Invalid dir $dir")
            }
        } else {
            val cubePos = when (dir) {
                right -> Pos(pos.x + 1, pos.y)
                down -> Pos(pos.x, pos.y + 1)
                left -> Pos(pos.x - 1, pos.y)
                up -> Pos(pos.x, pos.y - 1)
                else -> throw IllegalStateException("Invalid dir $dir")
            }
            val cubeData = cubeDims.nextPos(pos, cubePos, dir)
            nextPos = cubeData.first
            nextDir = cubeData.second
//            println("   | Pos $pos ($dir x$move) -> $cubePos -> $nextPos ($nextDir)")
        }

        return if (blocked(nextPos)) (pos to dir) //.also { println("Blocked at $nextPos") }
        else move(nextPos, nextDir, move - 1, isCube)
    }

}

private data class Face(val n: String, val x: Int, val y: Int, val s: Int) {
    fun off(ox: Int, oy: Int): Pos {
        val nx = if (ox < 0) x + s + ox else x + ox
        val ny = if (oy < 0) y + s + oy else y + oy
        return Pos(nx, ny)
    }
}

private fun Pos.isIn(f: Face) =
    (x in (f.x until f.x + f.s)) && (y in (f.y until f.y + f.s))

private fun Int.n() = when (this) {
    right -> "R"
    down -> "D"
    left -> "L"
    else -> "U"
}

private data class CCube(val map: Map) {
    val size = map.rows.size / 4

    val topF = Face("top", size, 0, size)
    val rightF = Face("right", size * 2, 0, size)
    val frontF = Face("front", size, size, size)
    val leftF = Face("left", 0, size * 2, size)
    val bottomF = Face("bot", size, size * 2, size)
    val backF = Face("back", 0, size * 3, size)
    val faces = listOf(topF, rightF, frontF, leftF, bottomF, backF)

    fun nextPos(pos: Pos, nextPos: Pos, dir: Int): Pair<Pos, Int> {
        val curFace = faces.single { pos.isIn(it) }
        val nextFace = faces.singleOrNull { nextPos.isIn(it) }
        if (curFace == nextFace) return nextPos to dir

//        println("Cur $pos (${curFace.n}) -> $nextPos (${nextFace?.n})")

        val mRight = nextPos.x > pos.x
        val mDown = nextPos.y > pos.y
        val mLeft = nextPos.x < pos.x
        val mUp = nextPos.y < pos.y

        val off = Pos(pos.x % size, pos.y % size)

        // From top
        return when {
            pos.isIn(topF) -> when {
                mUp -> backF.off(0, off.x) to right
                mLeft -> leftF.off(0, -(off.y+1)) to right
                else -> nextPos to dir
            }
            pos.isIn(rightF) -> when {
                mUp -> backF.off(off.x, -1) to up
                mRight -> bottomF.off(-1, -(off.y+1)) to left
                mDown -> frontF.off(-1, off.x) to left
                else -> nextPos to dir
            }
            pos.isIn(frontF) -> when {
                mLeft -> leftF.off(off.y, 0) to down
                mRight -> rightF.off(off.y, -1) to up
                else -> nextPos to dir
            }
            pos.isIn(bottomF) -> when {
                mRight -> rightF.off(-1, -(off.y+1)) to left
                mDown -> backF.off(-1, off.x) to left
                else -> nextPos to dir
            }
            pos.isIn(leftF) -> when {
                mUp -> frontF.off(0, off.x) to right
                mLeft -> topF.off(0, -(off.y+1)) to right
                else -> nextPos to dir
            }
            pos.isIn(backF) -> when {
                mLeft -> topF.off(off.y, 0) to down
                mRight -> bottomF.off(off.y, -1) to up
                mDown -> rightF.off(off.x, 0) to down
                else -> nextPos to dir
            }

            else -> throw IllegalStateException("Crap!")
        }.also { println("Went from $pos.${dir.n()} ($curFace) -> ${it.first}.${it.second.n()} (${faces.single {f -> it.first.isIn(f)}})")}
    }
}

abstract class Op

data class Turn(val lr: String): Op() {
    fun apply(curDir: Int) = when (lr) {
        "L" -> (curDir - 1).mod(4)
        "R" -> (curDir + 1).mod(4)
        else -> throw IllegalStateException("Invalid turn $lr")
    }
}

data class Move(val count: Int): Op()


fun main() {
    val day = 22


    fun parseMapRow(input: String): Row {
        val offset = input.takeWhile { it == ' ' }
            .count()
        val path = input.mapNotNull { if (it != ' ') it else null }
        return Row(offset, path)
    }

    fun parseInput(input: List<String>): Pair<Map, List<Op>> {
        val map = input.takeWhile { it.isNotEmpty() }
            .map { parseMapRow(it) }
            .let { Map(it) }

        val dirs = input.last()
            .split("""(?<=\D)|(?=\D)""".toRegex())
            .map { if (it[0].isLetter()) Turn(it) else Move(it.toInt()) }

        return map to dirs
    }


    fun follow(map: Map, ops: List<Op>, cube: Boolean = false): Pair<Pos, Int> {
        var pos = Pos(0 + map.rows.first().offset, 0)
        var dir = right

        ops.forEach { op ->
            when (op) {
                is Turn -> dir = op.apply(dir)
                is Move -> {
                    val move = map.move(pos, dir, op.count, cube)
                    pos = move.first
                    dir = move.second
                }
            }
        }

        return pos to dir
    }

    fun part1(input: List<String>): Int {
        val (map, ops) = parseInput(input)
        val (pos, dir) = follow(map, ops)

        println("You ended up in $pos facing $dir")

        return (1000 * (pos.y + 1)) + (4 * (pos.x + 1)) + dir
    }

    fun part2(input: List<String>): Int {
        val (map, ops) = parseInput(input)
        val (pos, dir) = follow(map, ops, true)

        println("You ended up in $pos facing $dir")

        return (1000 * (pos.y + 1)) + (4 * (pos.x + 1)) + dir
    }

    println("OUTPUT FOR DAY $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(6032) { part1(testInput) }
//    checkTest(1) { part2(testInput) }
    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
    println("-".repeat(64))
}
