package pawnrace

interface Controller {

    abstract fun makeMove(validMoves: List<Move>, g: Game): Move?

    abstract fun getGaps(): List<Pair<Position,Position>>
}