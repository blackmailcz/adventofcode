package net.nooii.adventofcode.helpers

import kotlin.math.abs

/**
 * Gauss-Jordan elimination for general (non-square) matrices.
 *
 * @param A the coefficient matrix (m × n)
 * @param b the right-hand side vector (length m)
 * @param freeVariableValues optional values for free variables (if system has infinite solutions)
 */
class GeneralGaussJordanElimination(
    A: Array<DoubleArray>,
    b: DoubleArray,
    freeVariableValues: List<Double> = emptyList()
) {

    private val m = A.size      // number of equations
    private val n = A[0].size   // number of variables
    private val providedFreeValues = freeVariableValues

    // Augmented matrix [A|b]
    private val aug = Array(m) { i ->
        DoubleArray(n + 1) { j ->
            if (j < n) A[i][j] else b[i]
        }
    }

    // Track which column corresponds to each pivot row
    private val pivotColumns = IntArray(m) { -1 }

    sealed class Result {
        data class UniqueSolution(val solution: DoubleArray) : Result() {
            /**
             * Returns the sum of all variables if the solution contains only non-negative integers (with rounding tolerance).
             * Returns null if any variable is negative or not an integer.
             *
             * @param tolerance The tolerance for checking if a value is an integer (default: 0.01)
             * @return The sum as Long if all variables are non-negative integers, null otherwise
             */
            fun getSumIfAllNonNegativeIntegers(tolerance: Double = 0.01): Long? {
                val allNonNegativeIntegers = solution.all { value ->
                    val rounded = kotlin.math.round(value)
                    abs(value - rounded) < tolerance && rounded >= 0
                }

                return if (allNonNegativeIntegers) {
                    solution.sumOf { kotlin.math.round(it).toLong() }
                } else {
                    null
                }
            }
        }
        data class NeedsFreeVariables(
            val numberOfFreeVariables: Int,
            val particularSolution: DoubleArray // solution with all free variables = 0
        ) : Result()
        object NoSolution : Result()
    }

    init {
        performElimination()
    }

    private fun performElimination() {
        var currentRow = 0

        for (col in 0 until n) {
            if (currentRow >= m) break

            // Find pivot
            var pivotRow = currentRow
            for (row in currentRow + 1 until m) {
                if (abs(aug[row][col]) > abs(aug[pivotRow][col])) {
                    pivotRow = row
                }
            }

            // Skip if column is zero
            if (abs(aug[pivotRow][col]) <= EPSILON) {
                continue
            }

            // Swap rows
            if (pivotRow != currentRow) {
                val temp = aug[currentRow]
                aug[currentRow] = aug[pivotRow]
                aug[pivotRow] = temp
            }

            pivotColumns[currentRow] = col

            // Scale pivot row
            val pivot = aug[currentRow][col]
            for (j in 0..n) {
                aug[currentRow][j] /= pivot
            }

            // Eliminate column in all other rows
            for (row in 0 until m) {
                if (row != currentRow) {
                    val factor = aug[row][col]
                    for (j in 0..n) {
                        aug[row][j] -= factor * aug[currentRow][j]
                    }
                }
            }

            currentRow++
        }
    }

    private val isConsistent: Boolean
        get() {
            for (row in 0 until m) {
                if (pivotColumns[row] == -1) {
                    if (abs(aug[row][n]) > EPSILON) {
                        return false
                    }
                }
            }
            return true
        }

    private fun getFreeVariableIndices(): List<Int> {
        val pivotSet = pivotColumns.filter { it != -1 }.toSet()
        return (0 until n).filter { it !in pivotSet }
    }

    private fun getParticularSolution(): DoubleArray {
        val solution = DoubleArray(n)
        for (row in 0 until m) {
            val col = pivotColumns[row]
            if (col != -1) {
                solution[col] = aug[row][n]
            }
        }
        return solution
    }

    /**
     * Computes solution with provided free variable values.
     * Takes only the first N values needed if too many are provided.
     */
    private fun getSolutionWithFreeValues(freeValues: List<Double>): DoubleArray {
        val solution = DoubleArray(n)
        val freeVarIndices = getFreeVariableIndices()

        // Set free variables (take only what's needed)
        val valuesToUse = freeValues.take(freeVarIndices.size)
        for ((i, index) in freeVarIndices.withIndex()) {
            solution[index] = valuesToUse.getOrElse(i) { 0.0 }
        }

        // Calculate basic variables
        for (row in 0 until m) {
            val col = pivotColumns[row]
            if (col != -1) {
                var rhs = aug[row][n]
                // Subtract contributions from free variables
                for (j in 0 until n) {
                    if (pivotColumns.indexOf(j) == -1 && j != col) {
                        rhs -= aug[row][j] * solution[j]
                    }
                }
                solution[col] = rhs
            }
        }

        return solution
    }

    /**
     * Returns the solution or information about what's needed.
     *
     * If free variable values were provided in constructor and the system has infinite solutions,
     * they will be used automatically.
     */
    fun solve(): Result {
        if (!isConsistent) {
            return Result.NoSolution
        }

        val freeVarIndices = getFreeVariableIndices()
        val freeVarCount = freeVarIndices.size

        // Debug: print pivot columns and free variables
//        println("DEBUG: pivotColumns = ${pivotColumns.contentToString()}")
//        println("DEBUG: freeVarIndices = $freeVarIndices")
//        println("DEBUG: freeVarCount = $freeVarCount")
//        println("DEBUG: providedFreeValues.size = ${providedFreeValues.size}")

        return when {
            freeVarCount == 0 -> {
                // Unique solution
                Result.UniqueSolution(getParticularSolution())
            }
            providedFreeValues.isNotEmpty() -> {
                // Free variables provided, compute solution
                Result.UniqueSolution(getSolutionWithFreeValues(providedFreeValues))
            }
            else -> {
                // Need free variables
                Result.NeedsFreeVariables(
                    numberOfFreeVariables = freeVarCount,
                    particularSolution = getParticularSolution()
                )
            }
        }
    }

    companion object {
        private const val EPSILON = 1e-8
    }
}
fun main() {
    println("=== Test 1: Unique Solution (Square System) ===")
    // 2x + y = 5
    // x - y = 1
    // Solution: x=2, y=1
    val A1 = arrayOf(
        doubleArrayOf(2.0, 1.0),
        doubleArrayOf(1.0, -1.0)
    )
    val b1 = doubleArrayOf(5.0, 1.0)

    val result1 = GeneralGaussJordanElimination(A1, b1).solve()
    when (result1) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✓ Unique solution: ${result1.solution.contentToString()}")
        is GeneralGaussJordanElimination.Result.NoSolution ->
            println("✗ No solution")
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables ->
            println("? Needs ${result1.numberOfFreeVariables} free variables")
    }

    println("\n=== Test 2: Infinite Solutions (Underdetermined) ===")
    // x + 2y + z = 5
    // 2x + 4y + 2z = 10 (dependent on first equation)
    // Has infinite solutions, one free variable
    val A2 = arrayOf(
        doubleArrayOf(1.0, 2.0, 1.0),
        doubleArrayOf(2.0, 4.0, 2.0)
    )
    val b2 = doubleArrayOf(5.0, 10.0)

    val result2 = GeneralGaussJordanElimination(A2, b2).solve()
    when (result2) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✓ Unique solution: ${result2.solution.contentToString()}")
        is GeneralGaussJordanElimination.Result.NoSolution ->
            println("✗ No solution")
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables -> {
            println("? Needs ${result2.numberOfFreeVariables} free variable(s)")
            println("  Particular solution (free vars = 0): ${result2.particularSolution.contentToString()}")

            // Now provide free variable value
            println("\n  Trying with free variable = 1.0:")
            val result2b = GeneralGaussJordanElimination(A2, b2, listOf(1.0)).solve()
            when (result2b) {
                is GeneralGaussJordanElimination.Result.UniqueSolution ->
                    println("  ✓ Solution: ${result2b.solution.contentToString()}")
                else -> println("  ✗ Unexpected result")
            }

            println("  Trying with free variable = 3.0:")
            val result2c = GeneralGaussJordanElimination(A2, b2, listOf(3.0)).solve()
            when (result2c) {
                is GeneralGaussJordanElimination.Result.UniqueSolution ->
                    println("  ✓ Solution: ${result2c.solution.contentToString()}")
                else -> println("  ✗ Unexpected result")
            }
        }
    }

    println("\n=== Test 3: No Solution (Inconsistent) ===")
    // x + y = 1
    // x + y = 2 (contradicts first equation)
    val A3 = arrayOf(
        doubleArrayOf(1.0, 1.0),
        doubleArrayOf(1.0, 1.0)
    )
    val b3 = doubleArrayOf(1.0, 2.0)

    val result3 = GeneralGaussJordanElimination(A3, b3).solve()
    when (result3) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✗ Unexpected: got solution ${result3.solution.contentToString()}")
        is GeneralGaussJordanElimination.Result.NoSolution ->
            println("✓ Correctly identified: No solution")
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables ->
            println("✗ Unexpected: needs free variables")
    }

    println("\n=== Test 4: Providing Too Many Free Variables ===")
    // Same as Test 2, but provide more values than needed
    val result4 = GeneralGaussJordanElimination(
        A2, b2,
        listOf(1.0, 2.0, 3.0, 4.0, 5.0) // Only 1 needed, rest ignored
    ).solve()
    when (result4) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✓ Solution (used only first value): ${result4.solution.contentToString()}")
        else ->
            println("✗ Unexpected result")
    }

    println("\n=== Test 5: More Variables Than Equations ===")
    // x + y + z + w = 10
    // Has 3 free variables
    val A5 = arrayOf(
        doubleArrayOf(1.0, 1.0, 1.0, 1.0)
    )
    val b5 = doubleArrayOf(10.0)

    val result5 = GeneralGaussJordanElimination(A5, b5).solve()
    when (result5) {
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables -> {
            println("? Needs ${result5.numberOfFreeVariables} free variables")

            // Provide values for the free variables
            val result5b = GeneralGaussJordanElimination(
                A5, b5,
                listOf(1.0, 2.0, 3.0)
            ).solve()
            when (result5b) {
                is GeneralGaussJordanElimination.Result.UniqueSolution ->
                    println("✓ Solution: ${result5b.solution.contentToString()}")
                else -> println("✗ Unexpected")
            }
        }
        else -> println("✗ Unexpected result type")
    }

    println("\n=== Test 6: User's Matrix (Should Have Free Variable x4) ===")
    // Matrix:
    // 1 1 1 0 | 10
    // 1 0 1 1 | 11
    // 1 1 0 0 | 5
    // 0 0 1 0 | 5
    // Expected: x1 = 6-x4, x2 = -1+x4, x3 = 5, x4 = free
    val A6 = arrayOf(
        doubleArrayOf(1.0, 1.0, 1.0, 0.0),
        doubleArrayOf(1.0, 0.0, 1.0, 1.0),
        doubleArrayOf(1.0, 1.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 1.0, 0.0)
    )
    val b6 = doubleArrayOf(10.0, 11.0, 5.0, 5.0)

    val result6 = GeneralGaussJordanElimination(A6, b6).solve()
    when (result6) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✗ Got unique solution: ${result6.solution.contentToString()}")
        is GeneralGaussJordanElimination.Result.NoSolution ->
            println("✗ Got no solution")
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables -> {
            println("✓ Correctly identified ${result6.numberOfFreeVariables} free variable(s)")
            println("  Particular solution (x4=0): ${result6.particularSolution.contentToString()}")

            // Test with x4 = 2
            val result6b = GeneralGaussJordanElimination(A6, b6, listOf(2.0)).solve()
            when (result6b) {
                is GeneralGaussJordanElimination.Result.UniqueSolution -> {
                    println("  Solution with x4=2: ${result6b.solution.contentToString()}")
                    println("  Expected: [4.0, 1.0, 5.0, 2.0]")
                }
                else -> println("  ✗ Unexpected")
            }
        }
    }

    println("\n=== Test 7: User's Second Matrix (8x10) - Find Optimal Solution ===")
    val A7 = arrayOf(
        doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
        doubleArrayOf(1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        doubleArrayOf(0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0),
        doubleArrayOf(0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0),
        doubleArrayOf(1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0),
        doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
        doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0)
    )
    val b7 = doubleArrayOf(30.0, 67.0, 220.0, 45.0, 35.0, 56.0, 52.0, 199.0)

    val result7 = GeneralGaussJordanElimination(A7, b7).solve()
    when (result7) {
        is GeneralGaussJordanElimination.Result.UniqueSolution ->
            println("✓ Unique solution: ${result7.solution.contentToString()}")
        is GeneralGaussJordanElimination.Result.NoSolution ->
            println("✗ No solution")
        is GeneralGaussJordanElimination.Result.NeedsFreeVariables -> {
            println("? Needs ${result7.numberOfFreeVariables} free variable(s)")
            println("  Particular solution (free vars = 0): ${result7.particularSolution.contentToString()}")

            // Identify which variables are free
            val gje = GeneralGaussJordanElimination(A7, b7)
            gje.solve() // Initialize
            val freeVarIndices = (0 until 10).filter { idx ->
                result7.particularSolution[idx] == 0.0 &&
                A7.all { row -> row[idx] == 0.0 || A7.any { r -> r != row } }
            }
            println("  Attempting to identify free variables by checking which are 0...")

            // Find optimal solution where all variables are non-negative integers
            var minSum = Int.MAX_VALUE
            var bestSolution: DoubleArray? = null
            var bestFreeVars: Pair<Int, Int>? = null

            println("\n  Searching for optimal solution (all variables must be non-negative integers)...")

            var sampleCount = 0
            var integerSolutions = 0
            var nonNegativeSolutions = 0

            for (fv1 in 0..250) {
                for (fv2 in 0..250) {
                    val testResult = GeneralGaussJordanElimination(A7, b7, listOf(fv1.toDouble(), fv2.toDouble())).solve()
                    if (testResult is GeneralGaussJordanElimination.Result.UniqueSolution) {
                        val sol = testResult.solution

                        // Debug: print first few solutions
                        if (sampleCount < 5) {
                            println("  Sample (fv1=$fv1, fv2=$fv2): ${sol.contentToString()}")
                            sampleCount++
                        }

                        // Check if all are integers (relaxed tolerance)
                        val allIntegers = sol.all { value ->
                            val rounded = kotlin.math.round(value)
                            abs(value - rounded) < 0.01 // More relaxed tolerance
                        }
                        if (allIntegers) integerSolutions++

                        // Check if all are non-negative
                        val allNonNegative = sol.all { it >= -1e-9 }
                        if (allNonNegative) nonNegativeSolutions++

                        // Check if all variables are non-negative integers (relaxed tolerance)
                        val allNonNegativeIntegers = sol.all { value ->
                            val rounded = kotlin.math.round(value)
                            abs(value - rounded) < 0.01 && rounded >= 0
                        }

                        if (allNonNegativeIntegers) {
                            val sum = sol.sumOf { kotlin.math.round(it).toLong().toInt() }
                            if (sum < minSum) {
                                minSum = sum
                                bestSolution = sol
                                bestFreeVars = fv1 to fv2
                            }
                        }
                    }
                }
            }

            println("  Debug: Found $integerSolutions integer solutions (out of ${251*251})")
            println("  Debug: Found $nonNegativeSolutions non-negative solutions")

            // Test the suggested free variables explicitly
            println("\n  Testing suggested free variables (22, 164):")
            val testSuggested = GeneralGaussJordanElimination(A7, b7, listOf(22.0, 164.0)).solve()
            if (testSuggested is GeneralGaussJordanElimination.Result.UniqueSolution) {
                val sol = testSuggested.solution
                println("  Solution: ${sol.contentToString()}")
                println("  As rounded integers: ${sol.map { kotlin.math.round(it).toLong() }}")
                println("  All non-negative? ${sol.all { it >= -0.01 }}")
                println("  All integers (tolerance 0.01)? ${sol.all { abs(it - kotlin.math.round(it)) < 0.01 }}")
                val allNonNegativeIntegers = sol.all { value ->
                    val rounded = kotlin.math.round(value)
                    abs(value - rounded) < 0.01 && rounded >= 0
                }
                println("  All non-negative integers? $allNonNegativeIntegers")
                if (allNonNegativeIntegers) {
                    println("  Sum: ${sol.sumOf { kotlin.math.round(it).toLong() }}")
                } else {
                    println("  Sum (if we round anyway): ${sol.sumOf { kotlin.math.round(it).toLong() }}")
                }
            }

            if (bestSolution != null) {
                println("  ✓ Found optimal solution!")
                println("  Free variables: ${bestFreeVars!!.first}, ${bestFreeVars.second}")
                println("  Solution (as integers): ${bestSolution.map { it.toLong() }.toString()}")
                println("  Solution (raw doubles): ${bestSolution.contentToString()}")
                println("  Sum: $minSum")
            } else {
                println("  ✗ No valid solution found where all variables are non-negative integers")
            }
        }
    }
}

