package pawnrace

class Move(val piece: Piece, val from: Position, val to: Position,
           val type: MoveType) {

    override fun toString(): String {
        return if (type == MoveType.PEACEFUL) "${to.pos}"
                else "${from.rank}x${to}"
    }
}