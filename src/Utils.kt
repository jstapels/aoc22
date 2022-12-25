import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

const val debug = false

data class Pos(val x: Int, val y: Int) {
    operator fun plus(p: Pos) =
        Pos(x + p.x, y + p.y)
}

infix fun Int.by(that: Int) = Pos(this, that)


fun Int.pad(n: Int) =
    this.toString().padStart(n, '0')

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Quick assertion check.
 */
fun <T> checkThat(actual: T, expected: T) {
    if (actual != expected)
        throw AssertionError("Actual $actual does not equal $expected")
}

var testCount = 1
fun <T> checkTest(expected: T, runner: () -> T) {
    var actual: T
    val ms = measureTimeMillis { actual = runner() }
    val good = actual == expected
    val out = if (good) "Pass! 🎉" else "Fail! ❌"
    println("Executed test ${testCount++} in $ms ms | $actual == $expected | $out")
    if (! good) throw AssertionError()
}

var solutionCount = 1
fun <T> solution(runner: () -> T) {
    var result: T
    val ms = measureTimeMillis { result = runner() }
    println("Solution ${solutionCount++} in $ms ms | $result")
}

fun debug(s: Any) {
    if (debug) println(s)
}
