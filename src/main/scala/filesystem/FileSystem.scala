package filesystem

import java.util.Scanner

import commands.Command
import files.Directory

import util.control.Breaks._

object FileSystem extends App {
  val scanner = new Scanner(System.in) // to get new commands
  var state = State(Directory.ROOT, Directory.ROOT) // intialize our filesystem root

  breakable {
    while (true) {
      state.show()
      val input = scanner.nextLine()
      if (input.toLowerCase.equals("exit") || input.toLowerCase.equals("quit"))
        break()
      else
        state = Command.from(input).apply(state)
    }
  }

  // TODO
  //  MKDIR
  //    implement mkdir nested command
  //    implement commands with file spaces (should be between quotes or single quotes")
  //  RM
  //    rm directory, should work? warniing? working with yes & no like linux?
  //    rm detect when i try to kill a parent, not worth it man
  //    not working with absolute paths from another dir...
  //  LS
  //    ls file functionalty
  //    implement ls recursively (come with an argument) command
  //  BONUS
  //    cat and echo relative & absolute paths (refactor cd logic and make it in amother class)
  //    find a way (by refactoring) to implement a help function
  //    "did you mean X" functionality
  //    file and folders with different colors in ls !!!!!

}
