package pawnrace

class Player(val piece: Piece, var opponent: Player? = null, val controller: Controller?) {

    fun getAllPawns(game: Game): List<Position> = game.board.positionsOf(piece)

    fun getAllValidMoves(game: Game): List<Move> = game.validMoves(piece)

//    fun isPassedPawn(pos: Position): Boolean {
//
//    }

    fun makeMove(game: Game, nn: NeuralEvaluator? = null): Move? {
        if (nn == null) {
            return controller?.makeMove(getAllValidMoves(game), game)
        } else {
            return (controller as NNMinMaxController)?.makeMove(getAllValidMoves(game), game, nn)
        }
    }
}