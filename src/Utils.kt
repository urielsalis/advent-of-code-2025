import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.pow


/**
 * Reads the entire input for the given day number.
 */
fun readInput(day: Int, isTest: Boolean = false): String {
    val fileName = "data/Day${day.toString().padStart(2, '0')}${if (isTest) "_test" else ""}.txt"
    return Path(fileName).readText()
}

/**
 * Reads lines from input file for the given day number.
 */
fun readLines(day: Int, isTest: Boolean = false): List<String> {
    return readInput(day, isTest).trim().lines()
}

/**
 * Computes an exponent of 2 Longs.
 */
fun Number.pow(other: Number): Long = this.toDouble().pow(other.toDouble()).toLong()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
