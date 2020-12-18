package pawnrace

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*
import kotlin.math.roundToInt


class MinimaxParallel : Controller {

    val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
//    val executorService = Executors.newFixedThreadPool(1)
    override fun makeMove(validMoves: List<Move>, g: Game): Move? {
//        val start = System.currentTimeMillis()

//        val validMovesScores = validMoves.map { Pair(minimax(0, g.player, g.applyMove(it), g.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), it) }
        val validMovesScores = parallelMap(validMoves, g, g.player, g.player.opponent!!, 0)
//        println("That took ${((start - System.currentTimeMillis()) / 1000f).roundToInt()} seconds")
        return validMovesScores.maxByOrNull { it.first }!!.second

    }

    @Throws(InterruptedException::class)
    private fun parallelMap(moves: List<Move>, game: Game, maximising: Player, currPlayer: Player, depth: Int): List<Pair<Float, Move>> {
        val scores: MutableList<Pair<Float, Move>> = mutableListOf()

        for (element in moves) {
            executorService.execute {
                var score = Float.NEGATIVE_INFINITY
                val g = game.copyGame()
//                println(game.moves)
//                println(g.moves)
                score = maxOf(score, minimax(0, game.player, g.applyMove(element), game.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY))
//                g.unapplyMove()
                scores.add(Pair(score, element))
//                println(scores)
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(100, TimeUnit.SECONDS)
//        Thread.sleep(4_500)
//        sumList.add(map["test"])
//        println(scores)
        return scores
    }

    override fun getGaps(): List<Pair<Position, Position>> {
        return ('a'..'h').map { it1 -> ('a'..'h').map { it2 -> Pair(Position("${it1}1"), Position("${it2}1"))} }.flatten().shuffled()
    }

    fun minimax(depth: Int, maximising: Player, game: Game, currPlayer: Player, alpha: Float, beta: Float): Float {
        val over = game.over(currPlayer)
        var alpha = alpha
        var beta = beta
        if (depth == 6 || over) {

            game.unapplyMove()
            if (over) {
                val winningPlayer = game.winner()
                if (winningPlayer == null) {
//                    println("Nobody Won")        val start = System.currentTimeMillis()

                    return 0f
                } else if (winningPlayer == maximising) {
                    return 1000f
                } else {
                    return -1000f
                }
            }
            if (currPlayer == maximising) {
                val score = heuristic(game, maximising)
                return score
            } else {
                val score = heuristic(game, maximising)
                return score
            }
        }

        if (currPlayer == maximising) {
            var score = Float.NEGATIVE_INFINITY
            for (it in game.validMoves(currPlayer.piece)) {
                score = maxOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta))
                alpha = maxOf(alpha, score)
                if (beta <= alpha) {
                    break
                }
            }
            game.unapplyMove()
            return score
        } else {
            var score = Float.POSITIVE_INFINITY

            for (it in game.validMoves(currPlayer.piece)) {
                score = minOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta))
                beta = minOf(beta, score)
                if (beta <= alpha) {
                    break
                }
            }
            game.unapplyMove()
            return score
        }
    }

    fun heuristic(g: Game, maximising: Player): Float {

        if (maximising.piece == Piece.WHITE) {
            val whiteScore = g.board.positionsOf(Piece.WHITE).map { it.file.intRep }.maxOrNull()!!.toFloat()
            val blackScore = 8 - g.board.positionsOf(Piece.BLACK).map { it.file.intRep }.minOrNull()!!.toFloat()
//            println(blackScore - whiteScore)

            return whiteScore - blackScore
        } else {
            val whiteScore = g.board.positionsOf(Piece.WHITE).map { it.file.intRep }.maxOrNull()!!.toFloat()
            val blackScore = 7 - g.board.positionsOf(Piece.BLACK).map { it.file.intRep }.minOrNull()!!.toFloat()
//            println(blackScore - whiteScore)

            return blackScore - whiteScore
        }
    }
}