package isel.leic.tds.checkers

import isel.leic.tds.checkers.model.Game
import isel.leic.tds.checkers.storage.BoardSerializer
import isel.leic.tds.checkers.storage.BoardStorage
import isel.leic.tds.checkers.storage.MongoDbAccess
import isel.leic.tds.checkers.ui.cmd.Command
import isel.leic.tds.checkers.ui.cmd.SyntaxError
import isel.leic.tds.checkers.ui.cmd.getCommands
import isel.leic.tds.checkers.ui.cmd.readCommand
import isel.leic.tds.storage.FileStorage

fun main() {
    val storage = saveGameDataIn("MongoDb")
    val cmds = getCommands(storage)
    var game: Game? = null
    printWelcomeMsg()
    while( true ) {
        val (name, args) = readCommand()
        val cmd: Command? = cmds[name]
        if (cmd == null) println("Invalid command: $name")
        else try {
            game = cmd.execute(args, game)
            cmd.show(game)
        } catch (ex: Exception) {
            println(ex.message)
            if (ex is SyntaxError)
                println("Use: $name ${cmd.argsSyntax}")
        }
    }
}

/**
 * Saves game data in a specified storage.
 * Currently supported storages: File (Simple text file), MongoDb (Online Documental database).
 * @return The indicated storage.
 * @throws [IllegalArgumentException] If that storage it's not supported.
 */
private fun saveGameDataIn(s: String): BoardStorage {
    val folder = "games"
    return when (s.uppercase()) {
        "FILE" -> FileStorage(folder, BoardSerializer)
        "MONGODB" -> MongoDbAccess.createClient()
        else -> throw IllegalArgumentException("Specified data storage it's not supported")
    }
}

/**
 * Prints a message to the stdout.
 */
private fun printWelcomeMsg() {
    listOf(
        "[Program]: Checkers in command line!",
        "[Commands]: To see the avalaible commands type: help"
    ).forEach { println(it) }
}
