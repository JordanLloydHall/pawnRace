package pawnrace

import java.lang.Math.pow
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextFloat

class ControllerTrainer {
    lateinit var pop: List<NeuralEvaluator>

    val oldQueue : Queue<NeuralEvaluator> = LinkedList()


    fun initializePop(popSize: Int) {
        pop = (0..popSize).map { NeuralEvaluator(2, 64) }
    }

    fun playGame(p1: NeuralEvaluator, p2: NeuralEvaluator): NeuralEvaluator? {
        val playerController1 = NNMinMaxController()
        val playerController2 = NNMinMaxController()

        val mainPlayer = Player(Piece.BLACK, null, playerController1)
        val otherPlayer = Player(Piece.WHITE, mainPlayer, playerController2)
        mainPlayer.opponent = otherPlayer

        val gap = mainPlayer.controller!!.getGaps().first()

        var game = Game(Board(gap.first.rank,gap.second.rank), mainPlayer)

//        println(game.board)

        if (mainPlayer.piece == Piece.WHITE) {
            val newMove = mainPlayer.makeMove(game, p2)!!
            game = game.applyMove(newMove)
//            println(game.board)

        }

        var done = false

        while (!done) {
            val otherMove = otherPlayer.makeMove(game, p2)!!
            game = game.applyMove(otherMove)
//            println(game.board)

            done = game.over(mainPlayer)
            if (!done) {

                val newMove = mainPlayer.makeMove(game, p1)!!
                game = game.applyMove(newMove)
//                println(game.board)

                done = game.over(otherPlayer)
            }
        }
        val winner = game.winner()
//        println(game.winner())
//        println(game.board)
        if (winner == null) {
            return null
        } else if (winner == mainPlayer) {
            return p1
        } else {
            return p2
        }

    }

    fun testAgainstRandom(p1: NeuralEvaluator): Int {
        val playerController1 = NNMinMaxController()
        val playerController2 = MinimaxController()

        val mainPlayer = Player(Piece.BLACK, null, playerController1)
        val otherPlayer = Player(Piece.WHITE, mainPlayer, playerController2)
        mainPlayer.opponent = otherPlayer

        val gap = mainPlayer.controller!!.getGaps().first()

        var game = Game(Board(gap.first.rank,gap.second.rank), mainPlayer)

//        println(game.board)

        if (mainPlayer.piece == Piece.WHITE) {
            val newMove = mainPlayer.makeMove(game)!!
            game = game.applyMove(newMove)
//            println(game.board)

        }

        var done = false

        while (!done) {
            val otherMove = otherPlayer.makeMove(game)!!
            game = game.applyMove(otherMove)
//            println(game.board)

            done = game.over(mainPlayer)
            if (!done) {

                val newMove = mainPlayer.makeMove(game, p1)!!
                game = game.applyMove(newMove)
//                println(game.board)

                done = game.over(otherPlayer)
            }
        }
        val winner = game.winner()
//        println(game.winner())
//        println(game.board)
        if (winner == null) {
            return 1
        } else if (winner == mainPlayer) {
            return 2
        } else {
            return 0
        }

    }

    fun tournament(individuals: List<NeuralEvaluator>): List<Pair<NeuralEvaluator, Int>> {

        val wins = Array(individuals.size) { 0 }
        for (i in individuals.indices) {
            for (j in individuals.indices) {
                if (i != j) {
                    val p1 = individuals[i]
                    val p2 = individuals[j]
                    val winner = playGame(p1, p2)
//                    println(winner)
                    if (winner == null) {
                        wins[i] += 1
                        wins[j] += 1
                    } else if (winner == p1) {
                        wins[i] += 2
                    } else{
                        wins[j] += 2
                    }
                }
            }
            println("Iteration $i of tournament complete")
        }
        val pairs = individuals.zip(wins).sortedByDescending { it.second }
//        println(pairs)
        return pairs
    }

    fun evaluatePopulation(epoch: Int): Pair<NeuralEvaluator, Int> {
        var numEvals = 0
        var maxScore : Pair<NeuralEvaluator, Int> = Pair(pop[0], 0)
        val newPop = mutableListOf<NeuralEvaluator>()
        val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
//        val executorService = Executors.newFixedThreadPool(1)
        for (i in pop.indices) {
            executorService.execute {
                val newSet = tournament(pop.shuffled().subList(0,minOf(5,pop.size)))

                var firstParent = newSet[0].first
                var secondParent = newSet[1].first

                var randNum1 = nextFloat()
                var randNum2 = nextFloat()
                val p = 0.5
                for (i in newSet.indices) {
                    val np = p * (1 - p).pow(i)
                    if (randNum1 >= np) {
                        firstParent = newSet[i].first
                        break
                    }
                    if (randNum2 >= np) {
                        secondParent = newSet[i].first
                        break
                    }
                }

                val newChild = firstParent.combineAndMutateLayers(secondParent)
                newPop.add(newChild)

                if (maxScore.second < newSet.first().second) {
                    maxScore = newSet.first()

                }
            }
        }

        executorService.shutdown()
        executorService.awaitTermination(Int.MAX_VALUE.toLong(), TimeUnit.SECONDS)

        var w = 0
        var l = 0
        var d = 0
        oldQueue.add(maxScore.first)
        if (oldQueue.size > 10) {
            val oldPlayer = oldQueue.remove()
            for (i in 0 until 5) {
                val winner: NeuralEvaluator?

                if (nextFloat() > 0.5) {
                    winner = playGame(maxScore.first, oldPlayer)
                } else {
                    winner = playGame(oldPlayer, maxScore.first)
                }
//                    println(winner)

                if (winner == null) {
                    d += 1
                } else if (winner == maxScore.first) {
                    w += 1
                } else {
                    l += 1
                }
//                randTotal += testAgainstRandom(maxScore.first)
            }
            println("New generation $epoch evaluated. Against 10 generations ago: $w/$d/$l")
        } else {
            println("New generation $epoch evaluated.")
        }
        pop = newPop

        return maxScore
    }

    init {
        initializePop(15)

    }

}

public fun m(args: Array<String>) {
    val trainer = ControllerTrainer()

//    val nn = NeuralEvaluator(2, 64)
//    println(nn.feedForward(Matrix(1,64, {it.toFloat()})).matrix.contentDeepToString())
//    nn.saveLayers("test")
//    val nn2 = NeuralEvaluator(2, 64)
//    println(nn2.feedForward(Matrix(1,64, {it.toFloat()})).matrix.contentDeepToString())
//    nn2.loadLayers("test")
//    println(nn2.feedForward(Matrix(1,64, {it.toFloat()})).matrix.contentDeepToString())
    println("Running on ${Runtime.getRuntime().availableProcessors()} cores!")
    for (i in 0..1000) {
        val start = currentTimeMillis()

        trainer.evaluatePopulation(i).first.saveLayers("best${i}")
        println("That took ${((start - currentTimeMillis()) / 1000f).roundToInt()} seconds")
    }
}

public fun main(args: Array<String>) {
    val trainer = ControllerTrainer()

    println("Running on ${Runtime.getRuntime().availableProcessors()} cores!")
    val networks = mutableListOf<NeuralEvaluator>()
    for (i in 450..499) {
        val nn = NeuralEvaluator(2, 64)
        nn.loadLayers("best${i}")

        networks.add(nn)
    }

    println("Loaded neural networks. Will now simulate ${(networks.size * networks.size) - networks.size} games. Let the tournament begin!")

    val scores = trainer.tournament(networks)

    println(scores.asReversed())

    println("Best network from tournament: ${scores.first().first} with a score of ${scores.first().second}")
    println("Saving network in ./BossMusic/")
    scores.first().first.saveLayers("BossMusic")
}