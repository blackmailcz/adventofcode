package net.nooii.adventofcode.helpers

import java.math.BigDecimal
import java.math.RoundingMode

class GaussJordanEliminationBigDecimal(A: Array<Array<BigDecimal>>, b: Array<BigDecimal>) {

    private val n = b.size // n-by-n system

    // build augmented matrix
    private val a = Array(n) { Array(n + 1) { BigDecimal.ZERO } } // n-by-(n+1) augmented matrix

    init {
        for (i in 0 until n) {
            for (j in 0 until n) {
                a[i][j] = A[i][j].setScale(64, RoundingMode.HALF_UP)
            }
        }
        // Augment the matrix with the vector b
        for (i in 0 until n) {
            a[i][n] = b[i].setScale(64, RoundingMode.HALF_UP)
        }
        solve()
    }

    private fun solve() {
        for (p in 0 until n) {
            // find pivot row
            var max = p
            for (i in p + 1 until n) {
                if (a[i][p].abs() > a[max][p].abs()) {
                    max = i
                }
            }
            // swap rows
            swap(p, max)
            // singular or nearly singular
            if (a[p][p].abs() <= EPSILON) {
                continue
            }
            // pivot
            pivot(p, p)
        }
    }

    private fun swap(row1: Int, row2: Int) {
        val temp = a[row1]
        a[row1] = a[row2]
        a[row2] = temp
    }

    private fun pivot(p: Int, q: Int) {
        // scale row p
        val alpha = a[p][q].let {
            if (it != BigDecimal.ZERO) BigDecimal.ONE.divide(
                it,
                64,
                RoundingMode.HALF_UP
            ) else BigDecimal.ZERO
        }
        for (j in 0..n) {
            a[p][j] *= alpha
        }

        // eliminate other rows
        for (i in 0 until n) {
            if (i != p) {
                val beta = a[i][q]
                for (j in 0..n) {
                    a[i][j] -= beta * a[p][j]
                }
            }
        }
    }

    fun solution(): Array<BigDecimal>? {
        val result = Array(n) { BigDecimal.ZERO }
        for (i in 0 until n) {
            if (a[i][i].abs() > EPSILON) {
                result[i] = a[i][n]
            } else if (a[i][n].abs() > EPSILON) {
                // Inconsistent system
                return null
            }
        }
        return result
    }

    companion object {
        private val EPSILON = BigDecimal("1E-64")
    }
}