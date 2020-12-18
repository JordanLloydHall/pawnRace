package pawnrace



class Position {

    constructor(pos: String) {
        if (pos.length != 2) throw IllegalArgumentException("'$pos' is not of length 2")
        if (pos[0] !in 'A'..'H' && pos[0] !in 'a'..'h') throw IllegalArgumentException("'$pos' does not have first char in range a..h")
        if (pos[1] !in '1'..'8') throw IllegalArgumentException("'$pos' does not have second char in range 1..8")

        this.pos = pos
        this.file = File(pos[1])
        this.rank = Rank(pos[0])
    }

    val pos: String
    val file: File
    val rank: Rank

    override fun toString(): String = pos.toLowerCase()
}