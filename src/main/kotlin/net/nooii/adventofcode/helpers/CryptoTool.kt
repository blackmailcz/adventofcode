package net.nooii.adventofcode.helpers

import java.math.BigInteger
import java.security.MessageDigest

/**
 * A utility class for various cryptographic operations.
 */
object CryptoTool {

    /**
     * Applies a Caesar cipher shift to a lowercase character.
     *
     * This function takes a lowercase character and shifts it by a specified amount,
     * wrapping around the alphabet if necessary. It only works with lowercase letters (a-z).
     *
     * @param char The lowercase character to be shifted.
     * @param shift The number of positions to shift the character. Can be positive or negative.
     * @return The shifted character, still within the lowercase alphabet range.
     */
    fun lowercaseCaesar(char: Char, shift: Int): Char {
        return ((char.code - 'a'.code + shift) % 26 + 'a'.code).toChar()
    }

    /**
     * Computes the MD5 hash of the given string.
     *
     * This function calculates the MD5 hash of the input string and returns it as a 32-character hexadecimal string.
     *
     * @param string The input string to be hashed.
     * @return A 32-character hexadecimal string representing the MD5 hash of the input.
     */
    fun md5hash(string: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
    }
}