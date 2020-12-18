package pawnrace

class MinimaxController : Controller {
    override fun makeMove(validMoves: List<Move>, g: Game): Move? {
        val validMovesScores = validMoves.map { Pair(minimax(0, g.player, g.applyMove(it), g.player.opponent!!, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), it) }
//        println(validMovesScores)
        return validMovesScores.maxByOrNull { it.first }!!.second

    }

    override fun getGaps(): List<Pair<Position, Position>> {
        return ('a'..'h').map { it1 -> ('a'..'h').map { it2 -> Pair(Position("${it1}1"), Position("${it2}1"))} }.flatten().shuffled()
    }

    fun minimax(depth: Int, maximising: Player, game: Game, currPlayer: Player, alpha: Float, beta: Float): Float {
        val over = game.over(currPlayer)
        if (depth == 4 || over) {

            game.unapplyMove()
            if (over) {
                val winningPlayer = game.winner()
                if (winningPlayer == null) {
//                    println("Nobody Won")
                    return 0f
                } else if (winningPlayer == maximising) {
//                    println("${winningPlayer} won with score ${1000} and maximising is ${maximising}")
                    return 1000f
                } else {
//                    println("${winningPlayer} won with score ${-1000}")
                    return -1000f
                }
            }
            if (currPlayer == maximising) {
                val score = heuristic(game, maximising)
//                println("$score from ${game.player.piece}")
                return score
            } else {
                val score = heuristic(game, maximising)
//                println("$score from ${game.player.piece}")
                return score
            }
        }

        if (currPlayer == maximising) {
            var score = Float.NEGATIVE_INFINITY
//            var scores = mutableListOf<Float>()
            var aPrime = alpha
            game.validMoves(currPlayer.piece).forEach {
                score = maxOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta))
                aPrime = maxOf(alpha, score)
                if (aPrime >= beta) {
                    return@forEach
                }
            }
            game.unapplyMove()
            return score
        } else {
            var score = Float.POSITIVE_INFINITY
//            var scores = mutableListOf<Float>()
            var aPrime = alpha
            game.validMoves(currPlayer.piece).forEach {
                score = minOf(score, minimax(depth + 1, maximising, game.applyMove(it), currPlayer.opponent!!, alpha, beta))
                aPrime = minOf(beta, score)
                if (aPrime <= alpha) {
                    return@forEach
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