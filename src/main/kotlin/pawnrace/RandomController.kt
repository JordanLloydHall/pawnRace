package pawnrace

class RandomController : Controller {
    override fun makeMove(validMoves: List<Move>, g: Game): Move? {
        return validMoves.shuffled().firstOrNull()
    }

    override fun getGaps(): List<Pair<Position,Position>> {
        return ('a'..'h').map { it1 -> ('a'..'h').map { it2 -> Pair(Position("${it1}1"), Position("${it2}1"))} }.flatten().shuffled()
    }
}