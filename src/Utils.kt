import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

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

fun <T> checkTest(expected: T, runner: () -> T) {
    var actual: T
    val ms = measureTimeMillis { actual = runner() }
    val good = actual == expected
    val out = if (good) "Pass! 🎉" else "Fail! ❌"
    println("Executed test in $ms ms | $actual == $expected | $out")
    if (! good) throw IllegalStateException()
}
fun <T> solution(runner: () -> T) {
    var result: T
    var ms = measureTimeMillis { result = runner() }
    println("--------------------------------")
    println("Solution in $ms ms | $result")
}
