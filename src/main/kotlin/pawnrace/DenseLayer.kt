package pawnrace

import kotlin.math.sqrt
import kotlin.random.Random.Default.nextFloat
import kotlin.math.tanh

class DenseLayer {

    val weightLayer : Matrix
    val weightAdaParam : Matrix
    val biasLayer : Matrix
    val biasAdaParam : Matrix
    val numParams: Int
    val tau: Float



    companion object {
        fun getRandomNumber(): Float {
            return (nextFloat()*0.2f)-0.4f
        }
    }



    fun activate(x: Matrix): Matrix {
        val activation = Matrix.mapMatrix(Matrix.addMatricies(Matrix.multiplyMatricies(x, weightLayer), biasLayer)) { tanh(it) }
//        println(Matrix.multiplyMatricies(x, weightLayer).matrix.contentDeepToString())
//        println(biasLayer.matrix.contentDeepToString())
//        println(Matrix.addMatricies(Matrix.multiplyMatricies(x, weightLayer), biasLayer).matrix.contentDeepToString())
//        println(Matrix.mapMatrix(Matrix.addMatricies(Matrix.multiplyMatricies(x, weightLayer), biasLayer)) { Matrix.sigmoid(it) }.matrix.contentDeepToString())
        return activation
    }

    constructor(inputUnits: Int, outputUnits: Int) {
        weightLayer = Matrix(inputUnits, outputUnits) { getRandomNumber() }
        biasLayer = Matrix(1,inputUnits) { getRandomNumber() }
        biasAdaParam = Matrix(1,inputUnits) { 0.05f }
        weightAdaParam = Matrix(inputUnits, outputUnits) { 0.05f }

        numParams = biasAdaParam.dim.first * biasAdaParam.dim.second + weightAdaParam.dim.first * weightAdaParam.dim.second
        tau = 1/sqrt(sqrt(numParams.toDouble())*2).toFloat()

//        println(weightLayer.matrix.contentDeepToString())
    }

    constructor(weight: Matrix, bias: Matrix, wAda: Matrix, bAda: Matrix) {
        weightLayer = weight
        biasLayer = bias
        weightAdaParam = wAda
        biasAdaParam = bAda
        numParams = biasAdaParam.dim.first * biasAdaParam.dim.second + weightAdaParam.dim.first * weightAdaParam.dim.second
        tau = 1/sqrt(sqrt(numParams.toDouble())*2).toFloat()

    }
}