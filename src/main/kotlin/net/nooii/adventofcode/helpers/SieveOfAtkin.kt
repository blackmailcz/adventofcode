package net.nooii.adventofcode.helpers

import kotlin.math.max
import kotlin.math.sqrt

object SieveOfAtkin {

    fun sieve(limit : Int) : MutableList<Int> {
        val output = mutableListOf<Int>()
        val sieve = BooleanArray(max(limit + 1, 4)) { false }
        val limitSqrt = sqrt(limit.toDouble()).toInt()

        // there may be more efficient data structure
        // arrangements than this (there are!) but
        // this is the algorithm in Wikipedia

        // the sieve works only for integers > 3, so
        // set these trivially to their proper values
        sieve[0] = false
        sieve[1] = false
        sieve[2] = true
        sieve[3] = true

        // loop through all possible integer values for x and y
        // up to the square root of the max prime for the sieve
        // we don't need any larger values for x or y since the
        // max value for x or y will be the square root of n
        // in the quadratics
        // the theorem showed that the quadratics will produce all
        // primes that also satisfy their wheel factorizations, so
        // we can produce the value of n from the quadratic first
        // and then filter n through the wheel quadratic
        // there may be more efficient ways to do this, but this
        // is the design in the Wikipedia article
        // loop through all integers for x and y for calculating
        // the quadratics
        for (x in 1..limitSqrt) {
            for (y in 1..limitSqrt) {
                // first quadratic using m = 12 and r in R1 = {r : 1, 5}
                var n = 4 * x * x + y * y
                if (n <= limit && (n % 12 == 1 || n % 12 == 5)) {
                    sieve[n] = !sieve[n]
                }
                // second quadratic using m = 12 and r in R2 = {r : 7}
                n = 3 * x * x + y * y
                if (n <= limit && n % 12 == 7) {
                    sieve[n] = !sieve[n]
                }
                // third quadratic using m = 12 and r in R3 = {r : 11}
                n = 3 * x * x - y * y
                if (x > y && n <= limit && n % 12 == 11) {
                    sieve[n] = !sieve[n]
                } // end if
                // note that R1 union R2 union R3 is the set R
                // R = {r : 1, 5, 7, 11}
                // which is all values 0 < r < 12 where r is
                // a relative prime of 12
                // Thus all primes become candidates
            } // end for
        } // end for
        // remove all perfect squares since the quadratic
        // wheel factorization filter removes only some of them
        for (n in 5..limitSqrt) {
            if (sieve[n]) {
                val x = n * n
                if (limit < 25000) {
                    var i = x
                    while (i <= limit) {
                        sieve[i] = false
                        i += x
                    }
                } else {
                    // Prevent int overflow for big numbers
                    var i : Long = x.toLong()
                    while (i <= limit) {
                        sieve[i.toInt()] = false
                        i += x
                    }
                }
            } // end if
        } // end for
        // put the results to the System.out device
        // in 10x10 blocks
        var i = 0
        while (i <= limit) {
            if (sieve[i]) {
                output.add(i)
            } // end if
            i++
        }
        return output
    }
}