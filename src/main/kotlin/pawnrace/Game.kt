package pawnrace

import java.util.*


class Game(var board: Board, val player: Player, val moves: Stack<Move> = Stack<Move>()) {

    fun applyMove(move: Move): Game {
        moves.push(move)
//        val newStack = Stack<Move>()
//        newStack.addAll(moves)
        return Game(board.move(move), player, moves)
    }

    fun copyGame(): Game {
        val newStack = Stack<Move>()
        newStack.addAll(moves)
        return Game(board, player, newStack)
    }

    fun unapplyMove() {
        moves.pop()
//        player = player.opponent!!
    }

    fun validMoves(piece: Piece): List<Move> {
        val moves = mutableListOf<Move>()

        val pieces = board.positionsOf(piece)

        val movesPerPiece = pieces.map{
            listOfNotNull(moveDiagonalBy(it, true, piece),
                    moveDiagonalBy(it, false, piece),
                    moveForwardBy(it, 2, piece),
                    moveForwardBy(it, 1, piece)
                )
        }.flatten()

        return movesPerPiece
    }

    fun over(player: Player): Boolean {
        for (pos in board.positionsOf(Piece.WHITE)) {
            if (pos.file.intRep == 7) {
                return true
            }
        }

        for (pos in board.positionsOf(Piece.BLACK)) {
            if (pos.file.intRep == 0) {
                return true
            }
        }

        if (validMoves(player.piece).isEmpty()) {
            return true
        }

        return false
    }

    fun winner(): Player? {
        for (pos in board.positionsOf(player.piece)) {
            if (pos.file.intRep == 7 || pos.file.intRep == 0) {
                return player
            }
        }

        for (pos in board.positionsOf(player.opponent!!.piece)) {
            if (pos.file.intRep == 7 || pos.file.intRep == 0) {
                return player.opponent
            }
        }

        return null
    }

    fun parseMove(san: String, player: Player): Move? {
        if (san.length == 2 && san[0].isLetter() && san[1].isDigit()) {
            val p = Position("${san[0]}${san[1]}")
            if (player.piece == Piece.WHITE) {
                val p1 = Position("${p.rank}${p.file.intRep}")
                val p2 = Position("${p.rank}${p.file.intRep - 1}")
                if (board.pieceAt(p1) == Piece.WHITE) {
                    return Move(Piece.WHITE, p1, p, MoveType.PEACEFUL)
                } else if (board.pieceAt(p2) == Piece.WHITE) {
                    return Move(Piece.WHITE, p2, p, MoveType.PEACEFUL)
                }
            } else {
                val p1 = Position("${p.rank}${p.file.intRep + 2}")
                val p2 = Position("${p.rank}${p.file.intRep + 3}")
                if (board.pieceAt(p1) == Piece.BLACK) {
                    return Move(Piece.BLACK, p1, p, MoveType.PEACEFUL)
                } else if (board.pieceAt(p2) == Piece.BLACK) {
                    return Move(Piece.BLACK, p2, p, MoveType.PEACEFUL)
                }
            }

        }

        if (san.length == 4 && san[0].isLetter() && san[1] == 'x' && san[2].isLetter() && san[3].isDigit()) {
            val p = Position(san.subSequence(2, 4) as String)
            if (player.piece == Piece.WHITE) {
                val p1 = Position("${san[0]}${p.file.intRep}")
                if (board.pieceAt(p1) == Piece.WHITE) {
                    if (board.pieceAt(p) == Piece.BLACK) {
                        return Move(Piece.WHITE, p1, p, MoveType.EN_PASSANT)
                    } else {
                        return Move(Piece.WHITE, p1, p, MoveType.CAPTURE)
                    }
                }

            } else {

                val p1 = Position("${san[0]}${p.file.intRep + 2}")
                if (board.pieceAt(p1) == Piece.BLACK) {
                    if (board.pieceAt(p) == Piece.WHITE) {
                        return Move(Piece.BLACK, p1, p, MoveType.EN_PASSANT)
                    } else {
                        return Move(Piece.BLACK, p1, p, MoveType.CAPTURE)
                    }
                }

            }
        }

        throw IllegalArgumentException(san)
        return null
    }

    private fun moveForwardBy(pos: Position, step: Int, piece: Piece): Move? {
        val m: Move
        if (piece == Piece.WHITE) {
            if (pos.file.intRep+1+step > 8) {
                return null
            }
            m = Move(piece, pos, Position("${pos.rank}${pos.file.intRep+1+step}"),MoveType.PEACEFUL)
        } else {
            if (pos.file.intRep+1-step < 1) {
                return null
            }
            m = Move(piece, pos, Position("${pos.rank}${pos.file.intRep-step+1}"),MoveType.PEACEFUL)
        }

        val lastMove: Move?
        if (moves.isEmpty()) {
            lastMove = null
        } else {
            lastMove = moves.peek()
        }

        if (board.isValidMove(m, lastMove)) {
            return m
        } else {
            return null
        }
    }

    private fun moveDiagonalBy(pos: Position, isLeft: Boolean, piece: Piece): Move? {
        val mCap: Move
        val mEn: Move
        if (piece == Piece.WHITE) {
            if (isLeft) {
                if (pos.rank.intRep - 1 >= 0) {
                    mCap = Move(piece, pos, Position("${board.ranks[pos.rank.intRep - 1]}${pos.file.intRep + 2}"), MoveType.CAPTURE)
                    mEn = Move(piece, pos, Position("${board.ranks[pos.rank.intRep - 1]}${pos.file.intRep + 2}"), MoveType.EN_PASSANT)
                } else {
                    return null
                }
            } else {
                if (pos.rank.intRep + 1 <= 7) {
                    mCap = Move(piece, pos, Position("${board.ranks[pos.rank.intRep + 1]}${pos.file.intRep + 2}"), MoveType.CAPTURE)
                    mEn = Move(piece, pos, Position("${board.ranks[pos.rank.intRep + 1]}${pos.file.intRep + 2}"), MoveType.EN_PASSANT)
                } else {
                    return null
                }
            }
        } else {
            if (isLeft) {
                if (pos.rank.intRep - 1 >= 0) {
                    mCap = Move(piece, pos, Position("${board.ranks[pos.rank.intRep - 1]}${pos.file.intRep}"), MoveType.CAPTURE)
                    mEn = Move(piece, pos, Position("${board.ranks[pos.rank.intRep - 1]}${pos.file.intRep}"), MoveType.EN_PASSANT)
                } else {
                    return null
                }
            } else {
                if (pos.rank.intRep + 1 <= 7) {
                    mCap = Move(piece, pos, Position("${board.ranks[pos.rank.intRep + 1]}${pos.file.intRep}"), MoveType.CAPTURE)
                    mEn = Move(piece, pos, Position("${board.ranks[pos.rank.intRep + 1]}${pos.file.intRep}"), MoveType.EN_PASSANT)
                } else {
                    return null
                }
            }
        }

        val lastMove: Move?
        if (moves.isEmpty()) {
            lastMove = null
        } else {
            lastMove = moves.peek()
        }

        if (board.isValidMove(mCap, lastMove)) {
            return mCap
        } else if (board.isValidMove(mEn, lastMove)) {
            return mEn
        } else {
            return null
        }
    }
}