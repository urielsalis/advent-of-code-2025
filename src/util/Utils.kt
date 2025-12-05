package util

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
 * Reads the entire input as a grid
 */
fun <T: GridCell> readGrid(day: Int, isTest: Boolean = false, f: (Char) -> T): Grid<T> {
    val lines = readLines(day, isTest)
    val content = lines.map { it.toList().map(f).toMutableList() }
    return Grid(content)
}

/**
 * Computes an exponent of 2 Longs.
 */
fun Number.pow(other: Number): Long = this.toDouble().pow(other.toDouble()).toLong()

/**
 * Converts string to util.md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Merges overlapping and contiguous LongRanges into the minimal set of LongRanges covering the same values.
 */
fun merge(ranges: List<LongRange>): List<LongRange> {
    val sorted = ranges.sortedBy { it.first }
    return sorted.drop(1).fold(mutableListOf(sorted.first())) { merged, range ->
        val last = merged.last()
        if (last.last >= range.first - 1) {
            merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
        } else {
            merged.add(range)
        }
        merged
    }
}