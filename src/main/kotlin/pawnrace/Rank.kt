package pawnrace

class Rank {
    val rank: Char
    val intRep: Int
    constructor(rank: Char) {
        this.rank = rank.toLowerCase()
        this.intRep = "abcdefgh".indexOf(this.rank)
    }
    override fun toString(): String = rank.toString().toLowerCase()

    override fun equals(other: Any?): Boolean {
        if (other is Rank) return (other.rank == rank)
        else return false
    }
}