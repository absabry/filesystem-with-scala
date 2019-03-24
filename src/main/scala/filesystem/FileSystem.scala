package filesystem

import java.util.Scanner

import commands.Command
import files.Directory

import util.control.Breaks._

object FileSystem extends App{

  val scanner = new Scanner(System.in) // to get new commands
  var state = State(Directory.ROOT, Directory.ROOT) // intialize our filesystem root

  breakable {
    while (true) {
      state.show()
      val input = scanner.nextLine()
      state = Command.from(input).apply(state)
    }
  }
}
