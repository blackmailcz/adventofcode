package net.nooii.adventofcode.helpers

import java.math.BigInteger
import java.security.MessageDigest

object CryptoTool {

    fun lowercaseCaesar(char: Char, shift: Int): Char {
        return ((char.code - 'a'.code + shift) % 26 + 'a'.code).toChar()
    }

    fun md5hash(string: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
    }
}