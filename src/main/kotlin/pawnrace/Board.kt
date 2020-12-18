package pawnrace

import kotlin.math.abs

class Board {
    val board: Array<Array<Piece?>>
    val ranks = "abcdefgh"
    constructor(whiteGap: Rank, blackGap: Rank) {
        

        this.board = Array(8) { Array(8) { null } }

        this.board[1] = Array(8) { if (whiteGap.intRep == it) null else Piece.WHITE }

        this.board[6] = Array(8) { if (blackGap.intRep == it) null else Piece.BLACK }
    }

    constructor(board: Array<Array<Piece?>>) {

        this.board = board
    }

    fun pieceAt(pos: Position): Piece? {
        val p = board[pos.file.intRep][pos.rank.intRep]

        return p
    }

    fun positionsOf(piece: Piece): List<Position> = board.mapIndexed {
            j, it ->
            it.mapIndexed {
                i, it2 -> if (it2 == piece ) Position("${ranks[i]}${j+1}") else null
            }.filterNotNull()
        }.flatten()

    fun isValidMove(move: Move, lastMove: Move? = null): Boolean {

        if (pieceAt(move.from) == null) return false

        if (pieceAt(move.from) != move.piece) return false

        if (move.type == MoveType.PEACEFUL) {

            if (pieceAt(move.to) != null) return false
            if (move.from.rank != move.to.rank) return false

            if (move.piece == Piece.WHITE) {
                if (move.from.file.intRep == 1 && move.to.file.intRep == 3 && pieceAt(Position("${move.to.rank}${move.to.file.intRep}")) == null) return true
                else if (move.to.file.intRep - move.from.file.intRep == 1 ) return true
                else return false
            } else {
                if (move.from.file.intRep == 6 && move.to.file.intRep == 4 ) return true
                else if (move.from.file.intRep - move.to.file.intRep == 1 ) return true
                else return false
            }
        } else if (move.type == MoveType.CAPTURE) {
            if (pieceAt(move.to) == null) return false
            if (move.from.rank == move.to.rank) return false
            if (pieceAt(move.from) == pieceAt(move.to)) return false

            if (move.piece == Piece.WHITE) {
//                if (move.from.file.intRep == 1 && move.to.file.intRep == 3 ) return true
                if (move.to.file.intRep - move.from.file.intRep == 1  && abs(move.from.rank.intRep - move.to.rank.intRep) == 1) return true
                else return false
            } else {
                if (move.from.file.intRep - move.to.file.intRep == 1  && abs(move.from.rank.intRep - move.to.rank.intRep) == 1) return true
                else return false
            }
        } else {
            if (pieceAt(move.to) != null) return false
            if (move.from.rank == move.to.rank) return false
            if (lastMove == null) return false
            if (pieceAt(move.from) == pieceAt(move.to)) return false

            if (abs(lastMove.from.file.intRep - lastMove.to.file.intRep) != 2) return false

            if (move.piece == Piece.WHITE) {
                if (move.to.file.intRep - move.from.file.intRep == 2  && abs(move.from.rank.intRep - move.to.rank.intRep) == 1) return true
                else return false
            } else {
                if (move.from.file.intRep - move.to.file.intRep == 2  && abs(move.from.rank.intRep - move.to.rank.intRep) == 1) return true
                else return false
            }

        }
    }

    fun move(m: Move): Board {
        val b = Array(8) { i -> Array(8) {j -> board[i][j]} }
//        val b = board.toList().toTypedArray()

        b[m.from.file.intRep][m.from.rank.intRep] = null
        b[m.to.file.intRep][m.to.rank.intRep] = m.piece

        return Board(b)
    }

    override fun toString(): String {
        var str = ""

        str += "  " + ranks.toUpperCase().toCharArray().joinToString(" ") + "  \n"
        board.reversedArray().forEachIndexed {
            i, r ->
            str += (8 - i).toString() + " "
            r.forEach {
                if (it == null) {
                    str += ". "
                } else {
                    str += "${it.cha} "
                }
            }
            str += "\n"
        }

        return str
    }
}