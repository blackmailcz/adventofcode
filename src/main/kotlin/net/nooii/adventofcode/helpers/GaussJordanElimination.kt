package net.nooii.adventofcode.helpers

import kotlin.math.abs

/**
 * Retrieves the solution to the linear system of equations.
 *
 * This function determines whether the system is feasible and returns the appropriate solution.
 * If the system is feasible, it returns the primal solution. Otherwise, it returns the dual solution.
 *
 * @return A [DoubleArray] representing the solution to the linear system.
 *         Returns the primal solution if the system is feasible, or the dual solution if it's not.
 *         May return null if no solution exists.
 */
// https://algs4.cs.princeton.edu/99scientific/GaussJordanElimination.java.html
class GaussJordanElimination(A: Array<DoubleArray>, b: DoubleArray) {

    private val n = b.size // n-by-n system

    // build augmented matrix
    private val a = Array(n) { DoubleArray(n + n + 1) } // n-by-(n+1) augmented matrix

    // Gauss-Jordan elimination with partial pivoting
    /**
     * Solves the linear system of equations *Ax* = *b*,
     * where *A* is an *n*-by-*n* matrix and *b*
     * is a length *n* vector.
     *
     * @param  A the *n*-by-*n* constraint matrix
     * @param  b the length *n* right-hand-side vector
     */
    init {
        for (i in 0 until n) for (j in 0 until n) a[i][j] = A[i][j]

        // only needed if you want to find certificate of infeasibility (or compute inverse)
        for (i in 0 until n) a[i][n + i] = 1.0

        for (i in 0 until n) a[i][n + n] = b[i]

        solve()

        assert(certifySolution(A, b))
    }

    private fun solve() {
        // Gauss-Jordan elimination

        for (p in 0 until n) {
            // show();

            // find pivot row using partial pivoting

            var max = p
            for (i in p + 1 until n) {
                if (abs(a[i][p]) > abs(a[max][p])) {
                    max = i
                }
            }

            // exchange row p with row max
            swap(p, max)

            // singular or nearly singular
            if (abs(a[p][p]) <= EPSILON) {
                continue
                // throw new ArithmeticException("Matrix is singular or nearly singular");
            }

            // pivot
            pivot(p, p)
        }
        // show();
    }

    // swap row1 and row2
    private fun swap(row1: Int, row2: Int) {
        val temp = a[row1]
        a[row1] = a[row2]
        a[row2] = temp
    }


    // pivot on entry (p, q) using Gauss-Jordan elimination
    private fun pivot(p: Int, q: Int) {
        // everything but row p and column q

        for (i in 0 until n) {
            val alpha = a[i][q] / a[p][q]
            for (j in 0..n + n) {
                if (i != p && j != q) a[i][j] -= alpha * a[p][j]
            }
        }

        // zero out column q
        for (i in 0 until n) if (i != p) a[i][q] = 0.0

        // scale row p (ok to go from q+1 to n, but do this for consistency with simplex pivot)
        for (j in 0..n + n) if (j != q) a[p][j] /= a[p][q]
        a[p][q] = 1.0
    }

    /**
     * Returns a solution to the linear system of equations *Ax* = *b*.
     *
     * @return a solution *x* to the linear system of equations
     * *Ax* = *b*; `null` if no such solution
     */
    private fun primal(): DoubleArray? {
        val x = DoubleArray(n)
        for (i in 0 until n) {
            if (abs(a[i][i]) > EPSILON) x[i] = a[i][n + n] / a[i][i]
            else if (abs(a[i][n + n]) > EPSILON) return null
        }
        return x
    }

    /**
     * Returns a solution to the linear system of equations *yA* = 0,
     * *yb*  0.
     *
     * @return a solution *y* to the linear system of equations
     * *yA* = 0, *yb*  0; `null` if no such solution
     */
    private fun dual(): DoubleArray? {
        val y = DoubleArray(n)
        for (i in 0 until n) {
            if ((abs(a[i][i]) <= EPSILON) && (abs(
                    a[i][n + n]
                ) > EPSILON)
            ) {
                for (j in 0 until n) y[j] = a[i][n + j]
                return y
            }
        }
        return null
    }

    val isFeasible: Boolean
        /**
         * Returns true if there exists a solution to the linear system of
         * equations *Ax* = *b*.
         *
         * @return `true` if there exists a solution to the linear system
         * of equations *Ax* = *b*; `false` otherwise
         */
        get() = primal() != null

    // check that Ax = b or yA = 0, yb != 0
    private fun certifySolution(A: Array<DoubleArray>, b: DoubleArray): Boolean {
        // check that Ax = b

        if (isFeasible) {
            val x = primal()
            for (i in 0 until n) {
                var sum = 0.0
                for (j in 0 until n) {
                    sum += A[i][j] * x!![j]
                    println("s $sum || ${A[i][j] * x!![j]}")
                }
                println(">>>>> $sum ${b[i]}")
                if (abs(sum - b[i]) > EPSILON) {
                    println("not feasible")
                    System.out.printf("b[%d] = %8.3f, sum = %8.3f\n", i, b[i], sum)
                    return false
                }
            }
            return true
        } else {
            val y = dual()
            for (j in 0 until n) {
                var sum = 0.0
                for (i in 0 until n) {
                    sum += A[i][j] * y!![i]
                }
                if (abs(sum) > EPSILON) {
                    println("invalid certificate of infeasibility")
                    System.out.printf("sum = %8.3f\n", sum)
                    return false
                }
            }
            var sum = 0.0
            for (i in 0 until n) {
                sum += y!![i] * b[i]
            }
            if (abs(sum) < EPSILON) {
                println("invalid certificate of infeasibility")
                System.out.printf("yb  = %8.3f\n", sum)
                return false
            }
            return true
        }
    }

    fun solution(): DoubleArray? {
        return if (isFeasible) {
            primal()
        } else {
            dual()
        }
    }

    companion object {
        private const val EPSILON = 1e-8
    }
}