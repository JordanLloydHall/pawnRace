package pawnrace

import kotlin.math.exp
import java.util.*


class Matrix {
    companion object {
        fun multiplyMatricies(first: Matrix, second: Matrix): Matrix {
//            println("first ${first.dim}, second ${second.dim}")
            val product = Array<Array<Float>>(first.matrix.size) { i -> Array<Float>(second.matrix.first().size) { j ->
                var s = 0f
                for (k in first.matrix.first().indices) {
                    s += first.matrix[i][k] * second.matrix[k][j]
                }
                s
            } }
//            for (i in first.matrix.indices) {
//                for (j in second.matrix.first().indices) {
//                    for (k in first.matrix.first().indices) {
//                        product[i][j] += first.matrix[i][k] * second.matrix[k][j]
//                    }
//                }
//            }
            return Matrix(product)
        }

        fun addMatricies(first: Matrix, second: Matrix): Matrix {
//            val sum = Array<Array<Float>>(first.matrix.size) { Array<Float>(first.matrix.first().size) { 0f } }
//            if (first.matrix.size == 1) {
//                println(first.matrix.contentDeepToString())
//                println(second.matrix.contentDeepToString())
////            }
            for (i in first.matrix.indices) {
                for (j in first.matrix.first().indices) {
//                    println(first.matrix[i][j] + second.matrix[i][j])
                    first.matrix[i][j] = first.matrix[i][j] + second.matrix[i][j]
                }
            }

            return first
        }

        fun mapMatrix(first: Matrix, f: (Float) -> Float): Matrix {
//            val sum = Array<Array<Float>>(first.matrix.size) { Array<Float>(first.matrix.first().size) { 0f } }
            for (i in first.matrix.indices) {
                for (j in first.matrix.first().indices) {
                    first.matrix[i][j] = f(first.matrix[i][j])
                }
            }
            return first
        }

        fun scale(first: Matrix, f: Float): Matrix {
//            val sum = Array<Array<Float>>(first.matrix.size) { Array<Float>(first.matrix.first().size) { 0f } }
            for (i in first.matrix.indices) {
                for (j in first.matrix.first().indices) {
                    first.matrix[i][j] = first.matrix[i][j] * f
                }
            }
            return first
        }

        fun sigmoid(f: Float): Float {
            return 1.0f / (1.0f + exp(-f))
        }

        fun gaussianPerturbation(f: Float): Float {
            return f + Random().nextGaussian().toFloat()
        }
    }

    var matrix: Array<Array<Float>>
    val dim: Pair<Int, Int>
    constructor(m: Int, n: Int, f: (Int) -> Float) {
        matrix = Array<Array<Float>>(m) { i1 ->  Array<Float>(n) { i2 ->
//            println("${i1}   ${i2}   ${m*i1 + i2}")

            f(m*i2 + i1) } }

        dim = Pair(m,n)
    }

    constructor(m: Array<Array<Float>>) {
        matrix = m

        dim = Pair(m.size,m.first().size)
    }

    fun copyMatrix(): Matrix {
        return Matrix(matrix.map { it.toList().toTypedArray() }.toList().toTypedArray())
    }

}