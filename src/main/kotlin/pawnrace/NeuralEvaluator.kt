package pawnrace

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import kotlin.math.exp
import kotlin.random.Random
import kotlin.random.Random.Default.nextFloat

class NeuralEvaluator {

    val layers: MutableList<DenseLayer>
    val numLayers: Int
    val unitsPerLayer: Int

    constructor(numLayers: Int, unitsPerLayer: Int) {
        this.numLayers = numLayers
        this.unitsPerLayer = unitsPerLayer
        layers = mutableListOf<DenseLayer>()
        layers.add(DenseLayer(64, unitsPerLayer))
        for (i in 0 until numLayers) {
            layers.add(DenseLayer(unitsPerLayer, unitsPerLayer))
        }
        layers.add(DenseLayer(unitsPerLayer, 1))

    }

    constructor(numLayers: Int, unitsPerLayer: Int, layers: MutableList<DenseLayer>) {
        this.numLayers = numLayers
        this.unitsPerLayer = unitsPerLayer
        this.layers = layers

    }

    fun saveLayers(name: String) {
        try {
            Files.createDirectory(Paths.get("./$name"))
        } catch (e: IOException) {

        }
        for (i in layers.indices) {

            File("./${name}/layer${i}weights.txt").writeText(layers[i].weightLayer.matrix.flatten().joinToString(","))
            File("./${name}/layer${i}biases.txt").writeText(layers[i].biasLayer.matrix.flatten().joinToString(","))
        }

    }

    fun loadLayers(name: String) {
        for (i in layers.indices) {

            val newWeightsList = File("./kotlinpawnrace_jh4020/${name}/layer${i}weights.txt").readText().split(',').map { it.toFloat() }
            val newBiasesList = File("./kotlinpawnrace_jh4020/${name}/layer${i}biases.txt").readText().split(',').map { it.toFloat() }
//            val newWeightsList = File("./${name}/layer${i}weights.txt").readText().split(',').map { it.toFloat() }
//            val newBiasesList = File("./${name}/layer${i}biases.txt").readText().split(',').map { it.toFloat() }
//            println(newWeightsList.size)
//            println(i)
//            println(newWeightsList.size)
//            println(layers[i].weightLayer.dim)
            val newWeights = Matrix(layers[i].weightLayer.dim.first, layers[i].weightLayer.dim.second) { newWeightsList[it] }
//            println(newWeights.matrix.contentDeepToString())
            val newBiases = Matrix(layers[i].biasLayer.dim.first, layers[i].biasLayer.dim.second) { newBiasesList[it] }

            layers[i] = DenseLayer(newWeights, newBiases, layers[i].weightAdaParam, layers[i].biasAdaParam)
        }
    }

    fun combineAndMutateLayers(other: NeuralEvaluator): NeuralEvaluator {

        val newLayers = mutableListOf<DenseLayer>()
        for (l in 0 until layers.size) {
            var newWeightMatrix: Matrix
            var newWeightAdaMatrix: Matrix
            var newBiasMatrix: Matrix
            var newBiasAdaMatrix: Matrix

            newWeightAdaMatrix = layers[l].weightAdaParam.copyMatrix()
            for (i in newWeightAdaMatrix.matrix.indices) {
                for (j in newWeightAdaMatrix.matrix.first().indices) {
                    if (nextFloat() > 0.5) {
                        newWeightAdaMatrix.matrix[i][j] = other.layers[l].weightAdaParam.matrix[i][j]
                    }
                }
            }
            newWeightAdaMatrix = Matrix.mapMatrix(newWeightAdaMatrix) { it * exp(layers[l].tau * java.util.Random().nextGaussian()).toFloat() }

            newWeightMatrix = layers[l].weightLayer.copyMatrix()
            for (i in newWeightMatrix.matrix.indices) {
                for (j in newWeightMatrix.matrix.first().indices) {
                    if (nextFloat() > 0.5) {
                        newWeightMatrix.matrix[i][j] = other.layers[l].weightLayer.matrix[i][j]
                    }
                }
            }
            newWeightMatrix = Matrix.addMatricies(newWeightMatrix, Matrix.mapMatrix(newWeightAdaMatrix.copyMatrix()) { it * java.util.Random().nextGaussian().toFloat() })


            newBiasAdaMatrix = layers[l].biasAdaParam.copyMatrix()
            for (i in newBiasAdaMatrix.matrix.indices) {
                for (j in newBiasAdaMatrix.matrix.first().indices) {
                    if (nextFloat() > 0.5) {
                        newBiasAdaMatrix.matrix[i][j] = other.layers[l].biasAdaParam.matrix[i][j]
                    }
                }
            }
            newBiasAdaMatrix = Matrix.mapMatrix(newBiasAdaMatrix) { it * exp(layers[l].tau * java.util.Random().nextGaussian()).toFloat() }

            newBiasMatrix = layers[l].biasLayer.copyMatrix()
            for (i in newBiasMatrix.matrix.indices) {
                for (j in newBiasMatrix.matrix.first().indices) {
                    if (nextFloat() > 0.5) {
                        newBiasMatrix.matrix[i][j] = other.layers[l].biasLayer.matrix[i][j]
                    }
                }
            }
            newBiasMatrix = Matrix.addMatricies(newBiasMatrix, Matrix.mapMatrix(newBiasAdaMatrix.copyMatrix()) { it * java.util.Random().nextGaussian().toFloat() })
            newLayers.add(DenseLayer(newWeightMatrix, newBiasMatrix, newWeightAdaMatrix, newBiasAdaMatrix))
        }
        return NeuralEvaluator(numLayers, unitsPerLayer, newLayers)
    }

    fun feedForward(x: Matrix): Matrix {
//        println(x.matrix.contentDeepToString())
        val out = layers.fold(x) { acc, layer ->
//            println(layer.activate(acc).matrix.contentDeepToString())
            layer.activate(acc)
        }
//        println(out.matrix.contentDeepToString())
//
        return out
    }



}