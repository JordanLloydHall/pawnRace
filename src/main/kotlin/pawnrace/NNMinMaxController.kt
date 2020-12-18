package pawnrace

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class NNMinMaxController : Controller {


//    fun makeMove(validMoves: List<Move>, g: Game, nn: NeuralEvaluator): Move? {
////        val start = System.currentTimeMillis()
//
//        val validMovesScores = validMoves.map { Pair(minimax(0, g.player, g.applyMove(it), g.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, nn), it) }
////        println(validMovesScores)
////        println("That took ${((start - System.currentTimeMillis()) / 1000f).roundToInt()} seconds")
//        return validMovesScores.maxByOrNull { it.first }?.second
//
//    }

    fun makeMove(validMoves: List<Move>, g: Game, nn: NeuralEvaluator): Move? {
//        val start = System.currentTimeMillis()

//        val validMovesScores = validMoves.map { Pair(minimax(0, g.player, g.applyMove(it), g.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), it) }
        val validMovesScores = parallelMap(validMoves, g, g.player, g.player.opponent!!, 0, nn)
//        println("That took ${((start - System.currentTimeMillis()) / 1000f)} seconds")
        return validMovesScores.maxByOrNull { it.first }!!.second

    }

    @Throws(InterruptedException::class)
    private fun parallelMap(moves: List<Move>, game: Game, maximising: Player, currPlayer: Player, depth: Int, nn: NeuralEvaluator): List<Pair<Float, Move>> {
        val scores: MutableList<Pair<Float, Move>> = mutableListOf()
        val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2 - 2)

        for (element in moves) {
            executorService.execute {
                var score = Float.NEGATIVE_INFINITY
                val g = game.copyGame()
//                println(game.moves)
//                println(g.moves)
                score = maxOf(score, minimax(0, game.player, g.applyMove(element), game.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, nn))
//                g.unapplyMove()
                scores.add(Pair(score, element))
//                println(scores)
            }
        }

        executorService.shutdown()
        Thread.sleep(4_500)

//        executorService.awaitTermination(4_500, TimeUnit.MILLISECONDS)
//        sumList.add(map["test"])
//        println(scores)
        return scores
    }

    override fun makeMove(validMoves: List<Move>, g: Game): Move? {
        TODO("Not yet implemented")
    }

    override fun getGaps(): List<Pair<Position, Position>> {
        return ('a'..'h').map { it1 -> ('a'..'h').map { it2 -> Pair(Position("${it1}1"), Position("${it2}1"))} }.flatten().shuffled()
    }

    fun minimax(depth: Int, maximising: Player, game: Game, currPlayer: Player, alpha: Float, beta: Float, nn: NeuralEvaluator): Float {
        val over = game.over(currPlayer)
        var alpha = alpha
        var beta = beta
        if (depth == 6 || over) {

            game.unapplyMove()
            if (over) {
                val winningPlayer = game.winner()
                if (winningPlayer == null) {
//                    println("Nobody Won")
                    return 0f
                } else if (winningPlayer == maximising) {
                    return 1000f
                } else {
                    return -1000f
                }
            }
            if (currPlayer == maximising) {
                val score = heuristic(game, maximising, nn)
                return score
            } else {
                val score = heuristic(game, maximising, nn)
                return score
            }
        }

        if (currPlayer == maximising) {
            var score = Float.NEGATIVE_INFINITY
            for (it in game.validMoves(currPlayer.piece)) {
                score = maxOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta, nn))
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
                score = minOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta, nn))
                beta = minOf(beta, score)
                if (beta <= alpha) {
                    break
                }
            }
            game.unapplyMove()
            return score
        }
    }

    fun boardAsMatrix(board: Board, maximising: Player): Matrix {
        val boardMatrix = Matrix(1, 64) {
            val i = it / 8
            val j = it % 8
            val piece = board.board[i][j]
            if (piece == null) {
                0f
            } else if (piece == maximising.piece) {
                1f
            } else {
                -1f
            }
        }

//        println(boardMatrix.matrix.contentDeepToString())

//        for (i in board.board.indices) {
//            for (j in board.board.first().indices) {
//                val piece = board.board[i][j]
//                if (piece == null) {
//                    boardMatrix.matrix[0][i*board.board.first().size + j] = 0f
//                } else if (piece == maximising.piece) {
//                    boardMatrix.matrix[0][i*board.board.first().size + j] = 1f
//                } else {
//                    boardMatrix.matrix[0][i*board.board.first().size + j] = -1f
//                }
//            }
//        }

        return boardMatrix
    }

    fun heuristic(g: Game, maximising: Player, nn: NeuralEvaluator): Float {

        return nn.feedForward(boardAsMatrix(g.board, maximising)).matrix[0][0]
    }
}