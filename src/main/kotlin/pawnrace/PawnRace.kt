package pawnrace

import java.io.PrintWriter
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.*
import kotlin.random.Random

// You should not add any more member values or member functions to this class
// (or change its name!). The autorunner will load it in via reflection and it
// will be safer for you to just call your code from within the playGame member
// function, without any unexpected surprises!
class PawnRace {
    // Don't edit the type or the name of this method
    // The colour can take one of two values: 'W' or 'B', this indicates your player colour
    val gapConfigs = mutableListOf<Pair<Position,Position>>()
    var hasDoneOpposite = false



    fun playGame(colour: Char, output: PrintWriter, input: BufferedReader): Unit {
        // You should call your code from within here
        // Step 1: If you are the black player, you should send a string containing the gaps
        // It should be of the form "wb" with the white gap first and then the black gap: i.e. "AH"

        val controller = NNMinMaxController()

        val mainPlayer: Player
        val otherPlayer: Player

        val nn = NeuralEvaluator(2, 64)
        nn.loadLayers("BossMusic")

        if (colour == 'B') {
            var gaps = controller.getGaps().filterNot { gapConfigs.contains(it) }

            if (!hasDoneOpposite) {
                gaps = gaps.filterNot { it.first.pos == it.second.pos }
            }

            val gap = gaps.first()

            if (gap.first.file.toString() == gap.second.file.toString()) {
                hasDoneOpposite = true
            }
            
            gapConfigs.add(gap)

            output.println("${gap.first.rank.rank.toUpperCase()}${gap.second.rank.rank.toUpperCase()}")

            mainPlayer = Player(Piece.BLACK, null, controller)
            otherPlayer = Player(Piece.WHITE, mainPlayer, null)
            mainPlayer.opponent = otherPlayer
        } else {
            mainPlayer = Player(Piece.WHITE, null, controller)
            otherPlayer = Player(Piece.BLACK, mainPlayer, null)
            mainPlayer.opponent = otherPlayer
        }

        // Regardless of your colour, you should now receive the gaps verified by the autorunner
        // (or by the human if you are using your own main function below), these are provided
        // in the same form as above ("wb"), for example: "AH"

        val gap = input.readLine()

        // Now you may construct your initial board



        var game = Game(Board(Rank(gap[0]),Rank(gap[1])), mainPlayer)

//        println(game.board)
        // If you are the white player, you are now allowed to move
        // you may send your move, once you have decided what it will be, with output.println(move)
        // for example: output.println("axb4")

        if (mainPlayer.piece == Piece.WHITE) {
            val newMove = mainPlayer.makeMove(game, nn)!!
            output.println(newMove)
            game = game.applyMove(newMove)

//            println(game.board)
        }

        // After point, you may create a loop which waits to receive the other players move
        // (via input.readLine()), updates the state, checks for game over and, if not, decides
        // on a new move and again send that with output.println(move). You should check if the
        // game is over after every move.

        var done = false

        while (!done) {
            val otherMove = game.parseMove(input.readLine(), otherPlayer)!!
            game = game.applyMove(otherMove)
//            println(game.board)

            done = game.over(game.player.opponent!!)
            if (!done) {
                val newMove = mainPlayer.makeMove(game, nn)!!
                output.println(newMove)
                game = game.applyMove(newMove)
//                println(game.board)
                done = game.over(game.player)
            }
        }

        // Once the loop is over, the game has finished and you may wish to print who has won
        // If your advanced AI has used any files, make sure you close them now!

    }
}

// When runnining on the command, provide an argument either W or B, this indicates your player colour
public fun main(args: Array<String>) {
    PawnRace().playGame('W', PrintWriter(System.out, true), BufferedReader(InputStreamReader(System.`in`)))
//
//  val board = Board(Rank('A'), Rank('a'))
//
//  val player2 = Player(Piece.BLACK, null)
//  val player1 = Player(Piece.WHITE, player2)
//  player2.opponent = player1
//
//  var g = Game(board, player1, Stack())
//
////  val moves = g.validMoves(Piece.WHITE)
//
//  while (!g.over()) {
//    val moves = g.validMoves(g.player.piece)
//    val m = moves.shuffled().first()
//    g = g.applyMove(m)
//    println(g.board)
//    println(m)
//  }
//  println("${g.winner()?.piece} Won the game!")
//
////  println(g.parseMove("b5"))
//
////  for (m in moves) {
////    val newG = g.applyMove(m)
////    println(newG.board)
////  }

}
